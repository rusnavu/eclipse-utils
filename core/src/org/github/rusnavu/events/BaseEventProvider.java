package org.github.rusnavu.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class BaseEventProvider implements IEventProvider {

	private final ArrayList<Entry> listeners = new ArrayList<Entry>();

	private static abstract class Matcher {

		public abstract boolean matches(String value);

	}

	private static final class PatternMatcher extends Matcher {
	
		private Pattern pattern;
	
		public PatternMatcher(String filter) {
			String p = filter.replaceFirst("[*]", "\\\\w+([.]\\\\w+)*")
				.replaceAll("\\.", "[.]");
			pattern = Pattern.compile(p);
		}
	
		@Override
		public boolean matches(String value) {
			return pattern.matcher(value).matches();
		}
	
	}

	private static final class StringMatcher extends Matcher {
	
		private String filter;

		public StringMatcher(String filter) {
			assert filter != null;
			this.filter = filter;
		}
	
		@Override
		public boolean matches(String value) {
			return filter.equals(value);
		}
	
	}

	private class Entry {
		private IEventListener listener;
		private Matcher[] filters;

		protected Entry(String filter, IEventListener listener) {
			assert listener != null;
			this.filters = new Matcher[]{_compile(filter)};
			this.listener = listener;
		}

		@Override
		public int hashCode() {
			return listener.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Entry)
				return listener.equals(((Entry) obj).listener);
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return String.format("%s %s", Arrays.asList(filters), listener);
		}

		public void filter(String filter) {
			Matcher[] next = new Matcher[filters.length + 1];
			System.arraycopy(filters, 0, next, 1, filters.length);
			next[0] = _compile(filter);
			filters = next;
		}

		public boolean conforms(String type) {
			for (Matcher filter : filters)
				if (filter.matches(type))
					return true;
			return false;
		}
	}

	@Override
	public synchronized void addEventListener(String filter, IEventListener listener) {
		Entry entry = new Entry(filter, listener);
		int i = listeners.indexOf(entry);
		if (i >= 0) {
			entry = listeners.get(i);
			entry.filter(filter);
		} else
			listeners.add(entry);
	}

	@Override
	public synchronized void removeEventListener(IEventListener listener) {
		listeners.remove(new Entry("", listener));
	}

	@SuppressWarnings("unchecked")
	public void fireEvent(String type, Object arg) {
		Collection<Entry> listeners;
		synchronized (this) {
			listeners = (Collection<Entry>) this.listeners.clone();
		}
		for (Entry entry : listeners) try {
			if (entry.conforms(type))
				entry.listener.handleEvent(type, arg);
		} catch (Throwable e) {
			// TODO: handle exception
		}
	}

	private static Matcher _compile(String filter) {
		java.util.regex.Matcher matcher = Pattern.compile("(\\w+(?:[.]\\w+)*[.])?[*]").matcher(filter);
		if (matcher.matches())
			return new PatternMatcher(filter);
		else
			return new StringMatcher(filter);
	}

}

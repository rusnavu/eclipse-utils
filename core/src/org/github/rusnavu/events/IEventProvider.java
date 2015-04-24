package org.github.rusnavu.events;

public interface IEventProvider {

	void addEventListener(String filter, IEventListener listener);

	void removeEventListener(IEventListener listener);

}

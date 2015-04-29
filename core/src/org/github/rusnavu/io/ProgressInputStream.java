package org.github.rusnavu.io;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

public class ProgressInputStream extends InputStream {

	private final InputStream stream;
	private final IProgressMonitor monitor;
	private final int scale;

	public ProgressInputStream(InputStream stream, IProgressMonitor monitor, int ticks) {
		this(stream, monitor, ticks, IProgressMonitor.UNKNOWN);
	}

	public ProgressInputStream(InputStream stream, IProgressMonitor monitor, int ticks, long length) {
		assert length == IProgressMonitor.UNKNOWN || length > 0;
		this.stream = stream;
		int totalWork;
		if ((length == IProgressMonitor.UNKNOWN) || (length < Integer.MAX_VALUE)) {
			totalWork = (int) length;
			scale = 1;
		} else {
			scale = (int) (length / Integer.MAX_VALUE);
			totalWork = (int) (length / scale);
		}
		this.monitor = new SubProgressMonitor(monitor, totalWork);
		this.monitor.beginTask("", ticks);
	}

	private int worked(int readed) {
		monitor.worked(readed/scale);
		return readed;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return worked(stream.read(b)/scale);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return worked(stream.read(b, off, len)/scale);
	}

	@Override
	public int read() throws IOException {
		int read;
		if ((read = stream.read()) >= 0)
			monitor.worked(1/scale);
		return read;
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	@Override
	public long skip(long n) throws IOException {
		return stream.skip(n);
	}

	@Override
	public int available() throws IOException {
		return stream.available();
	}

	@Override
	public synchronized void mark(int readlimit) {
		stream.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		stream.reset();
	}

	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}

}
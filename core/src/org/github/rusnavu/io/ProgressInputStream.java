package org.github.rusnavu.io;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

public class ProgressInputStream extends InputStream {

	private final InputStream stream;
	private final IProgressMonitor monitor;

	public ProgressInputStream(InputStream stream, IProgressMonitor monitor) {
		this(stream, monitor, IProgressMonitor.UNKNOWN);
	}

	public ProgressInputStream(InputStream stream, IProgressMonitor monitor, int ticks) {
		this.stream = stream;
		this.monitor = new SubProgressMonitor(monitor, ticks);
	}

	private int worked(int readed) {
		monitor.worked(readed);
		return readed;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return worked(stream.read(b));
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return worked(stream.read(b, off, len));
	}

	@Override
	public int read() throws IOException {
		int read;
		if ((read = stream.read()) >= 0)
			monitor.worked(1);
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
package org.github.rusnavu.io;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;

public class ProgressInputStream extends InputStream {

	private final InputStream stream;
	private final IProgressMonitor monitor;
	private final int scale;
	private long worked;

	public ProgressInputStream(InputStream stream, IProgressMonitor monitor, long length) {
		this.stream = stream;
		this.monitor = monitor;
		this.scale = (int) (length / Integer.MAX_VALUE) + 1;
		int total;
		if (length <= 0)
			total = IProgressMonitor.UNKNOWN;
		else if (length > Integer.MAX_VALUE) {
			total = (int) (length / scale);
		} else
			total = (int) length;
		monitor.beginTask("Reading data", total);
	}

	private int worked(int readed) {
		worked += readed;
		if (worked > scale) {
			int part = (int)(worked/scale);
			monitor.worked(part);
			worked -= scale*part;
		}
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
			worked(1);
		return read;
	}

	@Override
	public void close() throws IOException {
		monitor.done();
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
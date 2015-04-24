package org.github.rusnavu.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamOnDemand extends InputStream {

	private static final InputStream CLOSED = new InputStream() {
		@Override
		public int read() throws IOException {
			return -1;
		}
	};

	private static class FileInputStreamFactory implements InputStreamFactory {
		private final File file;

		private FileInputStreamFactory(File file) {
			this.file = file;
		}

		@Override
		public InputStream open() throws IOException {
			return new FileInputStream(file);
		}
	}

	private class Starter extends InputStream {

		private final InputStreamFactory factory;

		public Starter(InputStreamFactory factory) {
			this.factory = factory;
		}

		private InputStream stream() throws IOException {
			return stream = factory.open();
		}

		@Override
		public int read() throws IOException {
			return stream().read();
		}

		public int read(byte[] b) throws IOException {
			return stream().read(b);
		}

		public int read(byte[] b, int off, int len) throws IOException {
			return stream().read(b, off, len);
		}

		public long skip(long n) throws IOException {
			return stream().skip(n);
		}

		public int available() throws IOException {
			return 0;
		}

		public void close() throws IOException {
			stream = CLOSED;
		}

		public void mark(int readlimit) {
			try {
				stream().mark(readlimit);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		public void reset() throws IOException {
			stream().reset();
		}

		public boolean markSupported() {
			try {
				return stream().markSupported();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

	}

	private InputStream stream;

	public interface InputStreamFactory {

		InputStream open() throws IOException;

	}

	public InputStreamOnDemand(InputStreamFactory factory) {
		this.stream = new Starter(factory);
	}

	public InputStreamOnDemand(File file) {
		this(new FileInputStreamFactory(file));
	}

	public int read(byte[] b) throws IOException {
		return stream.read(b);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return stream.read(b, off, len);
	}

	@Override
	public int read() throws IOException {
		return stream.read();
	}

	public int available() throws IOException {
		return stream.available();
	}

	public void close() throws IOException {
		stream.close();
	}

	public void mark(int readlimit) {
		stream.mark(readlimit);
	}

	public boolean markSupported() {
		return stream.markSupported();
	}

	public long skip(long n) throws IOException {
		return stream.skip(n);
	}

	public void reset() throws IOException {
		stream.reset();
	}

}

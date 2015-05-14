package org.github.rusnavu.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class Stream2File implements Runnable {

	private final InputStream in;
	private final File file;

	public Stream2File(InputStream in, File file) throws IOException {
		this.in = in;
		this.file = file;
	}

	@Override
	public void run() {
		synchronized (file) {
			FileOutputStream out;
			try {
				out = new FileOutputStream(file);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			} finally {
				file.notify();
			}
			try {
				IOUtils.copy(in, out);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void copy(InputStream in, File file) throws IOException {
		synchronized (file) {
			Thread thread = new Thread(new Stream2File(in, file));
			try {
				thread.start();
				file.wait();
			} catch (InterruptedException e) {
				throw new IOException("Thread interrupted", e);
			}
		}
	}

	/**
	 * @deprecated Use {@link IOUtils#copy(InputStream, OutputStream)}
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static void copy(InputStream input, OutputStream output) throws IOException {
		IOUtils.copy(input, output);
	}

	/**
	 * @deprecated Use {@link IOUtils#closeQuietly(Closeable)}
	 * @param res
	 */
	public static void close(Closeable res) {
		IOUtils.closeQuietly(res);
	}

}
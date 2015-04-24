package org.github.rusnavu.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
				copy(in, out);
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

	public static void copy(InputStream input, OutputStream output) throws IOException {
		int readed;
		byte[] buf = new byte[1024];
		try {
			while ((readed = input.read(buf)) > 0) {
				output.write(buf, 0, readed);
			}
		} finally {
			close(output);
			close(input);
		}
	}

	public static void close(Closeable res) {
		try {
			res.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
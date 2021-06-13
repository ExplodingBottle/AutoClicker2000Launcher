package autoclicker2000.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
	public static void downloadFile(File where, String from) throws IOException {

		URL fromURL = new URL(from);
		InputStream input = fromURL.openConnection().getInputStream();
		FileOutputStream fos = new FileOutputStream(where);

		byte[] buffer = new byte[8192];
		int readedLength;

		while ((readedLength = input.read(buffer, 0, buffer.length)) != -1) {
			fos.write(buffer, 0, readedLength);
		}
		input.close();
		fos.close();

	}

	public static void extract(File to, File from) throws IOException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(from));
		ZipEntry nextEntry;
		while ((nextEntry = zis.getNextEntry()) != null) {
			if (nextEntry.isDirectory()) {
				new File(to.getAbsolutePath() + "/" + nextEntry.getName()).mkdirs();
			} else {
				new File(to.getAbsolutePath() + "/" + nextEntry.getName()).getParentFile().mkdirs();
				FileOutputStream fos = new FileOutputStream(to.getAbsolutePath() + "/" + nextEntry.getName());
				byte[] buffer = new byte[8192];
				int readedLength;

				while ((readedLength = zis.read(buffer, 0, buffer.length)) != -1) {
					fos.write(buffer, 0, readedLength);
				}

				fos.close();
			}
		}
		zis.close();
	}
}

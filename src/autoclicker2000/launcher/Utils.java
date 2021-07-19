/*
 * AutoClicker2000 Launcher
 * Copyright (C) 2021  ExplodingBottle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

	private static final int BUFFER_SIZE = 1024 * 16;

	public static void downloadFile(File where, String from) throws IOException {

		URL fromURL = new URL(from);
		InputStream input = fromURL.openConnection().getInputStream();
		FileOutputStream fos = new FileOutputStream(where);

		byte[] buffer = new byte[BUFFER_SIZE];
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
				byte[] buffer = new byte[BUFFER_SIZE];
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

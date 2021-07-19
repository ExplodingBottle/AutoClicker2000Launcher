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

public class PythonChecker {
	public static PythonType getPythonCommand() {
		Runtime runtime = Runtime.getRuntime();
		try {
			if (checkForPython("python --version", runtime) == 0)
				return PythonType.PYTHON_REAL;

		} catch (Exception e) {

		}
		try {

			if (checkForPython("python3 --version", runtime) == 0)
				return PythonType.PYTHON_ALT;

		} catch (Exception e) {

		}
		return PythonType.NOT_INSTALLED;
	}

	private static int checkForPython(String command, Runtime runtime) throws Exception {
		Process proc = runtime.exec(command);
		return proc.waitFor();
	}
}

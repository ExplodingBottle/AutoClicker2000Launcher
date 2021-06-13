package autoclicker2000.launcher;

public class PythonChecker {
	public static PythonType getPythonCommand() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process proc = runtime.exec("python --version");
			int retCode = proc.waitFor();
			if (retCode == 0)
				return PythonType.PYTHON_REAL;

		} catch (Exception e) {

		}
		try {
			Process proc = runtime.exec("python3 --version");
			int retCode = proc.waitFor();

			if (retCode == 0)
				return PythonType.PYTHON_ALT;

		} catch (Exception e) {

		}
		return PythonType.NOT_INSTALLED;
	}
}

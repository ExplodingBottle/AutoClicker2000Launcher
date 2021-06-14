package autoclicker2000.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class WorkerThread extends Thread {
	private LauncherFrame frame;

	private Logger logger;
	private File directoryPath;
	private File releaseIndexPath;
	private File toDownloadZip;
	private File releaseExtracted;

	private String releaseUrl;

	private boolean isAlreadyDownloaded = false;

	public Process process;

	public String leftClick;
	public String rightClick;
	public int cpsAmount;

	public WorkerThread(LauncherFrame frame) {
		logger = Logger.getLogger(getClass().getName());
		this.frame = frame;
		directoryPath = new File(System.getProperty("user.home") + "/autoclicker2000");
		releaseIndexPath = new File(System.getProperty("user.home") + "/autoclicker2000/rel.json");
		toDownloadZip = new File(System.getProperty("user.home") + "/autoclicker2000/rel.zip");
		releaseExtracted = new File(System.getProperty("user.home") + "/autoclicker2000/bin");

		logger.log(Level.INFO, "System.getProperty(\"os.name\") == " + System.getProperty("os.name"));
		logger.log(Level.INFO, "System.getProperty(\"user.home\") == " + System.getProperty("user.home"));
		
		frame.progressIndicator.setString("Creating directories...");

		if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
			logger.log(Level.SEVERE, "Running on unsupported system.");
			frame.showError("Running on unsupported system. Please use Windows.");
			return;
		}

		logger.log(Level.INFO, "Creating directory " + directoryPath.getAbsolutePath() + "...");
		directoryPath.mkdirs();

		logger.log(Level.INFO, "Creating directory " + releaseExtracted.getAbsolutePath() + "...");
		releaseExtracted.mkdirs();

		frame.setLoading(false);

	}

	public void run() {

		frame.setLoading(true);
		frame.progressIndicator.setString("Fetching latest release index...");
		logger.log(Level.INFO, "Fetching latest release index from " + Constants.LATEST_RELEASE_URL + "...");
		try {
			Utils.downloadFile(releaseIndexPath, Constants.LATEST_RELEASE_URL);
		} catch (IOException e) {
			logger.log(Level.WARNING,
					"Failed to download latest release index from" + Constants.LATEST_RELEASE_URL + " !", e);
			if (toDownloadZip.exists()) {
				logger.log(Level.INFO, "Falling back on already downloaded release.");
				isAlreadyDownloaded = true;
			} else {
				logger.log(Level.SEVERE, "No release locally stored, aborting...");
				frame.showError("Failed to download the release index.");
				return;
			}
		}

		if (!isAlreadyDownloaded) {
			frame.progressIndicator.setString("Parsing release...");
			try {
				BufferedReader br = new BufferedReader(new FileReader(releaseIndexPath));
				Gson parser = new Gson();
				ReleaseObject ob = parser.fromJson(br, ReleaseObject.class);
				br.close();
				if (ob.assets.size() != 1) {
					logger.log(Level.SEVERE, "Found more than one candidates.");
					frame.showError("Failed to read release index.");
					return;
				}
				releaseUrl = ob.assets.get(0).browser_download_url;
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to read release index.", e);
				frame.showError("Failed to read release index.");
				return;
			}
			logger.log(Level.INFO, "Will download release from " + releaseUrl + " !");
			frame.progressIndicator.setString("Downloading release...");
			try {
				Utils.downloadFile(toDownloadZip, releaseUrl);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to download release.", e);
				frame.showError("Failed to download release.");
				return;
			}
		}

		logger.log(Level.INFO, "Will extract release to " + releaseExtracted.getAbsolutePath());
		frame.progressIndicator.setString("Extracting...");
		try {
			Utils.extract(releaseExtracted, toDownloadZip);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to extract release.", e);
			frame.showError("Failed to extract release.");
			return;
		}

		logger.log(Level.INFO, "Checking for python command");
		frame.progressIndicator.setString("Starting...");

		PythonType pythonCommand = PythonChecker.getPythonCommand();
		if (pythonCommand != PythonType.NOT_INSTALLED) {
			logger.log(Level.INFO, "Python type found: " + pythonCommand.toString());
		} else {
			logger.log(Level.SEVERE, "No python interpetor.");
			frame.showError("No python interpetor, please install it.");
			return;
		}

		logger.log(Level.INFO, "Will be launched in folder " + releaseExtracted.getAbsolutePath());

		Runtime runner = Runtime.getRuntime();
		String execCommand = null;
		if (pythonCommand == PythonType.PYTHON_ALT) {
			execCommand = new File(releaseExtracted.getAbsolutePath() + "/AutoClicker-Alt.bat").getAbsolutePath();
		}
		if (pythonCommand == PythonType.PYTHON_REAL) {
			execCommand = new File(releaseExtracted.getAbsolutePath() + "/AutoClicker.bat").getAbsolutePath();
		}
		try {
			process = runner.exec(execCommand, null, releaseExtracted);
			process.getOutputStream().write(new String(leftClick + "\n").getBytes());
			process.getOutputStream().write(new String(rightClick + "\n").getBytes());
			process.getOutputStream().write(new String(cpsAmount + "\n").getBytes());
			process.getOutputStream().flush();
			frame.progressIndicator.setString("Started !");
			frame.stop.setEnabled(true);
			process.waitFor();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to start the program.", e);
			frame.showError("Failed to start the program.");
			return;
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "Failed to wait for program to stop", e);
		}
		logger.log(Level.INFO, "Closing window...");
		frame.setVisible(false);
		frame.dispose();

	}
}

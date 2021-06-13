package autoclicker2000.launcher;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class LauncherFrame extends JFrame implements WindowListener, ActionListener {

	private static final long serialVersionUID = 547;

	private Logger logger;

	private JButton start;
	public JProgressBar progressIndicator;

	private WorkerThread worker;

	public LauncherFrame() {
		logger = Logger.getLogger(getClass().getName());

		logger.log(Level.INFO, "Construction requested !");
		setTitle("AutoClicker2000 - Launcher");
		setResizable(false);
		setSize(400, 200);

		progressIndicator = new JProgressBar();
		progressIndicator.setIndeterminate(true);
		progressIndicator.setStringPainted(true);

		start = new JButton("Start !");
		start.addActionListener(this);

		logger.log(Level.INFO, "Adding window listener...");
		addWindowListener(this);
		logger.log(Level.INFO, "Window listener added !");

		add(BorderLayout.NORTH, start);
		add(BorderLayout.SOUTH, progressIndicator);

		setLoading(true);

		setVisible(true);
		logger.log(Level.INFO, "Creating and starting worker thread...");
		worker = new WorkerThread(this);

	}

	public void showError(String error) {
		JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
		setVisible(false);
		dispose();

	}

	public void setLoading(boolean loading) {
		start.setEnabled(!loading);
		progressIndicator.setVisible(loading);
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		logger.log(Level.INFO, "Requesting closing...");
		worker.interrupt();
		dispose();

	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == start) {
			logger.log(Level.INFO, "Requesting start...");
			worker.start();
		}

	}

}

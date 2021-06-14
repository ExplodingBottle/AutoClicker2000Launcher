package autoclicker2000.launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class LauncherFrame extends JFrame implements WindowListener, ActionListener {

	private static final long serialVersionUID = 547;

	private Logger logger;

	private JLabel leftClickText;
	private JLabel rightClickText;
	private JLabel cpsText;

	private JTextField leftClick;
	private JTextField rightClick;
	private JTextField cps;

	private JButton start;
	public JButton stop;
	public JProgressBar progressIndicator;

	private WorkerThread worker;

	public LauncherFrame() {
		logger = Logger.getLogger(getClass().getName());

		logger.log(Level.INFO, "Construction requested !");
		setTitle("AutoClicker2000 - Launcher");
		setResizable(false);
		setSize(400, 200);

		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		progressIndicator = new JProgressBar();
		progressIndicator.setIndeterminate(true);
		progressIndicator.setStringPainted(true);

		leftClickText = new JLabel("Key for left click: ");
		rightClickText = new JLabel("Key for right click: ");
		cpsText = new JLabel("CPS: ");

		leftClick = new JTextField(5);
		rightClick = new JTextField(5);
		cps = new JTextField(5);

		start = new JButton("Start !");
		start.addActionListener(this);
		stop = new JButton("Stop !");
		stop.addActionListener(this);

		stop.setEnabled(false);

		logger.log(Level.INFO, "Adding window listener...");
		addWindowListener(this);
		logger.log(Level.INFO, "Window listener added !");

		add(start);
		add(stop);
		add(progressIndicator);
		add(leftClickText);
		add(rightClickText);
		add(cpsText);
		add(leftClick);
		add(rightClick);
		add(cps);

		layout.putConstraint(SpringLayout.WEST, start, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, start, 5, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, stop, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, stop, 40, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, progressIndicator, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, progressIndicator, 130, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, leftClickText, 80, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, leftClickText, 5, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, rightClickText, 80, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, rightClickText, 40, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, cpsText, 80, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, cpsText, 75, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, leftClick, 300, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, leftClick, 5, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, rightClick, 300, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, rightClick, 40, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, cps, 300, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, cps, 75, SpringLayout.NORTH, this);

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
		stopProgram();
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

	public void stopProgram() {
		logger.log(Level.INFO, "Requesting stop...");
		try {
			if (worker.process != null) {
				worker.process.getOutputStream().write(new String("\n").getBytes());
				worker.process.getOutputStream().flush();
				progressIndicator.setString("Stopping...");
			}
		} catch (IOException e1) {
			logger.log(Level.WARNING, "Failed to send the stop request to the program.", e1);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == start) {
			logger.log(Level.INFO, "Requesting start...");
			leftClick.setEnabled(false);
			rightClick.setEnabled(false);
			cps.setEnabled(false);
			worker.leftClick = leftClick.getText();
			worker.rightClick = rightClick.getText();
			try {
				worker.cpsAmount = Integer.parseInt(cps.getText());
			} catch (NumberFormatException exc) {
				logger.log(Level.WARNING, "Cannot cast cps.", exc);
				JOptionPane.showMessageDialog(this, "You should enter a correct number.", "Error", JOptionPane.WARNING_MESSAGE);
				leftClick.setEnabled(true);
				rightClick.setEnabled(true);
				cps.setEnabled(true);
				return;
			}
			
			worker.start();
		}
		if (e.getSource() == stop) {
			stopProgram();
		}

	}

}

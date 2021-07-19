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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class LauncherFrame extends JFrame implements WindowListener, ActionListener, KeyListener {

	private static final long serialVersionUID = 547;

	private Logger logger;
	private JLabel cpsText;

	private JButton leftClick;
	private JButton rightClick;
	private JTextField cps;

	private boolean choosingKeyLeft;
	private String chooseKeyLeft;

	private boolean choosingKeyRight;
	private String chooseKeyRight;

	private JButton start;
	public JButton stop;
	public JProgressBar progressIndicator;

	private WorkerThread worker;

	public LauncherFrame() {
		logger = Logger.getLogger(getClass().getName());

		logger.log(Level.INFO, "Construction requested !");
		setTitle("AutoClicker2000 - Launcher");
		setResizable(false);
		setSize(479, 200);

		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		progressIndicator = new JProgressBar();
		progressIndicator.setIndeterminate(true);
		progressIndicator.setStringPainted(true);

		cpsText = new JLabel("CPS: ");

		leftClick = new JButton("Left click");
		leftClick.setPreferredSize(new Dimension(140, 30));
		leftClick.addActionListener(this);
		rightClick = new JButton("Right click");
		rightClick.setPreferredSize(new Dimension(140, 30));
		rightClick.addActionListener(this);
		cps = new JTextField(5);

		start = new JButton("Start !");
		start.addActionListener(this);
		stop = new JButton("Stop !");
		stop.addActionListener(this);

		stop.setEnabled(false);

		logger.log(Level.INFO, "Adding window listener...");
		leftClick.addKeyListener(this);
		rightClick.addKeyListener(this);
		addWindowListener(this);
		logger.log(Level.INFO, "Window listener added !");

		add(start);
		add(stop);
		add(progressIndicator);
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

		layout.putConstraint(SpringLayout.WEST, cpsText, 320, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, cpsText, 75, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, leftClick, 320, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, leftClick, 5, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, rightClick, 320, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, rightClick, 40, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, cps, 400, SpringLayout.WEST, this);
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
		stop.setEnabled(false);
		if (worker.process != null) {
			worker.stopProgram();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == leftClick & !choosingKeyRight) {
			choosingKeyLeft = true;
			leftClick.setText("Press any key");
		}
		if (e.getSource() == rightClick & !choosingKeyLeft) {
			choosingKeyRight = true;
			rightClick.setText("Press any key");
		}
		if (e.getSource() == start) {
			logger.log(Level.INFO, "Requesting start...");
			leftClick.setEnabled(false);
			rightClick.setEnabled(false);
			cps.setEnabled(false);
			worker.leftClick = chooseKeyLeft;
			worker.rightClick = chooseKeyRight;
			try {
				worker.cpsAmount = Integer.parseInt(cps.getText());
			} catch (NumberFormatException exc) {
				logger.log(Level.WARNING, "Cannot cast cps.", exc);
				JOptionPane.showMessageDialog(this, "You should enter a correct number for CPS amount.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				leftClick.setEnabled(true);
				rightClick.setEnabled(true);
				cps.setEnabled(true);
				return;
			}
			if (worker.leftClick == null) {
				logger.log(Level.WARNING, "Left click triggering key has not been configured.");
				JOptionPane.showMessageDialog(this, "Left click triggering key has not been configured.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				leftClick.setEnabled(true);
				rightClick.setEnabled(true);
				cps.setEnabled(true);
				return;
			}
			if (worker.rightClick == null) {
				logger.log(Level.WARNING, "Right click triggering key has not been configured.");
				JOptionPane.showMessageDialog(this, "Right click triggering key has not been configured.", "Warning",
						JOptionPane.WARNING_MESSAGE);
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

	@Override
	public void keyTyped(KeyEvent e) {
		if (choosingKeyLeft) {
			chooseKeyLeft = "" + e.getKeyChar();
			choosingKeyLeft = false;
			leftClick.setText(chooseKeyLeft);
		}
		if (choosingKeyRight) {
			chooseKeyRight = "" + e.getKeyChar();
			choosingKeyRight = false;
			rightClick.setText(chooseKeyRight);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}

package de.lenaschimmel.reprails;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

enum Modes {
	ADD_POINTS, ADD_RAIL;
}

public class RepRailsFrame extends JFrame implements ActionListener {

	RailsPanel panel;

	private JButton buttonMode;
	private JButton buttonExport;

	public RepRailsFrame() {
		super("RepRails");
		setSize(800, 600);
		setLayout(new BorderLayout());

		panel = new RailsPanel();
		add(panel, BorderLayout.CENTER);

		JPanel buttonBar = new JPanel();
		buttonBar.setLayout(new FlowLayout());
		buttonMode = new JButton("Add Points");
		buttonExport = new JButton("Export");
		buttonBar.add(buttonMode);
		buttonBar.add(buttonExport);
		buttonMode.addActionListener(this);
		buttonExport.addActionListener(this);
		add(buttonBar, BorderLayout.NORTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void setNetwork(RailNetwork network) {
		panel.setNetwork(network);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonMode) 
			buttonModeClicked();
		if (e.getSource() == buttonExport) 
			buttonExportClicked();
	}

	private void buttonExportClicked() {
		try {
			panel.network.export();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buttonModeClicked() {
		if (panel.mode == Modes.ADD_POINTS) {
			panel.mode = Modes.ADD_RAIL;
			buttonMode.setText("Add Rails");
		} else if (panel.mode == Modes.ADD_RAIL) {
			panel.mode = Modes.ADD_POINTS;
			buttonMode.setText("Add Points");
		}
	}
}

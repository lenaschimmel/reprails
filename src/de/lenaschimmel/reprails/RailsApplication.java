package de.lenaschimmel.reprails;

public class RailsApplication {
	RepRailsFrame frame;
	private RailNetwork network;
	
	public static void main(String[] args) {
		new RailsApplication().start();
	}
	
	private void start() {
		network = new RailNetwork();
		
		//network.addPoint(new RailPoint(100,100));
		//network.addPoint(new RailPoint(100,200));
		
		frame = new RepRailsFrame();
		frame.setNetwork(network);
	}
}

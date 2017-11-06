package de.lenaschimmel.reprails;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class RailsPanel extends JPanel implements MouseListener {
	RailNetwork network;
	
	Modes mode = Modes.ADD_POINTS;
	
	public Modes getMode() {
		return mode;
	}

	public void setMode(Modes mode) {
		this.mode = mode;
		start = null;
		step = 0;
	}

	final int POINT_R = 5;
	
	RailPoint start = null;
	int step = 0;

	private RailSegment seg;
	
	public RailsPanel() {
		addMouseListener(this);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		for(RailPoint p : network.points)
		{
			g.setColor(Color.BLACK);
			g.fillOval((int)p.x - POINT_R, (int) p.y - POINT_R,  POINT_R * 2, POINT_R * 2);
			float dx = (float)Math.sin(p.angle) * 10;
			float dy = (float)Math.cos(p.angle) * 10;
			g.drawLine((int)(p.x - dx), (int)(p.y - dy), (int)(p.x + dx), (int)(p.y + dy));
		}
		for(RailSegment s : network.segments)
		{
			s.draw(g);	
		}
	}

	public void setNetwork(RailNetwork network2) {
		this.network = network2;
		invalidate();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(mode == Modes.ADD_POINTS)
		{
			if(step == 0)
			{
				start = network.getClosestPoint(e.getX(), e.getY(), 20);
				if(start == null)
				{
					start = new RailPoint(e.getX(), e.getY());
					network.addPoint(start);
				}
			}
			else if(step == 1)
			{
				start.angle = (float) Math.atan2(e.getX() - start.x, e.getY() - start.y);
			}
			step = (step + 1) % 2;
		}
		else if(mode == Modes.ADD_RAIL)
		{
			RailPoint p = network.getClosestPoint(e.getX(), e.getY(),10);
			if(step == 0)
			{
				start = p;
			}
			if(step == 1)
			{
				seg = network.getSegment(start, p);
				if(seg == null)
				{
					seg = new RailSegment(start, p, network);
					network.addSegment(seg);
				}
				start = null;
			}
			else if(step == 2)
			{
				seg.bezier.setP(e.getX(), e.getY(), 0);
			}	
			else if(step == 3)
			{
				seg.bezier.setP(e.getX(), e.getY(), 1);
				start = null;
				seg = null;
			}
			step = (step + 1) % 4;
		}
		repaint();
		network.invalidate();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

package de.lenaschimmel.reprails;

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RailSegment {
	Bezier bezier;
	RailNetwork network;

	public List<SimplePoint> outlineRail = new ArrayList<SimplePoint>();
	public List<Triangle> capRail = new ArrayList<Triangle>();

	public RailSegment(RailPoint start, RailPoint end, RailNetwork n) {
		network = n;
		bezier = new Bezier(start, end);

		outlineRail.add(new SimplePoint(1.2f, 0f));
		// outlineRail.add(new SimplePoint( 1.1f, 0.5f));
		// outlineRail.add(new SimplePoint( 0.3f, 0.7f));
		// outlineRail.add(new SimplePoint( 0.3f, 2f));
		// outlineRail.add(new SimplePoint( 0.6f, 2.5f));
		outlineRail.add(new SimplePoint(0.7f, 4f));
		outlineRail.add(new SimplePoint(-0.7f, 4f));
		// outlineRail.add(new SimplePoint(-0.6f, 2.5f));
		// outlineRail.add(new SimplePoint(-0.3f, 2f));
		// outlineRail.add(new SimplePoint(-0.3f, 0.7f));
		// outlineRail.add(new SimplePoint(-1.1f, 0.5f));
		outlineRail.add(new SimplePoint(-1.2f, 0f));

		capRail.add(new Triangle(0, 1, 2));
		capRail.add(new Triangle(0, 2, 3));

		/*
		 * capRail.add(new Triangle( 0, 1, 11)); capRail.add(new Triangle( 1, 2,
		 * 11)); capRail.add(new Triangle( 2, 9, 11)); capRail.add(new Triangle(
		 * 9, 10, 11)); capRail.add(new Triangle( 2, 3, 9)); capRail.add(new
		 * Triangle( 3, 8, 9)); capRail.add(new Triangle( 3, 4, 8));
		 * capRail.add(new Triangle( 4, 7, 8)); capRail.add(new Triangle( 4, 5,
		 * 6)); capRail.add(new Triangle( 4, 6, 7));
		 */
	}

	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.drawLine((int) bezier.o[0].x, (int) bezier.o[0].y,
				(int) bezier.o[1].x, (int) bezier.o[1].y);

		g.setColor(Color.GREEN);
		g.drawLine((int) bezier.o[0].x, (int) bezier.o[0].y,
				(int) bezier.p[0].x, (int) bezier.p[0].y);
		g.drawLine((int) bezier.p[0].x, (int) bezier.p[0].y,
				(int) bezier.p[1].x, (int) bezier.p[1].y);
		g.drawLine((int) bezier.p[1].x, (int) bezier.p[1].y,
				(int) bezier.o[1].x, (int) bezier.o[1].y);

		g.setColor(Color.BLACK);
		SimplePoint sp = new SimplePoint(bezier.o[0].x, bezier.o[0].y);
		SimplePoint lp = new SimplePoint(bezier.o[0].x, bezier.o[0].y);
		SimplePoint sd = new SimplePoint(0, 0);
		SimplePoint ld = new SimplePoint(0, 0);

		int max = 20;
		for (int i = 0; i <= max; i++) {
			float t = (float) i * (1f / max);

			bezier.getPforT(t, sp);
			bezier.getDforT(t, sd);
			float w = 10;
			g.drawLine((int) (sp.x + sd.y * w), (int) (sp.y - sd.x * w),
					(int) (lp.x + ld.y * w), (int) (lp.y - ld.x * w));
			g.drawLine((int) (sp.x - sd.y * w), (int) (sp.y + sd.x * w),
					(int) (lp.x - ld.y * w), (int) (lp.y + ld.x * w));
			lp.x = sp.x;
			lp.y = sp.y;
			ld.x = sd.x;
			ld.y = sd.y;
		}
	}

	public void exportPositive(FileWriter fw) throws IOException {
		fw.write("union () {\n");

		// Schienen
		exportProfile(fw, outlineRail, -9f, 0, 0, 1, 40);
		exportProfile(fw, outlineRail, 9f, 0, 0, 1, 40);

		SimplePoint p1 = new SimplePoint(0, 0);
		SimplePoint p2 = new SimplePoint(0, 0);

		// Radlenker
		float start = -1;
		for (float i = 0; i < bezier.length; i += 2) {
			boolean near = false;
			bezier.getPforT(bezier.computeTfromS(i + 1), p1);
			for (RailSegment r : network.segments) {
				if (this == r)
					continue;
				for (float n = 0; n < r.bezier.length; n += 2) {
					r.bezier.getPforT(r.bezier.computeTfromS(n + 1), p2);
					if (RailNetwork.distance(p1, p2) < 18) {
						near = true;

						if (start == -1) {
							System.out.println("Start at " + i);
							start = i;
							break;
						}
					}
				}
			}
			if (start != -1 && near == false) {
				System.out.println("End at " + i);
				float t1 = bezier.computeTfromS(start);
				float t2 = bezier.computeTfromS(i);
				exportProfile(fw, outlineRail, -5.95f, 0, t1, t2, 8);
				exportProfile(fw, outlineRail, 5.95f, 0, t1, t2, 8);
				exportProfile(fw, -9, 9, 0, 0.8f, t1, t2, 20);
				start = -1;
			}
		}

		// exportProfile(fw, outlineRail, -5.3f, 3, 0, 1, steps);
		// exportProfile(fw, outlineRail, 5.3f, 3, 0, 1, steps);

		// Schwellen
		for (float i = 0; i < bezier.length - 2; i += 8.3f) {
			/*exportProfile(fw, -12, 12, 0f, 0.75f,
					bezier.computeTfromS(i + 0.25f),
					bezier.computeTfromS(i + 2.75f), 2);*/
			exportProfile(fw, -12, 12, 0f, 1.25f,
					bezier.computeTfromS(i + 0.5f),
					bezier.computeTfromS(i + 2.5f), 2);
			// exportProfile(fw, -9.5f, -6.5f, 3f, 3.7f,
			// bezier.computeTfromS(i + 1), bezier.computeTfromS(i + 2), 2);
			// exportProfile(fw, 6.5f, 9.5f, 3f, 3.7f,
			// bezier.computeTfromS(i + 1), bezier.computeTfromS(i + 2), 2);
		}

		// Bett
		// exportProfile(fw, -15, 15, 1, 2.0f, 0, 1, 20);
		
		// Connector
		printConnector(fw, 0 ,false); 
		printConnector(fw, 1 ,false); 
		
		fw.write("}\n");
	}
	
	private void printConnector(FileWriter fw, int index, boolean neg) throws IOException
	{
		if(network.getSegmentCountForPoint(bezier.o[index]) > 1)
			return;
		SimplePoint p1 = new SimplePoint(0, 0);
		bezier.getPforT(index, p1);
		float w = (float) (bezier.o[index].angle*180/Math.PI);
		if(bezier.r[index] < 0)
			w = w + 180;
		fw.write("translate (["+ (-p1.x)+","+p1.y+",0]) rotate(a="+w+",v=[0,0,1]) connect"+(neg?"Neg":"")+"();\n");
	}

	public void exportNegative(FileWriter fw) throws IOException {
		fw.write("union () {\n");
		exportProfile(fw, -8.15f, -6.45f, 1.2f, 7, -0.01f, 1.01f, 20);
		exportProfile(fw, 6.45f, 8.15f, 1.2f, 7, -0.01f, 1.01f, 20);
		// Connector
		printConnector(fw, 0 ,true); 
		printConnector(fw, 1 ,true); 
		fw.write(" }\n");
	}

	public void exportProfile(FileWriter fw, float left, float right,
			float bottom, float top, float start, float end, int steps)
			throws IOException {
		fw.write("    polyhedron\n");
		fw.write("        ( points =    [\n");

		SimplePoint sp = new SimplePoint(0, 0);
		SimplePoint sd = new SimplePoint(0, 0);

		for (int i = 0; i <= steps; i++) {
			float t = (float) i * ((end - start) / steps) + start;

			bezier.getPforT(t, sp);
			bezier.getDforT(t, sd);

			fw.write("                         ");
			outputPoint(fw, sp, sd, left, bottom, true);
			outputPoint(fw, sp, sd, right, bottom, true);
			outputPoint(fw, sp, sd, right, top, true);
			outputPoint(fw, sp, sd, left, top, i != steps);
			fw.write("\n");
		}

		fw.write("                      ],\n");
		fw.write("          triangles = [\n");
		fw.write("                          ");
		outputTriangle(fw, 2, 1, 0, true);
		outputTriangle(fw, 0, 3, 2, true);
		for (int i = 0; i < steps; i++) {
			int s = i * 4;
			int t = i * 4 + 4;
			for (int n = 0; n < 4; n++) {
				outputTriangle(fw, s + (0 + n) % 4, s + (1 + n) % 4, t
						+ (0 + n) % 4, true);
				outputTriangle(fw, s + (1 + n) % 4, t + (1 + n) % 4, t
						+ (0 + n) % 4, true);
			}

		}
		outputTriangle(fw, steps * 4 + 0, steps * 4 + 1, steps * 4 + 2, true);
		outputTriangle(fw, steps * 4 + 2, steps * 4 + 3, steps * 4 + 0, false);
		fw.write("                      ]\n");
		fw.write("        );\n");
	}

	public void exportProfile(FileWriter fw, List<SimplePoint> outline,
			float x, float y, float start, float end, int steps)
			throws IOException {
		fw.write("    polyhedron\n");
		fw.write("        ( points =    [\n");

		SimplePoint sp = new SimplePoint(0, 0);
		SimplePoint sd = new SimplePoint(0, 0);

		int count = outline.size();

		// Output hull
		for (int i = 0; i <= steps; i++) {
			float t = (float) i * ((end - start) / steps) + start;

			bezier.getPforT(t, sp);
			bezier.getDforT(t, sd);

			fw.write("                         ");
			for (SimplePoint o : outline)
				outputPoint(fw, sp, sd, o.x + x, o.y + y, true);

			fw.write("\n");
		}

		// this is just a garbage point, so that the list does not end with a
		// comma
		fw.write("[0,0,0]");

		fw.write("                      ],\n");
		fw.write("          triangles = [\n");
		fw.write("                          ");

		// Output hull segments
		for (int i = 0; i < steps; i++) {
			int s = i * count;
			int t = i * count + count;
			for (int n = 0; n < count; n++) {
				outputTriangle(fw, s + (0 + n) % count, s + (1 + n) % count, t
						+ (0 + n) % count, true);
				outputTriangle(fw, s + (1 + n) % count, t + (1 + n) % count, t
						+ (0 + n) % count, true);
			}

		}

		// Output Caps
		for (int i = 0; i < capRail.size(); i++) {
			Triangle t = capRail.get(i);
			outputTriangle(fw, t.c, t.b, t.a, true);
		}
		for (int i = 0; i < capRail.size(); i++) {
			Triangle t = capRail.get(i);
			outputTriangle(fw, steps * count + t.a, steps * count + t.b, steps
					* count + t.c, i < capRail.size() - 1);
		}

		fw.write("                      ]\n");
		fw.write("        );\n");
	}

	private void outputTriangle(FileWriter fw, int i, int j, int k,
			boolean comma) throws IOException {
		fw.write("[" + i + "," + j + "," + k + "]");
		if (comma)
			fw.write(',');
	}

	/**
	 * 
	 * @param fw
	 *            where to write the output to
	 * @param sp
	 *            x,y position
	 * @param sd
	 *            direction in which to translate (unit vector)
	 * @param f
	 *            how far to extend into the directon sd
	 * @param z
	 *            z coordinate to use
	 * @param comma
	 *            whether or not to add a comma
	 * @throws IOException
	 */
	private void outputPoint(FileWriter fw, SimplePoint sp, SimplePoint sd,
			float f, float z, boolean comma) throws IOException {
		float x = -(sp.x + sd.y * f);
		float y = sp.y - sd.x * f;
		String k = comma ? "," : "";
		fw.write("[" + x + "," + y + "," + z + "]" + k);
	}
}

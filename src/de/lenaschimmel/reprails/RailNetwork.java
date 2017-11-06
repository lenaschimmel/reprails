package de.lenaschimmel.reprails;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class RailNetwork {
	Collection<RailPoint> points;
	Collection<RailSegment> segments;

	public RailNetwork() {
		points = new HashSet<RailPoint>();
		segments = new HashSet<RailSegment>();
	}

	public void addPoint(RailPoint railPoint) {
		points.add(railPoint);
	}

	public RailPoint getClosestPoint(float x, float y, float maxDist) {
		RailPoint ret = null;
		float minDist = 100000;
		for (RailPoint p : points) {
			float dist = distance(x, y, p.x, p.y);
			if (dist < maxDist && dist < minDist) {
				minDist = dist;
				ret = p;
			}
		}
		return ret;
	}

	public static float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	public static float distance(SimplePoint p1, SimplePoint p2) {
		return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y));
	}

	public void addSegment(RailSegment railSegment) {
		segments.add(railSegment);
	}

	public RailSegment getSegment(RailPoint p0, RailPoint p1) {
		for (RailSegment s : segments) {
			if (s.bezier.o[0] == p0 && s.bezier.o[1] == p1)
				return s;
			if (s.bezier.o[0] == p1 && s.bezier.o[1] == p0)
				return s;
		}
		return null;
	}

	public void invalidate() {
		for (RailSegment s : segments)
			s.bezier.invalidate();
	}

	public void export() throws IOException {
		FileWriter fw = new FileWriter("/home/lena/thingys/rail.scad");

		fw.write("use <connector.scad>;\n");
		/*fw.write("{\n");
		fw.write("linear_extrude(height = 1.75,convexity = 10, twist = 0) \n");
		fw.write("polygon(points=[[0,0],[-0.7,-2],[3.7,-2],[3,0],[6,0],[6,3],[2,18],[-2,18],[-6,3],[-6,0],[-3,0],[-4,2],[1,2]]);\n");
		fw.write("}\n");
		
		fw.write("module connectNeg()\n");
		fw.write("{\n");
		fw.write("translate([0,0,-0.5]) linear_extrude(height = 3,convexity = 10, twist = 0)\n");
		fw.write("polygon(points=[[-3.6,0],[-4.6,2],[0.4,2],[0,0]]);\n");
		fw.write("}");
*/
		fw.write("difference () {\n");
		fw.write("  union () {\n");
		for (RailSegment s : segments) {
			s.exportPositive(fw);

		}
		fw.write("  }\n");
		fw.write("  union () {\n");
		for (RailSegment s : segments) {
			s.exportNegative(fw);

		}
		fw.write("  }\n");
		fw.write("}\n");
		fw.close();
	}

	public int getSegmentCountForPoint(RailPoint railPoint) {
		int count = 0;
		for(RailSegment s : segments)
			if(s.bezier.o[0] == railPoint || s.bezier.o[1] == railPoint)
				count ++;
		return count;
	}
}

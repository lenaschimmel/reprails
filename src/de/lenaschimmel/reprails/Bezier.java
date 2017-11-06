package de.lenaschimmel.reprails;

public class Bezier {
	SimplePoint[] p = new SimplePoint[2];
	RailPoint[] o = new RailPoint[2];
	float r[] = new float[2];
	float length;
	
	public Bezier(RailPoint start, RailPoint end) {
		o[0] = start;
		o[1] = end;
		p[0] = new SimplePoint(start.x, start.y);
		p[1] = new SimplePoint(end.x, end.y);
		invalidate();
	}
	
	void computeP(int i)
	{
		p[i].x = o[i].x + (float)Math.sin(o[i].angle) * r[i];
		p[i].y = o[i].y + (float)Math.cos(o[i].angle) * r[i];
	}
	
	public void invalidate() {
		computeP(0);
		computeP(1);
		
		length = getLengthTo(1);
	}
	
	private float getLengthTo(float to) {
		SimplePoint p1 = new SimplePoint(0, 0);
		SimplePoint p2 = new SimplePoint(0, 0);
		
		float ret = 0;
		for(float t1 = 0; t1 < to; t1 += 0.01)
		{
			float t2 = (float)Math.min(t1 + 0.01, to);
			getPforT(t1, p1);
			getPforT(t2, p2);
		//	System.out.println("Dist from " + t1 + " to " + t2 + ": " + RailNetwork.distance(p1, p2));
			ret += RailNetwork.distance(p1, p2);
		}
		//System.out.println("Länge: " + ret);
		return ret;
	}

	void getPforT(float t, SimplePoint r)
	{
		r.x = (1-t)*(1-t)*(1-t)*o[0].x + 3*t*(1-t)*(1-t)*p[0].x + 3*t*t*(1-t)*p[1].x + t*t*t*o[1].x;
		r.y = (1-t)*(1-t)*(1-t)*o[0].y + 3*t*(1-t)*(1-t)*p[0].y + 3*t*t*(1-t)*p[1].y + t*t*t*o[1].y;
	}
	
	SimplePoint t1 = new SimplePoint(0, 0);
	SimplePoint t2 = new SimplePoint(0, 0);
	
	void getDforT(float t, SimplePoint r)
	{
		getPforT(t-0.01f, t1);
		getPforT(t+0.01f, t2);
		r.x = t1.x - t2.x;
		r.y = t1.y - t2.y;
		float m = (float)Math.sqrt(r.x * r.x + r.y * r.y);
		r.x /= m;
		r.y /= m;
	}
	
	/*
	 * Sets one of the two stützpunkte, but not absolutely. It uses the directon of the RailPoint.
	 */
	void setP(float x, float y, int i)
	{
		float ax = (float)Math.sin(o[i].angle);
		float ay = (float)Math.cos(o[i].angle);
		r[i] = (ax * (x - o[i].x) + ay * (y - o[i].y));
		computeP(i);
	}
	
	float computeTfromS(float s)
	{
		//System.out.println("Suche s " + s);
		float t = s / length;
		float l = getLengthTo(t);
		float step = 0.5f;
		while(Math.abs(l-s) > 0.1 && step >= 0.0001)
		{
			//System.out.println("Aktuelles t: " + t + " führt zu s " + l);
			float dir = Math.signum(l-s);
			t -= dir * step;
			//System.out.println("Step: " + step + " dir " + dir);
			l = getLengthTo(t);
			step *= 0.7f;
		}
		return t;
	}
}

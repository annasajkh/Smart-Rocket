package com.github.annasajkh.shapes;

import com.badlogic.gdx.math.Vector2;

public class Line
{
	Vector2 a;
	Vector2 b;

	public Line(Vector2 a, Vector2 b)
	{
		this.a = a;
		this.b = b;
	}

	public boolean intersects(Line other)
	{
		return intersects(a, b, other.a, other.b);
	}

	private static boolean intersects(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2)
	{
		Vector2 b = a2.cpy().sub(a1);
		Vector2 d = b2.cpy().sub(b1);
		float bDotDPerp = b.x * d.y - b.y * d.x;

		// if b dot d == 0, it means the lines are parallel so have infinite
		// intersection points
		if(bDotDPerp == 0)
		{
			return false;
		}

		Vector2 c = b1.cpy().sub(a1);
		float t = (c.x * d.y - c.y * d.x) / bDotDPerp;
		if(t < 0 || t > 1)
		{
			return false;
		}

		float u = (c.x * b.y - c.y * b.x) / bDotDPerp;

		if(u < 0 || u > 1)
		{
			return false;
		}

		return true;
	}

}

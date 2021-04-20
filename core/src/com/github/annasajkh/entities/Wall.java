package com.github.annasajkh.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.github.annasajkh.shapes.Line;
import com.github.annasajkh.shapes.Rect;

public class Wall extends Rect
{

	public Wall(Vector2 position, float width, float height)
	{
		super(position, width, height, Color.GRAY);

	}
	
	public boolean intersectLine(Line otherLine)
	{
		for(Line line : lines)
		{

			if(line.intersects(otherLine))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public void update()
	{

	}

}

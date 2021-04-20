package com.github.annasajkh.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.github.annasajkh.shapes.Rect;

public class Target extends Rect
{

	public Target(Vector2 position, float width, float height)
	{
		super(position, width, height, Color.GREEN);
	}

	@Override
	public void update()
	{

	}

}

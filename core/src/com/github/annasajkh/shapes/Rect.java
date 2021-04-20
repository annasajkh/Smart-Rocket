package com.github.annasajkh.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.annasajkh.GameObject;

public class Rect extends GameObject
{
	private float width;
	private float height;
	public Line[] lines;

	private float leftSide, rightSide, topSide, bottomSide;

	public Rect(Vector2 position, float width, float height, Color color)
	{
		super(position);

		this.width = width;
		this.height = height;

		lines = new Line[] {
				new Line(position.cpy().sub(width / 2, height / 2), position.cpy().add(width / 2, -height / 2)),
				new Line(position.cpy().sub(width / 2, height / 2), position.cpy().add(-width / 2, height / 2)),
				new Line(position.cpy().add(width / 2, height / 2), position.cpy().add(width / 2, -height / 2)),
				new Line(position.cpy().add(width / 2, height / 2), position.cpy().add(-width / 2, height / 2)),

		};
		this.color = color;

	}

	public void updateBounds()
	{
		rightSide = position.x + width * 0.5f;
		leftSide = position.x - width * 0.5f;
		topSide = position.y + height * 0.5f;
		bottomSide = position.y - height * 0.5f;
	}

	public boolean intersectPoint(float x, float y)
	{
		updateBounds();
		return x >= position.x - width * 0.5f && x <= position.x + width * 0.5f && y >= position.y - height * 0.5f
				&& y <= position.y + height * 0.5f;
	}

	@Override
	public void draw(ShapeRenderer shapeRenderer)
	{
		shapeRenderer.setColor(color);
		shapeRenderer.rect(position.x - width * 0.5f, position.y - height * 0.5f, width, height);
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}

}

package com.github.annasajkh.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.annasajkh.Game;
import com.github.annasajkh.GameObject;
import com.github.annasajkh.neuralnetwork.NeuralNetwork;
import com.github.annasajkh.shapes.Line;
import com.github.annasajkh.shapes.Rect;

public class Rocket extends GameObject
{

	public NeuralNetwork brain;

	private Vector2 velocity = new Vector2();

	private float rotation;
	public static float size = 10;
	
	private static float maxSpeed = 15;
	public float score;

	private Vector2[] vertices;

	private Line[] lines;
	
	public float timer = 0;
	

	public Rocket(Vector2 position)
	{
		super(position);
		vertices = new Vector2[3];
		brain = new NeuralNetwork(5, 6, 2, 2);
		color = Color.BLUE;
		
		rotation = velocity.angleDeg();

		vertices[0] = position.cpy().add(new Vector2(1, 0).rotateDeg(rotation).scl(size));
		vertices[1] = position.cpy().add(new Vector2(1, 0).rotateDeg(135 + rotation).scl(size));
		vertices[2] = position.cpy().add(new Vector2(1, 0).rotateDeg(225 + rotation).scl(size));

	}
	
	public Rocket(Vector2 position,NeuralNetwork brain)
	{
		super(position);
		vertices = new Vector2[3];
		this.brain = brain;
		color = Color.BLUE;
		
		rotation = velocity.angleDeg();

		vertices[0] = position.cpy().add(new Vector2(1, 0).rotateDeg(rotation).scl(size));
		vertices[1] = position.cpy().add(new Vector2(1, 0).rotateDeg(135 + rotation).scl(size));
		vertices[2] = position.cpy().add(new Vector2(1, 0).rotateDeg(225 + rotation).scl(size));

	}

	public boolean intersects(Rect rect)
	{
		for(Line line : lines)
		{

			for(Line rectLine : rect.lines)
			{
				if(line.intersects(rectLine))
				{
					return true;
				}
			}
		}

		byte vertexIntersectCount = 0;

		for(Vector2 vertex : vertices)
		{
			if(rect.intersectPoint(vertex.x, vertex.y))
			{
				vertexIntersectCount++;

				if(vertexIntersectCount == 3)
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void update()
	{
		rotation = velocity.angleDeg();

		vertices[0] = position.cpy().add(new Vector2(1, 0).rotateDeg(rotation).scl(size));
		vertices[1] = position.cpy().add(new Vector2(1, 0).rotateDeg(135 + rotation).scl(size));
		vertices[2] = position.cpy().add(new Vector2(1, 0).rotateDeg(225 + rotation).scl(size));

		lines = new Line[] { new Line(vertices[0], vertices[1]), new Line(vertices[1], vertices[2]),
				new Line(vertices[2], vertices[1]) };
		
		Wall closest;
		
		if(!Game.walls.isEmpty())
		{
			closest = Game.walls.get(0);
			
			for(Wall wall : Game.walls)
			{
				if(position.dst2(wall.position) < position.dst2(closest.position))
				{
					closest = wall;
				}
			}
		}
		else
		{
			closest = new Wall(new Vector2(0,0),0,0);
		}
		
		double[] output = brain.process(new double[] {	closest.position.x,
														closest.position.y,
														position.x,
														position.y,
														position.dst(Game.target.position)
		
		});
		
		velocity.x += (output[0] * 2 - 1) * maxSpeed;
		velocity.y += (output[1] * 2 - 1) * maxSpeed;
		
		position.x += velocity.x * Gdx.graphics.getDeltaTime(); 
		position.y += velocity.y * Gdx.graphics.getDeltaTime();
		
		timer += Gdx.graphics.getDeltaTime();
	}

	@Override
	public void draw(ShapeRenderer shapeRenderer)
	{
		shapeRenderer.setColor(color);
		shapeRenderer.triangle(vertices[0].x, vertices[0].y, vertices[1].x, vertices[1].y, vertices[2].x,
				vertices[2].y);
	}

}

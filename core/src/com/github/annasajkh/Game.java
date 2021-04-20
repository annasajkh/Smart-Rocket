package com.github.annasajkh;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.github.annasajkh.entities.Rocket;
import com.github.annasajkh.entities.Target;
import com.github.annasajkh.entities.Wall;
import com.github.annasajkh.neuralnetwork.NeuralNetwork;
import com.github.annasajkh.shapes.Line;
import com.github.annasajkh.shapes.Rect;

public class Game extends ApplicationAdapter
{
	public static ShapeRenderer shapeRenderer;
	public static OrthographicCamera camera;
	
	public static Target target;
	public static Vector2 mousePos;
	public static boolean paused = false;
	public static float clickTimer = 0;

	public static List<Wall> walls;
	public static Array<Rocket> deletedRockets;
	public static Rect boundry;
	public static int populationSize = 500;
	public static float lifeTime = 8;
	public static Array<Rocket> rockets;

	@Override
	public void create()
	{
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		camera.position.x = Gdx.graphics.getWidth() / 2;
		camera.position.y = Gdx.graphics.getHeight() / 2;
		camera.update();

		mousePos = new Vector2();
		target = new Target(new Vector2(250, 250), 10, 10);

		walls = new ArrayList<>();
		rockets = new Array<>(populationSize);
		deletedRockets = new Array<>(populationSize);
		
		for(int i = 0; i < populationSize; i++)
		{
			rockets.add(new Rocket(new Vector2(Gdx.graphics.getWidth()/2,Rocket.size)));
		}
		boundry = new Rect(new Vector2(	Gdx.graphics.getWidth() / 2,Gdx.graphics.getHeight() / 2),
										Gdx.graphics.getWidth() - Rocket.size * 2,Gdx.graphics.getHeight() - Rocket.size * 2,Color.WHITE);
	}

	public void getInput()
	{

		if(Gdx.input.isButtonPressed(0)
				&& (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)))
		{
			target.position = Game.mousePos.cpy();
		}

		if(Gdx.input.isKeyJustPressed(Keys.P))
		{
			paused = !paused;
		}
		
		
		if(clickTimer >= 0.0001f)
		{
			if(Gdx.input.isButtonPressed(0) &&
					!(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)))
			{
				walls.add(new Wall(mousePos.cpy(), 20, 20));
			} 
			else if(Gdx.input.isButtonPressed(1) &&
					!(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)))
			{
				for(int i = walls.size() - 1; i >= 0; i--)
				{
					Wall wall = walls.get(i);
					if(wall.intersectPoint(mousePos.x, mousePos.y))
					{
						walls.remove(wall);
						break;
					}
				}
			}
			
			
			clickTimer = 0;
		}

		clickTimer += Gdx.graphics.getDeltaTime();
	}

	public void update()
	{
		Vector3 mousePos3D = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		
		mousePos.x = mousePos3D.x;
		mousePos.y = mousePos3D.y;
		
		getInput();
		target.update();
		
		if(!paused)
		{
			for(Rocket rocket : rockets)
			{
				rocket.update();
			}
			
			for(int i = rockets.size - 1; i >= 0; i--)
			{
				Rocket rocket = rockets.get(i);
				
				if(!rocket.intersects(boundry) || rocket.timer >= lifeTime)
				{
					deletedRockets.add(rockets.removeIndex(i));

					rocket.score = rocket.position.dst(Game.target.position);
				}
				
				for(int j = 0; j < walls.size(); j++)
				{
					if(rocket.intersects(walls.get(j)))
					{
						if(rockets.contains(rocket,true))
						{
							rocket.score = rocket.position.dst(Game.target.position);
							deletedRockets.add(rockets.removeIndex(i));
							break;
						}
					}
				}
			}
			
		}
		if(rockets.isEmpty())
		{
			Rocket[] bestOfN = new Rocket[5];
			
			for(int i = deletedRockets.size - 1; i >= 0; i--)
			{

				Rocket rocket = deletedRockets.get(i);
				
				Line fromRocketToTarget = new Line(rocket.position,target.position);
				
				for(Wall wall : walls)
				{
					if(wall.intersectLine(fromRocketToTarget))
					{
						rocket.score *= rocket.score;
						break;
					}
				}
			}
			
			for(int i = bestOfN.length - 1; i >= 0; i--)
			{
				Rocket best = deletedRockets.get(0);
				
				for(int j = deletedRockets.size - 1; j >= 0; j--)
				{
					Rocket rocket = deletedRockets.get(j);
					
					if(rocket.score < best.score)
					{
						best = rocket;
					}
				}
				bestOfN[i] = deletedRockets.removeIndex(deletedRockets.indexOf(best,false));
			}
			
			rockets = new Array<>(populationSize);
			deletedRockets = new Array<>(populationSize);
			
			for(int j = 0; j < populationSize / 2; j++)
			{
				rockets.add(new Rocket(new Vector2(Gdx.graphics.getWidth()/2,Rocket.size)));
			}

			for(int i = 0; i < bestOfN.length; i++)
			{
				NeuralNetwork bestBrain = bestOfN[i].brain;
				
				for(int j = 0; j < populationSize / 2 / bestOfN.length; j++)
				{
					if(j == 0)
					{
						rockets.add(new Rocket(new Vector2(Gdx.graphics.getWidth()/2,Rocket.size),bestBrain.clone()));
					}
					else
					{
						
						rockets.add(new Rocket(new Vector2(Gdx.graphics.getWidth()/2,Rocket.size),bestBrain.clone().mutate(0.3f)));
					}
				}
			}
		}
		
	}

	@Override
	public void render()
	{
		update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);

		target.draw(shapeRenderer);
		
		for(Rocket rocket : rockets)
		{
			rocket.draw(shapeRenderer);
		}
		
		for(Rocket rocket : deletedRockets)
		{
			rocket.color = Color.RED;
			rocket.draw(shapeRenderer);
		}


		for(Wall wall : walls)
		{
			wall.draw(shapeRenderer);
		}

		shapeRenderer.end();
	}

	@Override
	public void dispose()
	{
		shapeRenderer.dispose();
	}
}

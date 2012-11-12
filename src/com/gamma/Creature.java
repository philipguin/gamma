package com.gamma;

import java.util.ArrayList;
import java.util.List;

import spawners.IMater;
import fitness.IFitnessed;

public class Creature extends Entity implements IFitnessed
{	
	private final float stepHeight = 1f;
	private ArrayList<Integer> currentPath = new ArrayList<Integer>();
	
	public Creature(int textureIndex)
	{
		super(textureIndex);
	}

	private float fitness = .1f;

	@Override
	public float getFitness()
	{
		return fitness;
	}
	
	@Override
	public void onUpdate()
	{
		performMovement();
	}
	
	private boolean performMovement()
	{
		if (currentPath.isEmpty())
		{
			int goalX = Math.max(0, Math.min(environment.getWidth()  - 1, getX() - 30 + random.nextInt(60)));
			int goalY = Math.max(0, Math.min(environment.getHeight() - 1, getY() - 30 + random.nextInt(60)));
			
			boolean foundPath = AStarEnvironmentSearch.performAStar(environment, 1.0001f, stepHeight, getX(), getY(), goalX, goalY, currentPath);
			
			if (!foundPath || currentPath.isEmpty())
				return false;
			
			currentPath.remove(0); // We're already in the first element

			if (currentPath.isEmpty())
				return false;
		}
		
		int nextIndex = currentPath.get(0);
		int newX = nextIndex & 0xffff;
		int newY = (nextIndex >>> 16) & 0xffff;

		Entity collidingEntity = environment.getEntity(newX, newY);
		
		if (collidingEntity != null)
		{
			onCollision(collidingEntity, newX, newY);
			currentPath.clear();
			return false;
		}

		currentPath.remove(0);
		setPosition(newX, newY);
		return true;
	}
	
	private void onCollision(Entity collidingEntity, int cx, int cy)
	{
	}
	
	public static final class Mater implements IMater<Creature>
	{
		@Override
		public Creature mate(List<? extends Creature> parents)
		{
			//TODO
			return new Creature(parents.get(0).getTextureIndex());
		}
	}
}

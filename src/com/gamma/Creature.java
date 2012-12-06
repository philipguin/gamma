package com.gamma;

import java.util.ArrayList;
import java.util.List;

import spawners.IMater;
import util.IMapping;
import util.TransparentList;
import fitness.IFitnessed;

public class Creature extends Entity implements IFitnessed
{	
	public static final int GENOME_LENGTH = 8, MOVES_PER_REPATHING = 7;
	public static final float MAX_ENERGY = 30f, ENERGY_LOST_PER_TICK = .25f, MIN_COLOR = 20;
	
	private final float[] genes;
	private final int ticksPerMove;
	private final boolean dealsDamage;
	private final float energyPerMove, damagePerHit, energyPerHit, stepHeight, energyCapacity, red, green, blue;

	private float fitness = .1f;
	private int killCount = 0, deathCount = 0;
	private float energy;

	private ArrayList<Integer> currentPath = new ArrayList<Integer>();
	private Entity target = null;
	private int oldTargetX, oldTargetY;
	
	public Creature(float[] genes)
	{
		super(119);
		this.genes = genes;
		
		this.ticksPerMove = 1 + (int)(genes[0] * 9f);
		this.energyPerMove = 1.1f - genes[0];
		this.energyCapacity = (genes[4] * MAX_ENERGY);
				
		this.damagePerHit = genes[1] * 5f;
		this.energyPerHit = genes[1] * genes[1] * 5f;
		this.dealsDamage = genes[2] >= .5f;
		
		this.stepHeight = 1f + genes[3];
		
		this.red = MIN_COLOR + 0.8f * genes[5];
		this.blue = MIN_COLOR + 0.8f * genes[6];
		this.green = MIN_COLOR + 0.8f * genes[7];
	}

	@Override public float getFitness() { return fitness; }
	@Override public float getColorRed() { return 1f; }
	@Override public float getColorGreen() { return energy / energyCapacity; }
	@Override public float getColorBlue() { return energy / energyCapacity; }
	
	public float[] getStats() {
		float[] stats = new float[11];
		//Put appropriate values in stats
		return stats;
	}
	
	public void onBirth()
	{
		isDead = false;
		energy = energyCapacity;
		currentPath.clear();
	}
	
	@Override
	public void onUpdate(long simulationTime)
	{
		performMovement(simulationTime);
		
		energy -= ENERGY_LOST_PER_TICK;
		
		if (energy <= 0f)
		{
			energy = 0f;
			setDead();
		}
	}
	
	private boolean performMovement(long simulationTime)
	{
		if (simulationTime % ticksPerMove != 0 || energy < energyPerMove)
			return false;
		
		if (target != null && simulationTime / ticksPerMove % MOVES_PER_REPATHING == 0 && target.getX() != oldTargetX && target.getY() != oldTargetY)
		{
			oldTargetX = target.getX();
			oldTargetY = target.getY();
			
			if (!repath(oldTargetX, oldTargetY))
				return false;
		}
		else if (currentPath.isEmpty())
		{
			if (!repath(Math.max(0, Math.min(environment.getWidth()  - 1, getX() - 30 + random.nextInt(60))),
						Math.max(0, Math.min(environment.getHeight() - 1, getY() - 30 + random.nextInt(60)))))
				return false;
		}
		
		int nextIndex = currentPath.get(0);
		int newX = nextIndex & 0xffff;
		int newY = (nextIndex >>> 16) & 0xffff;

		Entity collidingEntity = environment.getEntity(newX, newY);
		
		if (collidingEntity != null)
		{
			this.onCollision(collidingEntity);
			collidingEntity.onCollision(this);
			currentPath.clear();
			return false;
		}

		currentPath.remove(0);
		energy -= energyPerMove * Math.abs(environment.getElevation(newX, newY) - environment.getElevation(getX(), getY()));
		setPosition(newX, newY);
		return true;
	}
	
	private final boolean repath(int goalX, int goalY)
	{
		boolean foundPath = AStarEnvironmentSearch.performAStar(environment, 1.0001f, stepHeight, getX(), getY(), goalX, goalY, currentPath);
		
		if (!foundPath || currentPath.isEmpty())
			return false;
		
		currentPath.remove(0); // We're already in the first element

		return !currentPath.isEmpty();
	}
	
	@Override
	public void onCollision(Entity collidingEntity)
	{
		if (isDead || collidingEntity.isDead())
			return;
		
		if (dealsDamage && energy >= energyPerHit && collidingEntity instanceof Creature)
		{
			energy -= energyPerHit;
			
			if (((Creature)collidingEntity).takeDamage(damagePerHit))
				++killCount;
		}
	}
	
	public boolean takeDamage(float damage)
	{
		energy -= damage;
		
		if (energy <= 0f)
		{
			energy = 0f;
			setDead();
			return true;
		}
		
		return false;
	}
	
	@Override
	public void setDead()
	{
		if (!isDead)
			++deathCount;
			
		super.setDead();
	}
	
	public static class Mater implements IMater<Creature>
	{
		private final IMater<float[]> genomeMater;
		
		public Mater(IMater<float[]> genomeMater)
		{
			this.genomeMater = genomeMater;
		}
		
		@Override
		public Creature mate(List<? extends Creature> parents)
		{
			return new Creature(genomeMater.mate(new TransparentList<Creature, float[]>(parents, genomeMapping)));
		}
		
		private static final IMapping<Creature, float[]> genomeMapping = new IMapping<Creature, float[]>()
		{
			@Override
			public float[] map(Creature creature)
			{
				return creature.genes;
			}
		};
	}
}

package com.gamma;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import spawners.IMater;
import util.IMapping;
import util.TransparentList;
import fitness.IFitnessed;

public class Creature extends Entity implements IFitnessed
{	
	public static final int GENOME_LENGTH = 8, MOVES_PER_REPATHING = 7, TICKS_PER_TARGET_SCAN = 1000, TARGET_SCAN_RADIUS = 10;
	public static final float MIN_ENERGY_CAPACITY = 150f, MAX_ENERGY_CAPACITY = 300f, MIN_ENERGY_LOST_PER_TICK = .05f, MAX_ENERGY_LOST_PER_TICK = .5f, MIN_COLOR = .4f;
	public static final float SQRT_2 = (float)Math.sqrt(2), MAX_TARGET_DISTANCE = SQRT_2 * TARGET_SCAN_RADIUS;
	
	private final float[] genes;
	private final int timeOffset, ticksPerMove;
	private final float energyPerMove, damagePerHit, energyPerHit, stepHeight, red, green, blue, targetPriority_Energy, targetPriority_Distance;

	private float fitness = .1f;
	private int killCount = 0, deathCount = 0;
	private float energy, maxEnergy, energyLostPerTick;

	private ArrayList<Integer> currentPath = new ArrayList<Integer>();
	private Entity target = null;
	private int oldTargetX, oldTargetY;
	
	public Creature(Random random, float[] genes)
	{
		super(119);
		this.timeOffset = random.nextInt(1024);
		this.genes = genes;

		this.maxEnergy = MIN_ENERGY_CAPACITY + genes[0] * (MAX_ENERGY_CAPACITY - MIN_ENERGY_CAPACITY);
		this.energyLostPerTick = MIN_ENERGY_LOST_PER_TICK + genes[0] * (MAX_ENERGY_LOST_PER_TICK - MIN_ENERGY_LOST_PER_TICK);
		
		this.ticksPerMove = 1 + (int)(genes[1] * 9f);
		this.energyPerMove = 1.1f - genes[1];
				
		this.damagePerHit = genes[2] * 5f;
		this.energyPerHit = genes[2] * genes[1] * 5f;
		
		this.stepHeight = 1f + genes[3];
		
		this.red = MIN_COLOR + (1f - MIN_COLOR) * genes[4];
		this.green = MIN_COLOR + (1f - MIN_COLOR) * genes[5];
		this.blue = MIN_COLOR + (1f - MIN_COLOR) * genes[6];
		
		this.targetPriority_Energy = genes[7] / MIN_ENERGY_CAPACITY;
		this.targetPriority_Distance = (1f - genes[7]) / MAX_TARGET_DISTANCE;
	}

	@Override public float getFitness() { return fitness; }
	@Override public float getColorRed() { return red * energy / maxEnergy; }
	@Override public float getColorGreen() { return green * energy / maxEnergy; }
	@Override public float getColorBlue() { return blue * energy / maxEnergy; }
	
	public float getStepHeight() { return stepHeight; }
	public float getSpeed() { return ticksPerMove; }
	public float getStrength() { return damagePerHit; }
	public float getMaxEnergy() { return maxEnergy; }
	public float getEnergy() { return energy; }
	
	public void setFitness(float newFitness) {
		fitness = newFitness;
	}
	
	public void onBirth()
	{
		isDead = false;
		energy = maxEnergy;
		currentPath.clear();
	}
	
	@Override
	public void onUpdate(long simulationTime)
	{
		long offsetTime = timeOffset + simulationTime;
		
		if (target == null && offsetTime % TICKS_PER_TARGET_SCAN == 0)
		{
			target = findOptimalTarget();
			
			if (target != null)
			{
				oldTargetX = target.getX();
				oldTargetY = target.getY();
			}
		}
		
		performMovement(offsetTime);
		energy -= energyLostPerTick;
		
		if (energy <= 0f)
		{
			energy = 0f;
			setDead();
		}
	}
	
	private Entity findOptimalTarget()
	{
		int left = Math.max(0, getX() - TARGET_SCAN_RADIUS);
		int top  = Math.max(0, getY() - TARGET_SCAN_RADIUS);
		int right  = Math.min(environment.getWidth()  - 1, getX() + TARGET_SCAN_RADIUS);
		int bottom = Math.min(environment.getHeight() - 1, getY() + TARGET_SCAN_RADIUS);
		
		int i, j;
		Entity potentialTarget, bestTarget = null;
		float costEstimate, potentialTargetPriority, bestTargetPriority = Float.NEGATIVE_INFINITY;
		
		for (i = left; i <= right;  ++i)
		for (j = top;  j <= bottom; ++j)
		{
			if (environment.getEntity(i, j) == null)
				continue;
			
			potentialTarget = environment.getEntity(i, j);
			
			if (!(potentialTarget instanceof Creature))
				continue;
			
			costEstimate = AStarEnvironmentSearch.costEstimate(
					environment,
					1f,
					getX(),
					getY(),
					potentialTarget.getX(),
					potentialTarget.getY(),
					environment.getElevation(potentialTarget.getX(), potentialTarget.getY()));
			
			potentialTargetPriority = (1f - targetPriority_Distance * costEstimate) + targetPriority_Energy * ((Creature)potentialTarget).getEnergy();
			
			if (potentialTargetPriority > bestTargetPriority)
			{
				bestTarget = potentialTarget;
				bestTargetPriority = potentialTargetPriority;
			}
		}
		
		return bestTarget;
	}
	
	private boolean performMovement(long offsetTime)
	{
		if (offsetTime % ticksPerMove != 0 || energy < energyPerMove)
			return false;
		
		if (target != null && (currentPath.isEmpty() || offsetTime / ticksPerMove % MOVES_PER_REPATHING == 0 && (target.getX() != oldTargetX || target.getY() != oldTargetY)))
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
		
		if (energy >= energyPerHit && collidingEntity.equals(target) && collidingEntity instanceof Creature)
		{
			energy -= energyPerHit;
			
			if (((Creature)collidingEntity).takeDamageFromEntity(this, damagePerHit))
			{
				energy = maxEnergy;
				++killCount;
			}
		}
	}
	
	public boolean takeDamageFromEntity(Entity attacker, float damage)
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
	
	@Override
	public void onRemoval()
	{
		target = null;
	}
	
	public static class Mater implements IMater<Creature>
	{
		private final Random random;
		private final IMater<float[]> genomeMater;
		
		public Mater(Random random, IMater<float[]> genomeMater)
		{
			this.random = random;
			this.genomeMater = genomeMater;
		}
		
		@Override
		public Creature mate(List<? extends Creature> parents)
		{
			return new Creature(random, genomeMater.mate(new TransparentList<Creature, float[]>(parents, genomeMapping)));
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

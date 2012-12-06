package com.gamma;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import android.app.Activity;

import reproducers.IReproducer;
import spawners.IMater;

public class Simulation
{
	private final Random random;
	private final IEnvironment environment;
	private final IReproducer<Creature> reproducer;
	private final IMater<Creature> mater;
	private final long ticksPerRound;
	
	private int currentGeneration = 0;
	private long ticksThisRound = 0L;
	private List<Creature> creatures;
	private List<Entity> entities;
	private FitnessCalculator fitnessCalculator;
	
	public Simulation(Random random, IEnvironment environment, List<Creature> initialCreaturePopulation, IReproducer<Creature> reproducer, IMater<Creature> mater, long ticksPerRound, Activity context)
	{
		this.random = random;
		this.environment = environment;
		this.creatures = initialCreaturePopulation;
		this.reproducer = reproducer;
		this.mater = mater;
		this.ticksPerRound = ticksPerRound;
		
		this.entities = new LinkedList<Entity>(creatures);
		
		fitnessCalculator = new FitnessCalculator(context);
		onRoundBegin();
	}
	
	private void onRoundBegin()
	{
		ticksThisRound = 0L;
		
        for (int i = 0; i < creatures.size(); ++i)
        {
        	Creature c = creatures.get(i);
        	c.setRoundVariables(this, i);
        	c.setInitialPosition(random.nextInt(environment.getWidth()), random.nextInt(environment.getHeight()));
        	c.onBirth();
        }
        fitnessCalculator.setCreatures(creatures);
	}
	
	public void performTick()
	{
		ListIterator<Entity> it = entities.listIterator();
		
		while (it.hasNext())
		{
			Entity entity = it.next();
			entity.onUpdate(ticksThisRound);
			
			if (!entity.isDead())
				continue;
			
			if (entity instanceof Creature)
			{
				((Creature)entity).onBirth();
	        	entity.setPosition(random.nextInt(environment.getWidth()), random.nextInt(environment.getHeight()));
			}
			else
			{
				it.remove();
				entity.onRemoval();
			}
		}
		
		++ticksThisRound;
		
		if (ticksThisRound >= ticksPerRound)
		{
			for (Entity entity : entities)
				entity.onRemoval();
			
			fitnessCalculator.calculateFitnesses();
			creatures = reproducer.makeNextGeneration(creatures, mater);
			entities = new LinkedList<Entity>(creatures);
			++currentGeneration;
			onRoundBegin();
		}
	}
	
	public final int getGeneration() { return currentGeneration; }
	public final IEnvironment getEnvironment() { return environment; }
	public final Random getRandom() { return random; }
	public final List<Creature> getCreatures() { return Collections.unmodifiableList(creatures); }
	public final List<Entity> getEntities() { return Collections.unmodifiableList(entities); }
}

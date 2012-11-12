package com.gamma;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import reproducers.IReproducer;
import spawners.IMater;

public class Simulation
{
	private final Random random;
	private final IEnvironment environment;
	private final IReproducer<Creature> reproducer;
	private final IMater<Creature> mater;
	private int ticksPerRound, ticksThisRound = 0;
	private List<Creature> creatures;
	private List<Entity> entities;
	
	public Simulation(Random random, IEnvironment environment, List<Creature> initialCreaturePopulation, IReproducer<Creature> reproducer, IMater<Creature> mater, int ticksPerRound)
	{
		this.random = random;
		this.environment = environment;
		this.creatures = initialCreaturePopulation;
		this.reproducer = reproducer;
		this.mater = mater;
		this.ticksPerRound = ticksPerRound;
		
		this.entities = new LinkedList<Entity>(creatures);
		onRoundBegin();
	}
	
	private void onRoundBegin()
	{
		ticksThisRound = 0;
		
        for (int i = 0; i < creatures.size(); ++i)
        {
        	Creature c = creatures.get(i);
        	c.setRoundVariables(this, i);
        	c.setInitialPosition(random.nextInt(environment.getWidth()), random.nextInt(environment.getHeight()));
        }
	}
	
	public void performTick()
	{
		ListIterator<Entity> it = entities.listIterator();
		
		while (it.hasNext())
		{
			Entity entity = it.next();
			entity.onUpdate();
			
			if (!entity.isDead())
				continue;
			
			it.remove();
			entity.onRemoval();
			// handle removal from simulation
		}
		
		++ticksThisRound;
		
		if (ticksThisRound >= ticksPerRound)
		{
			for (Entity entity : entities)
				entity.onRemoval();
			
			creatures = reproducer.makeNextGeneration(creatures, mater);
			entities = new LinkedList<Entity>(creatures);
			onRoundBegin();
		}
	}
	
	public final IEnvironment getEnvironment() { return environment; }
	public final Random getRandom() { return random; }
	public final List<Creature> getCreatures() { return Collections.unmodifiableList(creatures); }
	public final List<Entity> getEntities() { return Collections.unmodifiableList(entities); }
}

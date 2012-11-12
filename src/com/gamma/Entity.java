package com.gamma;

import java.nio.FloatBuffer;
import java.util.Random;

public class Entity
{
	private static final float CREATURE_TILE_RESOLUTION = 1 / 16f;
	private final int textureIndex;
	private final float sheetX, sheetXEnd, sheetY, sheetYEnd;
	
	private int entityID;
	private boolean isDead = false;
	private int posX = -1, posY = -1;
	private float colorRed = 1f, colorGreen = 1f, colorBlue = 1f;

	protected Simulation simulation;
	protected IEnvironment environment;
	protected Random random;
	
	public Entity(int textureIndex)
	{
		this.textureIndex = textureIndex;
		
		this.sheetX = ((textureIndex & 0xf) * CREATURE_TILE_RESOLUTION);
		this.sheetY = (((textureIndex >>> 4) & 0xf) * CREATURE_TILE_RESOLUTION);
		this.sheetXEnd = (sheetX + CREATURE_TILE_RESOLUTION);
		this.sheetYEnd = (sheetY + CREATURE_TILE_RESOLUTION);
	}
	
	public void setRoundVariables(Simulation simulation, int entityID)
	{
		this.simulation = simulation;
		this.environment = simulation.getEnvironment();
		this.random = simulation.getRandom();
		this.entityID = entityID;
	}
	
	public final int getTextureIndex() { return textureIndex; }
	public final float getColorRed() { return colorRed; }
	public final float getColorGreen() { return colorGreen; }
	public final float getColorBlue() { return colorBlue; }

	@Override
	public final boolean equals(Object o)
	{
		return o instanceof Entity && ((Entity)o).entityID == entityID;
	}
	
	@Override
	public final int hashCode()
	{
		return entityID;
	}
	
	public final void loadTexCoordBuffer(FloatBuffer buffer)
	{
		buffer.put(0, sheetX);
		buffer.put(1, sheetYEnd);
		buffer.put(2, sheetXEnd);
		buffer.put(3, sheetYEnd);
		buffer.put(4, sheetX);
		buffer.put(5, sheetY);
		buffer.put(6, sheetXEnd);
		buffer.put(7, sheetY);
	}
	
	/** When a creature is dead, treat it as though it no longer exists in the world 
	 * (since it will literally be removed in the next few ticks.)
	 * To prevent memory leaks, don't maintain references to dead creatures. */
	public final boolean isDead() { return isDead; }
	
	/** Flags the creature as dead, so that it will be removed from the world after the current tick. */
	public final void setDead()
	{
		isDead = true;
		environment.setEntity(this.posX, this.posY, null);
	}
	
	public final int getX() { return posX; }
	public final int getY() { return posY; }
	
	public void setInitialPosition(int posX, int posY)
	{
		this.posX = posX;
		this.posY = posY;
		environment.setEntity(this.posX, this.posY, this);
	}
	
	public void setPosition(int posX, int posY)
	{
		environment.setEntity(this.posX, this.posY, null);
		this.posX = posX;
		this.posY = posY;
		environment.setEntity(this.posX, this.posY, this);
	}

	public void onUpdate()
	{
		
	}
	
	/** Called when the creature is removed from the simulation (this is a chance to clean up references) */
	public void onRemoval()
	{
	}
}

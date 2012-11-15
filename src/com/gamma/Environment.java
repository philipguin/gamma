package com.gamma;

public class Environment implements IEnvironment
{
	private final byte[] blocks;
	private final float[] elevationMap, temperature;
	private final Entity[] entities;
	private final int blocksWidth, blocksHeight, blocksHeightBitShift;
	
	public Environment(byte[] blocks, float[] elevationMap, float[] temperature, int blocksHeightBitShift)
	{
		assert blocksHeightBitShift >= 0;
		
		this.blocksHeightBitShift = blocksHeightBitShift;
		this.blocksHeight = 1 << blocksHeightBitShift;
		
		assert blocks.length % blocksHeight == 0;

		this.blocks = blocks;
		this.blocksWidth = blocks.length / blocksHeight;
		
		assert blocksWidth > 0;
		assert elevationMap.length == blocks.length;
		assert temperature.length == blocks.length;
		
		this.elevationMap = elevationMap;
		this.temperature = temperature;
		this.entities = new Entity[blocksWidth * blocksHeight];
	}

	@Override public int getWidth() { return blocksWidth; }
	@Override public int getHeight() { return blocksHeight; }
	@Override public boolean isWithinBounds(int x, int y) { return x >= 0 && y >= 0 && x < blocksWidth && y < blocksHeight; }
	
	@Override public float getTemperature(int x, int y) { return temperature[y << blocksHeightBitShift | x]; }
	@Override public void setTemperature(int x, int y, float t) { temperature[y << blocksHeightBitShift | x] = t; }
	@Override public float getElevation(int x, int y) { return elevationMap[y << blocksHeightBitShift | x]; }
	
	@Override public byte getBlockID(int x, int y) { return blocks[y << blocksHeightBitShift | x]; }
	@Override public void setBlockID(int x, int y, byte blockID) { blocks[y << blocksHeightBitShift | x] = blockID; }
	@Override public Entity getEntity(int x, int y) { return entities[y << blocksHeightBitShift | x]; }
	
	@Override
	public void setEntity(int x, int y, Entity entity)
	{
		assert entity == null || entities[y << blocksHeightBitShift | x] == null; //TODO: remove in actual release
		entities[y << blocksHeightBitShift | x] = entity;
	}

}

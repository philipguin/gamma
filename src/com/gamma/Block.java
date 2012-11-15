package com.gamma;

import java.nio.FloatBuffer;

public class Block
{
	private static final float BLOCK_TILE_RESOLUTION = 1f / 16f;
	
	private final int blockID;
	private final String name;
	private final float sheetX, sheetY, sheetXEnd, sheetYEnd;
	
	public Block(int blockID, int sheetIndex, String name)
	{
		this.blockID = blockID;
		this.name = name;

		this.sheetX = ((sheetIndex & 0xf) * BLOCK_TILE_RESOLUTION);
		this.sheetY = (((sheetIndex >>> 4) & 0xf) * BLOCK_TILE_RESOLUTION);
		this.sheetXEnd = (sheetX + BLOCK_TILE_RESOLUTION);
		this.sheetYEnd = (sheetY + BLOCK_TILE_RESOLUTION);
	}
	
	public final int getID() { return blockID; }
	public final String toString() { return name; }
	
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
	
	private static final Block[] blockList = new Block[256];
	
	public static final Block getBlock(int index)
	{
		return blockList[index];
	}
	
	static
	{
		blockList[0] = new Block(0, 0, "grass");
	}
}

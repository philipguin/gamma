package com.gamma;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES10;


public class SimulationDrawable implements IDrawable<GL10>
{
	private final Simulation simulation;
	private final IBlockColorizer blockColorizer;
	
	public SimulationDrawable(Simulation simulation, IBlockColorizer blockColorizer)
	{
		this.simulation = simulation;
		this.blockColorizer = blockColorizer;
	}
	
	private final ByteBuffer quadTexCoordBufferDirect = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder());
	private final ByteBuffer quadVertexBufferDirect = ByteBuffer.allocateDirect(12 * 4).order(ByteOrder.nativeOrder());
	private final ByteBuffer quadColorBufferDirect = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder());
	private final FloatBuffer quadTexCoordBuffer = quadTexCoordBufferDirect.asFloatBuffer();
	private final FloatBuffer quadVertexBuffer = quadVertexBufferDirect.asFloatBuffer();
	private final FloatBuffer quadColorBuffer = quadColorBufferDirect.asFloatBuffer();
	
	public static final int BLOCK_BIT_SHIFT = 5;
	public static final int BLOCK_DIMENSIONS = 1 << BLOCK_BIT_SHIFT;
	
	@Override
	public void draw(GL10 gl, TextureManager textureManager, Viewport v)
	{
		gl.glEnable(GLES10.GL_TEXTURE_2D);
		gl.glTexEnvx(GLES10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE, GLES10.GL_MODULATE);

		gl.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, quadTexCoordBufferDirect);
		gl.glVertexPointer(3, GLES10.GL_FLOAT, 0, quadVertexBufferDirect);
		gl.glColorPointer(4, GLES10.GL_FLOAT, 0, quadColorBufferDirect);
    	
    	// Put z vertices, since they never change
    	quadVertexBuffer.put(2, 0f);
    	quadVertexBuffer.put(5, 0f);
    	quadVertexBuffer.put(8, 0f);
    	quadVertexBuffer.put(11, 0f);

    	// Put color alpha, since they also never change
		quadColorBuffer.put(3,  1f);
		quadColorBuffer.put(7,  1f);
		quadColorBuffer.put(11, 1f);
		quadColorBuffer.put(15, 1f);

		synchronized (simulation)
		{
			drawEnvironment(gl, textureManager, v);
			drawCreatures(gl, textureManager, v);
		}

		gl.glDisable(GLES10.GL_TEXTURE_2D);
	}
	
	private void drawEnvironment(GL10 gl, TextureManager textureManager, Viewport v)
	{
		// Draw environment;
		IEnvironment environment = simulation.getEnvironment();
		textureManager.bindTexture(gl, R.drawable.ic_terrain);
		
    	int drawX, drawY, drawXNext, drawYNext, blockX, blockY;
    	int blockID, lastBlockID = -1;
		
		int drawXStart = v.getLeft() & ~(BLOCK_DIMENSIONS - 1);
		int drawYStart = v.getTop()  & ~(BLOCK_DIMENSIONS - 1);
		int drawXEnd = Math.min(drawXStart + (environment.getWidth()  << BLOCK_BIT_SHIFT), v.getRight());
		int drawYEnd = Math.min(drawYStart + (environment.getHeight() << BLOCK_BIT_SHIFT), v.getBottom());
		
		for (drawY = drawYStart, drawYNext = drawY + BLOCK_DIMENSIONS, blockY = v.getTop()  >> BLOCK_BIT_SHIFT; drawY < drawYEnd; drawY = drawYNext, drawYNext += BLOCK_DIMENSIONS, ++blockY)
		for (drawX = drawXStart, drawXNext = drawX + BLOCK_DIMENSIONS, blockX = v.getLeft() >> BLOCK_BIT_SHIFT; drawX < drawXEnd; drawX = drawXNext, drawXNext += BLOCK_DIMENSIONS, ++blockX)
		{
			blockID = environment.getBlockID(blockX, blockY);

			if (blockID != lastBlockID)
			{
				Block.getBlock(blockID).loadTexCoordBuffer(quadTexCoordBuffer);
		    	lastBlockID = blockID;
			}
			
			blockColorizer.fillColorBuffer(quadColorBuffer, environment.getTemperature(blockX, blockY), environment.getElevation(blockX, blockY));
			
	    	quadVertexBuffer.put(0, drawX);
	    	quadVertexBuffer.put(1, drawY);
	    	quadVertexBuffer.put(3, drawXNext);
	    	quadVertexBuffer.put(4, drawY);
	    	quadVertexBuffer.put(6, drawX);
	    	quadVertexBuffer.put(7, drawYNext);
	    	quadVertexBuffer.put(9, drawXNext);
	    	quadVertexBuffer.put(10, drawYNext);

	    	gl.glDrawArrays(GLES10.GL_TRIANGLE_STRIP, 0, 4);
		}
	}

	private void drawCreatures(GL10 gl, TextureManager textureManager, Viewport v)
	{
		int lastTextureIndex = -1;
		int drawX, drawY, drawXEnd, drawYEnd;
		textureManager.bindTexture(gl, R.drawable.ic_terrain);
		
		for (Entity entity : simulation.getEntities())
		{
			drawX = (entity.getX() << BLOCK_BIT_SHIFT);
			drawY = (entity.getY() << BLOCK_BIT_SHIFT);
			drawXEnd = drawX + BLOCK_DIMENSIONS;
			drawYEnd = drawY + BLOCK_DIMENSIONS;
			
			if (drawXEnd <= v.getLeft() || drawYEnd <= v.getTop() || drawX >= v.getRight() || drawY >= v.getBottom())
				continue;
			
			if (entity.getTextureIndex() != lastTextureIndex)
			{
				entity.loadTexCoordBuffer(quadTexCoordBuffer);
		    	lastTextureIndex = entity.getTextureIndex();
			}
			
			// Set color
			quadColorBuffer.put(0,  entity.getColorRed());
			quadColorBuffer.put(1,  entity.getColorGreen());
			quadColorBuffer.put(2,  entity.getColorBlue());
			quadColorBuffer.put(4,  entity.getColorRed());
			quadColorBuffer.put(5,  entity.getColorGreen());
			quadColorBuffer.put(6,  entity.getColorBlue());
			quadColorBuffer.put(8,  entity.getColorRed());
			quadColorBuffer.put(9,  entity.getColorGreen());
			quadColorBuffer.put(10, entity.getColorBlue());
			quadColorBuffer.put(12, entity.getColorRed());
			quadColorBuffer.put(13, entity.getColorGreen());
			quadColorBuffer.put(14, entity.getColorBlue());
			
			// Set vertices
	    	quadVertexBuffer.put(0, drawX);
	    	quadVertexBuffer.put(1, drawY);
	    	quadVertexBuffer.put(3, drawXEnd);
	    	quadVertexBuffer.put(4, drawY);
	    	quadVertexBuffer.put(6, drawX);
	    	quadVertexBuffer.put(7, drawYEnd);
	    	quadVertexBuffer.put(9, drawXEnd);
	    	quadVertexBuffer.put(10, drawYEnd);

	    	gl.glDrawArrays(GLES10.GL_TRIANGLE_STRIP, 0, 4);
		}
	}
}

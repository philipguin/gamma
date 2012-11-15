package com.gamma;

import java.nio.FloatBuffer;

public interface IBlockColorizer
{
	/** Assumes alpha is already filled. */
	public void fillColorBuffer(FloatBuffer buffer, float temperature, float elevation);
}

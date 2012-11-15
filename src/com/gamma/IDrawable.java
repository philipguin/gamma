package com.gamma;

public interface IDrawable<G>
{
	public void draw(G graphics, TextureManager textureManager, Viewport viewport);
}

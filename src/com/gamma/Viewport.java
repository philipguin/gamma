package com.gamma;


public class Viewport
{
	private int x, y, width, height;
	
	public Viewport(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public final void setPosition(int x, int y) { this.x = x; this.y = y; }
	public final void movePosition(int x, int y) { this.x += x; this.y += y; }
	public final void setDimensions(int width, int height) { this.width = width; this.height = height; }
	
	public final int getLeft() { return x; }
	public final int getRight() { return x + width; }
	public final int getTop() { return y; }
	public final int getBottom() { return y + height; }
	public final int getWidth() { return width; }
	public final int getHeight() { return height; }
}

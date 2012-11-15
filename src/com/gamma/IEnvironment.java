package com.gamma;

public interface IEnvironment
{
	int getWidth();
	int getHeight();
	boolean isWithinBounds(int x, int y);
	float getTemperature(int x, int y);
	void setTemperature(int x, int y, float temperature);
	float getElevation(int x, int y);
	byte getBlockID(int x, int y);
	void setBlockID(int x, int y, byte blockID);
	Entity getEntity(int x, int y);
	void setEntity(int x, int y, Entity entity);
}

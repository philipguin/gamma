package com.gamma;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import android.util.FloatMath;
import android.util.SparseArray;
import android.util.SparseIntArray;

public abstract class AStarEnvironmentSearch
{
	private static final float SQRT_OF_TWO = FloatMath.sqrt(2f);
	private static final float SQRT_OF_TWO_MINUS_TWO = SQRT_OF_TWO - 2f;
	
	private AStarEnvironmentSearch() { }
	
	private static final float costEstimate(IEnvironment environment, float laxness, int fromX, int fromY, int goalX, int goalY, float goalElevation)
	{
		int dx = Math.abs(goalX - fromX);
		int dy = Math.abs(goalY - fromY);
		
		//float h_diagonal = Math.min(dx, dy);
		//return SQRT_OF_TWO * h_diagonal + D * (dx + dy - 2 * h_diagonal);
		
		return Math.min(dx, dy) * SQRT_OF_TWO_MINUS_TWO + dx + dy + Math.abs(goalElevation - environment.getElevation(fromX, fromY));
	}
	
	private static final int indexOf(int x, int y) { return y << 16 | x; }
	
	private static final boolean couldMove(IEnvironment environment, float stepHeight, int indexFrom, int indexTo)
	{
		int newX = indexTo & 0xffff;
		int newY = (indexTo >>> 16) & 0xffff;
		
		if (!environment.isWithinBounds(newX, newY) || environment.getEntity(newX, newY) != null)
			return false;
		
		int x = indexFrom & 0xffff;
		int y = (indexFrom >>> 16) & 0xffff;
		
		return Math.abs(environment.getElevation(x, y) - environment.getElevation(newX, newY)) <= stepHeight;
	}
	
	//private static final int[] directions = new int[]{0x00010000, 0x00000001, 0xffff0000, 0xffffffff};
	
	/** Set laxness to be less than 1f / (expected maximum path length) for faster search with no consequence.
	 * Greater values will result in sub-optimal (but more quickly found) results. */
	public static final boolean performAStar(
			IEnvironment environment,
			float stepHeight,
			float laxness,
			int startX,
			int startY,
			int goalX,
			int goalY,
			List<Integer> pathToBuild)
	{	
		int startIndex = indexOf(startX, startY);
		int goalIndex = indexOf(goalX, goalY);
		float goalElevation = environment.getElevation(goalX, goalY);
	    
	    SparseIntArray visitation = new SparseIntArray(16);
	    SparseIntArray came_from = new SparseIntArray(16);
	    final SparseArray<Float> g_score = new SparseArray<Float>(16);
	    final SparseArray<Float> f_score = new SparseArray<Float>(16);
	    
	    f_score.put(startIndex, costEstimate(environment, laxness, startX, startY, goalX, goalY, goalElevation));
	    
	    PriorityQueue<Integer> queue = new PriorityQueue<Integer>(8, new Comparator<Integer>()
		{
	    	@Override
	    	public int compare(Integer a, Integer b)
	    	{
	    		return Float.compare(f_score.get(a, Float.POSITIVE_INFINITY), f_score.get(b, Float.POSITIVE_INFINITY));
	    	}
		});

        visitation.put(startIndex, 1);
		queue.add(startIndex);
		
		/*for (int i = 0; i < environment.getWidth();  ++i)
		for (int j = 0; j < environment.getHeight(); ++j)
			environment.setTemperature(i, j, 0f);*/
	
		// Algorithm loop
	    while (!queue.isEmpty())
	    {
	        int currentIndex = queue.poll();
	        
	        if (currentIndex == goalIndex)
	        {
	        	if (pathToBuild != null)
	        		buildPath(came_from, goalIndex, pathToBuild);
	        	
	            return true;
	        }
	        
	        visitation.put(currentIndex, 2);
	        
	        int currentX = currentIndex & 0xffff;
	        int currentY = (currentIndex >>> 16) & 0xffff;
	        float currentElevation = environment.getElevation(currentX, currentY);

	        for (int d = 0; d < 8; ++d)
	        {
	        	int newX, newY;
	        	
	        	// Adjust current position to potential new location, and skip if its out of bounds.
	        	// Ignore the ugliness of the switch...
	        	switch (d)
	        	{
	        	case 0: if (currentX <= 0) continue; newX = currentX - 1; newY = currentY; break;
	        	case 1: if (currentY <= 0) continue; newY = currentY - 1; newX = currentX; break;
	        	case 2: newX = currentX + 1; if (newX >= environment.getWidth())  continue; newY = currentY; break;
	        	case 3: newY = currentY + 1; if (newY >= environment.getHeight()) continue; newX = currentX; break;

	        	case 4: if (currentX <= 0 || currentY <= 0) continue; newX = currentX - 1; newY = currentY - 1; break;
	        	
	        	case 5:
	        		if (currentX <= 0) continue;
	        		newY = currentY + 1;
	        		if (newY >= environment.getHeight()) continue;
	        		newX = currentX - 1;
	        		break;
	        		
	        	case 6:
	        		if (currentY <= 0) continue;
	        		newX = currentX + 1;
	        		if (newX >= environment.getWidth()) continue;
	        		newY = currentY - 1;
	        		break;
	        		
	        	case 7:
	        		newX = currentX + 1;
	        		if (newX >= environment.getWidth()) continue;
	        		newY = currentY + 1;
	        		if (newY >= environment.getHeight()) continue;
	        		break;
	        	
	        	default: throw new Error("This ain't supposed to happen.");
	        	}
	        	
	        	int to = indexOf(newX, newY);
	        	
		        if (visitation.get(to, 0) == 2 || !couldMove(environment, stepHeight, currentIndex, to))
		        	continue;
		        
		        float tentative_g_score = g_score.get(currentIndex, 0f) + (d < 4 ? 1f : SQRT_OF_TWO) + Math.abs(environment.getElevation(newX, newY) - currentElevation);

	            if (visitation.get(to, 0) != 1)
	            {
	                came_from.put(to, currentIndex);
	                g_score.put(to, tentative_g_score);
	                f_score.put(to, tentative_g_score + costEstimate(environment, laxness, newX, newY, goalX, goalY, goalElevation));
	            	//environment.setTemperature(newX, newY, tentative_g_score);
	                
                    visitation.put(to, 1);
                    queue.add(to);
	            }
	            else if (tentative_g_score < g_score.get(to, 0f))
	            {
	                came_from.put(to, currentIndex);
	                g_score.put(to, tentative_g_score);
	                f_score.put(to, tentative_g_score + costEstimate(environment, laxness, newX, newY, goalX, goalY, goalElevation));
	            	//environment.setTemperature(newX, newY, tentative_g_score);
	            }
	        }
	    }
	    
	    return false;
	}

	private static final void buildPath(SparseIntArray came_from, int to, List<Integer> path)
	{
	    path.add(0, to);

		int from = came_from.get(to, -1);
	    if (from == -1)
	    	return;
	    
    	buildPath(came_from, from, path);
	}
}

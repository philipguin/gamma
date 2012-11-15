package com.gamma;

import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class SimulationSurfaceView extends GLSurfaceView
{
	private final DrawableRenderer renderer;
	private final Simulation simulation;
	
	public SimulationSurfaceView(Context context, Simulation simulation)
	{
		super(context);
		this.simulation = simulation;
		
		IBlockColorizer blockColorizer = new IBlockColorizer()
		{
			@Override
			public void fillColorBuffer(FloatBuffer buffer, float temperature, float elevation)
			{
				float red = .2f + .02f * temperature;
				float green = .2f + .8f * elevation;
				float blue = .2f + .5f * elevation;
				
				buffer.put(0,  red);
				buffer.put(1,  green);
				buffer.put(2,  blue);
				buffer.put(4,  red);
				buffer.put(5,  green);
				buffer.put(6,  blue);
				buffer.put(8,  red);
				buffer.put(9,  green);
				buffer.put(10, blue);
				buffer.put(12, red);
				buffer.put(13, green);
				buffer.put(14, blue);
				
			}
		};
		this.renderer = new DrawableRenderer(new SimulationDrawable(simulation, blockColorizer), this.getResources());
        this.setRenderer(renderer);
		this.setRenderMode(RENDERMODE_WHEN_DIRTY);
	}
	
	private int previousMoveX = 0, previousMoveY = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
	    int x = (int)e.getX();
	    int y = (int)e.getY();

	    switch (e.getAction())
	    {
	        case MotionEvent.ACTION_MOVE:
	    		synchronized (simulation)
	    		{
		        	Viewport viewport = renderer.getViewport();
		            int dx = -(x - previousMoveX);
		            int dy = y - previousMoveY; // y is flipped
	
		    		int viewportX = Math.max(0, Math.min((simulation.getEnvironment().getWidth()  << SimulationDrawable.BLOCK_BIT_SHIFT) - viewport.getWidth(),  viewport.getLeft() + dx));
		    		int viewportY = Math.max(0, Math.min((simulation.getEnvironment().getHeight() << SimulationDrawable.BLOCK_BIT_SHIFT) - viewport.getHeight(), viewport.getTop()  + dy));
		    		
		    		viewport.setPosition(viewportX, viewportY);
		            //requestRender();
	    		}
	    }
		
	    previousMoveX = x;
	    previousMoveY = y;
	    return true;
	}
}

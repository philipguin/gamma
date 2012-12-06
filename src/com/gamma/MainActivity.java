package com.gamma;

import genome.GenomeMater;
import genome.UniformRandomGenomeCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import reproducers.SelectionReproducer;
import selectors.WeightedMultiselector;
import spawners.ICreator;
import squarediamond.SquareDiamondArray2DPopulator;
import squarediamond.UniformBiasedRandomStyle;
import squarediamond.UniformRandomlyInterpolatedSquareDiamondStyle;
import src.Array2D;
import src.IPopulator;
import src.SubarrayDerivator;
import src.WeightedArray2DAdder;
import weights.FitnessWeightMaker;
import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends Activity
{	
	private GLSurfaceView view;
	private Simulation simulation;
	private volatile boolean mainLoopRunning = true;

	private int frameRateCap = 100;
	private long lastDrawTime = 0;
	
	private static final int initialCreatureCount = 40;
	
	@SuppressWarnings("unchecked")
	private static final float[] generateTerrain(Random random, int width, int height)
	{
		Array2D result = new Array2D(width + 1, height + 1);
		
		new WeightedArray2DAdder(
				Arrays.<IPopulator<Array2D>>asList(
					new SquareDiamondArray2DPopulator(new UniformBiasedRandomStyle(
							random,
							new UniformRandomlyInterpolatedSquareDiamondStyle(
									random/*,
									Arrays.asList(1f, 1f, .8f, .8f, .8f, .8f, .6f, .6f, .4f)*/),
							Arrays.asList(1f,1f, 1f, 1f, .5f, .1f, 0f))
					),
					new SquareDiamondArray2DPopulator(new UniformBiasedRandomStyle(
							random,
							new UniformRandomlyInterpolatedSquareDiamondStyle(
									random/*,
									Arrays.asList(1f, 1f, .8f, .8f, .8f, .8f, .6f, .6f, .4f)*/),
							Arrays.asList(1f,1f, .2f, .2f, .1f, .05f, 0f))
					)
				),
				Arrays.asList(2f, 1f),
				1f
			).populate(result);
		
		return new SubarrayDerivator(0, 0, width, height).derive(result).exposeValues();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Random random = new Random();
        IEnvironment environment = new Environment(new byte[32 * 32], /*new float[32 * 32],*/ generateTerrain(random, 32, 32), new float[32 * 32], 5);
        ICreator<float[]> geneCreator = new UniformRandomGenomeCreator(random, Creature.GENOME_LENGTH, 0f, 1f);

        List<Creature> creatures = new ArrayList<Creature>(initialCreatureCount);
        for (int i = 0; i < initialCreatureCount; ++i)
        	creatures.add(new Creature(random, geneCreator.create()));
        
        this.simulation = new Simulation(
        		random,
    			environment,
    			creatures,
    			new SelectionReproducer<Creature>(2, new WeightedMultiselector<Creature>(random, new FitnessWeightMaker<Creature>(), true)),
    			new Creature.Mater(random, new GenomeMater(random, 0f, 1f, .25f, .2f, .25f)), 
    			5 * 60 * 40, 
    			this);
    			
        view = new SimulationSurfaceView(this, simulation);
        
        setContentView(view);
        
        Executors.newFixedThreadPool(1).submit(new Runnable()
        {
			@Override
			public void run()
			{
				while (mainLoopRunning)
				{
					synchronized (simulation)
					{
						simulation.performTick();
					}
					
					if (frameRateCap > 0)
					{
						long sleepTime = 1000 / frameRateCap - (System.currentTimeMillis() - lastDrawTime);
					
						if (sleepTime > 0)
						{
							try { Thread.sleep(sleepTime); }
							catch (InterruptedException e) { e.printStackTrace(); }
						}
					}
					
					lastDrawTime = System.currentTimeMillis();
					
					view.requestRender();
				}
			}
		}, "simulationUpdateLoop");
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        view.onResume();
    }
     
    @Override
    protected void onPause()
    {
        super.onPause();
        view.onPause();
    }
    
    /** Called when the user clicks the "Return to Main Menu" menu button
     *  (via onOptionsItemSelected)
     */
    private void goToMainMenu() {
    	Intent intent = new Intent(this, HomeActivity.class);
    	startActivity(intent);
    }
    
    /** Acts as a dispatch for when the user clicks menu buttons **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main_menu_return:
            	goToMainMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

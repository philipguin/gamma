package com.gamma;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

/** Activity for the Main Menu **/
public class HomeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    
    /** Called when the user clicks the Create New Population button **/
    public void goToNewPopMenu(View view) {
    	Intent intent = new Intent(this, ModFitActivity.class);
    	intent.putExtra("title", "Create New Population");
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Modify Fitness Algorithm button **/
    public void goToModFitMenu(View view) {
    	Intent intent = new Intent(this, ModFitActivity.class);
    	intent.putExtra("title", "Modify Fitness Algorithm");
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Simulate Evolution button **/
    public void goToRunSimMenu(View view) {
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    }
}

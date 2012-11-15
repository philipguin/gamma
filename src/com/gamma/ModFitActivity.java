package com.gamma;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/** Activity for the Enter Fitness Algorithm menu **/
public class ModFitActivity extends Activity {
	private boolean isNewPopScreen;
	private ArrayList<String> inputStrings;
	private int totalTextboxes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set layout to Modify Fitness with one textbox
        setContentView(R.layout.activity_mod_fit_1);
        totalTextboxes = 1;
        
        //Get title from intent
        String title = getIntent().getStringExtra("title");
        
        //Set activity's title
        setTitle(title);
        
        //Set boolean value based on whether or not the title is Create New Population
        // (This will be used later to determine the behavior of the "Done" button
        if (title == "Create New Population") {
        	isNewPopScreen = true;
        } 
        else {
        	isNewPopScreen = false;
        }
        
        //Initialize inputStrings
        inputStrings = new ArrayList<String>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mod_fit, menu);
        return true;
    }
    
    /** Called when the user clicks the Clear button 
     * 		Clears the textbox (only one is present if clear 
     * 		button is visible)
     **/
    public void clearField(View view) {
    	EditText textField = (EditText)findViewById(R.id.mod_fit_textbox);
    	textField.setText("");
    }
    
    /** Called when the user clicks the Expand button 
     * 		Changes the layout to support an additional textbox
     **/
    public void expandTextboxList(View view) {
    	//Store the textbox data, switch the layout, then extract
    	// data to put in the new layout
    	storeTextboxData();
    	chooseAndSetLayout(totalTextboxes + 1);
    	extractTextboxData();
    }
    
    /** Sets the layout based on how many textboxes are desired **/
    private void chooseAndSetLayout(int numberOfTextboxes) {
    	totalTextboxes = numberOfTextboxes;
    	switch (numberOfTextboxes) {
    		case 1:
    	        setContentView(R.layout.activity_mod_fit_1);
    	        break;
    		case 2:
    			setContentView(R.layout.activity_mod_fit_2);
    			break;
    		case 3:
    			setContentView(R.layout.activity_mod_fit_3);
    			break;
    		case 4:
    			setContentView(R.layout.activity_mod_fit_4);
    			break;
    		case 5:
    			setContentView(R.layout.activity_mod_fit_5);
    			break;
    		case 6:
    			setContentView(R.layout.activity_mod_fit_6);
    			break;
    		case 7:
    			setContentView(R.layout.activity_mod_fit_7);
    			break;
    		case 8:
    			setContentView(R.layout.activity_mod_fit_8);
    			break;
    		case 9:
    			setContentView(R.layout.activity_mod_fit_9);
    			break;
    	}
    }
    
    /** Stores the data from the textboxes (besides the textbox indicated 
     * by exceptionID) in inputStrings
     **/
    private void storeTextboxData() {
    	//Store all textboxes: send 0 as the exceptionID, so no textboxes
    	// will be ignored
    	storeTextboxData(0);
    }
    
    private void storeTextboxData(int exceptionID) {
    	//Loop through available textboxes, storing the string
    	// if textbox's ID does not equal exceptionID
    	int id;
    	for (int i = 1; i <= totalTextboxes; i++) {
    		//Get the textbox's ID
    		id = getTextboxId(i);
    		
    		if (id != exceptionID) {
    			//Get the textbox object
    			EditText textbox = (EditText)findViewById(id);
    			
    			//Append textbox's contents to inputStrings
    			String contents = textbox.getText().toString();
    			inputStrings.add(contents);
    		}
    	}
    }
    
    /** Returns the id associated with the boxNum'th textbox **/
    private int getTextboxId(int boxNum) {
    	switch (boxNum) {
    		case 1:
    			return R.id.mod_fit_textbox;
    		case 2:
    			return R.id.mod_fit_textbox_2;
    		case 3:
    			return R.id.mod_fit_textbox_3;
    		case 4:
    			return R.id.mod_fit_textbox_4;
    		case 5:
    			return R.id.mod_fit_textbox_5;
    		case 6:
    			return R.id.mod_fit_textbox_6;
    		case 7:
    			return R.id.mod_fit_textbox_7;
    		case 8:
    			return R.id.mod_fit_textbox_8;
    		case 9:
    			return R.id.mod_fit_textbox_9;
    		default:
    			return 0;
    	}
    }
    
    /** Returns the id associated with the given "X" button **/
    private int getTextboxId(View button) {
    	int buttonId = button.getId();
    	switch (buttonId) {
    		case R.id.delete_button_1:
    			return R.id.mod_fit_textbox;
    		case R.id.delete_button_2:
    			return R.id.mod_fit_textbox_2;
    		case R.id.delete_button_3:
    			return R.id.mod_fit_textbox_3;
    		case R.id.delete_button_4:
    			return R.id.mod_fit_textbox_4;
    		case R.id.delete_button_5:
    			return R.id.mod_fit_textbox_5;
    		case R.id.delete_button_6:
    			return R.id.mod_fit_textbox_6;
    		case R.id.delete_button_7:
    			return R.id.mod_fit_textbox_7;
    		case R.id.delete_button_8:
    			return R.id.mod_fit_textbox_8;
    		case R.id.delete_button_9:
    			return R.id.mod_fit_textbox_9;
    		default:
    			return 0; 			
    	}
    }
    
    /** Extracts the data that was stored about the textboxes, inserting
     *  those strings into the new layout.
     */
    private void extractTextboxData() {
    	int id;
    	int max = inputStrings.size();
    	for (int i = 1; i <= max; i++) {
    		//Get the id of the textbox that will contain the string
    		id = getTextboxId(i);
    		
    		//Get the textbox object
    		EditText textbox = (EditText)findViewById(id);

    		//Remove string from front of inputStrings and put in textbox
    		String inputString = inputStrings.get(0);
    		textbox.setText(inputString);
    		inputStrings.remove(0);
    	}
    }
    
    /** Called when the user clicks the "X" button next to a textbox
     * 		Removes the textbox, setting the layout accordingly
     */
    public void delete(View view) { 	
    	//Get the id of the textbox associated with the clicked button
    	int textboxId = getTextboxId(view);
    	
    	//Store all textbox data besides the one with id textboxId
    	storeTextboxData(textboxId);
    	
    	//Switch the layout
    	chooseAndSetLayout(totalTextboxes - 1);
    	
    	//Put stored data into new layout
    	extractTextboxData();
    	
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

package com.tymevotec.stocqt;

import java.util.Arrays;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	// Store a unique message using your package name to avoid conflicts
	// with other apps. This stores the stock symbol I plan on displaying
	public final static String STOCK_SYMBOL = "com.tymevotec.stocqt";
	
	// Manages key valued pairs associated with stock symbols
	private SharedPreferences stockSymbolsEntered;
	
	
	//Table inside the scroll view that holds stock symbols
	// and buttons
	private TableLayout stockTableScrollView;
	
	// Where the user enters a new stock symbol
	private EditText stockSymbolEditText;
	
	//Button that enters a new stock and another that
	// deletes all of them
	Button enterStockSymbolButton;
	Button deleteStocksButton;
	
	// Set up the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Retrieve saved stocks entered by the user.
        // MODE_PRIVATE: Only accessible by your app (Most Common)
        // MODE_WORLD_READABLE: Any app can read (Can OR with the Following)
        //MODE_WORLD_WRITABLE:  Any app can write to this
        stockSymbolsEntered = getSharedPreferences("stockList", MODE_PRIVATE);
        
        // Initialize components
        stockTableScrollView = (TableLayout) findViewById(R.id.stockTableScrollView);
        stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);
        enterStockSymbolButton = (Button) findViewById(R.id.enterStockButton);
        deleteStocksButton = (Button) findViewById(R.id.deleteStocksButton);
        
        // Add ClickoListeners to the button
        enterStockSymbolButton.setOnClickListener(enterStockButtonListener);
        deleteStocksButton.setOnClickListener(deleteStocksButtonListener);
        
        // Add saved stocks to the Stock Scrollview
        updateSavedStockList(null);
    }
    
    // Either adds a new stock or if null is entered the stock
    // List is updated with saved stocks
    private void updateSavedStockList(String newStockSymbol) {
		
    	// Get the saved stocks
    	String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);
    	
    	// Sort the stocks in alphabetical order
    	Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);
    	
    	// If the attribute in alphabetical order 
    	if(newStockSymbol !=null){
    		
    		// enter the new stock in sorted order into the array
    		insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));
    	} else {
    		
    		// display saved stock list
    		for(int i=0; i<stocks.length; ++i){
    			
    			insertStockInScrollView(stocks[i], i);
    		}
    	}
		
	}
    
    private void saveStockSymbol(String newStock){
    	
    	// Used to check if this is a new stock
    	String isTheStockNew = stockSymbolsEntered.getString(newStock, null);
    	
    	// Editor is used to store a key / value pair
    	// I'm using the stock symbol for both, but I could have used company
    	// name or something else
    	SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
    	preferencesEditor.putString(newStock, newStock);
    	preferencesEditor.apply();
    	
    	// If this is a new stock add its components
    	if(isTheStockNew == null){
    		updateSavedStockList(newStock);
    	}
    	         
    }
    
    private void insertStockInScrollView(String stock, int arrayIndex){
     
    	// Get the LayoutInflator service
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	// Use the inflater to inflate a stock row from stock_quote_row.xml
    	View newStockRow = inflater.inflate(R.layout.stock_quote_row, null);
    	 
    	// Create the TextView for the ScrollView Row
    	TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.stockSymbolTextView);
    	 
    	// Add the stock symbol to the TextView
    	newStockTextView.setText(stock);
    	 
    	Button stockQuoteButton = (Button) newStockRow.findViewById(R.id.stockQuoteButton);
    	stockQuoteButton.setOnClickListener(getStockActivityListener);
    	 
    	Button quoteFromWebButton = (Button) newStockRow.findViewById(R.id.quoteFromWebButton);
    	quoteFromWebButton.setOnClickListener(getStockFromWebsiteListener);
    	
    	// Add the new components for the stock to the TableLayout
    	stockTableScrollView.addView(newStockRow, arrayIndex);
    	      
    }
    	 
    public OnClickListener enterStockButtonListener = new OnClickListener(){
    
    	@Override
    	public void onClick(View theView) {
    	 
    		// If there is a stock symbol entered into the EditText
    		// field
    		if(stockSymbolEditText.getText().length() > 0){
    	
    			// Save the new stock and add its components
    			saveStockSymbol(stockSymbolEditText.getText().toString());
    		                 
    			stockSymbolEditText.setText(""); // Clear EditText box
    		
    			// Force the keyboard to close
    			InputMethodManager imm = (InputMethodManager)getSystemService(
    			Context.INPUT_METHOD_SERVICE);
    			imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(), 0);
    		} else {
    	
    		// Create an alert dialog box
    		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	 
    		// Set alert title
    		builder.setTitle(R.string.invalid_stock_symbol);
    		                 
    		// Set the value for the positive reaction from the user
    		// You can also set a listener to call when it is pressed
    		builder.setPositiveButton(R.string.ok, null);
    	                 
    		// The message
    		builder.setMessage(R.string.missing_stock_symbol);
    		
    	// Create the alert dialog and display it
    	AlertDialog theAlertDialog = builder.create();
    	theAlertDialog.show();
    	
    		}
    	
    	}
    };
    	
    private void deleteAllStocks(){
    	
    	// Delete all the stocks stored in the TableLayout
    	stockTableScrollView.removeAllViews();
    	    
    	}
  	 
  	public OnClickListener deleteStocksButtonListener = new OnClickListener(){

    	public void onClick(View v) {
    	            
    		deleteAllStocks();
    	 
    		// Editor is used to store a key / value pairs
    		
    		SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
    		    
    		// Here I'm deleting the key / value pairs
    		preferencesEditor.clear();
    		preferencesEditor.apply();
    		
    	}
    		
   };
   	 
   public OnClickListener getStockFromWebsiteListener = new OnClickListener(){
    
    	public void onClick(View v) {
    	
    		// Get the text saved in the TextView next to the clicked button
    		// with the id stockSymbolTextView
    	
    		TableRow tableRow = (TableRow) v.getParent();
    		TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);
    		String stockSymbol = stockTextView.getText().toString();
    		
    		// The URL specific for the stock symbol
    		String stockURL = getString(R.string.yahoo_stock_url) + stockSymbol;
    		
    		Intent getStockWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(stockURL));
    		             
    		startActivity(getStockWebPage);
    		
    	}
    		
   	};
    		
    public OnClickListener getStockActivityListener = new OnClickListener(){
    		
    	public void onClick(View v) {
    	
    		// Get the text saved in the TextView next to the clicked button
    		// with the id stockSymbolTextView
    		
    		TableRow tableRow = (TableRow) v.getParent();
    		TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);
    		String stockSymbol = stockTextView.getText().toString();
    		
    		// An intent is an object that can be used to start another activity
    		Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);
    			             
    		// Add the stock symbol to the intent
    		intent.putExtra(STOCK_SYMBOL, stockSymbol);
    	     
    		startActivity(intent);
    		 
    	}
    		   
    };
     
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}

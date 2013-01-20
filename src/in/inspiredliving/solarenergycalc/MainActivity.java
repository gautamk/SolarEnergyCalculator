package in.inspiredliving.solarenergycalc;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	protected int getWattHours( int bb , int watts ){
		return bb*watts;
	}
	protected int getBatteryPrice(int watthours){
		return 16*watthours;
	}
	protected int getPhotoVoltPrice(int watthours){
		return 34*watthours;
	}
	
	protected long calculateKVA(int watts){
		double k = watts/1000;
		return Math.round(k/0.7);
	}
	
	protected int roundoffToInverterKVA(long kva){
		if(kva <= 1) kva = 1;
		 if(kva > 1 && kva <= 2) kva = 2;
		 if(kva > 2 && kva <= 3 ) kva = 3;
		 if(kva > 3 && kva <= 5 ) kva = 5;
		 if(kva > 5 && kva <= 10 ) kva = 10;
		 if(kva > 10 && kva <= 15 ) kva = 15;
		 if(kva > 15 && kva <= 20 ) kva = 20;
		return (int) kva;
	}
	
	protected int inverterPrice(int kva ){
		HashMap<Integer, Integer> map= new HashMap<Integer, Integer>();
		map.put(1, 35000);
		map.put(2, 50000);
		map.put(3, 65000);
		map.put(5, 96000);
		map.put(10, 200000);
		map.put(15, 250000);
		map.put(20, 300000);
		return map.get(roundoffToInverterKVA(kva));
	}
	protected int roundoffWattHours(int watthours){
		if(watthours <= 100) watthours = 100;
		 if(watthours > 100 && watthours <= 1000) watthours = 1000;
		 if(watthours > 1000 && watthours <= 4000 ) watthours = 4000;
		 if(watthours > 4000 && watthours <= 8000 ) watthours = 8000;
		 if(watthours > 8000 && watthours <= 12000 ) watthours = 12000;
		 if(watthours > 12000 && watthours <= 20000 ) watthours = 20000;
		 if(watthours > 20000 && watthours <= 30000 ) watthours = 30000;
		 if(watthours > 30000 && watthours <= 40000 ) watthours = 40000;
		 if(watthours > 40000 && watthours <= 60000 ) watthours = 60000;
		 if(watthours > 60000 && watthours <= 80000 ) watthours = 80000;
		return (int) watthours;
	}
	
	protected int getInstallationCost(int watthours){
		HashMap<Integer, Integer> map= new HashMap<Integer, Integer>();
		map.put(100, 3000);
		map.put(1000, 8000);
		map.put(4000, 10000);
		map.put(8000, 15000);
		map.put(12000, 20000);
		map.put(20000, 40000);
		map.put(30000, 75000);
		map.put(40000, 100000);
		map.put(60000, 150000);
		map.put(80000, 200000);
		return map.get(roundoffWattHours(watthours));
	}
	
	protected int getTotalprice(int backuptime,int watts){
		int watthours = getWattHours(backuptime, watts);
		int batteryprice = getBatteryPrice(watthours);
		int photovoltaicPrice = getPhotoVoltPrice(watthours);
		int totalprice = 
				batteryprice + 
				photovoltaicPrice + 
				inverterPrice((int) calculateKVA(watts)) +
				getInstallationCost(watthours)
				;
		return totalprice;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final SeekBar seekBar = (SeekBar) this.findViewById(R.id.seekBarhours);
		seekBar.setMax(24);
	    seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {}
		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {}
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		    	TextView tv = (TextView) findViewById(R.id.hours);
		    	tv.setText(" "+ progress + " Hours");
		    }
	    });
	    
	    final EditText consumption = (EditText) findViewById(R.id.totalconsumption);
	    Button getQuote = (Button) findViewById(R.id.getquote);
	    final EditText priceText = (EditText) findViewById(R.id.priceOfSolution);
	    final Button fowardButton = (Button) findViewById(R.id.forwardbutton);
	    
	    getQuote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int backuptime = seekBar.getProgress();
				int totalconsumption = Integer.parseInt(consumption.getText().toString());
				priceText.setText("Rs."+getTotalprice(backuptime, totalconsumption)+"/-");
			}
		});
	    fowardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int backuptime = seekBar.getProgress();
				int totalconsumption = Integer.parseInt(consumption.getText().toString());
				String price = "Rs."+getTotalprice(backuptime, totalconsumption)+"/-";
				priceText.setText(price);
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
				sendIntent.setData(Uri.parse("sms:"));
				sendIntent.putExtra("sms_body", 
						"The price of your solar solution for " + 
						seekBar.getProgress() + 
						"hours with a consumption of " + 
						consumption.getText().toString() + "watts is " +price  );
				startActivity(sendIntent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

package com.encdcr.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.EventLogTags.Description;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.encdec.R;

public class MainActivity extends Activity {

	Button encButton;
	Button dcrButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		encButton=(Button)findViewById(R.id.buttonEnc);
		encButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				try {
					ConnectivityManager conMgr = (ConnectivityManager)getSystemService(MainActivity.CONNECTIVITY_SERVICE);
					NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
					if (netInfo == null){
						Toast.makeText(getApplicationContext(), "Please check your internet connection ...", Toast.LENGTH_LONG).show();
					}else
					{
						Intent intent = new Intent(MainActivity.this,EncryptionActivity.class);
						startActivity(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		dcrButton=(Button)findViewById(R.id.buttonDcr);
		dcrButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,DecryptionActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

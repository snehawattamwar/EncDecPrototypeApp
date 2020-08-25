package com.encdcr.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.example.admin.encdec.R;

public class TextViewActivity extends Activity {

	TextView decryptedText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_view);
		decryptedText=(TextView) findViewById(R.id.decryptedText);
		Log.d("grp6","onCreate");
		Intent in = getIntent();
		byte [] filedata= in.getByteArrayExtra("FileData");
		Log.d("grp6","filedata.length"+filedata.length);
		String fileString = new String(filedata);
		Log.d("grp6","filedata: "+fileString);
		decryptedText.setText(fileString);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text_view, menu);
		return true;
	}

}

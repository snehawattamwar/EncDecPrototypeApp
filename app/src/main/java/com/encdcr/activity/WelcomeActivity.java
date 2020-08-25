package com.encdcr.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.admin.encdec.R;

public class WelcomeActivity extends Activity {

	Button start;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);


		start=(Button)findViewById(R.id.buttonStart);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					/*AssetManager assetManager = getApplicationContext().getAssets();
					InputStream in= null;
					in = assetManager.open("pskey");   // if files resides inside the "Files" directory itself
					byte [] fileData = new byte [in.available()];
					Log.d("grp6 ", "fileData: "+fileData.length);
					int bytesRead = 0;
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					while((bytesRead = in.read(fileData)) != -1) {
						bao.write(fileData, 0, bytesRead);
					}
					byte[] data = bao.toByteArray();
					File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "KeyData");
					if(!folder.exists())
						folder.mkdirs();
					File file = new File(folder.getAbsolutePath() + File.separator + "pskey");
					Log.d("grp6", "Final path of pskey :"+file.getAbsolutePath());
					FileOutputStream fout = new FileOutputStream(file);
					fout.write(data);
					fout.flush();
					fout.close();*/
					Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

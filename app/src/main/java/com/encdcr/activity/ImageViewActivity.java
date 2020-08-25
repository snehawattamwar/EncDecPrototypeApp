package com.encdcr.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import com.example.admin.encdec.R;

public class ImageViewActivity extends Activity {

	ImageView decryptedImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view);
		decryptedImage= (ImageView) findViewById(R.id.decryptImage);
		Log.d("grp6","onCreate");
		Intent in = getIntent();
		byte [] filedata= in.getByteArrayExtra("FileData");
		Log.d("grp6","filedata.length"+filedata.length);
		Bitmap bitmap = BitmapFactory.decodeByteArray(filedata, 0, filedata.length);
		decryptedImage.setImageBitmap(bitmap);
		Log.d("grp6","after setImageBitmap");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_view, menu);
		return true;
	}

}

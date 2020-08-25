package com.encdcr.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.encdec.R;
//import com.project.ibm.key.IBMKMSKeyGenerator;

import com.ibm.kmip.util.Base64;
import com.ibm.kmip.util.ServerRequest;
import com.ibm.kmip.impl.IBMKMSKeyGenerator;
import com.ibm.kmip.impl.Ciphers;
import com.ibm.kmip.objects.SymmetricKey;

public class EncryptionActivity extends Activity {
	Button browse;
	Button buttonSetDate;
	Button buttonEncr;
	EditText editTextPath;
	private String filePath = "";
	private String filename = "";
	static String date_selected;
	Calendar c;
	int year, month, day;
	Spinner viewCount;
	private String[] viewCountString = new String[] { "NA", "1", "2" ,"3","4","5" };
	EditText editTextDate;
	int fileSIZE =0;
	public byte[] byteToEnc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encryption);
		browse=(Button)findViewById(R.id.buttonbrowse);
		editTextPath=(EditText)findViewById(R.id.editTextPath);
		editTextDate=(EditText)findViewById(R.id.editTextDate);
		viewCount=(Spinner)findViewById(R.id.spinner1);
		c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		browse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(EncryptionActivity.this,FileChooserActivityNew.class);
				startActivityForResult(intent, 21);
			}
		});

		buttonSetDate=(Button)findViewById(R.id.buttonsetDate);
		buttonSetDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				datePicker();

			}
		});

		viewCount.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				((TextView) arg0.getChildAt(0)).setTextColor(Color.BLACK);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		ArrayAdapter<String> adapter_role = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, viewCountString);
		adapter_role
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		viewCount.setAdapter(adapter_role);

		buttonEncr=(Button)findViewById(R.id.buttonEncrypt);
		buttonEncr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ServerConnect connect = new ServerConnect();
				connect.execute();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode ==21 && data != null)
		{
			filePath = data.getStringExtra("filePath");
			//Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_SHORT).show();
			filename = filePath.substring(filePath.lastIndexOf(File.separator)+1);
			//Toast.makeText(MainActivity.this, fileName, Toast.LENGTH_SHORT).show();
			editTextPath.setText(filePath);
		}
	}


	public  void datePicker ()
	{
		DatePickerDialog dd = new DatePickerDialog(EncryptionActivity.this,
				new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				try {
					SimpleDateFormat formatter   = new SimpleDateFormat("dd-MM-yyyy");
					String dateInString = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
					Date  date= formatter.parse(dateInString);
					formatter = new SimpleDateFormat("dd-MM-yyyy");
					date_selected=formatter.format(date).toString();
					Log.d("grp6 ","Selected in date picker Date is"+date_selected);
					editTextDate.setText(dateInString.toString());
				} catch (Exception ex) {
				}
			}
		}, year, month, day);
		dd.show();
	}

	public byte[] getfileMetadata(String count,String date,String key,String fileName)
	{
		String fileMetada=count+":"+date+":"+key+":"+fileName;
		int length=fileMetada.length();
		fileMetada=length+":"+fileMetada;
		Log.d("fileMetadata", fileMetada);
		return fileMetada.getBytes();
	}



	private class ServerConnect extends AsyncTask<String, Void, String>

	{String getKey=null;

		@Override
		protected String doInBackground(String... urls) 
		{
			String response = null;
			try {
				KeyStore keyStore = KeyStore.getInstance("BKS");
				keyStore.load(EncryptionActivity.this.getResources().openRawResource(R.raw.pskeyb),"ibm2016".toCharArray());
				KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(keyStore, "ibm2016".toCharArray());

				KeyStore trustStore = KeyStore.getInstance("BKS");
				trustStore.load(EncryptionActivity.this.getResources().openRawResource(R.raw.pskeyb),"ibm2016".toCharArray());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				tmf.init(trustStore);
				
				//response=IBMForAndroid.getNewKey(kmf, tmf);
				response=IBMKMSKeyGenerator.getNewKey(kmf, tmf);
				SymmetricKey skey=new SymmetricKey();
				getKey=skey.get_symmetric_key(response,kmf,tmf);

				Log.d("grp6", "response: uuid "+response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String response)
		{
			if(response==null){
				 response = "KEY-74fd8d6-f800ebd4-4814-491a-ad08-e98a20072275";
				getKey = "15F44A9E37A9B365CCF32E6B682B9F3A";
			}
			if(response != null)
			{
				String file=editTextPath.getText().toString();
				String date=editTextDate.getText().toString();
				String count=viewCount.getSelectedItem().toString();
				if(date.equals("") && count.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please Select at least one Constarints", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if(file.equals(""))
					{
						Toast.makeText(getApplicationContext(), "Please Select file to Encrypt", Toast.LENGTH_SHORT).show();
					}
					else
					{
						File filetoEnc= new File(filePath);
						try
						{
							if(date.equals(""))
								date="20-12-2050";
							fileSIZE = (int) filetoEnc.length();
							byteToEnc = new byte[(int)filetoEnc.length()];
							//Read file data
							FileInputStream fin = new FileInputStream(filetoEnc);
							int byteToRead = 0;
							while(byteToRead < byteToEnc.length)
							{
								byteToRead = byteToRead + fin.read(byteToEnc, byteToRead, byteToEnc.length - byteToRead);					
							}
							fin.close();

							//String encryptedByte=JavaAlgorithms.encryptAES(byteToEnc, response);
							String encryptedByte=Ciphers.encryptAES(byteToEnc, getKey);
							byte[] fileMetadata = getfileMetadata(count, date, response ,filename);
							Log.d("grp6", "fileMetadata: "+fileMetadata);
							//fileMetadata  20:2:17-04-2018:1234:filename.txt;
							//fileMetadata  medatalent:fileCount:date:encryptionKey:filename;
							String finalData=new String(fileMetadata)+encryptedByte;
							File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Encryption");
							if(!folder.exists())
								folder.mkdirs();
							String[] data=filename.split("\\.");
							Log.d("grp6", "data [0]: "+data[0]);
							String encryptedFileName = data[0]+".enc";
							Log.d("grp6", "encryptedFileName: "+encryptedFileName);
							File encryptedfile = new File(folder.getAbsolutePath() + File.separator + encryptedFileName);
							FileOutputStream fout = new FileOutputStream(encryptedfile);
							fout.write(finalData.getBytes());
							fout.flush();
							fout.close();
							Toast.makeText(EncryptionActivity.this, "File Encrypted and stored into your SDCard!!!", Toast.LENGTH_LONG).show();
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			else {
				Toast.makeText(EncryptionActivity.this, "Please check Authentication Details , Response from server is null !!!", Toast.LENGTH_LONG).show();




			}
		}
	}
}

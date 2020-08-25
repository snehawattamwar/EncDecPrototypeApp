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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.encdec.R;

import com.ibm.kmip.util.Base64;
import com.ibm.kmip.util.ServerRequest;
import com.ibm.kmip.impl.IBMKMSKeyGenerator;
import com.ibm.kmip.impl.Ciphers;
import com.ibm.kmip.objects.SymmetricKey;

public class DecryptionActivity extends Activity {


	Button browse;
	Button buttonDcrypt;
	private String filePath = "";
	EditText decpath;
	int fileSIZE = 0;
	public byte[] byteOfFile;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dcryption);
		decpath = (EditText) findViewById(R.id.editTextDecPath);
		browse = (Button) findViewById(R.id.buttonbrowseDec);
		browse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(DecryptionActivity.this, FileChooserActivityNew.class);
				startActivityForResult(intent, 21);

			}
		});
		buttonDcrypt = (Button) findViewById(R.id.buttonDcrypt);
		buttonDcrypt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String filepath=decpath.getText().toString();

				if(filepath.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please Select file to Dcrypt", Toast.LENGTH_SHORT).show();
				}
				else
				{
					try {
						//get encrypted file
						File file= new File(filepath);
						fileSIZE = (int) file.length();
						byteOfFile = new byte[(int)file.length()];

						FileInputStream	fin = new FileInputStream(file);
						int byteToRead = 0;
						while(byteToRead < byteOfFile.length)
						{
							byteToRead = byteToRead + fin.read(byteOfFile, byteToRead, byteOfFile.length - byteToRead);
						}
						fin.close();
						//get length of meta-data
						byte[] readfilMetaDataLength=new byte[2];

						System.arraycopy(byteOfFile, 0, readfilMetaDataLength, 0, 2);

						Log.d("grp6", "readfilMetaDataLength: "+new String(readfilMetaDataLength));

						int metaDataLenght=Integer.parseInt(new String(readfilMetaDataLength));
						// now actual reading of meta-data
						byte [] getfileMetadata = new byte[metaDataLenght];
						System.arraycopy(byteOfFile, 3, getfileMetadata, 0, metaDataLenght);

						Log.d("grp6","fileMetadata"+new String(getfileMetadata));
						String fileMetadata [] = new String(getfileMetadata).split(":");

						String count=fileMetadata[0];
						String date=fileMetadata[1];
						String key=fileMetadata[2];
						String fileName=fileMetadata[3];
						Log.d("grp6", "count: "+count+" date: "+date+" key: "+key+" fileName: "+fileName);
						String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
						Date date1 = sdf.parse(date);
						Date date2 = sdf.parse(timeStamp);
						Log.d("date1", date1+"");
						Log.d("date2", date2+"");
						Log.d("grp6","finalFileMetaData"+count+" "+date+" "+key+" "+fileName);
						// we checking if count is NA then file can open unlimited time but up-to encrypted date
						if (count.equals("NA"))
						{
							if((date1.after(date2) || date1.equals(date2)))
							{
								Log.d("grp6", "in if ");
								int length=byteOfFile.length - (3+metaDataLenght);
								byte[] byteToDecrypt = new byte[length];
								System.arraycopy(byteOfFile, 3+metaDataLenght, byteToDecrypt, 0,length);
								Log.d("grp6", "byteToDecryptLenth: "+byteToDecrypt.length+"");
								//byte [] fileData=JavaAlgorithms.decryptAES(new String(byteToDecrypt), key);

							}


						}//here, we checking count and date of encryption

						ServerConnect connect = new ServerConnect();
						connect.execute(key);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 21 && data != null) {
			filePath = data.getStringExtra("filePath");
			//Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_SHORT).show();
			//fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1);
			//Toast.makeText(MainActivity.this, fileName, Toast.LENGTH_SHORT).show();
			decpath.setText(filePath);
		}
	}

	private String getPath() {
		//return Environment.getExternalStorageDirectory().getAbsolutePath();
		PackageManager m = getPackageManager();
		String s = getPackageName();
		try {
			PackageInfo p = m.getPackageInfo(s, 0);
			s = p.applicationInfo.dataDir;
			Log.d("path", s);
		} catch (PackageManager.NameNotFoundException e) {
			Log.w("yourtag", "Error Package name not found ", e);
		}
		return s;
	}


	public void OpenFile(String filename, byte[] fileData) {

		if (filename.toString().contains(".doc") || filename.toString().contains(".docx")) {
			// Word document
			//intent.setDataAndType(uri, "application/msword");
		} else if (filename.toString().contains(".pdf")) {
			// PDF file
			//intent.setDataAndType(uri, "application/pdf");
		} else if (filename.toString().contains(".ppt") || filename.toString().contains(".pptx")) {
			// Powerpoint file
			//intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		} else if (filename.toString().contains(".xls") || filename.toString().contains(".xlsx")) {
			// Excel file
			//intent.setDataAndType(uri, "application/vnd.ms-excel");
		} else if (filename.toString().contains(".zip") || filename.toString().contains(".rar")) {
			// WAV audio file
			//intent.setDataAndType(uri, "application/x-wav");
		} else if (filename.toString().contains(".rtf")) {
			// RTF file
			//intent.setDataAndType(uri, "application/rtf");
		} else if (filename.toString().contains(".wav") || filename.toString().contains(".mp3")) {
			// WAV audio file
			//intent.setDataAndType(uri, "audio/x-wav");
		} else if (filename.toString().contains(".gif")) {
			// GIF file
			//intent.setDataAndType(uri, "image/gif");
		} else if (filename.toString().contains(".jpg") || filename.toString().contains(".jpeg") || filename.toString().contains(".png")) {
			// JPG file
			Log.d("grp6", "putExtra FileData: " + fileData.length);
			Intent in = new Intent(DecryptionActivity.this, ImageViewActivity.class);
			in.putExtra("FileData", fileData);
			startActivity(in);

		} else if (filename.toString().contains(".txt")) {
			Log.d("grp6", "putExtra FileData: " + fileData.length);
			Intent in = new Intent(DecryptionActivity.this, TextViewActivity.class);
			in.putExtra("FileData", fileData);
			startActivity(in);

		} else if (filename.toString().contains(".3gp") || filename.toString().contains(".mpg") ||
				filename.toString().contains(".mpeg") || filename.toString().contains(".mpe") || filename.toString().contains(".mp4") || filename.toString().contains(".avi")) {
			// Video files
			//intent.setDataAndType(uri, "video/*");
		} else {
			//intent.setDataAndType(uri, "*/*");
		}
		try {
			//startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Unsupported File", Toast.LENGTH_LONG).show();
		}
	}


	public byte[] getfileMetadata(String count, String date, String key, String fileName) {

		String fileMetada = count + ":" + date + ":" + key + ":" + fileName;

		int length = fileMetada.length();

		fileMetada = length + ":" + fileMetada;

		Log.d("fileMetadata", fileMetada);

		return fileMetada.getBytes();


	}


	private class ServerConnect extends AsyncTask<String, Void, String>

	{
		String getKey = null;

		@Override
		protected String doInBackground(String... urls) {
			String response = urls[0];
			try {
				KeyStore keyStore = KeyStore.getInstance("BKS");
				keyStore.load(DecryptionActivity.this.getResources().openRawResource(R.raw.pskeyb), "ibm2016".toCharArray());
				KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(keyStore, "ibm2016".toCharArray());

				KeyStore trustStore = KeyStore.getInstance("BKS");
				trustStore.load(DecryptionActivity.this.getResources().openRawResource(R.raw.pskeyb), "ibm2016".toCharArray());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				tmf.init(trustStore);

				//response=IBMForAndroid.getNewKey(kmf, tmf);
				//response = IBMKMSKeyGenerator.getNewKey(kmf, tmf);
				SymmetricKey skey = new SymmetricKey();
				if(response==null)
					response="KEY-74fd8d6-f800ebd4-4814-491a-ad08-e98a20072275";
				getKey = skey.get_symmetric_key(response, kmf, tmf);

				Log.d("grp6", "response: get " + getKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return getKey;
		}

		@Override
		protected void onPostExecute(String response) {
	//here, we checking count and date of encryptionif (count.equals("NA")) {
			String filepath=decpath.getText().toString();
			if(response==null){
				response="15F44A9E37A9B365CCF32E6B682B9F3A";
			}
			Log.d("grp6", "response should be get  "+response);
			if(filepath.equals(""))
			{
				Toast.makeText(getApplicationContext(), "Please Select file to Dcrypt", Toast.LENGTH_SHORT).show();
			}
			else
			{
				try {
					//get encrypted file
					File file= new File(filepath);
					fileSIZE = (int) file.length();
					byteOfFile = new byte[(int)file.length()];

					FileInputStream	fin = new FileInputStream(file);
					int byteToRead = 0;
					while(byteToRead < byteOfFile.length)
					{
						byteToRead = byteToRead + fin.read(byteOfFile, byteToRead, byteOfFile.length - byteToRead);
					}
					fin.close();
					//get length of meta-data
					byte[] readfilMetaDataLength=new byte[2];

					System.arraycopy(byteOfFile, 0, readfilMetaDataLength, 0, 2);

					Log.d("grp6", "readfilMetaDataLength: "+new String(readfilMetaDataLength));

					int metaDataLenght=Integer.parseInt(new String(readfilMetaDataLength));
					// now actual reading of meta-data
					byte [] getfileMetadata = new byte[metaDataLenght];
					System.arraycopy(byteOfFile, 3, getfileMetadata, 0, metaDataLenght);

					Log.d("grp6","fileMetadata"+new String(getfileMetadata));
					String fileMetadata [] = new String(getfileMetadata).split(":");

					String count=fileMetadata[0];
					String date=fileMetadata[1];
					String key=fileMetadata[2];
					String fileName=fileMetadata[3];
					Log.d("grp6", "count: "+count+" date: "+date+" key: "+key+" fileName: "+fileName);
					String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					Date date1 = sdf.parse(date);
					Date date2 = sdf.parse(timeStamp);
					Log.d("date1", date1+"");
					Log.d("date2", date2+"");
					Log.d("grp6","finalFileMetaData"+count+" "+date+" "+key+" "+fileName);
					// we checking if count is NA then file can open unlimited time but up-to encrypted date
					if (count.equals("NA"))
					{
						if((date1.after(date2) || date1.equals(date2)))
						{
							Log.d("grp6", "in if ");
							int length=byteOfFile.length - (3+metaDataLenght);
							byte[] byteToDecrypt = new byte[length];
							System.arraycopy(byteOfFile, 3+metaDataLenght, byteToDecrypt, 0,length);
							Log.d("grp6", "byteToDecryptLenth: "+byteToDecrypt.length+"");
							//byte [] fileData=JavaAlgorithms.decryptAES(new String(byteToDecrypt), key);
							byte [] fileData=Ciphers.decryptAES(new String(byteToDecrypt), response);
							Log.d("grp6", "decryptedfile fileName: "+fileName);
							OpenFile(fileName, fileData);
						}
						else {
							Toast.makeText(getApplicationContext(), "File not Decrypt", Toast.LENGTH_LONG).show();
						}

					}//here, we checking count and date of encryption
					else if ( Integer.parseInt(count)> 0 && (date1.after(date2) || date1.equals(date2)))
					{
						Log.d("grp6", "in else if ");
						int length=byteOfFile.length - (3+metaDataLenght);
						byte[] byteToDecrypt = new byte[length];
						// extracting exact bytes of file and meta-data
						System.arraycopy(byteOfFile, 3+metaDataLenght, byteToDecrypt, 0,length);

						//byte [] fileData=JavaAlgorithms.decryptAES(new String(byteToDecrypt), key);
						byte [] fileData=Ciphers.decryptAES(new String(byteToDecrypt), response);

						//again encryptedByte
						//String encryptedByte=JavaAlgorithms.encryptAES(fileData, key);
						String encryptedByte=Ciphers.encryptAES(fileData, response);

						// we updating the file count with -1;
						String updatedCount=String.valueOf(Integer.parseInt(count)-1);
						Log.d("grp6", "updatedCount: "+updatedCount);
						//
						byte[] fileMetadataforEncy = getfileMetadata(updatedCount, date, key ,fileName);
						String finalData=new String(fileMetadataforEncy)+encryptedByte;
						String[] data=fileName.split("\\.");
						String encryptedFileName = data[0]+".enc";
						Log.d("grp6", "again encryptedFileName: "+encryptedFileName);
						Log.d("grp6", "again en cryptedFilepath: "+file.toString());
						FileOutputStream fout = new FileOutputStream(file);
						fout.write(finalData.getBytes());
						fout.flush();
						fout.close();
						OpenFile(fileName, fileData);

					}
					else
					{
						Toast.makeText(getApplicationContext(), "File not Decrypt", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
					}

			}

	}



//to save in  .DEC folder to open file
/*String decryptFilepath=getPath();
//	Log.d("decryptFilepath", decryptFilepath);
	 */
	/*File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+".DEC");
	if(!folder.exists())
		folder.mkdir();

	File decryptedfile = new File(folder.getAbsolutePath() + File.separator + fileName);
	FileOutputStream fout = new FileOutputStream(decryptedfile);
	fout.write(fileData);
	fout.flush();
	fout.close();*/
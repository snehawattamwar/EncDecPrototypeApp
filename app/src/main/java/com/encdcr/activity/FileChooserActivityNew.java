package com.encdcr.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.admin.encdec.R;

public class FileChooserActivityNew extends Activity {
    /** Called when the activity is first created. */
    private File currentDir;
    private FileArrayAdapter adapter;
    private ListView listView;
    private ImageButton backImageButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_chooser_layout);
        listView = (ListView)findViewById(R.id.file_chooser_layout_allFilesListView);
        backImageButton = (ImageButton)findViewById(R.id.file_chooser_layout_backImageButton);
        currentDir = new File("/sdcard/");
        fill(currentDir);
        
        
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Option o = adapter.getItem(arg2);
		        if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
		                currentDir = new File(o.getPath());
		                fill(currentDir);
		        }
		        else
		        {
		            onFileClick(o);
		        }
				
			}
		});
        
        backImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FileChooserActivityNew.this.finish();
			}
		});
        
    }
    private void fill(File f)
    {
        File[]dirs = f.listFiles();
         this.setTitle("Current Dir: "+f.getName());
         List<Option>dir = new ArrayList<Option>();
         List<Option>fls = new ArrayList<Option>();
         try{
             for(File ff: dirs)
             {
                if(ff.isDirectory())
                    dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
                else
                {
                    fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
                }
             }
         }catch(Exception e)
         {
              
         }
         Collections.sort(dir);
         Collections.sort(fls);
         dir.addAll(fls);
         if(!f.getName().equalsIgnoreCase("sdcard"))
             dir.add(0,new Option("..","Parent Directory",f.getParent()));
         adapter = new FileArrayAdapter(FileChooserActivityNew.this,R.layout.file_view,dir);
         listView.setAdapter(adapter);
    }
    /*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
                currentDir = new File(o.getPath());
                fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }*/
    private void onFileClick(Option o)
    {
    	Intent intent = new Intent();
        // put the message to return as result in Intent
        intent.putExtra("filePath",o.getPath());
        // Set The Result in Intent
        setResult(21,intent);
        Toast.makeText(this, "File Clicked: "+o.getPath(), Toast.LENGTH_SHORT).show();
        finish();
    }

}

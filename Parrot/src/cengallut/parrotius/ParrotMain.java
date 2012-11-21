package cengallut.parrotius;

import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.annotation.TargetApi;
import android.app.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

@TargetApi(11)
public class ParrotMain extends Activity {
	private Button btnStart;
	private Button btnAdjust;
	private TextView tvStatus;
	
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private String mFileName;
	private Timer mCycler;
	private Handler mHandler = new Handler();
	
	private boolean isEchoing = false;
	private int SEGMENT_LENGTH = 1000; // milliseconds
	private final String LOG_TAG = "Parrotius";
	private String FILE_EXTENSION = "/parrot-audio-buffer.3gp";
	private int CYCLE;
	private int COUNT = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parrot_main);
        
        // ActionBar init
        ActionBar bar = this.getActionBar();
        bar.setDisplayShowTitleEnabled(true);
        
        uiInit();
        

        //mRecorder = new MediaRecorder();
        //mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        
        
        
        

    }	// END OnCreate()
    
    @Override
    public void onPause(){
    	super.onPause();
    	exitSequence();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	exitSequence();
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	exitSequence();
    }
    
    private void exitSequence(){
    	/*
    	 * To be called when the Parrotius exits.
    	 * Must terminate all timer and thread
    	 * Delete all residue file in the Storage
    	 */
    	
    	mCycler.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_parrot_main, menu);
        return true;
    }
    
    public ParrotMain(){
    	mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += FILE_EXTENSION;
    }
    
    private void uiInit(){
    	btnStart = (Button)findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		Toast.makeText(ParrotMain.this, "Start is clicked", Toast.LENGTH_SHORT).show();
        		
        		if (isEchoing){
        			mCycler.cancel();
        			isEchoing = false;
        		} else {
        			mCycler = new Timer();
        			mCycler.schedule(
        	        		new TimerTask(){
        						@Override
        						public void run() {
        							COUNT ++;
        							mHandler.post(new Runnable(){
										public void run() {
											tvStatus.setText("count: " + COUNT);
										}
        							});
        						}
        	        		},0,SEGMENT_LENGTH);
        	        isEchoing = true;
        		}
        	}
        });
        
        btnAdjust = (Button)findViewById(R.id.btn_adjust);
        btnAdjust.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		Toast.makeText(ParrotMain.this, "Adjust is clicked", Toast.LENGTH_SHORT).show();
        	}
        });
        
        tvStatus = (TextView)findViewById(R.id.tv_status);
        
    }
}

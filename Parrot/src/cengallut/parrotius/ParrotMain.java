package cengallut.parrotius;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.annotation.TargetApi;
import android.app.*;
import android.util.Log;
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
	
	private boolean isEchoing;
	private boolean isRecording;
	private boolean isPlaying;
	private final int SEGMENT_LENGTH = 5000; // milliseconds
	private final String LOG_TAG = "Parrotius";
	private final String FILE_EXTENSION = "/parrot-audio-buffer.3gp";
	private int COUNT;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parrot_main);
        
        // ActionBar init
        ActionBar bar = this.getActionBar();
        bar.setDisplayShowTitleEnabled(true);
        
        COUNT = 0;
        isEchoing = false;
        isPlaying = false;
        isRecording = false;
        
        uiInit();
        
    }	// END OnCreate()
    
    @Override
    public void onPause(){
    	super.onPause();
    	exitSequence();
    }
    
    private void exitSequence(){
    	/*
    	 * To be called when the Parrotius exits.
    	 * Must terminate all timer and thread
    	 * Delete all residue file in the Storage
    	 */
    	if (mCycler != null){
    		mCycler.cancel();
    	}
    	
    	if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
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
        			exitSequence();
        			
        			isEchoing = false;
        			isRecording = false;
        			isPlaying = false;
        			tvStatus.setText("count: " + COUNT);
        		} else {
        			mCycler = new Timer();
        			mCycler.schedule(new Conductor(),0,SEGMENT_LENGTH);
        	        isEchoing = true;
        		}
        	}
        });
        
        btnAdjust = (Button)findViewById(R.id.btn_adjust);
        btnAdjust.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		Toast.makeText(ParrotMain.this, "Adjust is clicked", Toast.LENGTH_SHORT).show();
        		COUNT = 0;
        		tvStatus.setText("count: " + COUNT);
        	}
        });
        
        tvStatus = (TextView)findViewById(R.id.tv_status);
        
    }
    
    public class Conductor extends TimerTask{
    	private static final String c = "count: ";
    	private static final String rec = " | RECORDING";
    	private static final String pl = " | PLAYING";
    	private static final String rdy = " | READYING";
    	
    	public void run(){
    		COUNT ++;
    		statusUpdate(0);
    		
    		// Start or stop audio activity
    		if (COUNT > 1){
    			
    			if (!isRecording && !isPlaying){
    				// this is at the start of the process
    				isRecording = !isRecording;
    				onRecord(isRecording);
    				statusUpdate(1);
    			} else {
    				isRecording = !isRecording;
    				onRecord(isRecording);
    				isPlaying = !isPlaying;
    				onPlay(isPlaying);
    				
    				if (isRecording){
    					statusUpdate(1);
    				} else {
    					statusUpdate(2);
    				}
    			}
    			
    		} else {
    			statusUpdate(3);
    		}
    	}
    	
    	private void statusUpdate(int stat){
    		switch (stat){
    		case 0:
    			mHandler.post(new Runnable(){
					public void run(){
						tvStatus.setText(c + COUNT);
					}
				});
    			return;
    		case 1:
    			mHandler.post(new Runnable(){
					public void run(){
						tvStatus.setText(c + COUNT + rec);
					}
				});
    			return;
    		case 2:
    			mHandler.post(new Runnable(){
					public void run(){
						tvStatus.setText(c + COUNT + pl);
					}
				});
    			return;
    		case 3:
    			mHandler.post(new Runnable(){
					public void run(){
						tvStatus.setText(c + COUNT + rdy);
					}
				});
    			return;
    		}
    	}
    }
    
    // ---------- BEGIN audio methods ---------- //
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    // ---------- END audio methods ---------- //
}

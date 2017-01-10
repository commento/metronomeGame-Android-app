package com.example.riccardo.metronomegame;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class CentralActivity extends AppCompatActivity {

    private static final String LOG_TAG = "RIC";
    private static final String DEFAULT_BPM = "120";
    public static int DEFAULT_BPM_IN_MS = 500;
    public static int DEFAULT_SAMPLE_RATE = 8000;
    public static int GAME_DURATION_TICK = 34;
    public static long RESET_DURATION = 30000;


    private final double duration = 0.1; // seconds
    private final int sampleRate = DEFAULT_SAMPLE_RATE;
    private final double numSamples = duration * (double)sampleRate;
    private final double sample[] = new double[(int)numSamples];
    int ms = DEFAULT_BPM_IN_MS;
    private final byte generatedSnd[] = new byte[2 * (int)numSamples];

    Runnable runnable;
    Handler handler = new Handler();
    AudioTrack audioTrack;
    BpmCalculator bpmCalculator;
    Timer timer;
    String bpmString = DEFAULT_BPM;

    int jj = 0;
    boolean isPlaying = true;



    /*************************/
    /*AUDIO PLAYBACK SECTION */
    /*************************/


    private void do_loopback() {

        runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(isPlaying){
                        genTone();
                        playSound();
                        String st = Integer.toString(jj);
                        Log.i("RIC: TICK", st);

                        RadioButton radioButton = (RadioButton) findViewById(R.id.radioButton);
                        if (radioButton != null) {
                            if(radioButton.isChecked()){
                                radioButton.setChecked(false);
                            }else{
                                radioButton.setChecked(true);
                            }
                        }

                        jj++;
                        TextView initCount = (TextView) findViewById(R.id.countDownView);
                        if(initCount!=null){
                            if(jj< 4){
                                initCount.setText(Integer.toString(jj));
                            }else if(jj==4){
                                initCount.setText("GO!");
                                handleTouch();
                            }else if(jj==5){
                                initCount.setText("");
                            }
                        }


                        if(jj > GAME_DURATION_TICK){
                            isPlaying = false;

                            handler.removeCallbacks(runnable);

                            //intent to Results Activity
                            Intent intent = new Intent(CentralActivity.this, Results.class);
                            TextView editText = (TextView) findViewById(R.id.bpmTextView);
                            String message = editText.getText().toString();

                            if(!message.equals("tap again")){
                                handleTouch();
                            }

                            intent.putExtra("resultBPM", message);
                            intent.putExtra("startBPM", bpmString);
                            startActivity(intent);

                            jj = 0;
                        }
                        }

                        handler.postDelayed(this, ms);
                    }
                };
        runnable.run();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            bpmString = extras.getString("startBPM");
            ms = 60000 / Integer.parseInt(bpmString);
        }
        isPlaying = true;
        do_loopback();

        bpmCalculator = new BpmCalculator();

    }


    @Override
    protected void onStart() {
        super.onStart();
        initialize();

    }


    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }


        bpmCalculator.clearTimes();
        super.onDestroy();
    }

    private void initialize() {
        TextView bpmTextView = (TextView) findViewById(R.id.bpmTextView);
        if (bpmTextView != null) {
            bpmTextView.setText("TAP!");
        }
        setupTouchListener();
    }

    /*************************************/
    /*TAP TEMPO TIMER and BUTTON SECTION */
    /*************************************/

    private void setupTouchListener() {
        View tapButton = findViewById(R.id.tapButtonView);
        if (tapButton != null) {
            tapButton.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        handleTouch();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void handleTouch() {
        bpmCalculator.recordTime();
        audioTrack.release();
        restartResetTimer();
        updateView();
    }

    private void updateView() {
        String displayValue;

        if (bpmCalculator.times.size() >= 2) {
            int bpm = bpmCalculator.getBpm();
            displayValue = Integer.valueOf(bpm).toString();
        } else {
            displayValue = "tap again";
        }

        TextView bpmTextView = (TextView) findViewById(R.id.bpmTextView);
        bpmTextView.setText(displayValue);
    }

    private void restartResetTimer() {
        stopResetTimer();
        startResetTimer();
    }

    private void startResetTimer() {
        timer = new Timer("reset-bpm-calculator", true);
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                stopResetTimer();
                bpmCalculator.clearTimes();

            }
        }, RESET_DURATION);
    }

    private void stopResetTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }


    /*************************/
    /*AUDIO SETUP SECTION    */
    /*************************/

    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>

    void genTone(){
        // fill out the array

        try{
            audioTrack.release();
        }catch (Exception e)
        {
            Log.i(LOG_TAG, "released an un initialized audio track");
        }

        //tick creation
        sample[0] = 1;

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound(){
         audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        try{
        audioTrack.play();
        }catch (Exception e)
        {
            Log.i(LOG_TAG, "called an un initialized audio track");
        }
    }


}

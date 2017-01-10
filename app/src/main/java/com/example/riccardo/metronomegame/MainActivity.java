package com.example.riccardo.metronomegame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnFocusChangeListener;
import android.util.Log;
import android.content.Intent;
import android.widget.TextView.OnEditorActionListener;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "RIC";

    //string and msValue of StartBPM - Default is 120 BPM
    int ms = 500;
    String num = "120";
    boolean isPlaying = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initialize() {
        setupBpmStartListener();
        setupStartButtonListener();
    }

    private void setupBpmStartListener() {
        final EditText editText = (EditText) findViewById(R.id.startBpmNum);
        if (editText != null) {
//            editText.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (!hasFocus) {
//
//                        num = editText.getText().toString();
//                    }
//                }
//            });
            editText.setOnEditorActionListener( new OnEditorActionListener(){

                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_NULL
                            && event.getAction() == KeyEvent.ACTION_DOWN) {
                        return true;
                    }
                    num = editText.getText().toString();
                    Log.i(LOG_TAG, num);
                    return false;
                }
            });

        }
    }


    private void setupStartButtonListener() {
        View tapButton = findViewById(R.id.startButton);
        if (tapButton != null) {
            tapButton.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        //intent to CentralActivity
                        Intent intent = new Intent(MainActivity.this, CentralActivity.class);
                        intent.putExtra("startBPM", num);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

}

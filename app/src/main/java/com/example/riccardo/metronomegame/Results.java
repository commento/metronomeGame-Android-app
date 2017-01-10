package com.example.riccardo.metronomegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import static java.lang.StrictMath.abs;

public class Results extends Activity {

    private static final String LOG_TAG = "RIC";
    String startString = "120";
    String resultString = "120";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            startString = extras.getString("startBPM");
            resultString = extras.getString("resultBPM");

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        setupBackButtonListener();

        if (resultString.equals("tap again")) {

            TextView resultText = (TextView) findViewById(R.id.textResultView);
            TextView percText = (TextView) findViewById(R.id.percentView);

            percText.setText("0%");
            resultText.setText("you have not tapped at ALL");

        } else {

            Log.i(LOG_TAG, startString);
            Log.i(LOG_TAG, resultString);

            int difference = abs(Integer.parseInt(startString) - Integer.parseInt(resultString));

            Log.i(LOG_TAG, Integer.toString(difference));

            int percentage = (int) (((double) (Integer.parseInt(startString)) / ((double) ( Integer.parseInt(startString)) + (double) difference)) * 100);

            //rescaling factor
            if(percentage > difference){
                percentage = percentage - difference;
            }
            else{
                percentage = 1;
            }

            Log.i(LOG_TAG, Integer.toString(percentage));

            TextView resultText = (TextView) findViewById(R.id.textResultView);
            TextView percText = (TextView) findViewById(R.id.percentView);

            StringBuilder stPercentage = new StringBuilder();
            stPercentage.append(percentage);
            stPercentage.append("%");

            percText.setText(stPercentage);

            if (percentage == 100) {
                resultText.setText("Rhythm Master!!");
            } else if (percentage < 100 && percentage >= 90) {
                resultText.setText("Excellent");
            } else if (percentage < 90 && percentage >= 80) {
                resultText.setText("Wow");
            } else if (percentage < 80 && percentage >= 70) {
                resultText.setText("Great");
            } else if (percentage < 70 && percentage >= 60) {
                resultText.setText("Alright");
            } else if (percentage < 60 && percentage >= 50) {
                resultText.setText("Okish");
            } else if (percentage < 50 && percentage >= 40) {
                resultText.setText("Meh");
            } else if (percentage < 40) {
                resultText.setText("Uh oh…");
            }
        }

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void setupBackButtonListener() {
        View tapButton = findViewById(R.id.backButton);
        if (tapButton != null) {
            tapButton.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        //intent to MainActivity
                        Intent intent = new Intent(Results.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

}

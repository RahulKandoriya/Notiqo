package com.abottech.apps.dailyquotesnotification.notiqo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    public static String data;

    public SeekBar seekBarTimeValue;
    public int timeValue;

    TextView  timeStringText;
    public Button startButton, startLibraryButton;

    public boolean isMainRunning;

    public String stringText;


    public ImageView imageView, share_imageView, rate_imageView;
    public Intent shareIntentMain, rateIntentMain;



    public WorkManager workManager = WorkManager.getInstance();

    public PeriodicWorkRequest periodicWorkRequest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        timeStringText = findViewById(R.id.timeStringText);
        timeValue = prefs.getInt("time_value",1);
        seekBarTimeValue = findViewById(R.id.seekBarView);
        startButton = findViewById(R.id.startButtonView);
        startLibraryButton = findViewById(R.id.libraryButton);

        imageView = findViewById(R.id.imageView);
        share_imageView = findViewById(R.id.share_imageView);
        rate_imageView = findViewById(R.id.rate_imageView);
        share_imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_share_primarycolor_24dp));
        rate_imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));

        seekBarTimeValue.setMax(24);
        seekBarTimeValue.setProgress(timeValue);

        imageMethod(timeValue, imageView);
        ViewCompat.setBackgroundTintList(startLibraryButton, ContextCompat.getColorStateList(this, R.color.colorGreen));

        //findViewById(R.id.main_layout).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        stringText = "You will Get Quotes After every " + String.valueOf(timeValue) + " hour(s).";


        timeStringText.setText(stringText);

        isMainRunning = prefs.getBoolean("Status",false);

        editor = prefs.edit();

        checkStatus(startButton);



        seekBarTimeValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                int min = 1;

                if (i<min) {
                    timeValue = min;
                    seekBarTimeValue.setProgress(timeValue);
                } else {

                    timeValue = i;
                    seekBarTimeValue.setProgress(timeValue);

                }

                imageMethod(timeValue, imageView);

                stringText = "You will Get Quotes After every " + String.valueOf(timeValue) + " hour(s).";
                timeStringText.setText(stringText);

                editor.putInt("time_value",timeValue).apply();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void share_method(View view){
        shareIntentMain = new Intent();
        shareIntentMain.setAction(Intent.ACTION_SEND);
        shareIntentMain.putExtra(Intent.EXTRA_TEXT, "Get Hourly Quotes Notifications\nInstall Now https://goo.gl/L3RQYb");
        shareIntentMain.setType("text/plain");
        startActivity(shareIntentMain);
    }

    public void rate_method(View view){
        rateIntentMain = new Intent();
        Uri uriMain = Uri.parse("https://play.google.com/store/apps/details?id=com.abottech.apps.dailyquotesnotification.notiqo");
        rateIntentMain  = new Intent(Intent.ACTION_VIEW, uriMain);
        startActivity(rateIntentMain);
    }


    public void imageMethod(int j, ImageView imageView){
        String imageName = String.format("drawable/ic_image_%d", j);
        imageView.setImageResource(getResources().getIdentifier(imageName, "drawable", getPackageName()));
    }

    public void startButton(View view){

        timeValue = prefs.getInt("time_value",1);

        periodicWorkRequest = new PeriodicWorkRequest.Builder(PeriodicTaskWork.class, timeValue, TimeUnit.HOURS).build();


        if (isMainRunning){
            startButton.setText(R.string.start_text);
            ViewCompat.setBackgroundTintList(startButton, ContextCompat.getColorStateList(this, R.color.colorBlue));
            workManager.cancelAllWork();
            Snackbar.make(findViewById(R.id.main_layout),"Stopped",Snackbar.LENGTH_SHORT).show();
            isMainRunning = false;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("Status", isMainRunning).apply();
            editor.putInt("time_value",1).apply();
            imageMethod(1, imageView);
            seekBarTimeValue.setProgress(1);
            seekBarTimeValue.setEnabled(true);

        } else {


            startButton.setText(R.string.stop_text);
            ViewCompat.setBackgroundTintList(startButton, ContextCompat.getColorStateList(this, R.color.colorRed));
            workManager.enqueue(periodicWorkRequest);
            Snackbar.make(findViewById(R.id.main_layout),"First Quote within 10 seconds, You can close the App!",Snackbar.LENGTH_LONG).show();
            isMainRunning = true;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("Status", isMainRunning).apply();
            editor.putInt("time_value",timeValue).apply();
            imageMethod(timeValue, imageView);
            seekBarTimeValue.setProgress(timeValue);
            seekBarTimeValue.setEnabled(false);
        }

    }

    public void startLibrary(View view){
        Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
        startActivity(intent);
    }



    public void checkStatus(View view) {

        isMainRunning =  prefs.getBoolean("Status",false);

        if (isMainRunning) {
            startButton.setText(R.string.stop_text);
            ViewCompat.setBackgroundTintList(startButton, ContextCompat.getColorStateList(this, R.color.colorRed));
            seekBarTimeValue.setEnabled(false);

            Snackbar.make(findViewById(R.id.main_layout),"Already Running at " + String.valueOf(timeValue) + " hour(s)",Snackbar.LENGTH_SHORT).show();

        } else {
            startButton.setText(R.string.start_text);
            ViewCompat.setBackgroundTintList(startButton, ContextCompat.getColorStateList(this, R.color.colorBlue));
            seekBarTimeValue.setEnabled(true);
        }
    }
}
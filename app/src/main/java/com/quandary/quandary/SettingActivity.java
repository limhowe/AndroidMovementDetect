package com.quandary.quandary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.quandary.quandary.service.FliiikService;

public class SettingActivity extends AppCompatActivity {

    SeekBar mMotionSeekBar, mDistanceSeekBar;
    EditText mMotionEdit, mDistanceEdit;

    double MOTION_MIN = 1.5;
    double DISTANCE_MIN = 1.5;

    Button mBtnUpdate;

    ConfigurationManager sharedConfigurationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mMotionSeekBar = (SeekBar) findViewById(R.id.seekBarMotion);
        mDistanceSeekBar = (SeekBar) findViewById(R.id.seekBarDistance);

        mMotionEdit = (EditText) findViewById(R.id.editMotionSensor);
        mDistanceEdit = (EditText) findViewById(R.id.editMotionDistance);

        mMotionEdit.setEnabled(false);
        mDistanceEdit.setEnabled(false);

        sharedConfigurationManager = new ConfigurationManager(getApplicationContext());
        float distanceSensor = sharedConfigurationManager.getDistanceSensibility();
        float motionSensor = sharedConfigurationManager.getMotionSensibility();

        int motionSeek =  (int)Math.round((motionSensor - MOTION_MIN) * 10);
        int distanceSeek = (int)Math.round((distanceSensor - DISTANCE_MIN) * 10);

        mDistanceSeekBar.setProgress(distanceSeek);
        mMotionSeekBar.setProgress(motionSeek);

        double value = MOTION_MIN + (float)(motionSeek)/10.0f;
        mMotionEdit.setText(String.format("%.1f",value));

        value = DISTANCE_MIN + (float)(distanceSeek)/10.0f;
        mDistanceEdit.setText(String.format("%.1f",value));


        mMotionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double value = MOTION_MIN + (float)(progress)/10.0f;
                mMotionEdit.setText(String.format("%.1f",value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mDistanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double value = DISTANCE_MIN + (float)(progress)/10.0f;
                mDistanceEdit.setText(String.format("%.1f",value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mBtnUpdate = (Button) findViewById(R.id.btnUpdate);
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String motionText = mMotionEdit.getText().toString();
                String distanceText = mDistanceEdit.getText().toString();

                sendCommandToService(Float.parseFloat(motionText),Float.parseFloat(distanceText));
            }
        });

    }

    private void sendCommandToService(float motion, float distance ) {
        sharedConfigurationManager.setDistanceSensibility(distance);
        sharedConfigurationManager.setMotionSensibility(motion);

        Intent serviceIntent = new Intent(this,FliiikService.class);
        serviceIntent.putExtra("FliiikCommand", "config");
        this.startService(serviceIntent);

        Toast.makeText(getApplicationContext(),"Sensor Updated", Toast.LENGTH_LONG).show();
    }
}

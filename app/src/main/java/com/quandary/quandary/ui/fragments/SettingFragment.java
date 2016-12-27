package com.quandary.quandary.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.quandary.quandary.ConfigurationManager;
import com.quandary.quandary.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class SettingFragment extends Fragment {

    private OnSettingChangeListener mListener;

    DiscreteSeekBar mMotionSeekBar, mDistanceSeekBar;
    EditText mMotionEdit, mDistanceEdit;

    double MOTION_MIN = 1.5;
    double DISTANCE_MIN = 1.5;

    Button mBtnUpdate,mBtnReset;

    ConfigurationManager sharedConfigurationManager;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment SettingFragment.
     */
    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getActivity().setTitle("Setting");


        mMotionSeekBar = (DiscreteSeekBar) view.findViewById(R.id.seekBarMotion);
        mDistanceSeekBar = (DiscreteSeekBar) view.findViewById(R.id.seekBarDistance);

        mMotionEdit = (EditText) view.findViewById(R.id.editMotionSensor);
        mDistanceEdit = (EditText) view.findViewById(R.id.editMotionDistance);

        mMotionEdit.setEnabled(false);
        mDistanceEdit.setEnabled(false);

        sharedConfigurationManager = new ConfigurationManager(getActivity().getApplicationContext());
        float distanceSensor = sharedConfigurationManager.getDistanceSensibility();
        float motionSensor = sharedConfigurationManager.getMotionSensibility();

        int motionSeek =  (int)Math.round((motionSensor - MOTION_MIN) * 10);
        int distanceSeek = (int)Math.round((distanceSensor - DISTANCE_MIN) * 10);

        int motionSeekVal =  (int)Math.round(motionSensor* 10);
        int distanceSeekVal = (int)Math.round(distanceSensor * 10);

        mDistanceSeekBar.setProgress(distanceSeekVal);
        mMotionSeekBar.setProgress(motionSeekVal);

        double value = MOTION_MIN + (float)(motionSeek)/10.0f;
        mMotionEdit.setText(String.format("%.1f",value));

        value = DISTANCE_MIN + (float)(distanceSeek)/10.0f;
        mDistanceEdit.setText(String.format("%.1f",value));


        mMotionSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                double calcValue = (float)(value)/10.0f;
                String format = String.format("%.1f",calcValue);
                mMotionEdit.setText(format);
                seekBar.setIndicatorFormatter(format);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        mDistanceSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                double calcValue = (float)(value)/10.0f;
                String format = String.format("%.1f",calcValue);
                mDistanceEdit.setText(format);
                seekBar.setIndicatorFormatter(format);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });


        mBtnUpdate = (Button) view.findViewById(R.id.btnUpdate);
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String motionText = mMotionEdit.getText().toString();
                String distanceText = mDistanceEdit.getText().toString();

                mListener.onSettingUpdated(Float.parseFloat(motionText),Float.parseFloat(distanceText));
            }
        });

        mBtnReset = (Button) view.findViewById(R.id.btnReset);
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMotionEdit.setText("2.7");
                mDistanceEdit.setText("2.9");

                String motionText = mMotionEdit.getText().toString();
                String distanceText = mDistanceEdit.getText().toString();

                mDistanceSeekBar.setProgress(29);
                mMotionSeekBar.setProgress(27);

                mListener.onSettingUpdated(Float.parseFloat(motionText),Float.parseFloat(distanceText));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingChangeListener) {
            mListener = (OnSettingChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSettingChangeListener {
        void onSettingUpdated(float motion, float distance);
    }
}

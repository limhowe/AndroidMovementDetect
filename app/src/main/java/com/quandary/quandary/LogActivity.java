package com.quandary.quandary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.quandary.quandary.detector.FliiikMove;
import com.quandary.quandary.detector.FliiikMoveDetector;

public class LogActivity extends AppCompatActivity implements FliiikMoveDetector.OnFliiikMoveListener{

    TextView mTextView;

    @Override
    public void onResume() {
        super.onResume();

        FliiikMoveDetector.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        FliiikMoveDetector.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                mTextView.setText("");
            }
        });


        //
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setMovementMethod(new ScrollingMovementMethod());


        if (FliiikMoveDetector.create(this, this)) {
            FliiikMoveDetector.updateConfiguration(2.0f, false, false, true);
//            FliiikMoveDetector.updateConfiguration(2.0f, false, true, false);
//            FliiikMoveDetector.updateConfiguration(2.0f, true, false, false);
        }
    }


    @Override
    public void OnFliiikMove(FliiikMove move) {
        // This callback is triggered by the ShakeDetector. In a real implementation, you should
        // do here a real action.


        mTextView.append(move.getDesc());
    }




}

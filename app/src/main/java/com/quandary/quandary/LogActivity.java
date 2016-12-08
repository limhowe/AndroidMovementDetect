package com.quandary.quandary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.quandary.quandary.db.FliiikGesture;
import com.quandary.quandary.detector.FliiikMoveDetector;
import com.quandary.quandary.detector.SensorFilterBundle;
import com.quandary.quandary.utils.FliiikHelper;
import com.quandary.quandary.utils.match.FliiikMatcher;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity implements  FliiikMoveDetector.OnFliiikMoveListener{

    TextView mXLog,mYLog,mZLog, mAllLog, mTextResult;
    Button mBtnConfig;
    EditText editSensibility;
    EditText editDistance;

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
                exportDB();
            }
        });

        if (FliiikMoveDetector.create(this, this)) {
            FliiikMoveDetector.updateConfiguration(2.0f, 2.6f);
        }

        mXLog = (TextView) findViewById(R.id.textXLog);
        mYLog = (TextView) findViewById(R.id.textYLog);
        mZLog = (TextView) findViewById(R.id.textZLog);
        mAllLog = (TextView) findViewById(R.id.textAllLog);

        mTextResult = (TextView) findViewById(R.id.textResult);


        mBtnConfig = (Button) findViewById(R.id.btnConfig);
        editSensibility = (EditText) findViewById(R.id.editSensibility);
        editDistance = (EditText) findViewById(R.id.editLength);
        mBtnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float sensibility = Float.parseFloat(editSensibility.getText().toString());
                float distance = Float.parseFloat(editDistance.getText().toString());
                FliiikMoveDetector.stop();
                FliiikMoveDetector.updateConfiguration(sensibility, distance);
                FliiikMoveDetector.start();
            }
        });

    }

    private void exportDB() {

        FliiikMoveDetector.stop();
        ArrayList<SensorFilterBundle> allList = FliiikMoveDetector.getInstance().getAllBundleHistory();

        String allBody = "";
        for (SensorFilterBundle filterBundle : allList) {
            allBody = allBody + FliiikHelper.getAxisValueAsString(filterBundle.getAxis()) + " ";
        }
        mAllLog.setText(allBody);


        String resultString = "";
        resultString += "run 000 : RESULT = " + FliiikMatcher.match("000", allList) + System.getProperty ("line.separator");
        resultString += "run 111 : RESULT = " + FliiikMatcher.match("111", allList) + System.getProperty ("line.separator");
        resultString += "run 222 : RESULT = " + FliiikMatcher.match("222", allList) + System.getProperty ("line.separator");
        resultString += "run 010 : RESULT = " + FliiikMatcher.match("010", allList) + System.getProperty ("line.separator");
        resultString += "run 110 : RESULT = " + FliiikMatcher.match("110", allList) + System.getProperty ("line.separator");
        resultString += "run 220 : RESULT = " + FliiikMatcher.match("220", allList) + System.getProperty ("line.separator");

        mTextResult.setText(resultString);

//        ArrayList<SensorFilterBundle> testList = new ArrayList<SensorFilterBundle>();

        //CASE 1
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Y_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Y_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Y_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Y_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Y_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Y_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Y_NEGATIVE,0));

        //CASE 2

//        testList.add(new SensorFilterBundle(FliiikConstant.Z_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.X_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.X_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.X_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.X_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.X_POSITIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.X_NEGATIVE,0));
//        testList.add(new SensorFilterBundle(FliiikConstant.Z_POSITIVE,0));
//
//
//        String allBody = "";
//        for (SensorFilterBundle filterBundle : testList) {
//            allBody = allBody + FliiikHelper.getAxisValueAsString(filterBundle.getAxis()) + " ";
//        }
//        mAllLog.setText(allBody);
//
//        String resultString = "";
//        resultString += "run 000 : RESULT = " + FliiikMatcher.match("000", testList) + System.getProperty ("line.separator");
//        resultString += "run 111 : RESULT = " + FliiikMatcher.match("111", testList) + System.getProperty ("line.separator");
//        resultString += "run 222 : RESULT = " + FliiikMatcher.match("222", testList) + System.getProperty ("line.separator");
//        resultString += "run 010 : RESULT = " + FliiikMatcher.match("010", testList) + System.getProperty ("line.separator");
//        resultString += "run 110 : RESULT = " + FliiikMatcher.match("110", testList) + System.getProperty ("line.separator");
//        resultString += "run 220 : RESULT = " + FliiikMatcher.match("220", testList) + System.getProperty ("line.separator");

//        mTextResult.setText(resultString);

        //Save to File;
//        String sBody = "log;X;Y;Z\n";
//        try {
//            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
//            if (!root.exists()) {
//                root.mkdirs();
//            }
//            File gpxfile = new File(root, "analyzer.csv");
//            FileWriter writer = new FileWriter(gpxfile);
//
//            writer.append(sBody);
//            writer.flush();
//            writer.close();
//
//            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        FliiikMoveDetector.start();
    }


    @Override
    public void OnFliiikMove(FliiikGesture move) {

    }

}

package com.quandary.quandary;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quandary.quandary.detector.FliiikMoveDetector;
import com.quandary.quandary.detector.SensorBundle;
import com.quandary.quandary.detector.move.FliiikMove;
import com.quandary.quandary.ui.FliiikHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LogActivity extends AppCompatActivity {

    TextView mTextLog;
    Button mBtnConfig;
    EditText editSensibility;

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

        if (FliiikMoveDetector.create(this, null)) {
            FliiikMoveDetector.updateConfiguration(2.7f);
        }

        mTextLog = (TextView) findViewById(R.id.textLog);
        mBtnConfig = (Button) findViewById(R.id.btnConfig);
        editSensibility = (EditText) findViewById(R.id.editSensibility);
        mBtnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float sensibility = Float.parseFloat(editSensibility.getText().toString());
                FliiikMoveDetector.stop();
                FliiikMoveDetector.updateConfiguration(sensibility);
                FliiikMoveDetector.start();
            }
        });

    }

    private void exportDB() {

        String testStr = FliiikHelper.getActionAxisString("110");
        StringBuilder testBuilder = new StringBuilder(testStr);
        testBuilder.insert(2,1);
        testBuilder.insert(4,6);
        testStr = testBuilder.toString();
        if (FliiikHelper.compareActionString(testStr, "110")) {
            Toast.makeText(this,testStr + " - Compare Success",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,testStr + " - Compare failed",Toast.LENGTH_LONG).show();
        }

//        double DEFAULT_DISTANCE_THRESHOLD = 4;
//
//        FliiikMoveDetector.stop();
//        List<SensorBundle> list = FliiikMoveDetector.getInstance().getBundleHistory();
//
//        if (list == null || list.size() < 6 ) {
//            Toast.makeText(this, "List size < 6", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
////        String sBody = "log;X;Y;Z\n";
//
//        String sBody = "Start - ";
//
//        SensorBundle baseBundle = list.get(0);
//        long baseTimestamp = baseBundle.getTimestamp();
//        sBody = sBody + baseBundle.toString(baseTimestamp) + "\n";
//
//        SensorBundle currentBundle = list.get(1);
//
//        int xDirection = (currentBundle.getXAcc() - baseBundle.getXAcc()) > 0 ? 1 : -1;
//        int yDirection = (currentBundle.getYAcc() - baseBundle.getZAcc()) > 0 ? 1 : -1;
//        int zDirection = (currentBundle.getZAcc() - baseBundle.getZAcc()) > 0 ? 1 : -1;
//
//        double xDistance = currentBundle.getXAcc() - baseBundle.getXAcc();
//        double yDistance = currentBundle.getYAcc() - baseBundle.getYAcc();
//        double zDistance = currentBundle.getZAcc() - baseBundle.getZAcc();
//
//        baseBundle = currentBundle;
//
//        for (int i=2; i<list.size(); i++) {
//            currentBundle = list.get(i);
//
//            int curXDirection = (currentBundle.getXAcc() - baseBundle.getXAcc()) > 0 ? 1 : -1;
//            int curYDirection = (currentBundle.getYAcc() - baseBundle.getZAcc()) > 0 ? 1 : -1;
//            int curZDirection = (currentBundle.getZAcc() - baseBundle.getZAcc()) > 0 ? 1 : -1;
//
//            String elementS = "";
//
//            if (curXDirection != xDirection) {
//                if (Math.abs(xDistance) > DEFAULT_DISTANCE_THRESHOLD) {
//                    String element = "(";
//                    if (xDirection < 0) {
//                        element = element + "-";
//                    }
//
//                    element = element + "X)";
//
//                    elementS = elementS + element;
//                }
//
//                xDistance = 0;
//            }
//
//            if (curYDirection != yDirection) {
//                if (Math.abs(yDistance) > DEFAULT_DISTANCE_THRESHOLD) {
//                    String element = "(";
//                    if (yDirection < 0) {
//                        element = element + "-";
//                    }
//
//                    element = element + "Y)";
//
//                    elementS = elementS + element;
//                }
//
//                yDistance = 0;
//            }
//
//            if (curZDirection != zDirection) {
//                if (Math.abs(zDistance) > DEFAULT_DISTANCE_THRESHOLD) {
//                    String element = "(";
//                    if (zDirection < 0) {
//                        element = element + "-";
//                    }
//
//                    element = element + "Z)";
//
//                    elementS = elementS + element;
//                }
//
//                zDistance = 0;
//            }
//
//            if (elementS != "") {
//                sBody = sBody + " #" + elementS + "# ";
//            }
//
//            xDistance += currentBundle.getXAcc() - baseBundle.getXAcc();
//            yDistance += currentBundle.getYAcc() - baseBundle.getYAcc();
//            zDistance += currentBundle.getZAcc() - baseBundle.getZAcc();
//
//            xDirection = curXDirection;
//            yDirection = curYDirection;
//            zDirection = curZDirection;
//
//            baseBundle = currentBundle;
//        }
//
//        mTextLog.setText(sBody);
//
//        //Save to File;
//
////        try {
////            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
////            if (!root.exists()) {
////                root.mkdirs();
////            }
////            File gpxfile = new File(root, "analyzer.csv");
////            FileWriter writer = new FileWriter(gpxfile);
////
////            writer.append(sBody);
////            writer.flush();
////            writer.close();
////
////            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        FliiikMoveDetector.start();

    }

}

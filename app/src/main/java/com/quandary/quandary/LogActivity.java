package com.quandary.quandary;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.quandary.quandary.detector.FliiikDetector;
import com.quandary.quandary.ui.PackageItem;

import java.util.List;

public class LogActivity extends AppCompatActivity implements FliiikDetector.OnFliiikListener{

    TextView mTextView;
    RadioGroup mRadioGroup;

    String mPackageName = null;

    int mDefaultAction = 1;

    @Override
    public void onResume() {
        super.onResume();
        FliiikDetector.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        FliiikDetector.stop();
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
                mTextView.setText("");
            }
        });

        //
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        mRadioGroup = (RadioGroup) findViewById(R.id.radioAction);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio0 :
                        mDefaultAction = 0;
                        Log.d("Log Activity", "onCheckedChanged: taptaptap");
                        break;
                    default:
                        mDefaultAction = 1;
                        Log.d("Log Activity", "onCheckedChanged: chopchopchop");
                        break;
                }
            }
        });


        if (FliiikDetector.create(this, this)) {
            FliiikDetector.updateConfiguration(3,2);
        }


        //Get Calculator
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packs = pm.getInstalledApplications(0);

        for (ApplicationInfo content : packs) {
            try {
                if( content.packageName.toString().toLowerCase().contains("calcul")){
                    mPackageName = content.packageName.toString();
                    break;
                }
            } catch (Exception e) {
                Log.e("", "Parse Application Manager: " + e.toString());
            } catch(Error e2) {
                Log.e("", "Parse Application Manager 2: " + e2.toString());
            }
        }
    }

    @Override
    public void OnFliiik(int index) {

        if (index == 0) {
            mTextView.append("TAP TAP TAP");
            mTextView.append(",");
        } else {
            mTextView.append("CHOP CHOP CHOP");
            mTextView.append(",");
        }

        /*if (index == mDefaultAction) {
            try {
                if (mPackageName != "") {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(mPackageName);
                    if (launchIntent != null) {
                        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                }
            } catch (Exception e) {

            } catch (Error er) {

            }
        }*/
    }

}

package com.quandary.quandary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.quandary.quandary.db.FliiikGesture;
import com.quandary.quandary.db.GesturesDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.quandary.quandary.FliiikConstant.*;

public class AddEditGestureActivity extends AppCompatActivity implements View.OnClickListener{

    List<FliiikGesture> mCurrentList;

    ArrayList<Button> mButtons1;
    ArrayList<Button> mButtons2;
    ArrayList<Button> mButtons3;

    Button mBtnReset1,mBtnReset2,mBtnReset3;
    Button mSendButton;

    int mGestureResult[] = { -1, -1, -1 };

    public static int ACTIVITY_TYPE_ADD = 1;
    public static int ACTIVITY_TYPE_EDIT = 2;
    public static String EXTRA_BUNDLE_KEY_GESTURE_ID = "gesture_id";

    int mActivityType =  ACTIVITY_TYPE_ADD;
    FliiikGesture mCurrentGesture = null;

    CoordinatorLayout mCoordinatorLayout;
    View.OnClickListener mOnClickListener;
    Button mDescriptionTap, mDescriptionChop, mDescriptionRoll, mDescriptionSpin;

    String mVideoUrl = GESTURE_TAP_VIDEO_LINK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture);


        Bundle extraBundle = getIntent().getExtras();

        if (extraBundle != null && extraBundle.containsKey(EXTRA_BUNDLE_KEY_GESTURE_ID)) {
            mActivityType = ACTIVITY_TYPE_EDIT;
            int gestureId = extraBundle.getInt(EXTRA_BUNDLE_KEY_GESTURE_ID);
            mCurrentGesture = GesturesDatabaseHelper.getInstance(getApplicationContext()).getGesture(gestureId);

            mGestureResult[0] = Integer.parseInt(mCurrentGesture.action.substring(0,1));
            mGestureResult[1] = Integer.parseInt(mCurrentGesture.action.substring(1,2));
            mGestureResult[2] = Integer.parseInt(mCurrentGesture.action.substring(2));
        } else {
            mActivityType = ACTIVITY_TYPE_ADD;
        }


        mButtons1 = new ArrayList<Button>();
        mButtons2 = new ArrayList<Button>();
        mButtons3 = new ArrayList<Button>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mBtnReset1 = (Button) findViewById(R.id.btnReset10);
        mBtnReset2 = (Button) findViewById(R.id.btnReset20);
        mBtnReset3 = (Button) findViewById(R.id.btnReset30);

        mBtnReset1.setOnClickListener(this);
        mBtnReset2.setOnClickListener(this);
        mBtnReset3.setOnClickListener(this);

        mCurrentList = GesturesDatabaseHelper.getInstance(getApplicationContext()).getAllGestures();

        for (int i = 0; i < 4; i++) {
            String buttonID = "btnGesture" + (10 + i);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            Button btn = (Button) findViewById(resID);
            btn.setOnClickListener(this);

            if (mActivityType == ACTIVITY_TYPE_ADD) {
                btn.setEnabled(true);
            } else {
                if (mGestureResult[0] == i) {
                    btn.setEnabled(true);
                } else {
                    btn.setEnabled(false);
                }
            }

            mButtons1.add(btn);
        }

        for (int i = 0; i < 4; i++) {
            String buttonID = "btnGesture" + (20 + i);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            Button btn = (Button) findViewById(resID);
            btn.setOnClickListener(this);

            if (mActivityType == ACTIVITY_TYPE_ADD) {
                btn.setEnabled(true);
            } else {
                if (mGestureResult[1] == i) {
                    btn.setEnabled(true);
                } else {
                    btn.setEnabled(false);
                }
            }

            mButtons2.add(btn);
        }

        for (int i = 0; i < 4; i++) {
            String buttonID = "btnGesture" + (30 + i);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            Button btn = (Button) findViewById(resID);
            btn.setOnClickListener(this);

            if (mActivityType == ACTIVITY_TYPE_ADD) {
                btn.setEnabled(true);
            } else {
                if (mGestureResult[2] == i) {
                    btn.setEnabled(true);
                } else {
                    btn.setEnabled(false);
                }
            }

            mButtons3.add(btn);
        }


        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(this);


        if (mActivityType == ACTIVITY_TYPE_ADD) {
            mBtnReset1.setEnabled(false);
            mBtnReset2.setEnabled(false);
            mBtnReset3.setEnabled(false);

            mSendButton.setEnabled(false);
            mSendButton.setText(getResources().getString(R.string.button_text_add_gesture));
        } else if (mActivityType == ACTIVITY_TYPE_EDIT) {
            mBtnReset1.setEnabled(true);
            mBtnReset2.setEnabled(true);
            mBtnReset3.setEnabled(true);

            mSendButton.setEnabled(true);
            mSendButton.setText(getResources().getString(R.string.button_text_edit_gesture));
        }

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.activity_add_gesture);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = mVideoUrl;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
        };



        mDescriptionTap = (Button) findViewById(R.id.btnGestureDesc40);
        mDescriptionChop = (Button) findViewById(R.id.btnGestureDesc41);
        mDescriptionRoll = (Button) findViewById(R.id.btnGestureDesc42);
        mDescriptionSpin = (Button) findViewById(R.id.btnGestureDesc43);


        mDescriptionTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGestureDescription(GESTURE_TAP);
            }
        });

        mDescriptionChop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGestureDescription(GESTURE_CHOP);
            }
        });

        mDescriptionRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGestureDescription(GESTURE_ROLL);
            }
        });

        mDescriptionSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGestureDescription(GESTURE_SPIN);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean checkExistGesture(String action) {

        if (mCurrentList == null || mCurrentList.size() == 0) return  false;
        for (FliiikGesture gesture : mCurrentList) {
            if (gesture.action.equalsIgnoreCase(action)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkExistGestureExclude(String action, int excludeId) {

        if (mCurrentList == null || mCurrentList.size() == 0) return  false;
        for (FliiikGesture gesture : mCurrentList) {

            if (gesture.id == excludeId) continue;

            if (gesture.action.equalsIgnoreCase(action)) {
                return true;
            }
        }

        return false;
    }

    public boolean addGesture(String action, boolean safetyChecked) {

        if (!safetyChecked && checkExistGesture(action)) {
            return false;
        }

        FliiikGesture gesture = new FliiikGesture();
        gesture.action = action;
        GesturesDatabaseHelper.getInstance(getApplicationContext()).addGesture(gesture);

        return true;
    }

    public boolean updateGesture(FliiikGesture gesture, String action, boolean safetyChecked) {

        if (!safetyChecked && checkExistGestureExclude(action, gesture.id)) {
            return false;
        }

        gesture.action = action;
        GesturesDatabaseHelper.getInstance(getApplicationContext()).updateGesture(gesture);

        return true;
    }


    //OnCliickListner Implementation

    @Override
    public void onClick(View view) {
        ArrayList<Button> loopArray = null;
        int indexTag = -1;
        boolean isReset = false;
        switch (view.getId()) {
            case R.id.btnGesture10:case R.id.btnGesture11:case R.id.btnGesture12:case R.id.btnGesture13:
                indexTag = Integer.parseInt(view.getTag().toString()) - 1010;
                mGestureResult[0] = indexTag;

                mBtnReset1.setEnabled(true);
                loopArray = mButtons1;
                break;
            case R.id.btnGesture20:case R.id.btnGesture21:case R.id.btnGesture22:case R.id.btnGesture23:
                indexTag = Integer.parseInt(view.getTag().toString()) - 1020;
                mGestureResult[1] = indexTag;

                mBtnReset2.setEnabled(true);
                loopArray = mButtons2;
                break;
            case R.id.btnGesture30:case R.id.btnGesture31:case R.id.btnGesture32:case R.id.btnGesture33:
                indexTag = Integer.parseInt(view.getTag().toString()) - 1030;
                mGestureResult[2] = indexTag;

                mBtnReset3.setEnabled(true);
                loopArray = mButtons3;
                break;

            case R.id.btnReset10:
                mBtnReset1.setEnabled(false);
                isReset = true;
                loopArray = mButtons1;
                mGestureResult[0] = -1;
                break;
            case R.id.btnReset20:
                mBtnReset2.setEnabled(false);
                isReset = true;
                loopArray = mButtons2;
                mGestureResult[1] = -1;
                break;

            case R.id.btnReset30:
                mBtnReset3.setEnabled(false);
                isReset = true;
                loopArray = mButtons3;
                mGestureResult[2] = -1;
                break;

            case R.id.sendButton: {

                String action = String.format("%d%d%d", mGestureResult[0],mGestureResult[1],mGestureResult[2]);

                if (mActivityType == ACTIVITY_TYPE_ADD) {
                    if (checkExistGesture(action)) {
                        Toast.makeText(getApplicationContext(), "You already have these gesture", Toast.LENGTH_LONG).show();
                    } else {
                        if (addGesture(action, true)) {
                            Toast.makeText(getApplicationContext(), "Successfully Added Gesture", Toast.LENGTH_LONG).show();

                            Intent resultIntent = new Intent();
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    }
                } else if (mActivityType == ACTIVITY_TYPE_EDIT) {
                    if (checkExistGestureExclude(action, mCurrentGesture.id)) {
                        Toast.makeText(getApplicationContext(), "You already have these gesture", Toast.LENGTH_LONG).show();
                    } else {
                        if (updateGesture(mCurrentGesture, action, true)) {
                            Toast.makeText(getApplicationContext(), "Successfully Updated Gesture", Toast.LENGTH_LONG).show();

                            Intent resultIntent = new Intent();
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    }
                }

                return;
            }
        }

        if (loopArray != null) {
            for (Button btn: loopArray ) {
                if (isReset) {
                    btn.setEnabled(true);
                } else if (btn.getId() != view.getId()) {
                    btn.setEnabled(false);
                }
            }
        }

        if (mGestureResult[0] != -1 && mGestureResult[1] != -1 && mGestureResult[2] != -1) {
            mSendButton.setEnabled(true);
        } else {
            mSendButton.setEnabled(false);
        }
    }

    //Show Descriptions

    private void showGestureDescription(int gestureType) {


        String gestureString = getResources().getString(R.string.gesture_text_spin);

        switch (gestureType) {
            case GESTURE_TAP:
                gestureString = getResources().getString(R.string.gesture_text_tap);
                mVideoUrl = GESTURE_TAP_VIDEO_LINK;
                break;
            case GESTURE_CHOP:
                gestureString = getResources().getString(R.string.gesture_text_chop);
                mVideoUrl = GESTURE_CHOP_VIDEO_LINK;
                break;
            case GESTURE_ROLL:
                gestureString = getResources().getString(R.string.gesture_text_roll);
                mVideoUrl = GESTURE_ROLL_VIDEO_LINK;
                break;
            case GESTURE_SPIN:
                gestureString = getResources().getString(R.string.gesture_text_spin);
                mVideoUrl = GESTURE_SPIN_VIDEO_LINK;
                break;
        }


        Snackbar snackbar = Snackbar
                .make(mCoordinatorLayout, gestureString, Snackbar.LENGTH_INDEFINITE)
                .setDuration(10000)
                .setAction("View", mOnClickListener);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(10);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

}

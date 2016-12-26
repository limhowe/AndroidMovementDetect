package com.quandary.quandary;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.quandary.quandary.db.FliiikGesture;
import com.quandary.quandary.db.GesturesDatabaseHelper;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.quandary.quandary.FliiikConstant.GESTURE_CHOP;
import static com.quandary.quandary.FliiikConstant.GESTURE_ROLL;
import static com.quandary.quandary.FliiikConstant.GESTURE_SPIN;
import static com.quandary.quandary.FliiikConstant.GESTURE_TAP;

public class AddGestureActivity extends AppCompatActivity implements View.OnClickListener{

    List<FliiikGesture> mCurrentList;

    ArrayList<Button> mButtons1;
    ArrayList<Button> mButtons2;
    ArrayList<Button> mButtons3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture);

        mButtons1 = new ArrayList<Button>();
        mButtons2 = new ArrayList<Button>();
        mButtons3 = new ArrayList<Button>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mCurrentList = GesturesDatabaseHelper.getInstance(getApplicationContext()).getAllGestures();

        for (int i = 0; i < 4; i++) {
                String buttonID = "btnGesture" + (10 + i);
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                Button btn = (Button) findViewById(resID);
                btn.setOnClickListener(this);
                mButtons1.add(btn);
        }

        for (int i = 0; i < 4; i++) {
            String buttonID = "btnGesture" + (20 + i);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            Button btn = (Button) findViewById(resID);
            btn.setOnClickListener(this);
            mButtons2.add(btn);
        }

        for (int i = 0; i < 4; i++) {
            String buttonID = "btnGesture" + (30 + i);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            Button btn = (Button) findViewById(resID);
            btn.setOnClickListener(this);
            mButtons3.add(btn);
        }


//        String action = String.format("%d%d%d", (Integer)selectedAction1.value,(Integer)selectedAction2.value,(Integer)selectedAction3.value);
//        if (checkExistGesture(action)) {
//            Toast.makeText(getApplicationContext(), "You already have these gesture", Toast.LENGTH_LONG).show();
//        } else {
//            if (addGesture(action, true)) {
//                Toast.makeText(getApplicationContext(), "Successfully Added Gesture", Toast.LENGTH_LONG).show();
//
//                Intent resultIntent = new Intent();
//                setResult(Activity.RESULT_OK, resultIntent);
//                finish();
//            }
//        }

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

    public boolean addGesture(String action, boolean safetyChecked) {

        if (!safetyChecked && checkExistGesture(action)) {
            return false;
        }

        FliiikGesture gesture = new FliiikGesture();
        gesture.action = action;
        GesturesDatabaseHelper.getInstance(getApplicationContext()).addGesture(gesture);

        return true;
    }


    //OnCliickListner Implementation

    @Override
    public void onClick(View view) {
        ArrayList<Button> loopArray = null;
        switch (view.getId()){
            case R.id.btnGesture10:case R.id.btnGesture11:case R.id.btnGesture12:case R.id.btnGesture13:
                loopArray = mButtons1;
                break;
            case R.id.btnGesture20:case R.id.btnGesture21:case R.id.btnGesture22:case R.id.btnGesture23:
                loopArray = mButtons2;
                break;
            case R.id.btnGesture30:case R.id.btnGesture31:case R.id.btnGesture32:case R.id.btnGesture33:
                loopArray = mButtons3;
                break;
        }

        if (loopArray != null) {
            for (Button btn: loopArray ) {
                if (btn.getId() != view.getId()) {
                    btn.setEnabled(false);
                }
            }
        }
    }

}

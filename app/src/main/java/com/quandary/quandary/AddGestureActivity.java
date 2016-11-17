package com.quandary.quandary;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.quandary.quandary.db.FliiikGesture;
import com.quandary.quandary.db.GesturesDatabaseHelper;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.util.List;

import static com.quandary.quandary.FliiikConstant.GESTURE_CHOP;
import static com.quandary.quandary.FliiikConstant.GESTURE_ROLL;
import static com.quandary.quandary.FliiikConstant.GESTURE_TAP;

public class AddGestureActivity extends AppCompatActivity {

    List<FliiikGesture> mCurrentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture);

        mCurrentList = GesturesDatabaseHelper.getInstance(getApplicationContext()).getAllGestures();

        final SwipeSelector action1Selector = (SwipeSelector) findViewById(R.id.sizeSelector);
        action1Selector.setItems(
                new SwipeItem(GESTURE_TAP, "TAP", "Hold the device comfortably with one or two hands and 'tap' the top width of the device against an imaginary surface."),
                new SwipeItem(GESTURE_CHOP, "CHOP", "Hold the device comfortably with one or two hands and pretend to use the edge of the side of the device to cut or chop a stick, vegetable/ or chicken neck."),
                new SwipeItem(GESTURE_ROLL, "ROLL", "Hold the device comfortably with one or two hands and rotate 90 degrees +/-around the X axis")
        );

        final SwipeSelector action2Selector = (SwipeSelector) findViewById(R.id.toppingSelector);
        action2Selector.setItems(
                new SwipeItem(GESTURE_TAP, "TAP", "Hold the device comfortably with one or two hands and 'tap' the top width of the device against an imaginary surface."),
                new SwipeItem(GESTURE_CHOP, "CHOP", "Hold the device comfortably with one or two hands and pretend to use the edge of the side of the device to cut or chop a stick, vegetable/ or chicken neck."),
                new SwipeItem(GESTURE_ROLL, "ROLL", "Hold the device comfortably with one or two hands and rotate 90 degrees +/-around the X axis")
        );

        final SwipeSelector action3Selector = (SwipeSelector) findViewById(R.id.deliverySelector);
        action3Selector.setItems(
                new SwipeItem(GESTURE_TAP, "TAP", "Hold the device comfortably with one or two hands and 'tap' the top width of the device against an imaginary surface."),
                new SwipeItem(GESTURE_CHOP, "CHOP", "Hold the device comfortably with one or two hands and pretend to use the edge of the side of the device to cut or chop a stick, vegetable/ or chicken neck."),
                new SwipeItem(GESTURE_ROLL, "ROLL", "Hold the device comfortably with one or two hands and rotate 90 degrees +/-around the X axis")
        );

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeItem selectedAction1 = action1Selector.getSelectedItem();
                SwipeItem selectedAction2 = action2Selector.getSelectedItem();
                SwipeItem selectedAction3 = action3Selector.getSelectedItem();

                String action = String.format("%d%d%d", (Integer)selectedAction1.value,(Integer)selectedAction2.value,(Integer)selectedAction3.value);
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


            }
        });
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
}

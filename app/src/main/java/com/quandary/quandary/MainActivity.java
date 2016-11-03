package com.quandary.quandary;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.quandary.quandary.db.FliiikGesture;
import com.quandary.quandary.db.GesturesDatabaseHelper;
import com.quandary.quandary.ui.FliiikHelper;
import com.quandary.quandary.ui.PackageAdapter;
import com.quandary.quandary.ui.PackageItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<FliiikGesture> mAppList;
    private AppAdapter mAdapter;

    private PackageAdapter packageAdapter;
    private List<PackageItem> data;

    OnClickListener clickListener;
    OnItemClickListener itemClickListener;

    static final int ADD_GESTURE_REQUEST = 1;  // The request code

    private SwipeMenuListView mListView;
    private PackageManager mPackageManager;

    private FliiikGesture mCurrentGesture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPackageManager = getPackageManager();

        mAppList = GesturesDatabaseHelper.getInstance(getApplicationContext()).getAllGestures();
        mListView= (SwipeMenuListView) findViewById(R.id.listView);
        mAdapter = new AppAdapter();
        mListView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                FliiikGesture item = mAppList.get(position);
                switch (index) {
                    case 0:
                        // delete
                        if (mCurrentGesture != null && item.action.equalsIgnoreCase(mCurrentGesture.action)) {
                            mCurrentGesture = null;
                        }
                        GesturesDatabaseHelper.getInstance(getApplicationContext()).deleteGesture(item);
                        mAppList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        clickListener = new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {
                switch (view.getId()) {
                    case R.id.footer_close_button:
                        dialog.dismiss();
                }

            }
        };

        itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                PackageItem itemSelected = (PackageItem) item;
                if (mCurrentGesture != null) {
                    mCurrentGesture.packageName = itemSelected.getPackageName();
                    GesturesDatabaseHelper.getInstance(getApplicationContext()).updateGesture(mCurrentGesture);
                    mAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        };

        final PackageManager pm = getPackageManager();

        //Create Adapter
        data = new ArrayList<PackageItem>();

        //Get Application list
        List<ApplicationInfo> packs = pm.getInstalledApplications(0);
        for (ApplicationInfo content : packs) {
            try {
                if (pm.getLaunchIntentForPackage(content.packageName) != null) {
                    PackageItem item = new PackageItem();
                    item.setName(getPackageManager().getApplicationLabel(content).toString());
                    item.setPackageName(content.packageName);
                    item.setIcon(getPackageManager().getDrawable(content.packageName, content.icon, content));
                    data.add(item);
                }
            } catch (Exception e) {
                Log.e("", "Parse Application Manager: " + e.toString());
            } catch (Error e2) {
                Log.e("", "Parse Application Manager 2: " + e2.toString());
            }
        }

        Collections.sort(data, new Comparator<PackageItem>() {
            public int compare(PackageItem o1, PackageItem o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        packageAdapter = new PackageAdapter(this, data);

//        //check if service is running and make sure service runs
//        if (!isMyServiceRunning(FliiikService.class)) {
//            Intent intent = new Intent(this, FliiikService.class);
//            this.startService(intent);
//        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                showAddGestureActivity();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    //Change Application

    private void changeApplication() {
        showCompleteDialog(new ListHolder(), Gravity.BOTTOM, packageAdapter, clickListener, itemClickListener,
                false);
    }

    //Dialog plus

    private void showCompleteDialog(Holder holder, int gravity, BaseAdapter adapter,
                                    OnClickListener clickListener, OnItemClickListener itemClickListener,
                                    boolean expanded) {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setHeader(R.layout.dialog_header)
                .setFooter(R.layout.dailog_footer)
                .setCancelable(true)
                .setGravity(gravity)
                .setAdapter(adapter)
                .setOnClickListener(clickListener)
                .setOnItemClickListener(itemClickListener)
                .setExpanded(expanded)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOverlayBackgroundResource(android.R.color.transparent)
                .create();
        dialog.show();
    }

    // CHECK SERVICE RUNNING

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    //NAVIGATION
    private void showAddGestureActivity() {
        Intent intent = new Intent(this, AddGestureActivity.class);
        startActivityForResult(intent, ADD_GESTURE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ADD_GESTURE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mAppList = GesturesDatabaseHelper.getInstance(getApplicationContext()).getAllGestures();
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public FliiikGesture getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.list_gesture_row, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();

            final FliiikGesture gesture = getItem(position);

            Log.i("ListView loading", "Action = " + gesture.action + " Position = " + position + " Package = " + gesture.packageName);

            holder.fk_title.setText(FliiikHelper.decodeActionString(gesture.action));
            holder.fk_switch.setChecked(gesture.status);

            holder.fk_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCurrentGesture = gesture;
                    setCurrentGestureEnable(isChecked);
                }
            });


            holder.fk_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentGesture = gesture;
                    changeApplication();
                }
            });

            if (gesture.packageName != null && gesture.packageName != "") {
                try {
                    ApplicationInfo content = mPackageManager.getApplicationInfo(gesture.packageName, 0);
                    holder.fk_package.setText(mPackageManager.getApplicationLabel(content).toString());
                    holder.fk_icon.setImageDrawable(mPackageManager.getDrawable(content.packageName, content.icon, content));
                } catch (Exception e) {
                    Log.e("", "Parse Application Manager: " + e.toString());
                } catch (Error e2) {
                    Log.e("", "Parse Application Manager 2: " + e2.toString());
                }
            } else {
                holder.fk_icon.setImageDrawable(getDrawable(android.R.drawable.ic_menu_edit));
                holder.fk_package.setText("Not Configured");
            }
            return convertView;
        }

        class ViewHolder {
            TextView fk_title;
            ImageView fk_icon;
            TextView fk_package;
            Switch fk_switch;
            View fk_more;

            public ViewHolder(View view) {
                fk_title = (TextView) view.findViewById(R.id.textGesture);
                fk_icon = (ImageView) view.findViewById(R.id.imageApp);
                fk_package = (TextView) view.findViewById(R.id.textApp);
                fk_switch = (Switch) view.findViewById(R.id.enableSwitch);
                fk_more = view.findViewById(R.id.more);

                view.setTag(this);
            }
        }
    }

    private void setCurrentGestureEnable(boolean isEnable) {
        mCurrentGesture.status = isEnable;
        GesturesDatabaseHelper.getInstance(getApplicationContext()).updateGesture(mCurrentGesture);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
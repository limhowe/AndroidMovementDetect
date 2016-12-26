package com.quandary.quandary.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.quandary.quandary.AddGestureActivity;
import com.quandary.quandary.MainActivity;
import com.quandary.quandary.R;
import com.quandary.quandary.db.FliiikGesture;
import com.quandary.quandary.db.GesturesDatabaseHelper;
import com.quandary.quandary.ui.PackageAdapter;
import com.quandary.quandary.ui.PackageItem;
import com.quandary.quandary.utils.FliiikHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EditFragment extends Fragment {

    OnGeustureConfigurationListener mListener;

    private List<FliiikGesture> mAppList;
    private EditFragment.AppAdapter mAdapter;

    private PackageAdapter packageAdapter;
    private List<PackageItem> data;

    OnClickListener clickListener;
    OnItemClickListener itemClickListener;

    static final int ADD_GESTURE_REQUEST = 1;  // The request code

    private SwipeMenuListView mListView;
    private PackageManager mPackageManager;

    private FliiikGesture mCurrentGesture;


    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment EditFragment.
     */
    public static EditFragment newInstance() {
        EditFragment fragment = new EditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPackageManager = getActivity().getPackageManager();
        mAdapter = new EditFragment.AppAdapter();


        final PackageManager pm = getActivity().getPackageManager();

        //Create Adapter
        data = new ArrayList<PackageItem>();

        //Get Application list
        List<ApplicationInfo> packs = pm.getInstalledApplications(0);
        for (ApplicationInfo content : packs) {
            try {
                if (pm.getLaunchIntentForPackage(content.packageName) != null) {
                    PackageItem item = new PackageItem();
                    item.setName(getActivity().getPackageManager().getApplicationLabel(content).toString());
                    item.setPackageName(content.packageName);
                    item.setIcon(getActivity().getPackageManager().getDrawable(content.packageName, content.icon, content));
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

        packageAdapter = new PackageAdapter(getActivity(), data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getActivity().setTitle("Gesture Management");


        mAppList = GesturesDatabaseHelper.getInstance(getActivity().getApplicationContext()).getAllGestures();
        mListView = (SwipeMenuListView) view.findViewById(R.id.listView);

        mListView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
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
                        GesturesDatabaseHelper.getInstance(getActivity().getApplicationContext()).deleteGesture(item);
                        mAppList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        mListener.onGestureUpdated();
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
                    GesturesDatabaseHelper.getInstance(getActivity().getApplicationContext()).updateGesture(mCurrentGesture);
                    mListener.onGestureUpdated();
                    mAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditFragment.OnGeustureConfigurationListener) {
            mListener = (EditFragment.OnGeustureConfigurationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingChangeListener");
        }
    }


    //NAVIGATION
    private void showAddGestureActivity() {
        Intent intent = new Intent(getActivity(), AddGestureActivity.class);
        startActivityForResult(intent, ADD_GESTURE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ADD_GESTURE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mAppList = GesturesDatabaseHelper.getInstance(getActivity().getApplicationContext()).getAllGestures();
                mAdapter.notifyDataSetChanged();
                mListener.onGestureUpdated();
            }
        }
    }


    /************ Start **********/

    //Change Application
    private void changeApplication() {
        showCompleteDialog(new ListHolder(), Gravity.BOTTOM, packageAdapter, clickListener, itemClickListener,
                false);
    }

    //Dialog plus

    private void showCompleteDialog(Holder holder, int gravity, BaseAdapter adapter,
                                    OnClickListener clickListener, OnItemClickListener itemClickListener,
                                    boolean expanded) {
        final DialogPlus dialog = DialogPlus.newDialog(getActivity())
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
                convertView = View.inflate(getActivity().getApplicationContext(),
                        R.layout.list_gesture_row, null);
                new EditFragment.AppAdapter.ViewHolder(convertView);
            }
            EditFragment.AppAdapter.ViewHolder holder = (EditFragment.AppAdapter.ViewHolder) convertView.getTag();

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
                holder.fk_icon.setImageDrawable(getActivity().getDrawable(android.R.drawable.ic_menu_edit));
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
        GesturesDatabaseHelper.getInstance(getActivity().getApplicationContext()).updateGesture(mCurrentGesture);
        mListener.onGestureUpdated();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    public interface OnGeustureConfigurationListener {
        void onGestureUpdated();
    }
}

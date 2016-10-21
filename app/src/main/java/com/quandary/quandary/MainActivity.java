package com.quandary.quandary;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.quandary.quandary.service.FliiikService;
import com.quandary.quandary.ui.PackageAdapter;
import com.quandary.quandary.ui.PackageItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PackageAdapter adapter;
    private List<PackageItem> data;

    OnClickListener clickListener;
    OnItemClickListener itemClickListener;

    ImageView imageApp;
    TextView textApp;
    Switch switchApp;

    String mPackageName = "";
    Boolean mSetEnabled = false;

    ConfigurationManager sharedConfigurationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageApp = (ImageView) findViewById(R.id.imageApp);
        textApp = (TextView) findViewById(R.id.textApp);
        switchApp = (Switch) findViewById(R.id.switch1);

        sharedConfigurationManager = new ConfigurationManager(getApplicationContext());

        clickListener = new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {
                switch (view.getId()) {
                    case R.id.footer_close_button :
                        dialog.dismiss();
                }

            }
        };

        itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                PackageItem itemSelected =  (PackageItem)item;
                String clickedAppName = itemSelected.getName();
                imageApp.setImageDrawable(itemSelected.getIcon());
                textApp.setText(clickedAppName);
                dialog.dismiss();
                Toast.makeText(MainActivity.this, clickedAppName + " selected", Toast.LENGTH_LONG).show();

                mPackageName = itemSelected.getPackageName();
                sharedConfigurationManager.setActionPackage(mPackageName);
            }
        };


        final PackageManager pm = getPackageManager();
        PackageItem calcItem = null;

        mSetEnabled = sharedConfigurationManager.getServiceEnabled();
        switchApp.setChecked(mSetEnabled);

        mPackageName = sharedConfigurationManager.getActionPackage();

        if (mPackageName != "") {
            try {
                if (pm.getLaunchIntentForPackage(mPackageName) != null) {
                    ApplicationInfo content = pm.getApplicationInfo(mPackageName,0);
                    String name  = getPackageManager().getApplicationLabel(content).toString();
                    Drawable icon = getPackageManager().getDrawable(content.packageName, content.icon, content);

                    calcItem = new PackageItem();
                    calcItem.setPackageName(mPackageName);
                    calcItem.setIcon(icon);
                    calcItem.setName(name);
                }
            } catch (Exception e) {
                Log.e("", "Parse Application Manager Step1: " + e.toString());
            } catch(Error e2) {
                Log.e("", "Parse Application Manager Step 22: " + e2.toString());
            }
        }

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

                    if( calcItem == null &&  content.packageName.toString().toLowerCase().contains("calcul")){
                        calcItem = item;
                    }
                }
            } catch (Exception e) {
                Log.e("", "Parse Application Manager: " + e.toString());
            } catch(Error e2) {
                Log.e("", "Parse Application Manager 2: " + e2.toString());
            }
        }

        Collections.sort(data, new Comparator<PackageItem>() {
            public int compare(PackageItem o1, PackageItem o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        adapter = new PackageAdapter(this,data);

        if (calcItem != null) {

            if (mPackageName == "") {
                mPackageName = calcItem.getPackageName();
                sharedConfigurationManager.setActionPackage(mPackageName);
            }
            imageApp.setImageDrawable(calcItem.getIcon());
            textApp.setText(calcItem.getName());
        }


        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.more);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeApplication();
            }
        });

        switchApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedConfigurationManager.setServiceEnabled(isChecked);
            }
        });


        //check if service is running and make sure service runs
        if (!isMyServiceRunning(FliiikService.class)) {
            Intent intent = new Intent(this,FliiikService.class);
            this.startService(intent);
        }
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
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    //Change Application

    private void changeApplication () {

        showCompleteDialog(new ListHolder(), Gravity.BOTTOM, adapter, clickListener, itemClickListener,
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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}

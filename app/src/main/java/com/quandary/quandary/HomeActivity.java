package com.quandary.quandary;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.quandary.quandary.db.GesturesDatabaseHelper;
import com.quandary.quandary.service.FliiikService;
import com.quandary.quandary.ui.draweritem.HeaderDrawerItem;
import com.quandary.quandary.ui.fragments.EditFragment;
import com.quandary.quandary.ui.fragments.HomeFragment;
import com.quandary.quandary.ui.fragments.SettingFragment;

public class HomeActivity extends AppCompatActivity implements EditFragment.OnGeustureConfigurationListener, SettingFragment.OnSettingChangeListener{

    private Drawer result = null;

    private int mCurrentDrawerId = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .addDrawerItems(
                        new HeaderDrawerItem().withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(2).withSelectable(true),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_edit).withIcon(GoogleMaterial.Icon.gmd_mode_edit).withIdentifier(3).withSelectable(true),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_game).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(4).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_video).withIcon(GoogleMaterial.Icon.gmd_video_library).withIdentifier(5).withSelectable(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(6).withSelectable(true),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_help).withIcon(GoogleMaterial.Icon.gmd_help).withIdentifier(7).withSelectable(false))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == mCurrentDrawerId) return false;

                            if (drawerItem.getIdentifier() == 2) {
                                mCurrentDrawerId = 2;
                                showHome();
                            } else if (drawerItem.getIdentifier() == 3) {
                                mCurrentDrawerId = 3;
                                showGestureConfig();
                            } else if (drawerItem.getIdentifier() == 5){
                                showVideoLibrary();
                                result.setSelection(mCurrentDrawerId);
                            } else  if (drawerItem.getIdentifier() == 6){
                                mCurrentDrawerId = 6;
                                showSetting();
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        result.setSelection(mCurrentDrawerId);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = HomeFragment.newInstance();
        ft.replace(R.id.mainFragment, f).commit();

        //check if service is running and make sure service runs
        if (!isMyServiceRunning(FliiikService.class)) {
            Intent intent = new Intent(this, FliiikService.class);
            intent.putExtra("FliiikCommand", "startService");
            this.startService(intent);
        }
    }

    // Navigation Methods

    private void showHome() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = HomeFragment.newInstance();
        ft.replace(R.id.mainFragment, f).commit();
    }

    private void showSetting() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = SettingFragment.newInstance();
        ft.replace(R.id.mainFragment, f).commit();
    }

    private void showGestureConfig() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = EditFragment.newInstance();
        ft.replace(R.id.mainFragment, f).commit();
    }

    private void showVideoLibrary() {
        String url = "http://www.youtube.com";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
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

    //Interaction Listeners

    @Override
    public void onGestureUpdated() {

    }

    @Override
    public void onSettingUpdated(float motion, float distance) {

    }
}

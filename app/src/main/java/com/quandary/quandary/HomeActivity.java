package com.quandary.quandary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialize.util.UIUtils;
import com.quandary.quandary.ui.draweritem.HeaderDrawerItem;

public class HomeActivity extends AppCompatActivity {

    private Drawer result = null;
    private AccountHeader headerResult = null;


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
                        new PrimaryDrawerItem().withName(R.string.drawer_item_game).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(3).withSelectable(true),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_video).withIcon(GoogleMaterial.Icon.gmd_video_library).withIdentifier(4).withSelectable(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(5).withSelectable(true),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_help).withIcon(GoogleMaterial.Icon.gmd_help).withIdentifier(6).withSelectable(true))
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }
}

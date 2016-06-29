package vn.edu.usth.swarmapplicationuiprototype3.DrawerActivity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.swarmapplicationuiprototype3.Activity.MainActivity;
import vn.edu.usth.swarmapplicationuiprototype3.SiteFragment.SiteFragment;
import vn.edu.usth.swarmapplicationuiprototype3.FunctionFragment.FunctionFragment;
import vn.edu.usth.swarmapplicationuiprototype3.FunctionMap.MapFragment;
import vn.edu.usth.swarmapplicationuiprototype3.AppModel.AppConstant;
import vn.edu.usth.swarmapplicationuiprototype3.R;


public class DrawerInterfaceActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ListView mProfileList;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter mNavDrawerListAdapter;
    private LinearLayout linearLayout;
    private Button surveyButton;
    private Button recordButton;
    private Button photoButton;
    private Button scriptButton;
    private boolean atMainInterface = false;
    private int functionPosition;
    private static final int PERMISSION_REQUEST_CODE_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filepath, AppConstant.SWARM_DIR);

        if (!file.exists()) {
            file.mkdirs();
        }


        setContentView(R.layout.drawer);

        linearLayout = (LinearLayout) findViewById(R.id.function_buttons);

        surveyButton = (Button) findViewById(R.id.button_survey);
        recordButton = (Button) findViewById(R.id.button_record);
        photoButton = (Button) findViewById(R.id.button_photo);
        scriptButton = (Button) findViewById(R.id.button_transcript);

        surveyButton.setOnClickListener(this);
        recordButton.setOnClickListener(this);
        photoButton.setOnClickListener(this);
        scriptButton.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        View view = View.inflate(this, R.layout.nav_header_main, null);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerList.addHeaderView(view);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                displayView(position);

            }
        });

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new NavDrawerItem("Home", R.drawable.ic_home_white_24dp));
        navDrawerItems.add(new NavDrawerItem("List of Sites", R.drawable.ic_list_white_24dp));
        navDrawerItems.add(new NavDrawerItem("Log out", R.drawable.ic_sentiment_dissatisfied_white_24dp));
        mNavDrawerListAdapter = new NavDrawerListAdapter(this, navDrawerItems);
        mDrawerList.setAdapter(mNavDrawerListAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Swarm Application");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.syncState();

        displayView(1);
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                break;
            case 1:
                fragment = new MapFragment(this);
                linearLayout.setVisibility(View.VISIBLE);
                atMainInterface = true;
                break;
            case 2:
                fragment = new SiteFragment(this);
                linearLayout.setVisibility(View.INVISIBLE);
                atMainInterface = false;
                break;
            case 3:
                fragment = new FunctionFragment(this, functionPosition);
                linearLayout.setVisibility(View.INVISIBLE);
                atMainInterface = false;
                break;
            default:
                break;
        }

        if (fragment != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (atMainInterface != true) {
            displayView(1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_survey:
                functionPosition = 0;
                displayView(3);
                break;
            case R.id.button_record:
                functionPosition = 1;
                displayView(3);
                break;
            case R.id.button_photo:
                functionPosition = 2;
                displayView(3);

                break;
            case R.id.button_transcript:
                functionPosition = 4;
                displayView(3);
                break;
        }
    }


}
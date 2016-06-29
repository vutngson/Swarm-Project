package vn.edu.usth.swarmapplicationuiprototype3.FunctionPhoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import vn.edu.usth.swarmapplicationuiprototype3.R;


public class FullScreenViewActivity extends Activity {
    private PhotoUtils utils;
    private FullScreenImageAdapter adapter;
    private CustomViewPager viewPager;


    public FullScreenViewActivity() {
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_fullscreen_view);
        this.viewPager = (CustomViewPager) this.findViewById(R.id.pager);
        Intent i = this.getIntent();

        String folderID = i.getStringExtra("folderID");
        this.utils = new PhotoUtils(this.getApplicationContext(), folderID);
        int position = i.getIntExtra("position", 0);
        this.adapter = new FullScreenImageAdapter(this.viewPager, this, this.utils.getPhotoFile());
        this.viewPager.setAdapter(this.adapter);
        this.viewPager.setCurrentItem(position);
    }
}
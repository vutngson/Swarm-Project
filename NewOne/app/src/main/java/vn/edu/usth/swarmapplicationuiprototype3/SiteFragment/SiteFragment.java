package vn.edu.usth.swarmapplicationuiprototype3.SiteFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import vn.edu.usth.swarmapplicationuiprototype3.R;

public class SiteFragment extends Fragment {
    private Context context;
    private ListView siteListView;
    private ArrayList<Site> mSiteArrayList;
    private SiteAdapter mSiteAdapter;
    private FloatingActionButton mFloatingActionButton;

    public SiteFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_site, container, false);
        siteListView = (ListView)rootView.findViewById(R.id.list_site);
        mFloatingActionButton = (FloatingActionButton)rootView.findViewById(R.id.fab_site);
        mSiteArrayList = new ArrayList<Site>();
        mSiteArrayList.add(new Site("58 Vương Thừa Vũ", "Thanh Xuân"));
        mSiteAdapter = new SiteAdapter(context, mSiteArrayList);
        siteListView.setAdapter(mSiteAdapter);
        return rootView;
    }
}

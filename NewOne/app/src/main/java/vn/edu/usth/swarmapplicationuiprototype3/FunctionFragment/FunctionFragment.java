package vn.edu.usth.swarmapplicationuiprototype3.FunctionFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.usth.swarmapplicationuiprototype3.FunctionPhoto.PhotoFragment;
import vn.edu.usth.swarmapplicationuiprototype3.FunctionScript.ScriptFragment;
import vn.edu.usth.swarmapplicationuiprototype3.FunctionSurvey.SurveyFragment;
import vn.edu.usth.swarmapplicationuiprototype3.R;
import vn.edu.usth.swarmapplicationuiprototype3.FunctionRecord.RecordFragment;

public class FunctionFragment extends Fragment {
    private Context context;
    private CantScrollViewPager mFunctionViewPager;
    private int currentPager;
    private TabLayout tabLayout;

    public FunctionFragment(Context context, int currentPager) {
        this.context = context;
        this.currentPager = currentPager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_functions, container, false);
        mFunctionViewPager = (CantScrollViewPager)rootView.findViewById(R.id.function_pager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        final TabLayout.Tab survey = tabLayout.newTab();
        final TabLayout.Tab record = tabLayout.newTab();
        final TabLayout.Tab photo = tabLayout.newTab();
        final TabLayout.Tab script = tabLayout.newTab();
        survey.setText("Survey");
        record.setText("Record");
        photo.setText("Photo");
        script.setText("Script");
        tabLayout.addTab(survey, 0);
        tabLayout.addTab(record, 1);
        tabLayout.addTab(photo, 2);
        tabLayout.addTab(script, 3);
        tabLayout.setTabTextColors(ContextCompat.getColorStateList(context, R.drawable.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.colorAccent));

        FragmentStatePagerAdapter fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        SurveyFragment surveyFragment1 = new SurveyFragment();
                        return surveyFragment1;
                    case 1:
                        RecordFragment recordFragment2 = new RecordFragment(position);
                        return  recordFragment2;
                    case 2:
                        PhotoFragment photoFragment = new PhotoFragment(context);
                        return photoFragment;
                    case 3:
                        ScriptFragment scriptFragment = new ScriptFragment(position);
                        return scriptFragment;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
        mFunctionViewPager.setAdapter(fragmentStatePagerAdapter);
        mFunctionViewPager.setCurrentItem(currentPager);
        mFunctionViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mFunctionViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }


}

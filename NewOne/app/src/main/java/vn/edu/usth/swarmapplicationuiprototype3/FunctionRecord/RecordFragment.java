package vn.edu.usth.swarmapplicationuiprototype3.FunctionRecord;


import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import it.sephiroth.android.library.tooltip.Tooltip;
import vn.edu.usth.swarmapplicationuiprototype3.AppModel.OnSwipeTouchListener;
import vn.edu.usth.swarmapplicationuiprototype3.R;

public class RecordFragment extends Fragment implements View.OnClickListener {
    private int fragmentPosition;
    private ScrollView scrollView;
    private int screenHeight;

    public RecordFragment() {
    }

    public RecordFragment(int fragmentPosition) {
        this.fragmentPosition = fragmentPosition;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scroll_record,container,false);
        setHasOptionsMenu(true);

        //Fragments
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RecordFragmentAddress recordFragmentAddress = new RecordFragmentAddress();
        fragmentTransaction.add(R.id.record_address, recordFragmentAddress);
        RecordFragmentType recordFragmentType = new RecordFragmentType();
        fragmentTransaction.add(R.id.record_type, recordFragmentType);
        RecordFragmentComment recordFragmentComment = new RecordFragmentComment();
        fragmentTransaction.add(R.id.record_comment, recordFragmentComment);
        fragmentTransaction.commit();

        //Scroll view
        scrollView = (ScrollView)rootView.findViewById(R.id.scrollView);
        RelativeLayout layout = (RelativeLayout)rootView.findViewById(R.id.layout1);
        RelativeLayout layout2 = (RelativeLayout)rootView.findViewById(R.id.layout2);
        RelativeLayout layout3 = (RelativeLayout)rootView.findViewById(R.id.layout3);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        final float scale = getResources().getDisplayMetrics().density;
        int tabHeight = (int) (45 * scale + 0.5f);
        Log.i("ActionBar", tabHeight + "");
        screenHeight = size.y - getStatusBarHeight() - getActionBarHeight() - tabHeight;
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = screenHeight;
        ViewGroup.LayoutParams params2 = layout2.getLayoutParams();
        params2.height = screenHeight;
        ViewGroup.LayoutParams params3 = layout3.getLayoutParams();
        params3.height = screenHeight;
        scrollView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {

            public void onSwipeTop() {
                scrollView.smoothScrollBy(0, +screenHeight);
            }

            public void onSwipeBottom() {
                scrollView.smoothScrollBy(0, -screenHeight);
            }

        });

        Button button = (Button) rootView.findViewById(R.id.button_downward1);
        button.setOnClickListener(this);
        Button button2 = (Button) rootView.findViewById(R.id.button_upward1);
        button2.setOnClickListener(this);
        Button button3 = (Button) rootView.findViewById(R.id.button_downward2);
        button3.setOnClickListener(this);
        Button button4 = (Button) rootView.findViewById(R.id.button_upward2);
        button4.setOnClickListener(this);

        Tooltip.dbg = true;
        return rootView;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i("ActionBar", result + "");
        return result;
    }

    public int getActionBarHeight(){
        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        Log.i("ActionBar", mActionBarSize + "");

        return  mActionBarSize;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_downward1:
                scrollView.smoothScrollTo(0,screenHeight );
                break;
            case R.id.button_upward1:
                scrollView.smoothScrollTo(0, 0);
                break;
            case R.id.button_downward2:
                scrollView.smoothScrollTo(0, screenHeight*2);
                break;
            case R.id.button_upward2:
                scrollView.smoothScrollTo(0, screenHeight);
                break;
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_record, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help_record:
                View view = getActivity().findViewById(R.id.help_record);
                Tooltip.make(getActivity(),
                        new Tooltip.Builder(101)
                                .anchor(view, Tooltip.Gravity.BOTTOM)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, true)
                                        .outsidePolicy(true, true),20000)
                                .activateDelay(900)
                                .showDelay(200)
                                .text("This is Record Interface. Press help button next to what you should record to have more information." +
                                        " To go to next pages either press directional buttons or swipe up/down")
                                .withStyleId(R.style.ToolTipLayoutHoianStyle)
                                .maxWidth(800)
                                .withArrow(true)
                                .withOverlay(true).build()
                ).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

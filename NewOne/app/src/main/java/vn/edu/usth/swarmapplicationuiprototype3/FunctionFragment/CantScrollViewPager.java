package vn.edu.usth.swarmapplicationuiprototype3.FunctionFragment;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import org.osmdroid.views.MapView;


public class CantScrollViewPager extends ViewPager {

    public CantScrollViewPager(Context context) {
        super(context);
    }

    public CantScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if(v instanceof MapView){
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

}
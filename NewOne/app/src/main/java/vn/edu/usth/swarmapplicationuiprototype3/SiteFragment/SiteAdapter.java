package vn.edu.usth.swarmapplicationuiprototype3.SiteFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vn.edu.usth.swarmapplicationuiprototype3.SiteFragment.Site;
import vn.edu.usth.swarmapplicationuiprototype3.R;


public class SiteAdapter extends BaseAdapter {
    private Context context;
    ArrayList<Site> itemList = new ArrayList<Site>();

    public SiteAdapter(Context context, ArrayList<Site> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Site getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.site_item, parent, false);

        }

        String siteAddress = itemList.get(position).getNumberAndStreet();
        String siteDistrict = itemList.get(position).getDistrict();

        TextView siteAddressTextView = (TextView)convertView.findViewById(R.id.list_site_address);
        TextView siteDistrictTextView = (TextView) convertView.findViewById(R.id.list_site_district);


        siteAddressTextView.setText(siteAddress);
        siteDistrictTextView.setText(siteDistrict);

        return convertView;
    }


}

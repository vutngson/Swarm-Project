package vn.edu.usth.swarmapplicationuiprototype3.FunctionMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cocoahero.android.geojson.Feature;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import vn.edu.usth.swarmapplicationuiprototype3.AppModel.AppConstant;
import vn.edu.usth.swarmapplicationuiprototype3.R;

public class MapFragment extends Fragment {
    private MapView mapView;
    private Context context;
    public static final GeoPoint HOANKIEMLAKE = new GeoPoint(21.023999904, 105.851496594);
    //first parameter indicates the folder inside the zip file containing the atlas.
    String atlasName = "MapQuest";
    String atlasExtension = ".jpg";
    int tileSizePixels = 350;
    int minZoom = 13;
    int maxZoom = 18;
    private ArrayList<OverlayItem> anotherOverlayItemArray;
    private ItemizedOverlayWithFocus<OverlayItem> anotherItemizedIconOverlay;
    boolean isNetworkEnabled = false;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
    private ImageButton imageButton;
    private ImageButton imageButtonNewSite;
    private String siteAddress;
    private JSONArray jsonArray;
    private Snackbar snackbarRecord;
    private ItemizedIconOverlay.OnItemGestureListener<OverlayItem> onItemGestureListener;
    private int activeSite;

    public MapFragment() {
    }

    public MapFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mapView != null){
            try {
                mapView.getController().animateTo(AppConstant.getGeo());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View rootView = inflater.inflate(R.layout.fragment_map_buttons, container, false);
        final RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.maplayout);
        if (isOnline())
            new GeocodeAsyncTask().execute(21.023999904, 105.851496594);
        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.button_get_location);
        ImageButton imageButtonNewSite = (ImageButton) rootView.findViewById(R.id.button_get_new_site);


        mapView = (MapView) rootView.findViewById(R.id.mapinterfacefunctionbuttons);
        showMapContent(mapView);
        //--- Create Another Overlay for multi marker
        anotherOverlayItemArray = new ArrayList<OverlayItem>();
        anotherOverlayItemArray.add(new OverlayItem("Hoan Kiem Lake", "sample", new GeoPoint(21.023999904, 105.851496594)));

        try {

            jsonArray = AppConstant.readFileSiteList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    anotherOverlayItemArray.add(new OverlayItem(json_data.getString("address"),
                            json_data.getString("id"), new GeoPoint(
                            json_data.getDouble("lat"),
                            json_data.getDouble("lon"))));
                    try {
                        if(json_data.getString("id").equals(AppConstant.readFileID())){

                            activeSite = i;
                            Log.i("ActiveSite", activeSite +"");
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(json_data.getString("address"));

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


        onItemGestureListener =  new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {

                anotherItemizedIconOverlay.setFocusedItem(index);
                Log.i("MapCheck", item.getTitle());
                Log.i("MapCheck", item.getSnippet());
                try {
                    updateActiveFile(item.getSnippet(), item.getPoint(), item.getTitle());
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(item.getTitle());
                    Log.i("ActivePress", index + "");
                    snackbarRecord = Snackbar.make(relativeLayout, item.getTitle() + " is selected", Snackbar.LENGTH_SHORT);
                    snackbarRecord.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        };


        anotherItemizedIconOverlay
                = new ItemizedOverlayWithFocus<>(getContext(), anotherOverlayItemArray, onItemGestureListener);


        anotherItemizedIconOverlay.setFocusItemsOnTap(true);
        if (activeSite != 0)
        anotherItemizedIconOverlay.setFocusedItem(activeSite + 1);

        mapView.getOverlays().add(anotherItemizedIconOverlay);
        mapView.invalidate();


        //Add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mapView);
        mapView.getOverlays().add(myScaleBarOverlay);

        gpsMyLocationProvider = new GpsMyLocationProvider(getContext());
        gpsMyLocationProvider.setLocationUpdateMinTime(20);
        gpsMyLocationProvider.startLocationProvider(new IMyLocationConsumer() {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGPS()) {
                    showSettingsAlert();
                } else {
                    Location currentLocation = gpsMyLocationProvider.getLastKnownLocation();
                    if (currentLocation != null) {
                        if (isOnline())
                            new GeocodeAsyncTask().execute(currentLocation.getLatitude(), currentLocation.getLongitude());
                        IGeoPoint geoPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mapView.getController().setZoom(17);
                        mapView.getController().animateTo(geoPoint);
                        mapView.invalidate();
                    } else {
                        Toast.makeText(getContext(), "Unable to get location at this time", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        imageButtonNewSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGPS()) {
                    showSettingsAlert();
                } else {
                    Location currentLocation = gpsMyLocationProvider.getLastKnownLocation();
                    if (currentLocation != null) {
                        String location = "New Site";
                        if (isOnline()) {
                            location = getLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                        }
                        IGeoPoint geoPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                        String id = AppConstant.generateSiteID();
                        try {
                            saveFileSites(id,
                                    location,
                                    currentLocation.getLatitude(),
                                    currentLocation.getLongitude());
                            updateActiveFile(id, geoPoint, location);
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(location);
                            snackbarRecord = Snackbar.make(relativeLayout, location + " is selected", Snackbar.LENGTH_SHORT);
                            snackbarRecord.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        OverlayItem overlayItem = new OverlayItem(location,id,new GeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude()));
                        ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
                        overlayItems.add(overlayItem);
                        ItemizedOverlayWithFocus<OverlayItem> overlayItemItemizedOverlayWithFocus = new ItemizedOverlayWithFocus<OverlayItem>(getContext(),
                                overlayItems,onItemGestureListener);

                        anotherItemizedIconOverlay.unSetFocusedItem();
                        overlayItemItemizedOverlayWithFocus.setFocusItemsOnTap(true);
                        overlayItemItemizedOverlayWithFocus.setFocusedItem(0);
                        mapView.getOverlays().add(overlayItemItemizedOverlayWithFocus);
                        mapView.getController().setZoom(17);
                        mapView.getController().animateTo(geoPoint);
                        mapView.invalidate();

                    } else {
                        Toast.makeText(getContext(), "Unable to get location at this time", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        return rootView;
    }

    private void saveFileSites(String id, String address, Double lat, Double lon) throws JSONException, IOException {
        JSONArray fileToSave = AppConstant.readFileSiteList();
        JSONObject jsonObject = new JSONObject();
        if (fileToSave == null)
            fileToSave = new JSONArray();

        jsonObject.put("id", id);
        jsonObject.put("address", address);
        jsonObject.put("lat", lat);
        jsonObject.put("lon", lon);

        fileToSave.put(jsonObject);

        String jsonString = fileToSave.toString();

        byte[] jsonArray = jsonString.getBytes();

        String fileFolder = AppConstant.SWARM_DIR + File.separator + AppConstant.DATAFOLDER;
        File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
        if (!root.exists()) {
            root.mkdirs();
        }
        File fileToSaveJson = new File(root, AppConstant.SITELIST);

        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(fileToSaveJson));
            bos.write(jsonArray);
            bos.flush();
            bos.close();

        } catch (FileNotFoundException e4) {
            // TODO Auto-generated catch block
            e4.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateActiveFile(String siteID, IGeoPoint point, String address) throws JSONException {
        JSONObject fileToSave = null;

        com.cocoahero.android.geojson.Point geoPoint = new com.cocoahero.android.geojson.Point(point.getLatitude(), point.getLongitude());

        Feature feature = new Feature(geoPoint);
        feature.setIdentifier(siteID);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("address", address);
        feature.setProperties(jsonObject);

        try {
            fileToSave = feature.toJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = fileToSave.toString();
        byte[] jsonArray = jsonString.getBytes();

        String fileFolder = AppConstant.SWARM_DIR + File.separator + AppConstant.DATAFOLDER;
        File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
        if (!root.exists()) {
            root.mkdirs();
        }
        File fileToSaveJson = new File(root, AppConstant.ACTIVESITE);

        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(fileToSaveJson));
            bos.write(jsonArray);
            bos.flush();
            bos.close();

        } catch (FileNotFoundException e4) {
            // TODO Auto-generated catch block
            e4.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("IO", "Write File" + siteID);
    }

    private void showMapContent(MapView mapView) {
        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);
        mapView.setTileSource(new XYTileSource(atlasName, minZoom, maxZoom, tileSizePixels, atlasExtension, new String[]{
                "http://otile1.mqcdn.com/tiles/1.0.0/map/",
                "http://otile2.mqcdn.com/tiles/1.0.0/map/",
                "http://otile3.mqcdn.com/tiles/1.0.0/map/",
                "http://otile4.mqcdn.com/tiles/1.0.0/map/"}));
        IMapController mapViewController = mapView.getController();
        mapViewController.setZoom(17);
        mapViewController.setCenter(HOANKIEMLAKE);

    }

    private boolean checkGPS() {
        locationManager = (LocationManager) getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            return false;
        } else {
            return true;
        }


    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_functions, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class GeocodeAsyncTask extends AsyncTask<Double, Void, Address> {

        String errorMessage = "";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Address doInBackground(Double... none) {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = null;

            double latitude = none[0];
            double longitude = none[1];

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException ioException) {
                errorMessage = "Service Not Available";
                Log.e("error", errorMessage, ioException);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage = "Invalid Latitude or Longitude Used";
                Log.e("error", errorMessage + ". " +
                        "Latitude = " + latitude + ", Longitude = " +
                        longitude, illegalArgumentException);
            }

            if (addresses != null && addresses.size() > 0)
                return addresses.get(0);

            return null;
        }

        protected void onPostExecute(Address address) {
            if (address == null) {

            } else {
                String addressName = "";
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressName += address.getAddressLine(i) + ", ";
                }
                siteAddress = address.getAddressLine(0) + ", " + address.getAddressLine(1);
                Toast.makeText(getContext(), siteAddress, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private String getLocation(double latitude, double longitude) {
        String location = null;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ioException) {
            String errorMessage = "Service Not Available";
            Log.e("error", errorMessage, ioException);
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException illegalArgumentException) {
            String errorMessage = "Invalid Latitude or Longitude Used";
            Log.e("error", errorMessage + ". " +
                    "Latitude = " + latitude + ", Longitude = " +
                    longitude, illegalArgumentException);
        }

        if (addresses != null && addresses.size() > 0)
            return addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1);

        return "New Site";
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean b = netInfo != null && netInfo.isConnectedOrConnecting();
        if (b)
            Log.i("Internet", "online");
        return b;
    }
}
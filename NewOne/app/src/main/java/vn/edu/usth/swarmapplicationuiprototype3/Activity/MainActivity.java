package vn.edu.usth.swarmapplicationuiprototype3.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.swarmapplicationuiprototype3.R;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private MapView mapView;
    private Button buttonLogin;
    public static final GeoPoint HOANKIEMLAKE = new GeoPoint(21.023999904, 105.851496594);
    //first parameter indicates the folder inside the zip file containing the atlas.
    private String atlasName = "MapQuest";
    private String atlasExtension = ".jpg";
    private int tileSizePixels = 350;
    private int minZoom = 13;
    private int maxZoom = 18;
    private GpsMyLocationProvider gpsMyLocationProvider;
    private ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
    private ImageButton imageButton;
    private IMapController mapViewController;
    boolean isNetworkEnabled = false;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        Log.d("States", "MainActivity: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        IMyLocationConsumer iMyLocationConsumer = new IMyLocationConsumer() {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                ResourceProxyImpl mResourceProxy = new ResourceProxyImpl(getApplicationContext());
                int lat = (int) (location.getLatitude());
                int lng = (int) (location.getLongitude());
                if (mMyLocationOverlay == null) {
                    final ArrayList<OverlayItem> items = new ArrayList<>();
                    items.add(new OverlayItem("Current Location", " ",
                            new GeoPoint(location)));

                    mMyLocationOverlay = new ItemizedOverlayWithFocus<>(items,
                            new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                                @Override
                                public boolean onItemSingleTapUp(final int index, final OverlayItem item) {

                                    Toast.makeText(getApplicationContext(), "Location Changed", Toast.LENGTH_SHORT).show();

                                    return true;
                                }

                                @Override
                                public boolean onItemLongPress(final int index, final OverlayItem item) {
                                    return false;
                                }
                            }, mResourceProxy);

                    mMyLocationOverlay.setFocusItemsOnTap(true);
                    mMyLocationOverlay.setFocusedItem(0);
                    mapView.getOverlays().add(mMyLocationOverlay);
                    mapView.getController().setZoom(20);
                    mapView.invalidate();
                }

            }
        };
        gpsMyLocationProvider = new GpsMyLocationProvider(this);
        gpsMyLocationProvider.setLocationUpdateMinTime(20);
        gpsMyLocationProvider.startLocationProvider(iMyLocationConsumer);

        Log.d("States", "MainActivity: onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        gpsMyLocationProvider.stopLocationProvider();
        Log.d("States", "MainActivity: onStop()");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapinterfaceloginbutton);
        buttonLogin = (Button) findViewById(R.id.button_login);
        imageButton = (ImageButton) findViewById(R.id.button_record_fragment_pause);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        showMapContent(mapView);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkGPS()) {
                    showSettingsAlert();
                } else {
                    Location currentLocation = gpsMyLocationProvider.getLastKnownLocation();
                    if (currentLocation != null) {
                        IGeoPoint geoPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mapView.getController().setZoom(17);
                        mapView.getController().animateTo(geoPoint);
                        mapView.invalidate();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to get location at this time", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int alreadyHasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            int alreadyHasStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            List<String> permissions = new ArrayList<String>();
            if (alreadyHasLocationPermission != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (alreadyHasStoragePermission != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void showMapContent(MapView mapView) {
        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(this, mapView);
        mapView.getOverlays().add(myLocationNewOverlay);
        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);
        mapView.setTileSource(new XYTileSource(atlasName, minZoom, maxZoom, tileSizePixels, atlasExtension, new String[]{
                "http://otile1.mqcdn.com/tiles/1.0.0/map/",
                "http://otile2.mqcdn.com/tiles/1.0.0/map/",
                "http://otile3.mqcdn.com/tiles/1.0.0/map/",
                "http://otile4.mqcdn.com/tiles/1.0.0/map/"}));
        mapViewController = mapView.getController();
        mapViewController.setZoom(17);
        mapViewController.setCenter(HOANKIEMLAKE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private boolean checkGPS() {
        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
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
}

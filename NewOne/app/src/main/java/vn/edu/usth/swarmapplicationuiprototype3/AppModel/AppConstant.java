package vn.edu.usth.swarmapplicationuiprototype3.AppModel;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AppConstant {


    //Photo Gallery
    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 1; // in dp

    // supported file formats
    public static final List<String> IMAGE_EXTENSION = Arrays.asList("jpg", "jpeg",
            "png");

    public static final String SWARM_DIR = "SwarmProject";

    // SD card image directory
    public static final String SURVEY_DIR = "Survey";

    // SD card image directory
    public static final String RECORD_DIR = "Records";

    // SD card image directory
    public static final String PHOTO_ALBUM = "Photos";

    // SD card image directory
    public static final String SCRIPT_DIR = "Script";

    public static final String DATAFOLDER = "data";

    public static final String ACTIVESITE = "active.txt";

    public static final String SITELIST = "sites.txt";


    public static String readFileID() throws IOException, JSONException {
        JSONObject jsonOb = null;
        String id = null;

        String fileFolder = AppConstant.SWARM_DIR + File.separator + DATAFOLDER;
        File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
        if (!root.exists()) {
            root.mkdirs();
        }
        File fileToSaveJson = new File(root, ACTIVESITE);
        if (fileToSaveJson.exists()) {

            InputStream in = new FileInputStream(fileToSaveJson);

            GeoJSONObject gJSOn = GeoJSON.parse(in);
            jsonOb = gJSOn.toJSON();
            id = jsonOb.getString("id");
            Log.i("IO", id);
            return id;

        } else {
            fileToSaveJson.createNewFile();
        }
        return id;


    }


    public static JSONObject readActiveSite() throws IOException, JSONException {
        JSONObject jsonOb = null;
        String id = null;

        String fileFolder = AppConstant.SWARM_DIR + File.separator + DATAFOLDER;
        File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
        if (!root.exists()) {
            root.mkdirs();
        }
        File fileToSaveJson = new File(root, ACTIVESITE);
        if (fileToSaveJson.exists()) {

            InputStream in = new FileInputStream(fileToSaveJson);

            GeoJSONObject gJSOn = GeoJSON.parse(in);
            jsonOb = gJSOn.toJSON();

            return jsonOb;

        } else {
            fileToSaveJson.createNewFile();
        }
        return jsonOb;

    }

    public static String readFileAddress() throws IOException, JSONException {
        JSONObject jsonOb = null;
        String id = null;

        String fileFolder = AppConstant.SWARM_DIR + File.separator + DATAFOLDER;
        File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
        if (!root.exists()) {
            root.mkdirs();
        }
        File fileToSaveJson = new File(root, ACTIVESITE);
        if (fileToSaveJson.exists()) {

            InputStream in = new FileInputStream(fileToSaveJson);

            GeoJSONObject gJSOn = GeoJSON.parse(in);
            jsonOb = gJSOn.toJSON();
            id = jsonOb.getJSONObject("properties").getString("address");
            Log.i("IO", id);
            return id;

        } else {
            fileToSaveJson.createNewFile();
        }
        return id;


    }

    public static String generateSiteID() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String siteID = timeStamp;
        return siteID;
    }

    public static JSONArray readFileSiteList() throws IOException, JSONException {
        JSONArray jsonOb = null;

        String fileFolder = AppConstant.SWARM_DIR + File.separator + AppConstant.DATAFOLDER;
        File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
        if (!root.exists()) {
            root.mkdirs();
        }
        File fileToSaveJson = new File(root, AppConstant.SITELIST);
        if (fileToSaveJson.exists()) {

            InputStream in = new FileInputStream(fileToSaveJson);
            int size = in.available();

            byte[] buffer = new byte[size];

            in.read(buffer);

            in.close();

            String json = new String(buffer, "UTF-8");
            if (TextUtils.isEmpty(json))
                return jsonOb;
            jsonOb = new JSONArray(json);
            return jsonOb;
        } else {
            fileToSaveJson.createNewFile();
        }

        return jsonOb;


    }

    public static void updateFileActive(String address) throws JSONException, IOException {
        JSONObject fileToSave = null;

        fileToSave = readActiveSite();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("address", address);
        fileToSave.put("properties", jsonObject);
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

    }

    public static void updateFileSites(String address) throws JSONException, IOException {
        JSONArray fileToSave = AppConstant.readFileSiteList();
        for (int i = 0; i < fileToSave.length(); i++) {
            JSONObject jObject = fileToSave.getJSONObject(i);
            if (readFileID().equals(jObject.getString("id"))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    fileToSave.remove(i);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", AppConstant.readFileID());
                jsonObject.put("address", address);
                jsonObject.put("lat", jObject.getString("lat"));
                jsonObject.put("lon", jObject.getString("lon"));
                fileToSave.put(jsonObject);
                break;
            }
        }

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

    public static GeoPoint getGeo() throws JSONException, IOException {
        GeoPoint geoPoint = new GeoPoint(21.023999904, 105.851496594);
        JSONArray fileToSave = AppConstant.readFileSiteList();
        if (fileToSave != null) {
            for (int i = 0; i < fileToSave.length(); i++) {
                JSONObject jObject = fileToSave.getJSONObject(i);
                if (readFileID().equals(jObject.getString("id"))) {
                    geoPoint = new GeoPoint(jObject.getDouble("lat"), jObject.getDouble("lon"));
                    return geoPoint;
                }

            }
        }

        return geoPoint;

    }


}
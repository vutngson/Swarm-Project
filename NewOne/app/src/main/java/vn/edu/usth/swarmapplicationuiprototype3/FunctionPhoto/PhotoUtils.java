package vn.edu.usth.swarmapplicationuiprototype3.FunctionPhoto;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.usth.swarmapplicationuiprototype3.AppModel.AppConstant;

public class PhotoUtils {

    private Context _context;
    private static String AppFolder = AppConstant.SWARM_DIR;
    private static String PhotoFolder = AppConstant.PHOTO_ALBUM;
    private String folderID;

    public PhotoUtils(Context _context, String folderID) {
        this._context = _context;
        this.folderID = folderID;
    }


    public String getFileFolder() {
         String fileFolder = AppFolder + "/" + folderID + "/"
                + PhotoFolder;
        return fileFolder;
    }

    public File getFolder(){
        File folder = new File(Environment.getExternalStorageDirectory() ,
                getFileFolder());

        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }
        return folder;
    }

    //Photo *****************************************************************************************
    public ArrayList<String> getPhotoFile() {
        ArrayList<String> filePaths = new ArrayList<String>();

        File directory = getFolder();

        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();

            // Check for count
            if (listFiles.length > 0) {

                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {

                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();

                    // check for supported file extension
                    if (IsSupportedFile(filePath)) {
                        // Add image path to array list
                        filePaths.add(filePath);
                    }
                }
            } else {
//                // image directory is empty
//                Toast.makeText(
//                        _context,
//                        getFileFolder()
//                                + " is empty!!!",
//                        Toast.LENGTH_LONG).show();
            }

        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(_context);
            alert.setTitle("Error!");
            alert.setMessage(getFileFolder()
                    + " directory path is not valid!");
            alert.setPositiveButton("OK", null);
            alert.show();

        }

        return filePaths;
    }
    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        return AppConstant.IMAGE_EXTENSION
                .contains(ext.toLowerCase(Locale.getDefault()));

    }

    /*
     * getting screen width
     */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
}
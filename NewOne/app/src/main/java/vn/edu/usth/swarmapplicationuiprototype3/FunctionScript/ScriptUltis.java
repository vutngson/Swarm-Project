package vn.edu.usth.swarmapplicationuiprototype3.FunctionScript;

import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import vn.edu.usth.swarmapplicationuiprototype3.AppModel.AppConstant;


public class ScriptUltis {
    private static String AppFolder = AppConstant.SWARM_DIR;
    private static String ScriptFolder = AppConstant.SCRIPT_DIR;
    private static String fileEXT = ".txt";


    public static void WriteFile(String folderID, String sFileName, String sBody) {
        try {
            String fileFolder = AppFolder + File.separator + folderID + File.separator +
                    ScriptFolder;
            File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, sFileName + fileEXT);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(sBody.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.i("WriteFile", "Saved File");
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static String ReadFile(String folderID, String sFileName) {
        String fileFolder = AppFolder + File.separator + folderID + File.separator +
                ScriptFolder;
        File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
        if (!root.exists()) {
            root.mkdirs();
        }
        File file = new File(root, sFileName + fileEXT);
        if(!file.exists()){
            return null;
        }

        StringBuilder text = new StringBuilder();

        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
        Log.i("ReadFile", "Read File");

        return text.toString();
    }
}



package vn.edu.usth.swarmapplicationuiprototype3.FunctionPhoto;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.sephiroth.android.library.tooltip.Tooltip;
import vn.edu.usth.swarmapplicationuiprototype3.AppModel.AppConstant;
import vn.edu.usth.swarmapplicationuiprototype3.R;

public class PhotoFragment extends Fragment {
    private ImageAdapter myImageAdapter;
    private final int REQUEST_IMAGE_CAPTURE = 300;
    private final int PERMISSION_REQUEST_CODE = 100;

    private String mImageFileLocation;
    private FloatingActionButton takephoto;
    GridView gridView;
    private int columnWidth;
    private PhotoUtils utils;
    private Context context;
    private View rootView;
    private String folderID ;

    public PhotoFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        try {
            folderID = AppConstant.readFileID();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        utils = new PhotoUtils(context, folderID);

        gridView = (GridView) rootView.findViewById(R.id.gridview);
        InitilizeGridLayout();
        myImageAdapter = new ImageAdapter(context, columnWidth);
        gridView.setAdapter(myImageAdapter);
        gridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, position + "", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), FullScreenViewActivity.class);
                i.putExtra("folderID", folderID);
                i.putExtra("position", position);
                startActivity(i);
            }
        });

        // Capture ListView item click
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = gridView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                myImageAdapter.toggleSelection(position);
                Toast.makeText(context, "Select Item", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = myImageAdapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                String selecteditem = myImageAdapter
                                        .getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                myImageAdapter.remove(selecteditem);
                            }
                        }
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.activity_main, menu);

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                myImageAdapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }


        });


        takephoto = (FloatingActionButton) rootView.findViewById(R.id.take_photo_button);
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    int alreadyHasStoragePermission = ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CAMERA);
                    List<String> permissions = new ArrayList<String>();

                    if (alreadyHasStoragePermission != PackageManager.PERMISSION_GRANTED)
                        permissions.add(Manifest.permission.CAMERA);

                    if (!permissions.isEmpty()) {
                        requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_CODE);
                    } else {

                        takePhoto();
                    }

                } else{

                    takePhoto();
                }

            }
        });


        initializeGridViewAdapter();
        rootView.clearAnimation();
        return rootView;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar snackbarPermission = Snackbar.make(rootView, "Permission Granted", Snackbar.LENGTH_SHORT);
                    snackbarPermission.show();
                } else {
                    Snackbar snackbarPermissionDeny = Snackbar.make(rootView, "Photo can not be used until permission granted", Snackbar.LENGTH_SHORT);
                    snackbarPermissionDeny.show();
                }
                break;

        }
    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
        gridView.setSelector(R.drawable.highlight);
    }

    private void initializeGridViewAdapter() {
//        File[] files = mGalleryFolder.listFiles();
//        for (File file : files) {
//            myImageAdapter.add(file.getAbsolutePath());
//        }
        myImageAdapter.setItemList(utils.getPhotoFile());
    }

    File createImageFile() throws IOException {
        String targetPath = Environment.getExternalStorageDirectory() +
                File.separator + utils.getFileFolder();

        Toast.makeText(context, targetPath, Toast.LENGTH_LONG).show();
        File mGalleryFolder = new File(targetPath);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";

        File image = File.createTempFile(imageFileName, ".jpg", mGalleryFolder);
        mImageFileLocation = image.getAbsolutePath();
        Log.i("FilePath", mImageFileLocation);
        return image;
    }

    public void takePhoto() {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(callCameraApplicationIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            myImageAdapter.add(mImageFileLocation);
            Toast.makeText(context, "Successfully Taken A Photo", Toast.LENGTH_LONG).show();

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode != Activity.RESULT_OK) {
            Toast.makeText(context, "Photo Cancel!!", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_photo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help_photo:
                View view = getActivity().findViewById(R.id.help_photo);
                Tooltip.make(getActivity(),
                        new Tooltip.Builder(101)
                                .anchor(view, Tooltip.Gravity.BOTTOM)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, true)
                                        .outsidePolicy(true, true),20000)
                                .activateDelay(900)
                                .showDelay(200)
                                .text("This is Photo Interface. Press Photo Button to take picture. Tab on single photo to get fullscreen. " +
                                        "Press and hold to choose to delete Photos")
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



package vn.edu.usth.swarmapplicationuiprototype3.FunctionRecord;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.tooltip.Tooltip;
import vn.edu.usth.swarmapplicationuiprototype3.AppModel.AppConstant;
import vn.edu.usth.swarmapplicationuiprototype3.R;

public class RecordFragmentType extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    private ImageButton buttonRecordPlay;
    private ImageButton buttonRecordStop;
    private Button buttonSaveRecord;
    private Button buttonDeleteRecord;
    private RecorderUtils recorderUtils;
    private View rootView;
    private Snackbar snackbarRecord;
    private TextView textExist;
    private ImageButton buttonPlay;
    private ImageButton buttonPause;
    private ImageButton buttonStop;
    private ImageButton buttonReplay;
    private Button buttonDeleteFile;
    private MediaPlayer mp;
    private SeekBar songProgressBar;
    private Handler mHandler = new Handler();
    private final int PERMISSION_REQUEST_CODE = 100;
    private boolean isPlaying = false;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private String AUDIO_RECORDER_FOLDER;
    private static final String fileName = "Type";
    private  String filePath;
    private String folderID = "test";


    public RecordFragmentType() {
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_record_2, container, false);
        recorderUtils = new RecorderUtils(getContext(), fileName, folderID);

        try {
            folderID = AppConstant.readFileID();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recorderUtils = new RecorderUtils(getContext(), fileName, folderID);


        AUDIO_RECORDER_FOLDER = AppConstant.SWARM_DIR + "/" + folderID + "/"
                + AppConstant.RECORD_DIR;
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                AUDIO_RECORDER_FOLDER + "/" + fileName + AUDIO_RECORDER_FILE_EXT_WAV;

        Button buttonHelp = (Button) rootView.findViewById(R.id.button_type_help);
        buttonHelp.setOnClickListener(this);

        buttonRecordPlay = (ImageButton) rootView.findViewById(R.id.button_record_fragment_record);
        buttonRecordPlay.setOnClickListener(this);
        buttonRecordStop = (ImageButton) rootView.findViewById(R.id.button_record_fragment_record_stop);
        buttonRecordStop.setOnClickListener(this);
        buttonSaveRecord = (Button) rootView.findViewById(R.id.button_record_fragment_record_done);
        buttonSaveRecord.setVisibility(View.INVISIBLE);
        buttonSaveRecord.setOnClickListener(this);
        buttonDeleteRecord = (Button) rootView.findViewById(R.id.button_record_fragment_record_clean);
        buttonDeleteRecord.setVisibility(View.INVISIBLE);
        buttonDeleteRecord.setOnClickListener(this);
        textExist = (TextView) rootView.findViewById(R.id.text_file_exist);
        buttonDeleteFile = (Button) rootView.findViewById(R.id.button_delete_file);
        buttonDeleteFile.setOnClickListener(this);
        controlRecordButtons(false);


        //play audio
        buttonPlay = (ImageButton) rootView.findViewById(R.id.button_record_fragment_play);
        buttonPlay.setOnClickListener(this);
        buttonStop = (ImageButton) rootView.findViewById(R.id.button_record_fragment_stop);
        buttonStop.setOnClickListener(this);
        buttonPause = (ImageButton) rootView.findViewById(R.id.button_record_fragment_pause);
        buttonPause.setOnClickListener(this);
        buttonReplay = (ImageButton) rootView.findViewById(R.id.button_record_fragment_replay);
        buttonReplay.setOnClickListener(this);

        songProgressBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        songProgressBar.setOnSeekBarChangeListener(this);
        controlPlayButtons(false, false);

        return rootView;
    }

    public void playSong() {
        try {
            mp = new MediaPlayer();
            mp.setOnCompletionListener(this);
            mp.setDataSource(filePath);
            mp.prepare();
            mp.start();
            isPlaying = true;
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);
            updateProgressBar();
        } catch (IllegalArgumentException var3) {
            var3.printStackTrace();
        } catch (IllegalStateException var4) {
            var4.printStackTrace();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100L);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = (long) mp.getDuration();
            long currentDuration = (long) mp.getCurrentPosition();
            int progress = PlayerUtils.getProgressPercentage(currentDuration, totalDuration);
            songProgressBar.setProgress(progress);
            mHandler.postDelayed(this, 100L);
        }
    };

    private boolean isFileExist() {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        File filePath = new File(file.getAbsolutePath() + "/" + fileName + AUDIO_RECORDER_FILE_EXT_WAV);
        if (filePath.exists()) {
            Log.i("CheckFile", "true");
            return true;
        } else {
            Log.i("CheckFile", "false");
            return false;
        }
    }

    private void controlRecordButtons(boolean isRecording) {
        if (isRecording) {
            buttonRecordPlay.setVisibility(View.INVISIBLE);
            buttonRecordStop.setVisibility(View.VISIBLE);
        } else {
            buttonRecordPlay.setVisibility(View.VISIBLE);
            buttonRecordStop.setVisibility(View.INVISIBLE);
        }
    }

    private void controlPlayButtons(boolean isPlaying, boolean onProgress) {
        if (isPlaying && onProgress) {
            buttonPlay.setVisibility(View.INVISIBLE);
            buttonPause.setVisibility(View.VISIBLE);
            buttonStop.setVisibility(View.VISIBLE);
            buttonReplay.setVisibility(View.VISIBLE);
            songProgressBar.setEnabled(true);

        } else if (isPlaying) {
            buttonPlay.setVisibility(View.VISIBLE);
            buttonPause.setVisibility(View.INVISIBLE);
            buttonStop.setVisibility(View.VISIBLE);
            buttonReplay.setVisibility(View.VISIBLE);
            songProgressBar.setEnabled(true);
        } else {
            buttonPlay.setVisibility(View.VISIBLE);
            buttonPause.setVisibility(View.INVISIBLE);
            buttonStop.setVisibility(View.INVISIBLE);
            buttonReplay.setVisibility(View.INVISIBLE);
            songProgressBar.setEnabled(false);
        }

    }

    private void isFileSave(boolean done) {
        if (!done) {
            buttonSaveRecord.setVisibility(View.VISIBLE);
            buttonDeleteRecord.setVisibility(View.VISIBLE);
            buttonRecordPlay.setEnabled(false);
        } else {
            buttonSaveRecord.setVisibility(View.INVISIBLE);
            buttonDeleteRecord.setVisibility(View.INVISIBLE);
            buttonRecordPlay.setEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_type_help:
                View view = getActivity().findViewById(R.id.button_type_help);
                Tooltip.make(getActivity(),
                        new Tooltip.Builder(101)
                                .anchor(view, Tooltip.Gravity.CENTER)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, true)
                                        .outsidePolicy(true, true), 20000)
                                .activateDelay(200)
                                .showDelay(200)
                                .text(getString(R.string.type_string_help))
                                .withStyleId(R.style.ToolTipLayoutHoianStyle)
                                .maxWidth(800)
                                .withArrow(true)
                                .withOverlay(true).build()
                ).show();
                break;
            case R.id.button_record_fragment_record:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    int alreadyHasStoragePermission = ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.RECORD_AUDIO);
                    List<String> permissions = new ArrayList<String>();

                    if (alreadyHasStoragePermission != PackageManager.PERMISSION_GRANTED)
                        permissions.add(Manifest.permission.RECORD_AUDIO);

                    if (!permissions.isEmpty()) {
                        requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_CODE);
                    } else {

                        controlRecordButtons(true);
                        recorderUtils.startRecording();
                        snackbarRecord = Snackbar.make(rootView, "Recording Address....", Snackbar.LENGTH_INDEFINITE);
                        snackbarRecord.show();
                    }

                } else{

                    controlRecordButtons(true);
                    recorderUtils.startRecording();
                    snackbarRecord = Snackbar.make(rootView, "Recording Address....", Snackbar.LENGTH_INDEFINITE);
                    snackbarRecord.show();
                }

                break;
            case R.id.button_record_fragment_record_stop:
                controlRecordButtons(false);
                recorderUtils.stopRecording();
                Snackbar snackbarRecordFinish = Snackbar.make(rootView, "Recording Address Finished", Snackbar.LENGTH_SHORT);
                snackbarRecordFinish.show();
                snackbarRecord.dismiss();
                isFileSave(false);
                break;
            case R.id.button_record_fragment_record_done:
                recorderUtils.saveRecord();
                Snackbar snackbarSave = Snackbar.make(rootView, "File Saved.", Snackbar.LENGTH_SHORT);
                snackbarSave.show();
                isFileSave(true);
                break;
            case R.id.button_record_fragment_record_clean:
                recorderUtils.deleteRecord();
                Snackbar snackbarDelete = Snackbar.make(rootView, "File Record Canceled.", Snackbar.LENGTH_SHORT);
                snackbarDelete.show();
                isFileSave(true);
                break;
            case R.id.button_delete_file:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                recorderUtils.deleteFile();
                                Snackbar snackbarFileDelete = Snackbar.make(rootView, "File Record Deleted.", Snackbar.LENGTH_SHORT);
                                snackbarFileDelete.show();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to delete recording file?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


                break;
            case R.id.button_record_fragment_play:
                if (isFileExist()) {

                    if (isPlaying) {
                        controlPlayButtons(true, true);
                        mp.start();
                    } else {
                        controlPlayButtons(true, true);
                        playSong();
                    }
                } else {
                    Snackbar snackbarNoFile = Snackbar.make(rootView, "No File Recorded.", Snackbar.LENGTH_SHORT);
                    snackbarNoFile.show();
                }
                break;
            case R.id.button_record_fragment_pause:
                controlPlayButtons(true, false);
                mp.pause();
                break;
            case R.id.button_record_fragment_stop:
                controlPlayButtons(false, false);
                mHandler.removeCallbacks(mUpdateTimeTask);
                songProgressBar.setProgress(0);
                mp.stop();
                isPlaying = false;
                mp.release();
                break;
            case R.id.button_record_fragment_replay:
                controlPlayButtons(true, true);
                mHandler.removeCallbacks(mUpdateTimeTask);
                songProgressBar.setProgress(0);
                mp.stop();
                mp.release();
                playSong();
                break;
        }

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

    @Override
    public void onCompletion(MediaPlayer mp) {
        controlPlayButtons(false, false);
        mHandler.removeCallbacks(mUpdateTimeTask);
        songProgressBar.setProgress(0);
        mp.release();
        isPlaying = false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = PlayerUtils.progressToTimer(seekBar.getProgress(), totalDuration);
        mp.seekTo(currentPosition);
        updateProgressBar();
    }
}

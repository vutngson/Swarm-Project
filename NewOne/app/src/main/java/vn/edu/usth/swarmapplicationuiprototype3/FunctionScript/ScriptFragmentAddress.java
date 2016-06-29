package vn.edu.usth.swarmapplicationuiprototype3.FunctionScript;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import vn.edu.usth.swarmapplicationuiprototype3.AppModel.AppConstant;
import vn.edu.usth.swarmapplicationuiprototype3.FunctionRecord.PlayerUtils;
import vn.edu.usth.swarmapplicationuiprototype3.R;

public class ScriptFragmentAddress extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    ;
    private View rootView;
    private ImageButton buttonPlay;
    private ImageButton buttonPause;
    private ImageButton buttonStop;
    private ImageButton buttonReplay;
    private MediaPlayer mp;
    private PlayerUtils utils;
    private SeekBar songProgressBar;
    private Handler mHandler = new Handler();
    private final int PERMISSION_REQUEST_CODE = 100;
    private boolean isPlaying = false;
    private String folderID;

    private String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private String AUDIO_RECORDER_FOLDER;
    private String fileName = "Address";
    private String filePath;

    private String editTextString = "";
    private EditText editText;


    public ScriptFragmentAddress() {
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_transcript_1, container, false);
        try {
            folderID = AppConstant.readFileID();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AUDIO_RECORDER_FOLDER = AppConstant.SWARM_DIR + "/" + folderID + "/"
                + AppConstant.RECORD_DIR;
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                AUDIO_RECORDER_FOLDER + "/" + fileName + AUDIO_RECORDER_FILE_EXT_WAV;
        if (editText != null) {

            editText.setText(ScriptUltis.ReadFile(folderID, fileName));
        }

        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.script1);
        editText = (EditText) rootView.findViewById(R.id.unique_survey_address);
        editText.setText(ScriptUltis.ReadFile(folderID, fileName));


        //play audio
        buttonPlay = (ImageButton) rootView.findViewById(R.id.button_record_fragment_play);
        buttonPlay.setOnClickListener(this);
        buttonStop = (ImageButton) rootView.findViewById(R.id.button_record_fragment_stop);
        buttonStop.setOnClickListener(this);
        buttonPause = (ImageButton) rootView.findViewById(R.id.button_record_fragment_pause);
        buttonPause.setOnClickListener(this);
        buttonReplay = (ImageButton) rootView.findViewById(R.id.button_record_fragment_replay);
        buttonReplay.setOnClickListener(this);
        utils = new PlayerUtils();
        songProgressBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        songProgressBar.setOnSeekBarChangeListener(this);
        controlPlayButtons(false, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (editText.isFocused()) {
                        Rect outRect = new Rect();
                        editText.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {

                            editText.clearFocus();
                            hideSoftKeyboard(getActivity());
                        }
                    }
                }
                return false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    editText.setText(ScriptUltis.ReadFile(folderID, fileName));
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                editTextString = editText.getText().toString();
                ScriptUltis.WriteFile(folderID, fileName, editTextString);
            }
        });

        return rootView;
    }


    /**
     * Hides virtual keyboard
     *
     * @author kvarela
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
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
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            songProgressBar.setProgress(progress);
            mHandler.postDelayed(this, 100L);
        }
    };

    private boolean isFileExist() {

        File file = new File(filePath);
        ;
        if (file.exists()) {
            Log.i("CheckFile", "true");
            return true;
        } else {
            Log.i("CheckFile", "false");
            return false;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
        mp.seekTo(currentPosition);
        updateProgressBar();
    }

}

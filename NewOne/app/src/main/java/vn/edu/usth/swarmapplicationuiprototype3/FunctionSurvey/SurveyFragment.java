package vn.edu.usth.swarmapplicationuiprototype3.FunctionSurvey;


import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import it.sephiroth.android.library.tooltip.Tooltip;
import vn.edu.usth.swarmapplicationuiprototype3.AppModel.AppConstant;
import vn.edu.usth.swarmapplicationuiprototype3.AppModel.OnSwipeTouchListener;

import vn.edu.usth.swarmapplicationuiprototype3.R;

public class SurveyFragment extends Fragment implements View.OnClickListener {
    private int fragmentPosition;
    private ScrollView scrollView;
    private int screenHeight;
    //layout 1
    private EditText SurveyAddress;
    private EditText SurveyProperName;
    private RadioButton siteVilla;
    private RadioButton siteStreetHouse;
    private RadioButton siteBuilding;
    private RadioButton sitePagoda;
    private RadioButton siteRubbish;
    private RadioButton sitePond;
    private RelativeRadioGroup toggleButtonGroupTableLayout;
    //layout2
    private EditText numberOfFloor;
    private RadioGroup surveyStatus;
    private RadioButton inProgress;
    private RadioButton suspended;
    //layout3
    private EditText workerProvince;
    private EditText workerDistrict;
    private RadioGroup surveyWorkerLiveOnTheSite;
    private RadioButton workerLiveYes;
    private RadioButton workerLiveNo;
    //layout4
    private EditText numberOfWorker;
    private EditText numberOfCloths;
    //layout5
    private EditText workerLiveStreet;
    private EditText workerLiveDistrict;
    //layout6
    private EditText numberOfBox;
    private RadioGroup surveyTemporaryWater;
    private RadioButton waterYes;
    private RadioButton waterNo;
    private RadioGroup surveyLarvae;
    private RadioButton larvaeYes;
    private RadioButton larvaeNo;
    //layout7
    private EditText surveyComment;

    RelativeLayout layout;
    RelativeLayout layout2;
    RelativeLayout layout3;
    RelativeLayout layout4;
    RelativeLayout layout5;
    RelativeLayout layout6;
    RelativeLayout layout7;

    private RelativeLayout mainLayout;

    private GeoJSONObject geoJSON;

    private JSONObject jsonObject;

    private View rootView;
    private String folderID = "test";

    private RadioGroup.OnCheckedChangeListener statusOnClickListener;
    private RadioGroup.OnCheckedChangeListener workerLiveOnTheSiteOnCheckedChange;


    public SurveyFragment() {
    }

    public SurveyFragment(int fragmentPosition) {
        this.fragmentPosition = fragmentPosition;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scroll_survey, container, false);
        setHasOptionsMenu(true);
        jsonObject = new JSONObject();
        mainLayout = (RelativeLayout) rootView.findViewById(R.id.survey_layout);
        //Scroll view
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        layout = (RelativeLayout) rootView.findViewById(R.id.layout1);
        layout2 = (RelativeLayout) rootView.findViewById(R.id.layout2);
        layout3 = (RelativeLayout) rootView.findViewById(R.id.layout3);
        layout4 = (RelativeLayout) rootView.findViewById(R.id.layout4);
        layout5 = (RelativeLayout) rootView.findViewById(R.id.layout5);
        layout6 = (RelativeLayout) rootView.findViewById(R.id.layout6);
        layout7 = (RelativeLayout) rootView.findViewById(R.id.layout7);

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        final float scale = getResources().getDisplayMetrics().density;
        int tabHeight = (int) (45 * scale + 0.5f);
        Log.i("ActionBar", tabHeight + "");
        screenHeight = size.y - getStatusBarHeight() - getActionBarHeight() - tabHeight;
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = screenHeight;
        ViewGroup.LayoutParams params2 = layout2.getLayoutParams();
        params2.height = screenHeight;
        ViewGroup.LayoutParams params3 = layout3.getLayoutParams();
        params3.height = screenHeight;
        ViewGroup.LayoutParams params4 = layout4.getLayoutParams();
        params4.height = screenHeight;
        ViewGroup.LayoutParams params5 = layout5.getLayoutParams();
        params5.height = screenHeight;
        ViewGroup.LayoutParams params6 = layout6.getLayoutParams();
        params6.height = screenHeight;
        ViewGroup.LayoutParams params7 = layout7.getLayoutParams();
        params7.height = screenHeight;

        //set visible for layouts
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
        layout5.setVisibility(View.GONE);
        layout6.setVisibility(View.VISIBLE);
        layout7.setVisibility(View.VISIBLE);

        //layout 1
        //declare for edit text and radio button


        toggleButtonGroupTableLayout = (RelativeRadioGroup) rootView.findViewById(R.id.radGroup1);
        SurveyAddress = (EditText) rootView.findViewById(R.id.survey_address_text);
        try {
            SurveyAddress.setText(AppConstant.readFileAddress());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SurveyProperName = (EditText) rootView.findViewById(R.id.survery_proper_name_text);
        siteVilla = (RadioButton) rootView.findViewById(R.id.site_villa);
        siteStreetHouse = (RadioButton) rootView.findViewById(R.id.site_street_house);
        siteBuilding = (RadioButton) rootView.findViewById(R.id.site_building);
        sitePagoda = (RadioButton) rootView.findViewById(R.id.site_pagoda);
        siteRubbish = (RadioButton) rootView.findViewById(R.id.site_rubbish);
        sitePond = (RadioButton) rootView.findViewById(R.id.site_pond);

        toggleButtonGroupTableLayout.setOnCheckedChangeListener(new RelativeRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RelativeRadioGroup group, int checkedId) {
                Log.i("RadioGroup", "Changed");
                switch (toggleButtonGroupTableLayout.getCheckedRadioButtonId()) {
                    case R.id.site_villa:
                        constructionSiteChecked();
                        break;
                    case R.id.site_building:
                        constructionSiteChecked();
                        break;
                    case R.id.site_street_house:
                        constructionSiteChecked();
                        break;
                    case R.id.site_pagoda:
                        notContructionSiteChecked();
                        break;
                    case R.id.site_rubbish:
                        notContructionSiteChecked();
                        break;
                    case R.id.site_pond:
                        notContructionSiteChecked();
                        break;
                }
                try {
                    saveFile();
                    readFile();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //layout 2
        numberOfFloor = (EditText) rootView.findViewById(R.id.survey_number_of_floor);
        surveyStatus = (RadioGroup) rootView.findViewById(R.id.survey_status);
        inProgress = (RadioButton) rootView.findViewById(R.id.survey_in_progress);
        suspended = (RadioButton) rootView.findViewById(R.id.survey_suspended);
        statusOnClickListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (surveyStatus.getCheckedRadioButtonId()) {
                    case R.id.survey_in_progress:
                        Log.i("RadioGroup", "Layout 3 v");
                        inProgressChecked();
                        break;
                    case R.id.survey_suspended:
                        Log.i("RadioGroup", "layout 3 g");
                        suspendedChecked();
                        break;
                }
                try {
                    saveFile();
                    readFile();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        surveyStatus.setOnCheckedChangeListener(statusOnClickListener);

        //layout 3
        workerProvince = (EditText) rootView.findViewById(R.id.worker_province);
        workerDistrict = (EditText) rootView.findViewById(R.id.worker_district);
        surveyWorkerLiveOnTheSite = (RadioGroup) rootView.findViewById(R.id.survey_temporary);
        workerLiveYes = (RadioButton) rootView.findViewById(R.id.worker_live_yes);
        workerLiveNo = (RadioButton) rootView.findViewById(R.id.worker_live_no);
        workerLiveOnTheSiteOnCheckedChange = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (surveyWorkerLiveOnTheSite.getCheckedRadioButtonId()) {
                    case R.id.worker_live_yes:
                        workerLiveOnTheSiteChecked();
                        Log.i("RadioGroup", "Layout 3 v");

                        break;
                    case R.id.worker_live_no:
                        workerNotLiveOnTheSiteChecked();
                        Log.i("RadioGroup", "layout 3 g");

                        break;
                }
                try {
                    saveFile();
                    readFile();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        surveyWorkerLiveOnTheSite.setOnCheckedChangeListener(workerLiveOnTheSiteOnCheckedChange);

        //layout 4
        numberOfWorker = (EditText) rootView.findViewById(R.id.number_of_worker);
        numberOfCloths = (EditText) rootView.findViewById(R.id.number_of_cloths_outside);

        //layout 5
        workerLiveDistrict = (EditText) rootView.findViewById(R.id.worker_live_district);
        workerLiveStreet = (EditText) rootView.findViewById(R.id.worker_live_street);

        //layout 6
        numberOfBox = (EditText) rootView.findViewById(R.id.survey_number_of_box);
        surveyTemporaryWater = (RadioGroup) rootView.findViewById(R.id.survey_temporary_open_water);
        waterYes = (RadioButton) rootView.findViewById(R.id.water_yes);
        waterNo = (RadioButton) rootView.findViewById(R.id.water_no);
        surveyLarvae = (RadioGroup) rootView.findViewById(R.id.survey_larvae);
        larvaeYes = (RadioButton) rootView.findViewById(R.id.larvae_yes);
        larvaeNo = (RadioButton) rootView.findViewById(R.id.larvae_no);

        surveyTemporaryWater.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (surveyTemporaryWater.getCheckedRadioButtonId()) {
                    case R.id.water_yes:
                        break;
                    case R.id.water_no:
                        break;
                }
                try {
                    saveFile();
                    readFile();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        surveyLarvae.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (surveyLarvae.getCheckedRadioButtonId()) {
                    case R.id.larvae_yes:
                        break;
                    case R.id.larvae_no:
                        break;
                }
                try {
                    saveFile();
                    readFile();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //layout 7
        surveyComment = (EditText) rootView.findViewById(R.id.survey_comment_further);


        scrollView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {

            public void onSwipeTop() {
                Log.i("Swipe", "top");

                scrollView.smoothScrollBy(0, +screenHeight);
                Log.i("Swipe", "top");

            }

            public void onSwipeBottom() {
                Log.i("Swipe", "top");

                scrollView.smoothScrollBy(0, -screenHeight);
                Log.i("Swipe", "top");

            }

        });

        Tooltip.dbg = true;


        mainLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    View view = getActivity().getCurrentFocus();
                    if (view instanceof EditText) {
                        Rect outRect = new Rect();
                        view.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            try {
                                saveFile();
                                readFile();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            rootView.clearFocus();
                            hideSoftKeyboard(getActivity());
                        }
                    }
                }
                return false;
            }
        });

        try

        {
            readFile();
        } catch (
                IOException e
                )

        {
            e.printStackTrace();
        } catch (
                JSONException e
                )

        {
            e.printStackTrace();
        }

        return rootView;
    }

    private void readFile() throws IOException, JSONException {
        JSONObject jsonOb = null;

        String fileFolder = AppConstant.SWARM_DIR + File.separator + AppConstant.readFileID() + File.separator;
        File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
        if (!root.exists()) {
            root.mkdirs();
        }
        File fileToSaveJson = new File(root, "Survey.txt");
        if (fileToSaveJson.exists()) {

            InputStream in = new FileInputStream(fileToSaveJson);

            GeoJSONObject gJSOn = GeoJSON.parse(in);
            Feature feature = new Feature(gJSOn.toJSON());
            jsonOb = feature.getProperties();
            readEditText("address", SurveyAddress, jsonOb);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(AppConstant.readFileAddress());
            readEditText("propername", SurveyProperName, jsonOb);
            if (jsonOb.has("type")) {
                if (jsonOb.getString("type").equals(siteVilla.getText().toString())) {
                    siteVilla.setChecked(true);
                } else if (jsonOb.getString("type").equals(siteBuilding.getText().toString())) {
                    siteBuilding.setChecked(true);
                } else if (jsonOb.getString("type").equals(siteStreetHouse.getText().toString())) {
                    siteStreetHouse.setChecked(true);
                } else if (jsonOb.getString("type").equals(sitePagoda.getText().toString())) {
                    sitePagoda.setChecked(true);
                } else if (jsonOb.getString("type").equals(siteRubbish.getText().toString())) {
                    siteRubbish.setChecked(true);
                } else {
                    sitePond.setChecked(true);
                }
            }
            readEditText("numberoffloor", numberOfFloor, jsonOb);
            if (jsonOb.has("status")) {
                if (jsonOb.getString("status").equals(inProgress.getText().toString())) {
                    inProgress.setChecked(true);
                } else {
                    suspended.setChecked(true);
                }
            }
            readEditText("workerprovince", workerProvince, jsonOb);
            readEditText("workerdistrict", workerDistrict, jsonOb);
            if (jsonOb.has("workerliveinthesite")) {
                if (jsonOb.getString("workerliveinthesite").equals(workerLiveYes.getText().toString())) {
                    workerLiveYes.setChecked(true);
                } else {
                    workerLiveNo.setChecked(true);
                }
            }
            readEditText("numberofworker", numberOfWorker, jsonOb);
            readEditText("numberofcloths", numberOfCloths, jsonOb);
            readEditText("workerliveHanoistreet", workerLiveStreet, jsonOb);
            readEditText("workerliveHanoidistrict", workerLiveDistrict, jsonOb);
            readEditText("numberofbox", numberOfBox, jsonOb);
            if (jsonOb.has("opentemporarywater")) {
                if (jsonOb.getString("opentemporarywater").equals(waterYes.getText().toString())) {
                    waterYes.setChecked(true);
                } else {
                    waterNo.setChecked(true);
                }
            }

            if (jsonOb.has("larvae")) {
                if (jsonOb.getString("larvae").equals(larvaeYes.getText().toString())) {
                    larvaeYes.setChecked(true);
                } else {
                    larvaeNo.setChecked(true);
                }
            }
            readEditText("comment", surveyComment, jsonOb);
            Log.i("IO","Read File");

        }


    }


    private void readEditText(String name, EditText editText, JSONObject jsonOb) throws JSONException {
        if (jsonOb.has(name)) {
            editText.setText(jsonOb.getString(name));
        }
    }

    private void saveFile() throws JSONException {
        JSONObject fileToSave = null;
        checkRadioGroupTabLayout("type", toggleButtonGroupTableLayout, jsonObject);
        checkEditText("address", SurveyAddress, jsonObject);
        checkEditText("propername", SurveyProperName, jsonObject);
        checkEditText("numberoffloor", numberOfFloor, jsonObject);
        checkRadioGroup("status", surveyStatus, jsonObject);
        checkEditText("workerprovince", workerProvince, jsonObject);
        checkEditText("workerdistrict", workerDistrict, jsonObject);
        checkRadioGroup("workerliveinthesite", surveyWorkerLiveOnTheSite, jsonObject);
        checkEditText("numberofworker", numberOfWorker, jsonObject);
        checkEditText("numberofcloths", numberOfCloths, jsonObject);
        checkEditText("workerliveHanoistreet", workerLiveStreet, jsonObject);
        checkEditText("workerliveHanoidistrict", workerLiveDistrict, jsonObject);
        checkEditText("numberofbox", numberOfBox, jsonObject);
        checkRadioGroup("opentemporarywater", surveyTemporaryWater, jsonObject);
        checkRadioGroup("larvae", surveyLarvae, jsonObject);
        checkEditText("comment", surveyComment, jsonObject);
        try {
            AppConstant.updateFileActive(SurveyAddress.getText().toString());
            AppConstant.updateFileSites(SurveyAddress.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        com.cocoahero.android.geojson.Point point = new com.cocoahero.android.geojson.Point(21.023999904, 105.851496594);
        Feature feature = new Feature(point);
        feature.setIdentifier(folderID);

        feature.setProperties(jsonObject);

        try {
            fileToSave = feature.toJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = fileToSave.toString();
        byte[] jsonArray = jsonString.getBytes();

        String fileFolder = null;
        try {
            fileFolder = AppConstant.SWARM_DIR + File.separator + AppConstant.readFileID();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File root = new File(Environment.getExternalStorageDirectory(), fileFolder);
        if (!root.exists()) {
            root.mkdirs();
        }
        File fileToSaveJson = new File(root, "Survey.txt");

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
        Log.i("IO","Write File");
    }

    private void checkRadioGroupTabLayout(String editTextValue, RelativeRadioGroup radioGroup, JSONObject jsonObject) throws JSONException {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            if (jsonObject.has(editTextValue)) {
                jsonObject.remove(editTextValue);
            }
        } else {
            if (jsonObject.has(editTextValue)) {
                jsonObject.remove(editTextValue);
            }
            RadioButton radioButton = (RadioButton) rootView.findViewById(radioGroup.getCheckedRadioButtonId());
            jsonObject.put(editTextValue, radioButton.getText().toString());
        }
    }

    private void checkRadioGroup(String editTextValue, RadioGroup radioGroup, JSONObject jsonObject) throws JSONException {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            if (jsonObject.has(editTextValue)) {
                jsonObject.remove(editTextValue);
            }
        } else {
            if (jsonObject.has(editTextValue)) {
                jsonObject.remove(editTextValue);
            }
            RadioButton radioButton = (RadioButton) rootView.findViewById(radioGroup.getCheckedRadioButtonId());
            jsonObject.put(editTextValue, radioButton.getText().toString());
        }
    }

    private void checkEditText(String editTextValue, EditText ed_text, JSONObject jsonObject) throws JSONException {


        if (ed_text.length() == 0 || ed_text.equals("") || TextUtils.isEmpty(ed_text.getText().toString())) {
            if (jsonObject.has(editTextValue)) {
                jsonObject.remove(editTextValue);
            }
        } else {
            if (jsonObject.has(editTextValue)) {
                jsonObject.remove(editTextValue);
            }
            jsonObject.put(editTextValue, ed_text.getText().toString());
        }


    }

    private void checkHasValue(String editTextValue) {
        if (jsonObject.has(editTextValue)) {
            jsonObject.remove(editTextValue);
        }
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

    private void clearEditText(EditText editText){
        if (editText.length() > 0) {
            TextKeyListener.clear(editText.getText());
        }
    }

    private void notContructionSiteChecked() {
        //layout 2
        surveyStatus.setOnCheckedChangeListener(null);
        surveyStatus.clearCheck();
        surveyStatus.setOnCheckedChangeListener(statusOnClickListener);
        clearEditText(numberOfFloor);
        layout2.setVisibility(View.GONE);

        //layout 3
        clearEditText(workerProvince);
        clearEditText(workerDistrict);
        surveyWorkerLiveOnTheSite.setOnCheckedChangeListener(null);
        surveyWorkerLiveOnTheSite.clearCheck();
        surveyWorkerLiveOnTheSite.setOnCheckedChangeListener(workerLiveOnTheSiteOnCheckedChange);
        layout3.setVisibility(View.GONE);

        //layout 4
        layout4.setVisibility(View.GONE);
        clearEditText(numberOfWorker);
        clearEditText(numberOfCloths);

        //layout5
        layout5.setVisibility(View.GONE);
        clearEditText(workerLiveStreet);
        clearEditText(workerLiveDistrict);

        checkHasValue("numberoffloor");
        checkHasValue("status");
        checkHasValue("workerprovince");
        checkHasValue("workerdistrict");
        checkHasValue("workerliveinthesite");
        checkHasValue("numberofworker");
        checkHasValue("numberofcloths");
        checkHasValue("workerliveHanoistreet");
        checkHasValue("workerliveHanoidistrict");
    }

    private void constructionSiteChecked() {
        layout2.setVisibility(View.VISIBLE);
        surveyStatus.setOnCheckedChangeListener(null);
        surveyStatus.clearCheck();
        surveyStatus.setOnCheckedChangeListener(statusOnClickListener);
        clearEditText(numberOfFloor);

        //layout 3
        clearEditText(workerProvince);
        clearEditText(workerDistrict);
        surveyWorkerLiveOnTheSite.setOnCheckedChangeListener(null);
        surveyWorkerLiveOnTheSite.clearCheck();
        surveyWorkerLiveOnTheSite.setOnCheckedChangeListener(workerLiveOnTheSiteOnCheckedChange);
        layout3.setVisibility(View.GONE);

        //layout 4
        layout4.setVisibility(View.GONE);
        clearEditText(numberOfWorker);
        clearEditText(numberOfCloths);

        //layout5
        layout5.setVisibility(View.GONE);
        clearEditText(workerLiveStreet);
        clearEditText(workerLiveDistrict);

        checkHasValue("numberoffloor");
        checkHasValue("status");
        checkHasValue("workerprovince");
        checkHasValue("workerdistrict");
        checkHasValue("workerliveinthesite");
        checkHasValue("numberofworker");
        checkHasValue("numberofcloths");
        checkHasValue("workerliveHanoistreet");
        checkHasValue("workerliveHanoidistrict");
    }

    private void inProgressChecked() {
        //layout 3
        clearEditText(workerProvince);
        clearEditText(workerDistrict);
        surveyWorkerLiveOnTheSite.setOnCheckedChangeListener(null);
        surveyWorkerLiveOnTheSite.clearCheck();
        surveyWorkerLiveOnTheSite.setOnCheckedChangeListener(workerLiveOnTheSiteOnCheckedChange);
        layout3.setVisibility(View.VISIBLE);

        //layout 4
        layout4.setVisibility(View.GONE);
        clearEditText(numberOfWorker);
        clearEditText(numberOfCloths);

        //layout5
        layout5.setVisibility(View.GONE);
        clearEditText(workerLiveStreet);
        clearEditText(workerLiveDistrict);

        checkHasValue("numberoffloor");
        checkHasValue("status");
        checkHasValue("workerprovince");
        checkHasValue("workerdistrict");
        checkHasValue("workerliveinthesite");
        checkHasValue("numberofworker");
        checkHasValue("numberofcloths");
        checkHasValue("workerliveHanoistreet");
        checkHasValue("workerliveHanoidistrict");
    }

    private void suspendedChecked() {
        //layout 3
        clearEditText(workerProvince);
        clearEditText(workerDistrict);
        surveyWorkerLiveOnTheSite.setOnCheckedChangeListener(null);
        surveyWorkerLiveOnTheSite.clearCheck();
        surveyWorkerLiveOnTheSite.setOnCheckedChangeListener(workerLiveOnTheSiteOnCheckedChange);
        layout3.setVisibility(View.GONE);

        //layout 4
        layout4.setVisibility(View.GONE);
        clearEditText(numberOfWorker);
        clearEditText(numberOfCloths);

        //layout5
        layout5.setVisibility(View.GONE);
        clearEditText(workerLiveStreet);
        clearEditText(workerLiveDistrict);

        checkHasValue("numberoffloor");
        checkHasValue("status");
        checkHasValue("workerprovince");
        checkHasValue("workerdistrict");
        checkHasValue("workerliveinthesite");
        checkHasValue("numberofworker");
        checkHasValue("numberofcloths");
        checkHasValue("workerliveHanoistreet");
        checkHasValue("workerliveHanoidistrict");
    }

    private void workerLiveOnTheSiteChecked() {
        //layout 4
        layout4.setVisibility(View.VISIBLE);
        clearEditText(numberOfWorker);
        clearEditText(numberOfCloths);

        //layout5
        layout5.setVisibility(View.GONE);
        clearEditText(workerLiveStreet);
        clearEditText(workerLiveDistrict);

        checkHasValue("numberoffloor");
        checkHasValue("status");
        checkHasValue("workerprovince");
        checkHasValue("workerdistrict");
        checkHasValue("workerliveinthesite");
        checkHasValue("numberofworker");
        checkHasValue("numberofcloths");
        checkHasValue("workerliveHanoistreet");
        checkHasValue("workerliveHanoidistrict");
        rootView.invalidate();
    }

    private void workerNotLiveOnTheSiteChecked() {
        //layout 4
        layout4.setVisibility(View.GONE);
        clearEditText(numberOfWorker);
        clearEditText(numberOfCloths);

        //layout5
        layout5.setVisibility(View.VISIBLE);
        clearEditText(workerLiveStreet);
        clearEditText(workerLiveDistrict);

        checkHasValue("numberoffloor");
        checkHasValue("status");
        checkHasValue("workerprovince");
        checkHasValue("workerdistrict");
        checkHasValue("workerliveinthesite");
        checkHasValue("numberofworker");
        checkHasValue("numberofcloths");
        checkHasValue("workerliveHanoistreet");
        checkHasValue("workerliveHanoidistrict");
        rootView.invalidate();

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i("ActionBar", result + "");
        return result;
    }

    public int getActionBarHeight() {
        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        Log.i("ActionBar", mActionBarSize + "");

        return mActionBarSize;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_record, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help_record:
                View view = getActivity().findViewById(R.id.help_record);
                Tooltip.make(getActivity(),
                        new Tooltip.Builder(101)
                                .anchor(view, Tooltip.Gravity.BOTTOM)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, true)
                                        .outsidePolicy(true, true), 20000)
                                .activateDelay(900)
                                .showDelay(200)
                                .text("This is Record Interface. Press help button next to what you should record to have more information." +
                                        " To go to next pages either press directional buttons or swipe up/down")
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("OnStop", "File Saved");
        try {
            saveFile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("OnDestroy", "File Saved");
        try {
            saveFile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}


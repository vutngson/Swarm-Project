package com.tagworld.ftptest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";
	private static final String TEMP_FILENAME = "testVTS.txt";
	String path = "/pub/incoming";
	private Context cntx = null;

	private MyFTPClientFunctions ftpclient = null;

	private Button btnLoginFtp, btnUploadFile, btnDisconnect, btnExit;
	private EditText edtHostName, edtUserName, edtPassword;
	private ProgressDialog pd;
	private String host = "192.168.1.3";
	private String username = "ftp1user";
	private String password = "123";

	private String[] fileList;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}


		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		cntx = this.getBaseContext();

		edtHostName = (EditText) findViewById(R.id.edtHostName);
		edtHostName.setText(host);
		edtUserName = (EditText) findViewById(R.id.edtUserName);
		edtUserName.setText(username);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
		edtPassword.setText(password);

		btnLoginFtp = (Button) findViewById(R.id.btnLoginFtp);
		btnUploadFile = (Button) findViewById(R.id.btnUploadFile);
		btnDisconnect = (Button) findViewById(R.id.btnDisconnectFtp);
		btnExit = (Button) findViewById(R.id.btnExit);

		btnLoginFtp.setOnClickListener(this);
		btnUploadFile.setOnClickListener(this);
		btnDisconnect.setOnClickListener(this);
		btnExit.setOnClickListener(this);

		// Create a temporary file. You can use this to upload
		createDummyFile();

		ftpclient = new MyFTPClientFunctions();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLoginFtp:
			if (isOnline(MainActivity.this)) {

				connectToFTPAddress();

			} else {
				Toast.makeText(MainActivity.this,
						"Please check your internet connection!",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btnUploadFile:
			pd = ProgressDialog.show(MainActivity.this, "", "Uploading...",
					true, false);
			new Thread(new Runnable() {
				public void run() {
					boolean status = false;
					status = ftpclient.ftpUpload(
							Environment.getExternalStorageDirectory()
									+ "/TAGFtp/" + TEMP_FILENAME,
							TEMP_FILENAME, path, cntx);
					if (status == true) {
						Log.d(TAG, "Upload success");
						handler.sendEmptyMessage(2);
					} else {
						Log.d(TAG, "Upload failed");
						handler.sendEmptyMessage(-1);
					}
				}
			}).start();
			break;
		case R.id.btnDisconnectFtp:
			pd = ProgressDialog.show(MainActivity.this, "", "Disconnecting...",
					true, false);

			new Thread(new Runnable() {
				public void run() {
					ftpclient.ftpDisconnect();
					handler.sendEmptyMessage(3);
				}
			}).start();

			break;
		case R.id.btnExit:
			this.finish();
			break;
		}

	}

	private void connectToFTPAddress() {

		final String host = edtHostName.getText().toString().trim();
		final String username = edtUserName.getText().toString().trim();
		final String password = edtPassword.getText().toString().trim();



			pd = ProgressDialog.show(MainActivity.this, "", "Connecting...",
					true, false);

			new Thread(new Runnable() {
				public void run() {
					boolean status = false;
					status = ftpclient.ftpConnect(host, username, password, 21);

					if (status == true) {
						Log.d(TAG, "Connection Success");
						handler.sendEmptyMessage(0);
					} else {
						Log.d(TAG, "Connection failed");
						handler.sendEmptyMessage(-1);
					}
				}
			}).start();




	}

	private void getFTPFileList() {
		pd = ProgressDialog.show(MainActivity.this, "", "Getting Files...",
				true, false);

		new Thread(new Runnable() {

			@Override
			public void run() {
				fileList = ftpclient.ftpPrintFilesList("/");
				handler.sendEmptyMessage(1);
			}
		}).start();
	}

	public void createDummyFile() {

		try {
			File root = new File(Environment.getExternalStorageDirectory(),
					"TAGFtp");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, TEMP_FILENAME);
			FileWriter writer = new FileWriter(gpxfile);
			writer.append("Hi this is a sample file to upload for android FTP client example from TheAppGuruz!");
			writer.flush();
			writer.close();
			Toast.makeText(this, "Saved : " + gpxfile.getAbsolutePath(),
					Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	private void showCustomDialog(String[] fileList) {
		// custom dialog
		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.setContentView(R.layout.custom);
		dialog.setTitle("/ Directory File List");

		TextView tvHeading = (TextView) dialog.findViewById(R.id.tvListHeading);
		tvHeading.setText(":: File List ::");

		if (fileList != null && fileList.length > 0) {
			ListView listView = (ListView) dialog
					.findViewById(R.id.lstItemList);
			ArrayAdapter<String> fileListAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_list_item_1, fileList);
			listView.setAdapter(fileListAdapter);
		} else {
			tvHeading.setText(":: No Files ::");
		}

		Button dialogButton = (Button) dialog.findViewById(R.id.btnOK);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
}

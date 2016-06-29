package vn.edu.usth.swarmapplicationuiprototype3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import vn.edu.usth.swarmapplicationuiprototype3.DrawerActivity.DrawerInterfaceActivity;
import vn.edu.usth.swarmapplicationuiprototype3.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText editTextEmail = (EditText)findViewById(R.id.login_email);
        EditText editTextpassword = (EditText)findViewById(R.id.login_password);
        Button buttonLogin = (Button)findViewById(R.id.button_login_to_map);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, DrawerInterfaceActivity.class);
                startActivity(intent);
            }
        });
        Button buttonRegister = (Button)findViewById(R.id.button_login_to_register);
    }
}

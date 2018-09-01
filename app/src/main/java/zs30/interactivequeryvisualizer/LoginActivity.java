package zs30.interactivequeryvisualizer;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The class represents the login activity.
 * The associated screen is for logging into
 * the system with username and password.
 *
 * @author Zhasmina Stoyanova
 * @version 1.0 August 2018
 */
public class LoginActivity extends AppCompatActivity {
    private EditText usernameView;
    private EditText passwordView;
    private TextView errorMsgLoginView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //referring objects to UI components
        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);
        errorMsgLoginView = findViewById(R.id.errorMsg);

        Button signInButton = findViewById(R.id.sign_in_button);
        //tries to login on sign in button click
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    /**
     * Checks if the username and password are correct
     */
    private void login() {
        //clears the errors for the username and password fields
        usernameView.setError(null);
        passwordView.setError(null);
        //clears the error message
        errorMsgLoginView.setText(null);

        //gets username and password from the text fields
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        //checks if the field for the username is empty, and displays error message if true
        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            return;
        }

        //checks if the field for the password is empty, and displays error message if true
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            return;
        }

        setGlobalParameters(username, password);

        //requests authentication to the web service
        String request = Utils.getAuthenticationRequest();
        String response = Utils.getResponse(request);

        //if the username and password are correct
        if (response.equals("ok")) {
            //opens lookup views screen
            Intent intent = new Intent(LoginActivity.this, LookupViewActivity.class);
            startActivity(intent);
        } else {
            //focuses on and displays error message
            (findViewById(R.id.errorMsg)).requestFocus();
            errorMsgLoginView.setText("Wrong username or password!");
        }
    }

    /**
     * sets the global parameters for username and password
     */
    private void setGlobalParameters(String username, String password) {
        GlobalVariables.username = username;
        GlobalVariables.password = password;
    }
}


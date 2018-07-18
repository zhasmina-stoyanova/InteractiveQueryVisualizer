package zs30.interactivequeryvisualizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameView;
    private EditText passwordView;
    private TextView errorMsgLoginView;
    private final String CREDENTIALS = "zs30:zs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);
        errorMsgLoginView = findViewById(R.id.errorMsg);

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button guestButton = findViewById(R.id.guest_button);
        guestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //opens views page
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin() {
        usernameView.setError(null);
        passwordView.setError(null);
        errorMsgLoginView.setText(null);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            return;
        }

        String[] pieces = CREDENTIALS.split(":");

        if (pieces[0].equals(username) && pieces[1].equals(password) ) {
            //opens views page
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }else{
            ((TextView) findViewById(R.id.errorMsg)).requestFocus();
            errorMsgLoginView.setText("Wrong username or password!");
        }
    }
}


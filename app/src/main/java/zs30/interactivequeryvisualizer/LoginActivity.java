package zs30.interactivequeryvisualizer;



import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;

import java.lang.reflect.Field;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameView;
    private EditText passwordView;
    private TextView errorMsgLoginView;
    private final String CREDENTIALS = "zs30:zs";
    private PopupWindow popupWindow;
    private LinearLayout linearLayoutLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*show logo
        getSupportActionBar().setIcon(R.drawable.logo);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        //set custom toolbar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        TextView view = new TextView(getApplicationContext());
        LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        view.setText(actionBar.getTitle());
        view.setTextColor(Color.WHITE);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(view);

        linearLayoutLogin = (LinearLayout) findViewById(R.id.linear_layout_login);

        //showing overflow menu icon for devices before 4.4
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {

        }

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
                ((GlobalVariables) getApplication()).setGraphicsBtnOn(false);
                //opens views page
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                //open the popup window
                LayoutInflater layoutInflater = (LayoutInflater) LoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.about_popup,null);

                Button closePopupBtn = (Button) popupView.findViewById(R.id.closePopupBtn);
                popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                popupWindow.showAtLocation(linearLayoutLogin, Gravity.CENTER, 0, 0);

                //close the popup
                closePopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            ((GlobalVariables) getApplication()).setGraphicsBtnOn(false);
            //opens views page
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }else{
            ((TextView) findViewById(R.id.errorMsg)).requestFocus();
            errorMsgLoginView.setText("Wrong username or password!");
        }
    }
}


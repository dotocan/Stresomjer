package es.esy.stresomjer.stresomjer.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import es.esy.stresomjer.stresomjer.helper.Constants;
import es.esy.stresomjer.stresomjer.R;
import es.esy.stresomjer.stresomjer.view.fragment.LoginFragment;

public class LoginRegistrationActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        sharedPreferences = getSharedPreferences("Login", 0);
        initFragments();
    }

    private void initFragments() {
        Fragment fragment = new LoginFragment();

        if (sharedPreferences.getBoolean(Constants.IS_LOGGED_IN, false)) {
            Intent iToMain = new Intent(this, MainActivity.class);
            startActivity(iToMain);
        } else {
            fragment = new LoginFragment();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();

    }

    @Override
    public void onBackPressed() {
    }
}

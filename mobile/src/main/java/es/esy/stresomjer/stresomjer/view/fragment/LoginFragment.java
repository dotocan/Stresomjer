package es.esy.stresomjer.stresomjer.view.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.esy.stresomjer.stresomjer.helper.Constants;
import es.esy.stresomjer.stresomjer.model.retrofit.UserServerRequest;
import es.esy.stresomjer.stresomjer.model.retrofit.UserServerResponse;
import es.esy.stresomjer.stresomjer.model.User;
import es.esy.stresomjer.stresomjer.R;
import es.esy.stresomjer.stresomjer.helper.LoginRegisterRequestInterface;
import es.esy.stresomjer.stresomjer.view.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private AppCompatButton btnLogin;
    private EditText etEmail, etPassword;
    private TextView tvRegister;
    private RelativeLayout rlFullScreenLoading;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(rootView);

        sharedPreferences = getActivity().getSharedPreferences("Login", 0);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_register:
                goToRegister();
                break;

            case R.id.btn_login:
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    rlFullScreenLoading.setVisibility(View.VISIBLE);
                    loginUser(email, password);
                } else {
                    Snackbar.make(getView(), R.string.fields_required, Snackbar.LENGTH_LONG).show();
                }

                break;
        }
    }

    private void loginUser(String email, String password) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginRegisterRequestInterface loginRegisterRequestInterface = retrofit.create(LoginRegisterRequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        final UserServerRequest userServerRequest = new UserServerRequest();
        userServerRequest.setOperation(Constants.LOGIN_OPERATION);
        userServerRequest.setUser(user);
        final Call<UserServerResponse> serverResponseCall = loginRegisterRequestInterface.operation(userServerRequest);

        serverResponseCall.enqueue(new Callback<UserServerResponse>() {
            @Override
            public void onResponse(Call<UserServerResponse> call, Response<UserServerResponse> response) {
                UserServerResponse userServerResponse = response.body();
                Snackbar.make(getView(), userServerResponse.getMessage(), Snackbar.LENGTH_LONG).show();

                if (userServerResponse.getResult().equals("success")) {
                    String userFirstName, userLastName, userEmail, userUniqueId;
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    userFirstName = userServerResponse.getUser().getFirst_name();
                    userLastName = userServerResponse.getUser().getLast_name();
                    userEmail = userServerResponse.getUser().getEmail();
                    userUniqueId = userServerResponse.getUser().getUnique_id();

                    editor.putBoolean(Constants.IS_LOGGED_IN, true);
                    editor.putString(Constants.FIRST_NAME, userFirstName);
                    editor.putString(Constants.LAST_NAME, userLastName);
                    editor.putString(Constants.EMAIL, userEmail);
                    editor.putString(Constants.UNIQUE_ID, userUniqueId);
                    editor.commit();

                    // Launches MainActivity after succesful login
                    goToMain();
                } else {
                    rlFullScreenLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<UserServerResponse> call, Throwable t) {
                rlFullScreenLoading.setVisibility(View.GONE);
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToRegister() {
        Fragment register = new RegisterFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, register);
        ft.commit();
    }

    private void goToMain() {
        Intent iToMain = new Intent(getActivity(), MainActivity.class);
        startActivity(iToMain);
    }

    public void initViews(View rootView) {
        btnLogin = (AppCompatButton) rootView.findViewById(R.id.btn_login);
        etEmail = (EditText) rootView.findViewById(R.id.et_email);
        etPassword = (EditText) rootView.findViewById(R.id.et_password);
        tvRegister = (TextView) rootView.findViewById(R.id.tv_register);
        rlFullScreenLoading = (RelativeLayout) rootView.findViewById(R.id.rl_full_screen_loading);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        // Changing the progress bar color
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        rlFullScreenLoading.setVisibility(View.GONE);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }
}

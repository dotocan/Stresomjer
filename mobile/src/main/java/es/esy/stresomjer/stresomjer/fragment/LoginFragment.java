package es.esy.stresomjer.stresomjer.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import es.esy.stresomjer.stresomjer.Constants;
import es.esy.stresomjer.stresomjer.model.ServerRequest;
import es.esy.stresomjer.stresomjer.model.ServerResponse;
import es.esy.stresomjer.stresomjer.model.User;
import es.esy.stresomjer.stresomjer.R;
import es.esy.stresomjer.stresomjer.RequestInterface;
import es.esy.stresomjer.stresomjer.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private AppCompatButton btnLogin;
    private EditText etEmail, etPassword;
    private TextView tvRegister;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(rootView);

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

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        final ServerRequest serverRequest = new ServerRequest();
        serverRequest.setOperation(Constants.LOGIN_OPERATION);
        serverRequest.setUser(user);
        final Call<ServerResponse> serverResponseCall = requestInterface.operation(serverRequest);

        serverResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                Snackbar.make(getView(), serverResponse.getMessage(), Snackbar.LENGTH_LONG).show();

                if (serverResponse.getResult().equals(Constants.SUCCESS)) {
                    String userFirstName, userLastName, userEmail, userUniqueId;
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    userFirstName = serverResponse.getUser().getFirst_name();
                    userLastName = serverResponse.getUser().getLast_name();
                    userEmail = serverResponse.getUser().getEmail();
                    userUniqueId = serverResponse.getUser().getUnique_id();

                    editor.putBoolean(Constants.IS_LOGGED_IN, true);
                    editor.putString(Constants.FIRST_NAME, userFirstName);
                    editor.putString(Constants.LAST_NAME, userLastName);
                    editor.putString(Constants.EMAIL, userEmail);
                    editor.putString(Constants.UNIQUE_ID, userUniqueId);
                    editor.commit();

                    goToMain();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
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
        sharedPreferences = getActivity().getSharedPreferences("Login", 0);

        btnLogin = (AppCompatButton) rootView.findViewById(R.id.btn_login);
        etEmail = (EditText) rootView.findViewById(R.id.et_email);
        etPassword = (EditText) rootView.findViewById(R.id.et_password);
        tvRegister = (TextView) rootView.findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }
}

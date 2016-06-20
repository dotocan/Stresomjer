package es.esy.stresomjer.stresomjer.fragment;

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

import es.esy.stresomjer.stresomjer.helper.Constants;
import es.esy.stresomjer.stresomjer.model.ServerRequest;
import es.esy.stresomjer.stresomjer.model.ServerResponse;
import es.esy.stresomjer.stresomjer.model.User;
import es.esy.stresomjer.stresomjer.R;
import es.esy.stresomjer.stresomjer.helper.LoginRegisterRequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterFragment extends Fragment implements View.OnClickListener{

    private AppCompatButton btnRegister;
    private EditText etFirstName, etLastName, etAge, etEmail, etPassword;
    private TextView tvLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_registrer, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                goToLogin();
                break;

            case R.id.btn_register:
                String first_name = etFirstName.getText().toString();
                String last_name = etLastName.getText().toString();
                String age = etAge.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (!first_name.isEmpty() &&
                        !last_name.isEmpty() &&
                        !age.isEmpty() &&
                        !email.isEmpty() &&
                        !password.isEmpty()) {

                    registerUser(first_name, last_name, age, email, password);

                } else {
                    Snackbar.make(getView(), getString(R.string.fields_required), Snackbar.LENGTH_LONG).show();
                }
        }
    }

    private void registerUser(String firstName, String lastName, String age, String email, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginRegisterRequestInterface loginRegisterRequestInterface = retrofit.create(LoginRegisterRequestInterface.class);

        User user = new User();
        user.setFirst_name(firstName);
        user.setLast_name(lastName);
        user.setAgeId(calculateAgeId(age));
        user.setEmail(email);
        user.setPassword(password);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTER_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = loginRegisterRequestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToLogin() {
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, login);
        ft.commit();
    }

    // Age ids are the same ones that can be found in
    // table "age" in mySQL database
    private int calculateAgeId(String age) {
        int ageInt = Integer.parseInt(age);
        int ageId;

        if (ageInt > 0 && ageInt < 18) ageId = 1;
        else if (ageInt > 18 && ageInt < 30) ageId = 2;
        else if (ageInt > 30 && ageInt < 50) ageId = 3;
        else ageId = 4;

        return ageId;
    }

    private void initViews(View rootView) {
        btnRegister = (AppCompatButton) rootView.findViewById(R.id.btn_register);
        tvLogin = (TextView) rootView.findViewById(R.id.tv_login);

        etFirstName = (EditText) rootView.findViewById(R.id.et_first_name);
        etLastName = (EditText) rootView.findViewById(R.id.et_last_name);
        etAge = (EditText) rootView.findViewById(R.id.et_age);
        etEmail = (EditText) rootView.findViewById(R.id.et_email);
        etPassword = (EditText) rootView.findViewById(R.id.et_password);

        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }
}

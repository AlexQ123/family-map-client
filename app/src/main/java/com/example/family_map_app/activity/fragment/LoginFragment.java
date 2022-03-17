package com.example.family_map_app.activity.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.os.Handler;
import android.os.Message;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.family_map_app.R;

import request.LoginRequest;
import request.RegisterRequest;

import com.example.family_map_app.serverdata.DataCache;
import com.example.family_map_app.task.LoginTask;
import com.example.family_map_app.task.RegisterTask;

public class LoginFragment extends Fragment {

    private static final String LOGIN_SUCCESS_KEY = "LoginSuccessKey";
    private static final String REGISTER_SUCCESS_KEY = "RegisterSuccessKey";

    private boolean isGenderSelected;
    private String gender;

    private Button signInButton;
    private Button registerButton;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private EditText serverHostText;
    private EditText serverPortText;
    private EditText userNameText;
    private EditText passwordText;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            validateSignIn();
            validateRegister();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void validateSignIn() {
        if (serverHostText.getText().toString().trim().length() > 0
                && serverPortText.getText().toString().trim().length() > 0
                && userNameText.getText().toString().trim().length() > 0
                && passwordText.getText().toString().trim().length() > 0) {
            signInButton.setEnabled(true);
        }
        else {
            signInButton.setEnabled(false);
        }
    }

    private void validateRegister() {
        if (serverHostText.getText().toString().trim().length() > 0
                && serverPortText.getText().toString().trim().length() > 0
                && userNameText.getText().toString().trim().length() > 0
                && passwordText.getText().toString().trim().length() > 0
                && firstNameText.getText().toString().trim().length() > 0
                && lastNameText.getText().toString().trim().length() > 0
                && emailText.getText().toString().trim().length() > 0
                && isGenderSelected) {
            registerButton.setEnabled(true);
        }
        else {
            registerButton.setEnabled(false);
        }
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isGenderSelected = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // connect to layout components
        signInButton = view.findViewById(R.id.signInButton);
        registerButton = view.findViewById(R.id.registerButton);
        maleRadioButton = view.findViewById(R.id.radioMale);
        femaleRadioButton = view.findViewById(R.id.radioFemale);
        serverHostText = view.findViewById(R.id.serverHostField);
        serverPortText = view.findViewById(R.id.serverPortField);
        userNameText = view.findViewById(R.id.userNameField);
        passwordText = view.findViewById(R.id.passwordField);
        firstNameText = view.findViewById(R.id.firstNameField);
        lastNameText = view.findViewById(R.id.lastNameField);
        emailText = view.findViewById(R.id.emailAddressField);

        // buttons disabled by default
        signInButton.setEnabled(false);
        registerButton.setEnabled(false);

        // handles enabling and disabling buttons
        serverHostText.addTextChangedListener(textWatcher);
        serverPortText.addTextChangedListener(textWatcher);
        userNameText.addTextChangedListener(textWatcher);
        passwordText.addTextChangedListener(textWatcher);
        firstNameText.addTextChangedListener(textWatcher);
        lastNameText.addTextChangedListener(textWatcher);
        emailText.addTextChangedListener(textWatcher);
        maleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGenderSelected = true;
                gender = "m";
                validateRegister();
            }
        });
        femaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGenderSelected = true;
                gender = "f";
                validateRegister();
            }
        });

        // run background login task
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler uiThreadMessageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        boolean isLoginSuccess = bundle.getBoolean(LOGIN_SUCCESS_KEY);
                        if (isLoginSuccess) {
                            DataCache dataCache = DataCache.getInstance();
                            String userFirst = dataCache.getUser().getFirstName();
                            String userLast = dataCache.getUser().getLastName();

                            Toast.makeText(getActivity(), userFirst + " " + userLast,
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getActivity(), R.string.loginFailToast,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };

                loginRequest = new LoginRequest(userNameText.getText().toString(),
                        passwordText.getText().toString());

                LoginTask loginTask = new LoginTask(uiThreadMessageHandler, loginRequest,
                        serverHostText.getText().toString(), serverPortText.getText().toString());
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(loginTask);
            }
        });

        // run background register task
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler uiThreadMessageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        boolean isRegisterSuccess = bundle.getBoolean(REGISTER_SUCCESS_KEY);
                        if (isRegisterSuccess) {
                            DataCache dataCache = DataCache.getInstance();
                            String userFirst = dataCache.getUser().getFirstName();
                            String userLast = dataCache.getUser().getLastName();

                            Toast.makeText(getActivity(), userFirst + " " + userLast,
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getActivity(), R.string.registerFailToast,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };

                registerRequest = new RegisterRequest(userNameText.getText().toString(),
                        passwordText.getText().toString(), emailText.getText().toString(),
                        firstNameText.getText().toString(), lastNameText.getText().toString(),
                        gender);

                RegisterTask registerTask = new RegisterTask(uiThreadMessageHandler, registerRequest,
                        serverHostText.getText().toString(), serverPortText.getText().toString());
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(registerTask);
            }
        });

        return view;
    }
}
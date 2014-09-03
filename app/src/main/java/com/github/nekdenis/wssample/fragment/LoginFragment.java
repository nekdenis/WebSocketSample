package com.github.nekdenis.wssample.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nekdenis.wssample.R;

import util.ConnectionStatus;
import util.Settings;

/**
 * fragment with login form
 */
public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private EditText nameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    //There are no any arguments so basically we do not need this method
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        nameEditText = (EditText) rootView.findViewById(R.id.login_name_edittext);
        passwordEditText = (EditText) rootView.findViewById(R.id.login_password_edittext);
        loginButton = (Button) rootView.findViewById(R.id.login_button);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Check if wrong activity add this fragment - it won't start
        if (!(getActivity() instanceof LoginManager)) {
            throw new RuntimeException("Activity not implements LoginHandler interface");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListeners();
    }

    private void initListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        //on soft keyboad Done button click
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    return true;
                }
                return false;
            }
        });
    }

    private void login() {
        if (validateInputs() && checkConnection()) {
            Settings.putUserLogin(getActivity(), nameEditText.getText().toString());
            Settings.putUserPassword(getActivity(), passwordEditText.getText().toString());
            //close soft keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
            ((LoginManager) getActivity()).onLoginSuccessful();
        }
    }

    private boolean checkConnection() {
        if (!ConnectionStatus.checkInternetConnection(getActivity())) {
            Toast.makeText(getActivity(), R.string.login_no_internet, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(nameEditText.getText().toString()) || TextUtils.isEmpty(passwordEditText.getText().toString())) {
            Toast.makeText(getActivity(), R.string.user_input_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public interface LoginManager {
        void onLoginSuccessful();
    }
}

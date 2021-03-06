package com.example.johnis.naviraklio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
//import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.johnis.naviraklio.R;
import com.example.johnis.naviraklio.helpers.InputValidation;
import com.example.johnis.naviraklio.model.User;
import com.example.johnis.naviraklio.sql.DatabaseHelper;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class SignupActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, OnClickListener {

    private final AppCompatActivity mActivity = SignupActivity.this;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private ScrollView mSignupFormView;
    private ProgressBar mProgressView;
    private TextInputLayout mTextInputLayoutName;
    private TextInputLayout mTextInputLayoutUsername;
    private TextInputLayout mTextInputLayoutPassword;
    private TextInputLayout mTextInputLayoutConfirmPassword;
    private TextInputLayout mTextInputLayoutAddress;
    private TextInputLayout mTextInputLayoutTel;
    private TextInputLayout mTextInputLayoutCreditCard;

    private TextInputEditText mNameView;
    private AutoCompleteTextView mUsernameView;
    private TextInputEditText mPasswordView;
    private TextInputEditText mConfirmPasswordView;
    private TextInputEditText mAddressView;
    private TextInputEditText mTelView;
    private TextInputEditText mCreditCardView;

    private Button mSignUpButton;
    private TextView mSignInLink;

    // Class references.
    private InputValidation mInputValidation;
    private DatabaseHelper mDatabaseHelper;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        // Set up the signup form.
        initViews();
        initListeners();
        initObjects();
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        mSignupFormView = (ScrollView) findViewById(R.id.signup_form);
        mProgressView = (ProgressBar) findViewById(R.id.signup_progress);

        mTextInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        mTextInputLayoutUsername = (TextInputLayout) findViewById(R.id.textInputLayoutUsername);
        mTextInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        mTextInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);
        mTextInputLayoutAddress = (TextInputLayout) findViewById(R.id.textInputLayoutAddress);
        mTextInputLayoutTel = (TextInputLayout) findViewById(R.id.textInputLayoutTel);
        mTextInputLayoutCreditCard = (TextInputLayout) findViewById(R.id.textInputLayoutCreditCard);

        mNameView = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.textInputEditTextUsername);
        populateAutoComplete();
        mPasswordView = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        mConfirmPasswordView = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);
        mAddressView = (TextInputEditText) findViewById(R.id.textInputEditTextAddress);
        mTelView = (TextInputEditText) findViewById(R.id.textInputEditTextTel);
        mCreditCardView = (TextInputEditText) findViewById(R.id.textInputEditTextCreditCard);

        mSignUpButton = (Button) findViewById(R.id.sign_up_button);

        mSignInLink = (TextView) findViewById(R.id.sign_in_link);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        mSignUpButton.setOnClickListener(this);
        mSignInLink.setOnClickListener(this);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        mInputValidation = new InputValidation(mActivity);
        mDatabaseHelper = new DatabaseHelper(mActivity);
        user = new User();

    }


    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sign_up_button:
                postDataToSQLite();
                break;

            case R.id.sign_in_link:
                finish();
                break;
        }
    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private void postDataToSQLite() {
        if (!mInputValidation.isInputEditTextFilled(mNameView, mTextInputLayoutName, getString(R.string.error_empty_name)))
            return;
        if (!mInputValidation.isAutoCompleteTextViewFilled(mUsernameView, mTextInputLayoutUsername, getString(R.string.error_empty_username)))
            return;
        if (!mInputValidation.isInputEditTextValidUsername(mUsernameView, mTextInputLayoutUsername, getString(R.string.error_invalid_username)))
            return;
        if (!mInputValidation.isInputEditTextFilled(mPasswordView, mTextInputLayoutPassword, getString(R.string.error_empty_password)))
            return;
        if (!mInputValidation.isInputEditTextValidPassword(mPasswordView, mTextInputLayoutPassword, getString(R.string.error_invalid_password)))
            return;
        if (!mInputValidation.isInputEditTextFilled(mConfirmPasswordView, mTextInputLayoutConfirmPassword, getString(R.string.error_empty_confirm_password)))
            return;
        if (!mInputValidation.isInputEditTextMatches(mPasswordView, mConfirmPasswordView,
                mTextInputLayoutConfirmPassword, getString(R.string.error_password_match)))
            return;
        if (!mInputValidation.isInputEditTextFilled(mAddressView, mTextInputLayoutAddress, getString(R.string.error_empty_address)))
            return;
        if (!mInputValidation.isInputEditTextFilled(mTelView, mTextInputLayoutTel, getString(R.string.error_empty_tel)))
            return;
        if (!mInputValidation.isInputEditTextFilled(mCreditCardView, mTextInputLayoutCreditCard, getString(R.string.error_empty_credit_card)))
            return;
        if (!mDatabaseHelper.checkUser(mUsernameView.getText().toString().trim())) {

            user.setName(mNameView.getText().toString().trim());
            user.setUsername(mUsernameView.getText().toString().trim());
            user.setPassword(mPasswordView.getText().toString().trim());
            user.setAddress(mAddressView.getText().toString().trim());
            user.setTel(mTelView.getText().toString().trim());
            user.setCreditCard(mCreditCardView.getText().toString().trim());

            mDatabaseHelper.addUser(user);

            // Snack Bar to show success message that record saved successfully
            Snackbar.make(mSignupFormView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();


        } else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(mSignupFormView, getString(R.string.error_username_exists), Snackbar.LENGTH_LONG).show();
        }


    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        mNameView.setText(null);
        mUsernameView.setText(null);
        mPasswordView.setText(null);
        mConfirmPasswordView.setText(null);
        mAddressView.setText(null);
        mTelView.setText(null);
        mCreditCardView.setText(null);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUsernameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignupFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.StructuredName
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> usernames = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            usernames.add(cursor.getString(ProfileQuery.DISPLAY_NAME));
            cursor.moveToNext();
        }

        addUsernameToAutoComplete(usernames);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addUsernameToAutoComplete(List<String> usernameCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignupActivity.this,
                        android.R.layout.simple_dropdown_item_1line, usernameCollection);

        mUsernameView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.IS_PRIMARY,
        };

        int DISPLAY_NAME = 0;
        int IS_PRIMARY = 1;
    }

}


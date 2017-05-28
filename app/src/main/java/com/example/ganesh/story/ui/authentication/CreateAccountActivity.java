package com.example.ganesh.story.ui.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ganesh.story.R;
import com.example.ganesh.story.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class CreateAccountActivity extends AppCompatActivity {

    private final String LOG_TAG = CreateAccountActivity.class.getName();

    private ProgressDialog mAuthProgessDialog;
    private EditText mEditTextUsernameCreate, mEditTextEmailCreate, mEditTextPasswordCreate;
    private Button mButtonSignUp;
    private String mUserName, mUserEmail, mUserPassword;
private TextView mTextViewGoForSignIn;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        initializeScreen();

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }

    private void initializeScreen() {
        mEditTextUsernameCreate = (EditText) findViewById(R.id.edit_text_create_username);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_create_email);
        mEditTextPasswordCreate = (EditText) findViewById(R.id.edit_text_create_password);
        mButtonSignUp = (Button) findViewById(R.id.button_create_signup);

        mAuthProgessDialog = new ProgressDialog(this);
        mAuthProgessDialog.setTitle("Loading...");
        mAuthProgessDialog.setMessage("Attempting to create account...");
        mAuthProgessDialog.setCancelable(false);

        mTextViewGoForSignIn=(TextView)findViewById(R.id.text_view_sign_in);
        mTextViewGoForSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class));
            }
        });
    }

    public void createAccount() {

        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString();
        mUserPassword = mEditTextPasswordCreate.getText().toString();

        boolean validEmail = isEmailValid(mUserEmail);
        boolean validUserName = isUserNameValid(mUserName);
        boolean validUserPassword = isPasswordValid(mUserPassword);

        if (!validEmail || !validUserName || !validUserPassword) {
            return;
        }
        mAuthProgessDialog.show();
        mAuth.createUserWithEmailAndPassword(mUserEmail, mUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuthProgessDialog.dismiss();
                    String user_id = mAuth.getCurrentUser().getUid();

                    createUserInFirebaseHelper(user_id);

                    Toast.makeText(CreateAccountActivity.this, "Register SuccessFully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
            }
        });

    }

    private void createUserInFirebaseHelper(String user_id) {
        final DatabaseReference userLocationReference = mDatabase.child(user_id);
        userLocationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userLocationReference.child("Username").setValue(mUserName);
                userLocationReference.child("Email").setValue(mUserEmail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CreateAccountActivity.this, "Error While Registration", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isEmailValid(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEditTextEmailCreate.setError(String.format(getString(R.string.error_invalid_email_not_valid),
                    email));
            return false;
        }
        return isGoodEmail;
    }

    private boolean isUserNameValid(String userName) {
        if (userName.equals("")) {
            mEditTextUsernameCreate.setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            mEditTextPasswordCreate.setError(getResources().getString(R.string.error_invalid_password_not_valid));
            return false;
        }
        return true;
    }

    public void goForSignIn(View view) {
        startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class));
    }
}

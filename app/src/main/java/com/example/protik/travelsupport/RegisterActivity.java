package com.example.protik.travelsupport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editEmail, editPass, editConfirmPass;
    private Button registerBtn;
    private ProgressDialog progressDialog;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    String email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mToolbar = findViewById(R.id.register_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        editEmail = findViewById(R.id.emailInId);
        editPass = findViewById(R.id.passwordInId);
        editConfirmPass = findViewById(R.id.confirmPasswordInId);
        registerBtn = findViewById(R.id.registerBtnId);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == registerBtn) {
            registerUser();
        }
    }

    private void registerUser() {

        email = editEmail.getText().toString().trim();
        password = editPass.getText().toString().trim();
        confirmPassword = editConfirmPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),"Please Enter Email..!!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),"Please Enter Password..!!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getApplicationContext(),"Please Re-Enter Password..!!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals(confirmPassword)) {

            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Registered Successfully..!!",Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {

                        progressDialog.dismiss();
                        String errorMsg = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(),"Failed to Register..!!\nERROR : "+ errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {

            Toast.makeText(getApplicationContext(), "Password doesn't Match..!!", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.protik.travelsupport;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetupActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private CircleImageView setupImage;
    private Uri mainImageUri = null;
    private EditText editName, editBirth;
    private RadioGroup radioGroup;
    private RadioButton genderBtn;
    private DatePickerDialog datePickerDialog;
    private Button submitBtn;
    //private ContentLoadingProgressBar setupProgress;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    String userName, birthDate, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        mToolbar = findViewById(R.id.profile_completion_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Complete Your Profile");

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        setupImage = findViewById(R.id.circleImageId);
        editName = findViewById(R.id.nameInId);
        editBirth = findViewById(R.id.dateInId);
        radioGroup = findViewById(R.id.radioBtnId);
        submitBtn = findViewById(R.id.setupAccBtnId);
        //setupProgress = findViewById(R.id.setupProgress);

        editBirth.setText("Not Specified..!!");

        editBirth.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        setupImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.circleImageId) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (ContextCompat.checkSelfPermission(AccountSetupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "Permission Denied..!!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(AccountSetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                } else {
                    Toast.makeText(getApplicationContext(), "You Already have Permission...", Toast.LENGTH_SHORT).show();

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(AccountSetupActivity.this);

                }
            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(AccountSetupActivity.this);
            }
        }

        else if (view.getId() == R.id.dateInId) {

            getDate();

        }

        else if (view.getId() == R.id.setupAccBtnId) {

            userName = editName.getText().toString().trim();

            int selectedId = radioGroup.getCheckedRadioButtonId();
            genderBtn = findViewById(selectedId);
            gender = genderBtn.getText().toString().trim();

            if (!TextUtils.isEmpty(userName) && mainImageUri != null) {

                //setupProgress.setVisibility(View.VISIBLE);
                final String user_id = mAuth.getCurrentUser().getUid();
                StorageReference imagePath = mStorageRef.child("profile_images").child(user_id + ".jpg");
                imagePath.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult().getDownloadUrl();
                            Toast.makeText(AccountSetupActivity.this, "The Image is Uploaded..!", Toast.LENGTH_SHORT).show();

                            //Store Data Here..
                            Map<String,String> userMap = new HashMap<>();
                            userMap.put("name", userName);
                            userMap.put("gender", gender);
                            userMap.put("birthday", birthDate);
                            userMap.put("image", downloadUri.toString());

                            mFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(AccountSetupActivity.this, "Account is set up", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AccountSetupActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        finish();
                                        startActivity(intent);

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(AccountSetupActivity.this, "ERROR: " + error, Toast.LENGTH_SHORT).show();
                                    }

                                    //setupProgress.setVisibility(View.INVISIBLE);
                                }
                            });

                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(AccountSetupActivity.this, "ERROR: " + error, Toast.LENGTH_SHORT).show();
                            //setupProgress.setVisibility(View.INVISIBLE);
                        }

                    }
                });
            }
        }
    }

    private void getDate() {

        DatePicker datePicker = new DatePicker(this);
        int currentDay = datePicker.getDayOfMonth();
        int currentMonth = ( datePicker.getMonth() ) + 1;
        int currentYear = datePicker.getYear();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                String day = i2+"/";
                String month = (i1+1)+"/";
                String year = i+"";

                birthDate = day+month+year;
                editBirth.setText(birthDate);
            }
        }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                setupImage.setImageURI(mainImageUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

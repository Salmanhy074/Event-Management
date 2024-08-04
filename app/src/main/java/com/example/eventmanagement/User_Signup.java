package com.example.eventmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User_Signup extends AppCompatActivity {
    TextView txtLogin;

    EditText textName, textNumber, textEmail;
    TextInputEditText setPasswordLayout;
    FirebaseAuth uAuth;    ProgressBar progressBar;
    MaterialButton signupBtnUser;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_signup);



        FirebaseApp.initializeApp(this);
        uAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_circular);
        textName = (findViewById(R.id.editTextUsername));
        textNumber = (findViewById(R.id.editTextUsernumber));
        textEmail = (findViewById(R.id.editTextUseremail));
        setPasswordLayout = (findViewById(R.id.passwordUser));
        signupBtnUser = findViewById(R.id.signupBtnUser);
        txtLogin = findViewById(R.id.logintxtUser);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Signup.this, login_screen.class);
                startActivity(intent);
            }
        });





        signupBtnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = textName.getText().toString();
                String number = textNumber.getText().toString();
                String email = textEmail.getText().toString();
                String password = setPasswordLayout.getText().toString();


                if (TextUtils.isEmpty(name)) {
                    textName.setError("Enter name");
                    textName.requestFocus();
                    return;

                } else if (TextUtils.isEmpty(number)) {
                    textNumber.setError("Enter number");
                    textNumber.requestFocus();
                    return;

                } else if (number.length() < 10) {
                    textNumber.setError("Number must be 11 digits");
                    textNumber.requestFocus();
                    return;

                } else if (TextUtils.isEmpty(email)) {
                    textEmail.setError("Enter email");
                    textEmail.requestFocus();
                    return;

                }


                else if (TextUtils.isEmpty(password)) {
                    setPasswordLayout.setError("Enter password");
                    setPasswordLayout.requestFocus();
                    return;

                }
                else if (password.length() < 6) {
                    setPasswordLayout.setError("Password must be 6 digits");
                    setPasswordLayout.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                uAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = uAuth.getCurrentUser();
                            String userId = currentUser.getUid();


                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Admin Data");
                            UserModel data = new UserModel(name, number, email);

                            databaseReference.child(userId)
                                    .setValue(data)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(User_Signup.this, "User Account Created.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(User_Signup.this, Home_Screen.class);
                                                startActivity(intent);
                                                finish();
                                                progressBar.setVisibility(View.GONE);
                                            }else {
                                                Toast.makeText(User_Signup.this, "Authentication error", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }

                        else {
                            // If registration fails, check for specific error codes
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                // If email already exists
                                Toast.makeText(getApplicationContext(), "Email already exists.",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                            else if (!isValidEmail(email)){
                                Toast.makeText(User_Signup.this, "Enter correct email!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }



                    }

                    private boolean isValidEmail(String email) {
                        String emailPattern =
                                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                        return email.matches(emailPattern);
                    }

                });
            }
        });


    }
}
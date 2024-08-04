package com.example.eventmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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


public class signup extends AppCompatActivity {

    EditText textName, textNumber, textEmail, textAddress, textCnic, textCompanyName;
    TextInputEditText setPasswordLayout;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView txtLogin;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



        MaterialButton signupBtn;
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_circular);
        textName = (findViewById(R.id.editTextname));
        textNumber = (findViewById(R.id.editTextnumber));
        textEmail = (findViewById(R.id.editTextemail));
        textCnic = (findViewById(R.id.editTexCnic));
        textAddress = (findViewById(R.id.editTextAddress));
        textCompanyName = (findViewById(R.id.editTextCompanyName));
        setPasswordLayout = (findViewById(R.id.password));
        signupBtn = findViewById(R.id.signupBtn);


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String name = textName.getText().toString();
                String number = textNumber.getText().toString();
                String email = textEmail.getText().toString();
                String address = textAddress.getText().toString();
                String cnic = textCnic.getText().toString();
                String company_name = textCompanyName.getText().toString();
                String password = setPasswordLayout.getText().toString();





                if (TextUtils.isEmpty(name)) {
                    textName.setError("Enter name");
                    textName.requestFocus();
                    return;

                } else if (TextUtils.isEmpty(number)) {
                    textNumber.setError("Enter number");
                    textNumber.requestFocus();
                    return;

                }else if (number.length() < 10) {
                    textNumber.setError("Number must be greater than 10 digits");
                    textNumber.requestFocus();
                    return;

                } else if (TextUtils.isEmpty(email)) {
                    textEmail.setError("Enter email");
                    textEmail.requestFocus();
                    return;

                }

                else if (TextUtils.isEmpty(address)) {
                    textAddress.setError("Enter your company address");
                    textAddress.requestFocus();
                    return;

                }

                else if (TextUtils.isEmpty(cnic)) {
                    textCnic.setError("Enter CNIC number");
                    textCnic.requestFocus();
                    return;

                }

                else if (TextUtils.isEmpty(company_name)) {
                    textCompanyName.setError("Enter company name");
                    textCompanyName.requestFocus();
                    return;

                }

                else if (TextUtils.isEmpty(password)) {
                    setPasswordLayout.setError("Enter password");
                    setPasswordLayout.requestFocus();
                    return;

                }
                else if (password.length() < 6) {
                    setPasswordLayout.setError("Password must be greater than 5 digits");
                    setPasswordLayout.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                String userId = currentUser.getUid();


                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Admin Data");
                                SignupModel data = new SignupModel(name, number, email, address, cnic, company_name);

                                databaseReference.child(userId).setValue(data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(signup.this, "Admin Account Created.", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(signup.this, Home_Screen.class);
                                                            startActivity(intent);
                                                            finish();
                                                            progressBar.setVisibility(View.GONE);
                                                        }else {
                                                            Toast.makeText(signup.this, "Authentication error", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(signup.this, "Enter correct email!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }



                        }

                    });
            }
        });


        txtLogin = findViewById(R.id.logintxt);
        Intent bNext;
        bNext = new Intent(signup.this, login_screen.class);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(bNext);
                finish();

            }
        });

    }

    // Example method for email validation using a simple regular expression
    private boolean isValidEmail(String email) {
        //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailPattern);
    }

}
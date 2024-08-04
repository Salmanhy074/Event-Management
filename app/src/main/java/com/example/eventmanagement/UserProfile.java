package com.example.eventmanagement;

import static com.example.eventmanagement.Home_Screen.bottomNavigationView;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;


public class UserProfile extends Fragment {

    TextView logout, txtAboutUs;
    TextView txtPrivate;
    @SuppressLint("UseSwitchCompatOrMaterialCode") Switch hideButton;
    TextView texviewName, textViewNumber, textViewEmail;
    DatabaseReference mDatabase;
    ProgressBar progressBar;
    TextView userNameTextView, userEmailTextView, userContactTextView, updateProfile;
    String email, name, number, cnic;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String THEME_KEY = "theme";


    @SuppressLint({"SetTextI18n", "MissingInflatedId", "CutPasteId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        TextView textView = view.findViewById(R.id.txtHome);
        logout = view.findViewById(R.id.txtLogout);
        progressBar = view.findViewById(R.id.logoutProgressBar);
        txtAboutUs = view.findViewById(R.id.txtAboutUs);
        TextView txtContactUs = view.findViewById(R.id.txtContactUs);

        TextView txtFaqs = view.findViewById(R.id.txFaqs);

        txtFaqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFaqsFragment();

            }

            private void loadFaqsFragment() {
                Questions_Fragment fragmentB = new Questions_Fragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragmentB);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        txtAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAddFragment();
            }

            private void loadAddFragment() {
                AddFragment homeFragment = new AddFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, homeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        TextView txtPasswordReset;
        txtPasswordReset = view.findViewById(R.id.txtPassword);

        txtPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), Reset_Password.class));
                getActivity().finish();
            }
        });



        userNameTextView = view.findViewById(R.id.txtName);
        userEmailTextView = view.findViewById(R.id.txtEmail);
        userContactTextView = view.findViewById(R.id.txtContact);

        // Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Admin Data").child(user.getUid());
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        name = dataSnapshot.child("owner_name").getValue(String.class);
                        email = dataSnapshot.child("email").getValue(String.class);
                        number = dataSnapshot.child("number").getValue(String.class);
                        if (name != null) {
                            userNameTextView.setText(name);
                        }
                        if (email != null) {
                            userEmailTextView.setText(email);
                        }
                        if (number != null) {
                            userContactTextView.setText(number);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

        updateProfile = view.findViewById(R.id.txtUpdateProfile);

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(requireContext(), R.style.CustomAlertDialogStyle);
                dialog.setContentView(R.layout.update_profile);
                EditText editName, editNumber;
                MaterialButton updateButton;
                updateButton = dialog.findViewById(R.id.profileUpdateButton);
                editName = dialog.findViewById(R.id.editProfileName);
                editNumber = dialog.findViewById(R.id.editProfileNumber);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Admin Data").child(user.getUid());
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String name = dataSnapshot.child("owner_name").getValue(String.class);
                                String number = dataSnapshot.child("number").getValue(String.class);
                                if (name != null) {
                                    editName.setText(name);
                                }
                                if (number != null) {
                                    editNumber.setText(number);
                                }

                            } else {
                                Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = editName.getText().toString().trim();
                        String newNumber = editNumber.getText().toString().trim();

                        if (!TextUtils.isEmpty(newName) && !TextUtils.isEmpty(newNumber)) {
                            HashMap<String, Object> updates = new HashMap<>();
                            updates.put("owner_name", newName);
                            updates.put("number", newNumber);

                            mDatabase.updateChildren(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }


                        // Get current user
                         if (user != null) {
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Admin Data").child(user.getUid());
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        name = dataSnapshot.child("owner_name").getValue(String.class);
                                        email = dataSnapshot.child("email").getValue(String.class);
                                        number = dataSnapshot.child("number").getValue(String.class);
                                        if (name != null) {
                                            userNameTextView.setText(name);
                                        }
                                        if (email != null) {
                                            userEmailTextView.setText(email);
                                        }
                                        if (number != null) {
                                            userContactTextView.setText(number);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                        }
                         else {
                            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });




        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signOut();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().startActivity(new Intent(getContext(), login_screen.class));
                        getActivity().finish();
                    }
                }, 2000);
            }
        });




        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switchTohomeFragment();
                loadNewFragment();
                bottomNavigationView.setSelectedItemId(R.id.home);

            }


            private void loadNewFragment() {
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, homeFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }

        });

        txtContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switchTohomeFragment();
                loadNewFragment();
                bottomNavigationView.setSelectedItemId(R.id.accessibility);

            }

            private void loadNewFragment() {
                AccessibilityFragment homeFragment = new AccessibilityFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, homeFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        // Initialize the switch button
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchButtonDark = view.findViewById(R.id.darkMode);

        // Load the saved theme state
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkModeEnabled = prefs.getBoolean(THEME_KEY, false);
        switchButtonDark.setChecked(isDarkModeEnabled);

        // Set a checked change listener for the switch button
        switchButtonDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the selected theme state
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(THEME_KEY, isChecked);
                editor.apply();

                // If the current fragment is the profile fragment, don't load the home fragment again
                if ((getParentFragment() instanceof UserProfile)) {
                    // Apply the selected theme
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String PREF_PRIVATE_ACCOUNT = "private_account";

// Initialize SharedPreferences
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

// Inside your Fragment's onCreateView or onViewCreated method
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        texviewName = view.findViewById(R.id.txtName);
        textViewNumber = view.findViewById(R.id.txtContact);
        textViewEmail = view.findViewById(R.id.txtEmail);
        hideButton = view.findViewById(R.id.privateAccount);
        txtPrivate = view.findViewById(R.id.txtPrivate);


        // Retrieve the saved state of the switch button from SharedPreferences
        boolean isPrivateAccount = sharedPreferences.getBoolean(PREF_PRIVATE_ACCOUNT, false);
        hideButton.setChecked(isPrivateAccount);
        toggleUserDataVisibility(isPrivateAccount);

        hideButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the state of the switch button to SharedPreferences
                editor.putBoolean(PREF_PRIVATE_ACCOUNT, isChecked);
                editor.apply();
                toggleUserDataVisibility(isChecked);

            }
        });

    }

    private void toggleUserDataVisibility(boolean isPrivate) {
        if (isPrivate) {
            txtPrivate.setVisibility(View.VISIBLE);
            texviewName.setVisibility(View.GONE);
            textViewNumber.setVisibility(View.GONE);
            textViewEmail.setVisibility(View.GONE);

        } else {
            txtPrivate.setVisibility(View.GONE);
            texviewName.setVisibility(View.VISIBLE);
            textViewNumber.setVisibility(View.VISIBLE);
            textViewEmail.setVisibility(View.VISIBLE);
        }
    }

}
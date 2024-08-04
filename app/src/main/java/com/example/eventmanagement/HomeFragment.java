package com.example.eventmanagement;

import static com.example.eventmanagement.MainActivity.eventsArray;
import static com.example.eventmanagement.MainActivity.privateEventsArray;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class HomeFragment extends Fragment {


    MaterialButton allEvents, myEvents;
    RecyclerView recyclerView;
    RecyclerView itemsPrivate;
    EventsAdapter eventsAdapter;
    PrivateEventsAdapter privateEventsAdapter;
    DatabaseReference databaseReference;
    private Calendar selectedDate;
    EditText editName, editNumber;
    TextView editDate, editTime;
    FloatingActionButton floatingActionButton;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.rvItems);
        itemsPrivate = view.findViewById(R.id.privateItems);
        floatingActionButton = view.findViewById(R.id.btnDialog);
        editTime = view.findViewById(R.id.edtTime);
        selectedDate = Calendar.getInstance();

        userAdmin();

        allEvents = view.findViewById(R.id.btnCompany);
        myEvents = view.findViewById(R.id.btnIndividual);

        eventsAdapter = new EventsAdapter(getContext(), eventsArray);
        privateEventsAdapter = new PrivateEventsAdapter(getContext(), privateEventsArray);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //itemsPrivate.setLayoutManager(layoutManager);

        eventsAdapter.notifyItemInserted(eventsArray.size());
        recyclerView.setAdapter(eventsAdapter);

        allEvents.setBackgroundColor(Color.parseColor("#FF5733"));

        //itemsPrivate.setVisibility(View.VISIBLE);

        myEvents.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                itemsPrivate.setLayoutManager(layoutManager);
                myEvents.setBackgroundColor(Color.parseColor("#FF5733"));
                allEvents.setBackgroundColor(Color.parseColor("#58D4CA"));
                recyclerView.setVisibility(View.GONE);
                itemsPrivate.setVisibility(View.VISIBLE);
                itemsPrivate.setAdapter(privateEventsAdapter);

            }
        });

        allEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                allEvents.setBackgroundColor(Color.parseColor("#FF5733"));
                myEvents.setBackgroundColor(Color.parseColor("#58D4CA"));
                eventsAdapter.notifyItemInserted(eventsArray.size());
                recyclerView.setAdapter(eventsAdapter);
                recyclerView.setVisibility(View.VISIBLE);
                itemsPrivate.setVisibility(View.GONE);
                recyclerView.setAdapter(eventsAdapter);

            }
        });



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(requireActivity(), R.style.CustomAlertDialogStyle);
                dialog.setContentView(R.layout.add_event);
                MaterialButton dialogButton = dialog.findViewById(R.id.btnAdd);
                editName = dialog.findViewById(R.id.edtName);
                editNumber = dialog.findViewById(R.id.edtNumber);
                editDate = dialog.findViewById(R.id.edtDate);
                editTime = dialog.findViewById(R.id.edtTime);



                editDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editDate.setFocusable(false);
                        editDate.setFocusableInTouchMode(false);
                        showDatePickerDialog();
                    }
                });
                editTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showTimeDialog();
                    }


                });


                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String event_name = editName.getText().toString();
                        String event_location = editNumber.getText().toString();
                        String event_date = editDate.getText().toString().trim();
                        String event_time = editTime.getText().toString().trim();


                        editName.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                editName.setError(null);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });


                        editNumber.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                editNumber.setError(null);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });


                        if (TextUtils.isEmpty(event_name)) {
                            editName.setError("Enter event name");
                            editName.requestFocus();
                            return;

                        } else if (TextUtils.isEmpty(event_location)) {
                            editNumber.setError("Enter location");
                            editNumber.requestFocus();
                            return;
                        } else if (TextUtils.isEmpty(event_date)) {
                            editDate.setError("Select Date");
                            editDate.requestFocus();
                            editDate.setFocusable(false);
                            return;
                        } else if (TextUtils.isEmpty(event_time)) {
                            editTime.setError("Enter Time");
                            editTime.requestFocus();
                            return;
                        }
                        dialog.dismiss();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Admin Data").child(user.getUid());
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String ownerName = dataSnapshot.child("owner_name").getValue(String.class);
                                        String ownerEmail = dataSnapshot.child("email").getValue(String.class);
                                        String UUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Events");
                                        DatabaseReference myReference = databaseReference.child(UUID);
                                        UserData data = new UserData(event_name, event_location, event_date, event_time, ownerName, ownerEmail);
                                        String userId = myReference.push().getKey();
                                        //Toast.makeText(getContext(), userId, Toast.LENGTH_SHORT).show();

                                        assert userId != null;
                                        myReference.child(userId).setValue(data);

                                        DatabaseReference adminEventDataRef = FirebaseDatabase.getInstance().getReference("Admin Data").child(UUID).child("Event Data");
                                        adminEventDataRef.child(userId).setValue(data);
                                    } else {
                                        Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }

                        Toast.makeText(getContext(), "Event Registered", Toast.LENGTH_SHORT).show();


                    }


                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
        });


        eventsArray.clear();
        fetchDataFromFirebase();


    }

    private void userAdmin() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String currentUserUid = user.getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Admin Data").child(currentUserUid);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("owner_cnic")) {
                        floatingActionButton.setVisibility(View.VISIBLE);
                        myEvents.setVisibility(View.VISIBLE);

                    } else {
                        floatingActionButton.setVisibility(View.GONE);
                        myEvents.setVisibility(View.GONE);
                    }
                } else{
                    Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    floatingActionButton.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Events");

        eventsArray.clear();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot itemsData : snapshot.getChildren()) {
                            UserData eventsModel = itemsData.getValue(UserData.class);
                            if (eventsModel != null) {
                                eventsArray.add(eventsModel);
                            }
                        }
                    }
                    eventsAdapter.notifyDataSetChanged();
                } else {
                    eventsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FirebaseData", "Failed to read value.", error.toException());
            }
        });
    }


    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.show();

    }

    private final DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTextView();
                }
            };

    private void updateDateTextView() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void showTimeDialog() {

        // Initialize two Calendar instances for "from" and "to" times
        Calendar fromTime = Calendar.getInstance();
        Calendar toTime = Calendar.getInstance();

        // Initialize TimePickerDialog for "from" time
        TimePickerDialog fromTimePickerDialog = new TimePickerDialog(
                getContext(),

                // OnTimeSetListener for "from" time
                (view, hourOfDay, minute) -> {
                    fromTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    fromTime.set(Calendar.MINUTE, minute);

                    // Initialize TimePickerDialog for "to" time
                    TimePickerDialog toTimePickerDialog = new TimePickerDialog(
                            getContext(),

                            // OnTimeSetListener for "to" time
                            (view1, hourOfDay1, minute1) -> {
                                toTime.set(Calendar.HOUR_OF_DAY, hourOfDay1);
                                toTime.set(Calendar.MINUTE, minute1);

                                // Validate the range (optional)
                                if (toTime.before(fromTime)) {
                                    // If the "to" time is before the "from" time, display an error message
                                    Toast.makeText(getContext(), "Please select a valid time range", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Display the selected time range
                                    String fromAmPm = fromTime.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
                                    String toAmPm = toTime.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
                                    String selectedTimeRange = String.format(Locale.getDefault(), " %02d:%02d %s to %02d:%02d %s",
                                            fromTime.get(Calendar.HOUR), fromTime.get(Calendar.MINUTE), fromAmPm,
                                            toTime.get(Calendar.HOUR), toTime.get(Calendar.MINUTE), toAmPm);
                                    editTime.setText(selectedTimeRange);
                                }
                            },
                            toTime.get(Calendar.HOUR_OF_DAY), // Initial hour for "to" time
                            toTime.get(Calendar.MINUTE), // Initial minute for "to" time
                            false // 24 hour format
                    );

                    // Show the "to" time picker dialog
                    toTimePickerDialog.show();
                },
                fromTime.get(Calendar.HOUR_OF_DAY), // Initial hour for "from" time
                fromTime.get(Calendar.MINUTE), // Initial minute for "from" time
                false // 24 hour format
        );

        // Show the "from" time picker dialog
        fromTimePickerDialog.show();
    }


}
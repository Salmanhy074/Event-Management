package com.example.eventmanagement;

import static com.example.eventmanagement.MainActivity.privateEventsArray;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PrivateEventsAdapter extends RecyclerView.Adapter<PrivateEventsAdapter.ViewHolder> {

    List<UserData> pData;
    LayoutInflater pInflator;
    Context context;
    Calendar selectedDate;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Registered Events")
            .child(user.getUid());
    EditText edtNameUpdate, edtNumberUpdate, editDate, editTime;
    String event_name, event_location, event_date, event_time;

    PrivateEventsAdapter(Context context, List<UserData> data) {
        this.context = context;
        this.pInflator = LayoutInflater.from(context);
        this.pData = data;
        selectedDate = Calendar.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = pInflator.inflate(R.layout.contact_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserData eventsModel = pData.get(position);
        holder.name.setText(eventsModel.getEventName());
        holder.address.setText(eventsModel.getEventLocation());
        holder.date.setText(eventsModel.getEventDate());
        holder.time.setText(eventsModel.getEventTime());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context, R.style.CustomAlertDialogStyle);
                dialog.setContentView(R.layout.alert_dialog);
                MaterialButton updateButton, deleteButton;
                updateButton = dialog.findViewById(R.id.updateDialog);
                deleteButton = dialog.findViewById(R.id.deleteDialog);


                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (i == position) {
                                String myEventId = snapshot.getKey();
                                editevent(myEventId, updateButton, deleteButton);
                                break;
                            }
                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

    }

    private void editevent(String myEventId, MaterialButton updateButton, MaterialButton deleteButton) {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog myDialog = new Dialog(context, R.style.CustomAlertDialogStyle);
                myDialog.setContentView(R.layout.update_event);
                MaterialButton btnUpdate = myDialog.findViewById(R.id.btnUpdate);
                MaterialButton btnCancel = myDialog.findViewById(R.id.btnCancel);
                edtNameUpdate = myDialog.findViewById(R.id.edtNameUpdate);
                edtNumberUpdate = myDialog.findViewById(R.id.edtNumberUpdate);
                editDate = myDialog.findViewById(R.id.edtDateUpdate);
                editTime = myDialog.findViewById(R.id.edtTimeUpdate);
                myDialog.setCanceledOnTouchOutside(false);


                //get data from firebase and display on EditText.
                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
                        .child("Admin Data")
                        .child(user.getUid())
                        .child("Event Data")
                        .child(myEventId);

                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            event_name = dataSnapshot.child("eventName").getValue(String.class);
                            event_location = dataSnapshot.child("eventLocation").getValue(String.class);
                            event_date = dataSnapshot.child("eventDate").getValue(String.class);
                            event_time = dataSnapshot.child("eventTime").getValue(String.class);

                            edtNameUpdate.setText(event_name);
                            edtNumberUpdate.setText(event_location);
                            editDate.setText(event_date);
                            editTime.setText(event_time);
                        } else {
                            Toast.makeText(context, "Event data not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, "Error fetching event data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




                editDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog();
                    }
                });
                editTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimeDialog();
                    }
                });

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        event_name = edtNameUpdate.getText().toString().trim();
                        event_location = edtNumberUpdate.getText().toString().trim();
                        event_date = editDate.getText().toString();
                        event_time = editTime.getText().toString();



                        edtNameUpdate.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                edtNameUpdate.setError(null);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });
                        edtNumberUpdate.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                edtNumberUpdate.setError(null);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        if (TextUtils.isEmpty(event_name)) {
                            edtNameUpdate.setError("Enter event name");
                            edtNameUpdate.requestFocus();
                            return;

                        } else if (TextUtils.isEmpty(event_location)) {
                            edtNumberUpdate.setError("Enter location");
                            edtNumberUpdate.requestFocus();
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
                        } else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("eventName", event_name);
                            hashMap.put("eventLocation", event_location);
                            hashMap.put("eventTime", event_time);
                            hashMap.put("eventDate", event_date);
                            databaseReference.child(myEventId).updateChildren(hashMap);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                    .child("Admin Data")
                                    .child(user.getUid())
                                    .child("Event Data");
                            reference.child(myEventId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Event Updated", Toast.LENGTH_SHORT).show();
                                        myDialog.dismiss();

                                    } else {
                                        Toast.makeText(context, "Error! " + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });




                        }
                        privateEventsArray.clear();
                        fetchPrivateDataFromFirebase(user);
                    }
                });


                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });



                myDialog.show();
            }

        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog deleteDialog = new Dialog(context, R.style.CustomAlertDialogStyle);
                deleteDialog.setContentView(R.layout.delete_dialog);
                MaterialButton deleteButton, cancelButton;
                deleteButton = deleteDialog.findViewById(R.id.deleteButton);
                cancelButton = deleteDialog.findViewById(R.id.cancelButton);
                deleteDialog.setCanceledOnTouchOutside(false);
                deleteDialog.show();

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference.child(myEventId).removeValue();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("Admin Data")
                                .child(user.getUid())
                                .child("Event Data");
                        reference.child(myEventId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Event Deleted", Toast.LENGTH_SHORT).show();
                                    deleteDialog.dismiss();
                                } else {
                                    Toast.makeText(context, "Error deleting event: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        privateEventsArray.clear();
                        fetchPrivateDataFromFirebase(user);
                    }
                });


                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return pData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, address, date, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtname);
            address = itemView.findViewById(R.id.txtnumber);
            date = itemView.findViewById(R.id.textDate);
            time = itemView.findViewById(R.id.textTime);
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener =
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

        Calendar fromTime = Calendar.getInstance();
        Calendar toTime = Calendar.getInstance();

        TimePickerDialog fromTimePickerDialog = new TimePickerDialog(
                context,

                (view, hourOfDay, minute) -> {
                    fromTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    fromTime.set(Calendar.MINUTE, minute);

                    TimePickerDialog toTimePickerDialog = new TimePickerDialog(
                            context,

                            (view1, hourOfDay1, minute1) -> {
                                toTime.set(Calendar.HOUR_OF_DAY, hourOfDay1);
                                toTime.set(Calendar.MINUTE, minute1);

                                if (toTime.before(fromTime)) {
                                    Toast.makeText(context, "Please select a valid time range", Toast.LENGTH_SHORT).show();
                                } else {
                                    String fromAmPm = fromTime.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
                                    String toAmPm = toTime.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
                                    String selectedTimeRange = String.format(Locale.getDefault(), " %02d:%02d %s to %02d:%02d %s",
                                            fromTime.get(Calendar.HOUR), fromTime.get(Calendar.MINUTE), fromAmPm,
                                            toTime.get(Calendar.HOUR), toTime.get(Calendar.MINUTE), toAmPm);
                                    editTime.setText(selectedTimeRange);
                                }
                            },
                            toTime.get(Calendar.HOUR_OF_DAY),
                            toTime.get(Calendar.MINUTE),
                            false // 24 hour format
                    );

                    toTimePickerDialog.show();
                },
                fromTime.get(Calendar.HOUR_OF_DAY),
                fromTime.get(Calendar.MINUTE),
                false // 24 hour format
        );

        fromTimePickerDialog.show();
    }

    private void fetchPrivateDataFromFirebase(FirebaseUser user) {
        if (user != null) {
            String UUID = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin Data").child(UUID);
            databaseReference.child("Event Data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    privateEventsArray.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserData userData = snapshot.getValue(UserData.class);
                        privateEventsArray.add(userData);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("FirebaseData", "Failed to read value.", error.toException());
                }
            });
        }
    }


}

package com.example.eventmanagement;



import static com.example.eventmanagement.Home_Screen.bottomAppBar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<UserData> mData;
    private LayoutInflater mInflater;
    Context context;
    Calendar selectedDate;
    EditText name, address, date, time;

    @SuppressLint("NotifyDataSetChanged")
    EventsAdapter(Context context, List<UserData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.contact_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserData eventsModel = mData.get(position);
        holder.name.setText(eventsModel.getEventName());
        holder.address.setText(eventsModel.getEventLocation());
        holder.date.setText(eventsModel.getEventDate());
        holder.time.setText(eventsModel.getEventTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.data = eventsModel;
                loadChatFragment(v.getContext());
            }
        });

    }

    private void loadChatFragment(Context context) {
        bottomAppBar.setVisibility(View.GONE);

        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, new ChatFragment());
        transaction.commit();
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, date, time;

        ViewHolder(View itemView) {
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
        date.setText(sdf.format(selectedDate.getTime()));
    }

    private void showTimeDialog(){

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
                                    time.setText(selectedTimeRange);
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



}

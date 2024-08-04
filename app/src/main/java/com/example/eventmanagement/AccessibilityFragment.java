package com.example.eventmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccessibilityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accessibility, container, false);

        TextView phoneNumberTextView = view.findViewById(R.id.phone_number);
        TextView emailAddressTextView = view.findViewById(R.id.email_address);
        TextView whatsappContactTextView = view.findViewById(R.id.whatsapp_contact);
        TextView website = view.findViewById(R.id.website);
        TextView address = view.findViewById(R.id.address);

        phoneNumberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+923082456659"));
                startActivity(callIntent);
            }
        });


        emailAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String headerReceiver = "Hello, I need some help!";
                String bodyMessageFormal = "I am reaching out regarding...";
                String subject = "Assistance Required";

                String uriText = "mailto:bsf2003016@ue.edu.pk" +
                        "?subject=" + Uri.encode(subject) +
                        "&body=" + Uri.encode(headerReceiver + "\n\n" + bodyMessageFormal);

                Uri uri = Uri.parse(uriText);

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(uri);
                startActivity(emailIntent);
            }
        });



        whatsappContactTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSupportChat ();
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String websiteUrl = "https://ue.edu.pk/campuscontact.php?id=39";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
                startActivity(intent);
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = "31.3946019,73.0679099";
                String searchQuery = "University of Education Faisalabad Campus";
                Uri gmmIntentUri = Uri.parse("geo:" + coordinates + "?q=" + Uri.encode(searchQuery));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });



        return view;
    }
    private void startSupportChat() {

        try {
            String headerReceiver = "*_Hello, I need some help!_*";
            String bodyMessageFormal = "*Assistance Required*";
            String whatsappContain = headerReceiver + "\n\n" + bodyMessageFormal;
            String trimToNumner = "+923082456659";
            Intent intent = new Intent ( Intent.ACTION_VIEW );
            intent.setData ( Uri.parse ( "https://wa.me/" + trimToNumner + "/?text=" + whatsappContain ) );
            startActivity ( intent );
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }
}
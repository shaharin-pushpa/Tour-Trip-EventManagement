package com.example.kowshick.travelmate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {
    private EditText eventNameET,locationET,destinationET,departureET,budgetET;
    private DatabaseReference eventRef;
    private DatabaseReference rootRef;
    private FirebaseAuth auth;
    private Calendar calendar;
    private int year,month,day;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        eventNameET=findViewById(R.id.eventName);
        locationET=findViewById(R.id.startLocationName);
        destinationET=findViewById(R.id.destination);
        departureET=findViewById(R.id.departure);
        budgetET=findViewById(R.id.budget);
        calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        departureET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
                        calendar.set(year,month,dayOfMonth);
                        String date=sdf.format(calendar.getTime());
                        departureET.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        rootRef= FirebaseDatabase.getInstance().getReference();
        eventRef=rootRef.child("Events");


    }

    public void eventcreate(View view) {
        String eventName = eventNameET.getText().toString();
        String start = locationET.getText().toString();
        String destination = destinationET.getText().toString();
        String departure = departureET.getText().toString();
        double budget = Double.parseDouble(budgetET.getText().toString());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String creatDate = df.format(c.getTime());
        if (creatDate.compareTo(departure) > 0) {
            Toast.makeText(this, "Please select valid date", Toast.LENGTH_SHORT).show();
        }

        else if (eventName.equals(null) || start.equals(null) || destination.equals(null) || departure.equals(null) || budget == 0)

        {
            Toast.makeText(this, "Please Fill the fields", Toast.LENGTH_SHORT).show();
        }
        else {
            String id = eventRef.push().getKey();
            Event ev = new Event(id, eventName, start, destination, departure, creatDate, budget);
            eventRef.child(currentUser.getUid()).child(id).setValue(ev);
            startActivity(new Intent(AddEventActivity.this, NavigationDrawer.class));

        }
    }

    public void back(View view) {
        startActivity(new Intent(AddEventActivity.this,NavigationDrawer.class));
    }
}

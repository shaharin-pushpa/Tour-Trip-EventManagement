package com.example.kowshick.travelmate;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private ExpandableListView exList;
    TextView nameTv,budgetTv,cex,tex,bug,exTv;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Event fc;
    private List<Expense>expenses=new ArrayList<>();
    private DatabaseReference exRef;
    private DatabaseReference rootRef;
    private DatabaseReference eventref;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private int position=0;
    private double eventbudget;
    private double restbudget;
    public double eventexpense = 0;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        nameTv=findViewById(R.id.nameTV);
        budgetTv=findViewById(R.id.bugetTV);
        exTv=findViewById(R.id.exTV);
        /*Toolbar toolbar =(Toolbar) findViewById(R.id.toolbarEventDetails);
        toolbar.setTitle("Event Detail");*/
        android.support.v7.widget.Toolbar toolbar=findViewById(R.id.toolbarEventDetails);
        toolbar.setTitle("Event Details");
        cex = findViewById(R.id.cex);
        tex = findViewById(R.id.tex);
        progressBar = findViewById(R.id.budgetProgress);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));

        Intent evntIntent=getIntent();
        fc= (Event) evntIntent.getSerializableExtra("msg");

        nameTv.setText(fc.getEventName());
        eventbudget=fc.getBudget();
        //budgetTv.setText("Budget "+String.valueOf(fc.getBudget())+"/"+String.valueOf(expense));
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        exRef= FirebaseDatabase.getInstance().getReference("Events");
        exRef.keepSynced(true);
        progressStatus = (int) ((float) Math.round(eventexpense * 100) / eventbudget);
        progressBar.setProgress(progressStatus);
        budgetTv.setText("Budget: "+String.valueOf(eventbudget));

        exRef.child("Expense").child(currentUser.getUid()).child(fc.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expenses.clear();
                eventexpense = 0;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Expense ex = d.getValue(Expense.class);
                        expenses.add(ex);
                        eventexpense += ex.getAmount();

                }
                if(expenses.size()>0) {
                    restbudget = eventbudget - eventexpense;
                    //budgetTv.setText("Budget Status ( " + eventexpense + "0 / " + eventbudget + "0 )");
                    exTv.setText("Expense: "+eventexpense);
                    //budgetTv.setText("Budget: "+eventbudget);
                    tex.setText("100%");
                    if (eventexpense > 0) {
                        progressStatus = (int) ((float) Math.round(eventexpense * 100) / eventbudget);
                        cex.setText(String.valueOf(progressStatus) + "%");
                        if (progressStatus > 80) {

                            progressBar.setProgress(progressStatus);
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));

                        } else if (progressStatus > 50) {

                            progressBar.setProgress(progressStatus);
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                        } else {
                            progressBar.setProgress(progressStatus);
                            progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                        }

                    }
                }
                else {
                    exTv.setText("Expense: "+String.valueOf(eventexpense));
                    tex.setText("100%");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*exRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    Event ev=dataSnapshot.getValue(Event.class);
                    if (ev.getId().equals(fc.getId())){
                        budgetTv.setText("Budget: "+ev.getBudget());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        exList=(ExpandableListView) findViewById(R.id.expandList);
        ExpandListAdapter adapter=new ExpandListAdapter(DetailsActivity.this,fc,expenses,budgetTv);
        exList.setAdapter(adapter);

    }

    public void listBack(View view) {
        startActivity(new Intent(DetailsActivity.this,NavigationDrawer.class));
    }
}

package com.example.kowshick.travelmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewMomentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView txt;
    private String userid,eventid;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference momentRef;
    DatabaseReference eventref;
    private ImageAdapter adapter;
    private List<Moments>moments=new ArrayList<>();
    private Event ev=new Event();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_moments);
        recyclerView=findViewById(R.id.recyler);
        txt=findViewById(R.id.noImg);
        firebaseDatabase = FirebaseDatabase.getInstance();
        eventref = firebaseDatabase.getReference("Events");
        Intent intnt = getIntent();
        ev= (Event) intnt.getSerializableExtra("eventid");
        userid = intnt.getStringExtra("userid");
        eventid=ev.getId();
        momentRef = eventref.child("Moments");
        momentRef.child(userid).child(eventid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                moments.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    Moments mo=d.getValue(Moments.class);
                    moments.add(mo);
                }
                if(moments.size()>0){
                    adapter=new ImageAdapter(ViewMomentsActivity.this,moments);
                    /*LinearLayoutManager llm = new LinearLayoutManager(ViewMomentsActivity.this);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);*/
                    GridLayoutManager gm=new GridLayoutManager(ViewMomentsActivity.this,2);
                    recyclerView.setLayoutManager(gm);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    txt.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.back_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.back_detais:
                Intent evntIntent=new Intent(ViewMomentsActivity.this,DetailsActivity.class);
                evntIntent.putExtra("msg",ev);
                startActivity(evntIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}

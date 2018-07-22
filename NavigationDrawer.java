package com.example.kowshick.travelmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listView;
    private TextView tv;
    private EventAdapter adapter;
    private List<Event>events=new ArrayList<>();
    private DatabaseReference eventRef;
    private DatabaseReference rootRef;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    public static int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView=findViewById(R.id.eventList);
        tv=findViewById(R.id.tvsh);
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        eventRef=FirebaseDatabase.getInstance().getReference("Events");
        eventRef.keepSynced(true);
        eventRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    Event ev=d.getValue(Event.class);
                    events.add(ev);
                }
                if(events.size()>0){
                    tv.setVisibility(View.VISIBLE);
                    adapter=new EventAdapter(NavigationDrawer.this,events);
                    listView.setAdapter(adapter);
                }
                else{
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("Create Events");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NavigationDrawer.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event ev=events.get(position);
                   // pos=position;
                    Intent evntIntent=new Intent(NavigationDrawer.this,DetailsActivity.class);
                    evntIntent.putExtra("msg",ev);
                    startActivity(evntIntent);
                }
            });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NavigationDrawer.this,AddEventActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.logout:
                auth.signOut();
                Intent loginscreen = new Intent(NavigationDrawer.this, MainActivity.class);
                (NavigationDrawer.this).finish();
                loginscreen.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(loginscreen);
                //startActivity(new Intent(NavigationDrawer.this,MainActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
          if (id == R.id.weathert) {
            startActivity(new Intent(NavigationDrawer.this,WeatherActivity.class));

        } else if (id == R.id.nearBy) {
              startActivity(new Intent(NavigationDrawer.this,MapActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

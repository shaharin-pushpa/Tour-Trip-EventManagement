package com.example.kowshick.travelmate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

//import static android.content.Context.LOCATION_SERVICE;

public class MapActivity extends AppCompatActivity {
    private FragmentManager fm;
    private FragmentTransaction ft;
    //public static double latitude=23.750854;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fm=getSupportFragmentManager();
        ft=fm.beginTransaction();
        NearbyFragments nearby=new NearbyFragments();
        ft.add(R.id.mapfragmentContainer,nearby);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void mapchangefragments(View view) {
        Fragment fragment=null;
        switch (view.getId()){
            case R.id.nearBy:
                fragment=new NearbyFragments();
                break;
            case R.id.direction:
                fragment=new DirectionFragments();
                break;
        }
        fm=getSupportFragmentManager();
        ft=fm.beginTransaction();
        ft.replace(R.id.mapfragmentContainer,fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

/*



    public boolean checkService() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            return !TextUtils.isEmpty(locationProviders);
        }

    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.back:
                startActivity(new Intent(MapActivity.this,NavigationDrawer.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}



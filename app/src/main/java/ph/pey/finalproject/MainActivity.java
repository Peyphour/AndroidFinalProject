package ph.pey.finalproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ph.pey.finalproject.fragment.CreateMatchFragment;
import ph.pey.finalproject.fragment.MatchFragment;
import ph.pey.finalproject.fragment.MatchContent;
import ph.pey.finalproject.sql.AppDatabase;
import ph.pey.finalproject.sql.MatchEntity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CreateMatchFragment.Listener, MatchFragment.OnListFragmentInteractionListener {

    private final int PERMISSION_REQUEST = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private AppDatabase db;

    private LatLng lastLocation;
    private CreateMatchFragment currentMatchFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                currentMatchFragment = CreateMatchFragment.newInstance();
                fragmentTransaction.replace(R.id.main_content_layout, currentMatchFragment);
                fragmentTransaction.commit();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    onLocationChanged(latLng);
                }
            }
        };

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db").build();

        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET, Manifest.permission.CAMERA}, PERMISSION_REQUEST);
            return;
        }
        startLocationTracking();

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadMatches();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content_layout, MatchFragment.newInstance(1)).commit();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    private void startLocationTracking() {
        mFusedLocationClient.requestLocationUpdates(new LocationRequest(), mLocationCallback, null);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            onLocationChanged(latLng);
                        }
                    }
                });
    }

    private void onLocationChanged(final LatLng latlng) {
        this.lastLocation = latlng;

        if(this.currentMatchFragment != null)
            this.currentMatchFragment.locationUpdate();
    }

    public LatLng getLastLocation() {
        return lastLocation;
    }

    public String reverseGeoCodeLastLocation() {
        if(this.lastLocation == null)
            return "no location";
        return getAddressFromLocation(this.lastLocation);
    }

    public String getAddressFromLocation(LatLng location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if(addressList == null || addressList.size() == 0) {
                return "no location";
            }
            ArrayList<String> addressFragments = new ArrayList<>();

            for(int i = 0; i <= addressList.get(0).getMaxAddressLineIndex(); i++) {
                addressFragments.add(addressList.get(0).getAddressLine(i));
            }

            return TextUtils.join("\n", addressFragments);
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
        }
        return "no location";
    }

    private void loadMatches() {
        MatchContent.clear();
        for(MatchEntity matchEntity : this.db.matchEntityDao().getAll()) {
            MatchContent.addItem(matchEntity);
        }
    }

    @Override
    public void onCreateButtonPressed(Integer duration, String score, String winner, String loser) {
        if(this.lastLocation == null) {
            Toast.makeText(this, "Please wait for location", Toast.LENGTH_SHORT).show();
            return;
        }
        final MatchEntity matchEntity = new MatchEntity(0, this.lastLocation.latitude, this.lastLocation.longitude, duration, score, winner, loser);

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.matchEntityDao().insertAll(matchEntity);
                loadMatches();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content_layout, MatchFragment.newInstance(1)).commit();
            }
        }).start();
    }

    @Override
    public void onListFragmentInteraction(MatchEntity item) {

    }
}

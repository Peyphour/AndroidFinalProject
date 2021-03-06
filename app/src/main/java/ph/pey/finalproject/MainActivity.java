/**
 * By Bertrand NANCY and Kevin NUNES
 * Copyright 2018
 */

package ph.pey.finalproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ph.pey.finalproject.fragment.match.CreateMatchFragment;
import ph.pey.finalproject.fragment.match.MatchFragment;
import ph.pey.finalproject.fragment.match.MatchContentHolder;
import ph.pey.finalproject.fragment.pictures.MatchPicturesFragment;
import ph.pey.finalproject.fragment.pictures.PictureHolder;
import ph.pey.finalproject.sql.AppDatabase;
import ph.pey.finalproject.sql.MatchEntity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements CreateMatchFragment.Listener, MatchFragment.OnListFragmentInteractionListener, BackendManager.BackendResponseListener {

    private final int PERMISSION_REQUEST = 2;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String MAIN_FRAGMENT_TAG = "MAIN";
    private static final String PICTURES_FRAGMENT_TAG = "PICS";
    private static final String CREATE_FRAGMENT_TAG = "CREATE";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private AppDatabase db;

    private LatLng lastLocation;
    private CreateMatchFragment currentMatchFragment;

    private BackendManager backendManager;


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
                fragmentTransaction.replace(R.id.main_content_layout, currentMatchFragment, CREATE_FRAGMENT_TAG);
                fragmentTransaction.commit();
            }
        });

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

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "db")
                .fallbackToDestructiveMigration()
                .build();

        backendManager = new BackendManager(this, this);

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
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content_layout, MatchFragment.newInstance(1), MAIN_FRAGMENT_TAG).commit();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT_TAG) != null || getSupportFragmentManager().findFragmentByTag(PICTURES_FRAGMENT_TAG) != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content_layout, MatchFragment.newInstance(1), MAIN_FRAGMENT_TAG).commit();
        } else {
            super.onBackPressed();
        }
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
        MatchContentHolder.clear();
        for(MatchEntity matchEntity : this.db.matchEntityDao().getAll()) {
            MatchContentHolder.addItem(matchEntity);
        }
        backendManager.getAllMatches();
    }

    @Override
    public void onCreateButtonPressed(Integer duration, String score, String winner, String loser, String[] picturesPath) {
        if(this.lastLocation == null) {
            Toast.makeText(this, "Please wait for location", Toast.LENGTH_SHORT).show();
            return;
        }
        final MatchEntity matchEntity = new MatchEntity(0, this.lastLocation.latitude, this.lastLocation.longitude, duration, score, winner, loser, picturesPath);

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.matchEntityDao().insertAll(matchEntity);
                backendManager.saveMatch(matchEntity);
            }
        }).start();
    }

    @Override
    public File createPicture() {
        return dispatchTakePictureIntent();
    }

    @Override
    public void onListFragmentInteraction(MatchEntity item) {
        PictureHolder.clear();
        for(String path: item.getPicturesPath())
            PictureHolder.addItem(new PictureHolder.Picture("0", path));

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_layout, MatchPicturesFragment.newInstance(1), PICTURES_FRAGMENT_TAG).commit();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private File dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("ERROR", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                return photoFile;
            }
        }
        return null;
    }

    @Override
    public void allMatches(MatchEntity[] matchEntities) {
        MatchContentHolder.clear();
        for(MatchEntity matchEntity : matchEntities)
            MatchContentHolder.addItem(matchEntity);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content_layout, MatchFragment.newInstance(1), MAIN_FRAGMENT_TAG).commit();
    }

    @Override
    public void matchSaved() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadMatches();
            }
        }).start();
    }
}

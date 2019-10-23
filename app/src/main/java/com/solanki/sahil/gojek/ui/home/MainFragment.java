package com.solanki.sahil.gojek.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.BuildConfig;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.solanki.sahil.gojek.R;
import com.solanki.sahil.gojek.data.network.Api;
import com.solanki.sahil.gojek.data.network.Network_Interceptor;
import com.solanki.sahil.gojek.data.network.RetrofitInstance;
import com.solanki.sahil.gojek.data.provider.CustomSharedPreference;
import com.solanki.sahil.gojek.data.repository.UserRepository;
import com.solanki.sahil.gojek.databinding.FragmentMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainFragment extends Fragment implements MainListener {
    NavController navController = null;
    private MainViewModel mViewModel;
    String locality;
    private static final String TAG = "GOJEK PROJECT ------";
    private static final int REQUEST_PERMISSIONS_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;


    public MainFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getLocation(savedInstanceState);

        Network_Interceptor network_interceptor = new Network_Interceptor(getContext());
        Api api = RetrofitInstance.getApi(network_interceptor);
        UserRepository userRepository = new UserRepository(api);
        MainViewModelFactory factory = new MainViewModelFactory(userRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        FragmentMainBinding bindingUtil = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        bindingUtil.setModel(mViewModel);
        bindingUtil.getModel().mainListener = this;
        bindingUtil.setLifecycleOwner(this);
        return bindingUtil.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        startLocationButtonClick();


    }

    @Override
    public void onResume() {
        super.onResume();

        if (mRequestingLocationUpdates && checkPermissions()) {
            Log.e(TAG, "onResume: success");
            startLocationUpdates();
        }

        if (mCurrentLocation != null) {
            updateLocation();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
    }


    @Override
    public void onSuccess(LiveData<String> response) {
        response.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                JSONObject object = null;
                try {
                    object = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (object.optString("request") != null && !object.optString("request").isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("response", object.toString());
                    navController.navigate(R.id.action_mainFragment_to_successFragment, bundle);
                    Log.d("success**$*$", "onChanged: ");
                } else {
                    navController.navigate(R.id.action_mainFragment_to_failureFragment);
                    Log.d("failure**$*$", "onChanged: ");
                }

            }
        });

    }


    private void getLocation(Bundle savedInstanceState) {
        init();

        updateValuesFromBundle(savedInstanceState);

    }

    private void init() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                stopLocationUpdates();
                Log.e(TAG, "onLocationResult: create location callback");
                updateLocation();
            }
        };


        mRequestingLocationUpdates = true;

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            updateLocation();
        }
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }



    private void updateLocation() {

        if (mCurrentLocation != null) {

            double lat = (mCurrentLocation.getLatitude());
            double lng = (mCurrentLocation.getLongitude());

            Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(lat, lng, 1);
                if (addresses != null && addresses.size() > 0) {

                    locality = addresses.get(0).getAddressLine(0);
                    Log.e("UL%%^^", ": " + locality);
                    CustomSharedPreference.setLocation(getContext(), locality);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        mViewModel.load();

    }


    private void startLocationUpdates() {

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.e(TAG, "All location settings are satisfied.");
                        Toast.makeText(getContext(), "All location settings are satisfied.", Toast.LENGTH_LONG).show();

                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.e(TAG, "onSuccess: 1");
                        } else {
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                            Log.e(TAG, "onSuccess: 2");

                        }


                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.e(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                Toast.makeText(getContext(), "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ", Toast.LENGTH_LONG).show();
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    startIntentSenderForResult(rae.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null);
                                    Log.e(TAG, "onFailure try block " + "location settings ");
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.e(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }


    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.e(TAG, "stopLocationUpdates: updates.");
            return;
        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;

                    }
                });
    }


    public void startLocationButtonClick() {

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        startLocationUpdates();
                        Log.e(TAG, "onPermissionGranted:  Dexter is here  " + response.getPermissionName());

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            Log.e(TAG, "onPermissionDenied: ");
                            openSettings();
                        } else {

                            showSnackbar_Result();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Log.e(TAG, "onPermissionRationaleShouldBeShown: ");
                        token.continuePermissionRequest();

                    }

                }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Log.e("Dexter", "There was an error: " + error.toString());
            }
        }).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        mRequestingLocationUpdates = true;
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        showSnackbar_Result();
                        break;
                }
                break;


            case REQUEST_PERMISSIONS_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "Location done");
                        showSnackbar_Result();

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "Location undone");
                        // mRequestingLocationUpdates = false;
                        showSnackbar_Result();
                        break;
                }

                break;
        }
    }


    private void openSettings() {

        Log.e(TAG, "openSettings: ");
        showSnackbar(R.string.permission_denied_explanation,
                R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData((Uri.parse("package:" + getActivity().getPackageName())));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, REQUEST_PERMISSIONS_LOCATION);
                    }
                });
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void showSnackbar_Result() {

        showSnackbar(R.string.restart_detail,
                R.string.alert, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                        System.exit(0);
                    }
                });
    }

    private boolean checkPermissions() {
        Log.e(TAG, "checkPermissions: ");
        int permissionState = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }



}

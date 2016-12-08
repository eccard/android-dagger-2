package com.example.programacaoapp.veicle_revend;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.programacaoapp.veicle_revend.component.DaggerVehicleComponent;
import com.example.programacaoapp.veicle_revend.component.VehicleComponent;
import com.example.programacaoapp.veicle_revend.model.Vehicle;
import com.example.programacaoapp.veicle_revend.module.VehicleModule;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    Vehicle vehicle;
    LocationGps locationGps;
    TextView speedValue;
    LocationGps mService;
    boolean mBound = false;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        speedValue = (TextView) findViewById(R.id.current_speed_value);
        startService(new Intent(this, LocationGps.class));
        locationGps = new LocationGps();

        VehicleComponent component = DaggerVehicleComponent.builder().vehicleModule(new VehicleModule()).build();
        vehicle = component.provideVehicle();

        speedValue.setText(String.valueOf(vehicle.getSpeed()));
        latitude = locationGps.getLatitude();
        longitude = locationGps.getLongitude();
    }

    public void showMesage() {
        Toast.makeText(this, "Your location is -\nLat: " + latitude + " -\nLong: " + longitude, Toast.LENGTH_LONG);
        Log.v("location ", "Your location is -\nLat: " + latitude + " -\nLong: " + longitude); 
    }

    public void callBrake(View v){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Velocity")
                        .setContentText("Max Velocity is " + String.valueOf(vehicle.getSpeed()) + "km/h !!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ResultActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ResultActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
        vehicle.stop();
        speedValue.setText(String.valueOf(vehicle.getSpeed()));

        showMesage();
    }

    public void callIncrease(View v){
        Intent intent = new Intent(this, LocationGps.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (mService != null) {
            latitude = mService.getLatitude();
            longitude = mService.getLongitude();
        }
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.cancelAll();
        vehicle.increaseSpeed(10);
        speedValue.setText(String.valueOf(vehicle.getSpeed()));
        showMesage();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationGps.LocalBinder binder = (LocationGps.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}

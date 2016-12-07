package com.example.programacaoapp.veicle_revend;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.programacaoapp.veicle_revend.component.DaggerVehicleComponent;
import com.example.programacaoapp.veicle_revend.component.VehicleComponent;
import com.example.programacaoapp.veicle_revend.model.Vehicle;
import com.example.programacaoapp.veicle_revend.module.VehicleModule;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    Vehicle vehicle;

    TextView speedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        speedValue = (TextView) findViewById(R.id.current_speed_value);

        VehicleComponent component = DaggerVehicleComponent.builder().vehicleModule(new VehicleModule()).build();
        vehicle = component.provideVehicle();

        speedValue.setText(String.valueOf(vehicle.getSpeed()));
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
    }

    public void callIncrease(View v){
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.cancelAll();
        vehicle.increaseSpeed(10);
        speedValue.setText(String.valueOf(vehicle.getSpeed()));
    }
}

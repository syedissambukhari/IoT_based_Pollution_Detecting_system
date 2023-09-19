package com.example.firebasetest.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.firebasetest.AlarmReceiver;
import com.example.firebasetest.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import az.plainpie.PieView;
import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayFragment extends Fragment {
    private ImageView imageView;
//    private ImageView btnfan;

//    private ImageView btnpump;
FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Users");

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    String uid = currentUser.getUid();
    DatabaseReference userRef = usersRef.child(uid);

    //    private ImageView btnlamp;
//    private ImageView btnmis;
//    private ImageView btnfan2;
    private TextView rain,alert,temper,location;
    CustomGauge temp;
    PieView gass,flamesensor;
    String Alert;
//    private ImageView btnfan11;
//    private ImageView btnfan22;
//    private ImageView btnpump11;
//    private ImageView btnmist1;
//    private ImageView btnlamp11;


//    private TextView anhsang;
//    private PieView pieView_hum;
//    private PieView pieView_soil;
//    private CustomGauge customGauge;
//    private TextView textView;
    public DisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display, container, false);
//        customGauge = view.findViewById(R.id.gauge2);
//        textView = view.findViewById(R.id.text1);
//        pieView_hum = view.findViewById(R.id.pieView_hum);
//        pieView_soil = view.findViewById(R.id.pieView_soil);
//        imageView = view.findViewById(R.id.image);
//        anhsang = view.findViewById(R.id.anhsang);

        rain=view.findViewById(R.id.light);
        flamesensor=view.findViewById(R.id.flamesansor);
        gass=view.findViewById(R.id.gassensor);
        temp=view.findViewById(R.id.temperature);
        alert=view.findViewById(R.id.alert);
        temper=view.findViewById(R.id.temper);
        location=view.findViewById(R.id.location);



        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String location1 = dataSnapshot.child("location").getValue(String.class);
                   location.setText(location1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference alertRef = databaseRef.child("Alert");
        alertRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                String alert1 = dataSnapshot.getValue(String.class);
                assert alert1 != null;
                alert.setText(alert1);
                if(alert1.equals("Gas detected")){
                    gass.setPercentage(100);

                    Alert="Gas Detected";
                    Alarmtrigger();
                    gass.setPercentageTextSize(20);
                    flamesensor.setPercentage(0);
                    flamesensor.setPercentageTextSize(20);
                }
                else if(alert1.equals("Flame detected")){
                    flamesensor.setPercentage(100);
                    flamesensor.setPercentageTextSize(20);
                    gass.setPercentage(0);
                    Alert="Flame Detected!";
                    Alarmtrigger();

                    gass.setPercentageTextSize(20);
                }
                else{
                    gass.setPercentage(0);
                    gass.setPercentageTextSize(20);
                    flamesensor.setPercentage(0);
                    flamesensor.setPercentageTextSize(20);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference flame = databaseRef.child("FlameSensor");
        flame.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                Integer flameSensorValue = dataSnapshot.getValue(Integer.class);
                assert flameSensorValue != null;
                if(flameSensorValue.equals(1)){

                    Alert="Flame Sensor Alert Detected!";
                    Alarmtrigger();
                    flamesensor.setPercentage(100);
                    flamesensor.setPercentageTextSize(20);
                }
                else{
                    flamesensor.setPercentage(0);
                    flamesensor.setPercentageTextSize(20);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference temp1 = databaseRef.child("Temperature");
        temp1.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                Integer temperature = dataSnapshot.getValue(Integer.class);
                assert temperature != null;
                temp.setEndValue(270);

                Alert="Temperature Alert Detected!";
                Alarmtrigger();
                temp.setValue(temperature);
                temper.setText(temperature.toString()+"C");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference rainSensorRef3 = databaseRef.child("RainSensor");
        rainSensorRef3.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                Integer rains = dataSnapshot.getValue(Integer.class);
                assert rains != null;

                Alert="Rain Alert Detected!";
                Alarmtrigger();
                rain.setText(rains.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference gasSensorRef4 = databaseRef.child("GasSensor");
        gasSensorRef4.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                String gas = dataSnapshot.getValue(String.class);
                assert gas != null;
             //   gass.setText(gas.toString());
              if(gas.equals("1")){
                  gass.setPercentage(100);

                  Alert="Gas Detected";
                  Alarmtrigger();
                  gass.setPercentageTextSize(20);
              }
              else{
                  gass.setPercentage(0);
                  gass.setPercentageTextSize(20);
              }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


























//        btnfan = view.findViewById(R.id.fan_status);
//        btnlamp = view.findViewById(R.id.lamp_status);
//        btnpump = view.findViewById(R.id.pump_status);
//        btnmis = view.findViewById(R.id.mis_status);
//        btnfan2 = view.findViewById(R.id.fan2_status);
//
//        btnfan11 = view.findViewById(R.id.btnfan11);
//        btnfan22 = view.findViewById(R.id.btnfan22);
//        btnpump11 = view.findViewById(R.id.btnpump1);
//        btnmist1 = view.findViewById(R.id.btnmist1);
//        btnlamp11 = view.findViewById(R.id.btnlamp1);


        Firebase.setAndroidContext(Objects.requireNonNull(getContext()));

        final Firebase mRef1 = new Firebase("https://iot-phong-cn.firebaseio.com/Smart_Farm1/sensor/dht/temp");
        Firebase mRef2 = new Firebase("https://iot-phong-cn.firebaseio.com/Smart_Farm1/sensor/light");
        Firebase mRef3 = new Firebase("https://iot-phong-cn.firebaseio.com/Smart_Farm1/sensor/soil_hum");
        Firebase mRef4 = new Firebase("https://iot-phong-cn.firebaseio.com/Smart_Farm1/sensor/dht/hum");


        Firebase btnfan1 = new Firebase("https://iot-phong-cn.firebaseio.com/Smart_Farm1/Status/Fan");
        Firebase btnlamp1 = new Firebase("https://iot-phong-cn.firebaseio.com/Smart_Farm1/Status/Lamp");
        Firebase btnpump1 = new Firebase("https://iot-phong-cn.firebaseio.com/Smart_Farm1/Status/Pump");
        Firebase btnmis1 = new Firebase("https://iot-phong-cn.firebaseio.com/Smart_Farm1/Status/Mis");
        Firebase btnfan21 = new Firebase("https://iot-phong-cn.firebaseio.com/Smart_Farm1/Status/Fan2");




//        pieView_hum.setPercentageBackgroundColor(getResources().getColor(R.color.bluehigh));
//        pieView_hum.setMainBackgroundColor(getResources().getColor(R.color.white));


//        pieView_soil.setPercentageBackgroundColor(getResources().getColor(R.color.browhigh));
//        pieView_soil.setMainBackgroundColor(getResources().getColor(R.color.white));


//        btnfan21.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                if (value.equals("1")) {
//                    btnfan22.setImageResource(R.drawable.fan);
//                    btnfan2.setImageResource(R.drawable.btnbat);
//                } else if (value.equals("0")) {
//                    btnfan22.setImageResource(R.drawable.fanoff);
//                    btnfan2.setImageResource(R.drawable.btntat);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });


//        btnfan1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                if (value.equals("1")) {
//                    btnfan11.setImageResource(R.drawable.fan);
//                    btnfan.setImageResource(R.drawable.btnbat);
//                } else if (value.equals("0")) {
//                    btnfan11.setImageResource(R.drawable.fanoff);
//                    btnfan.setImageResource(R.drawable.btntat);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//        btnlamp1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                if (value.equals("1")) {
//                    btnlamp11.setImageResource(R.drawable.lighon2);
//                    btnlamp.setImageResource(R.drawable.btnbat);
//                } else if (value.equals("0")) {
//                    btnlamp11.setImageResource(R.drawable.light_off1);
//                    btnlamp.setImageResource(R.drawable.btntat);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//        btnpump1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                if (value.equals("1")) {
//                    btnpump11.setImageResource(R.drawable.pump2);
//                    btnpump.setImageResource(R.drawable.btnbat);
//                } else if (value.equals("0")) {
//                    btnpump11.setImageResource(R.drawable.pumpoff);
//                    btnpump.setImageResource(R.drawable.btntat);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//        btnmis1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                if (value.equals("1")) {
//                    btnmist1.setImageResource(R.drawable.miston);
//                    btnmis.setImageResource(R.drawable.btnbat);
//                } else if (value.equals("0")) {
//                    btnmis.setImageResource(R.drawable.btntat);
//                    btnmist1.setImageResource(R.drawable.mistoff);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });


//        mRef4.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//
//                float i = Float.parseFloat(value);
//                pieView_hum.setPercentage(i);
//                pieView_hum.setPieInnerPadding(30);
//                pieView_hum.setPercentageTextSize(20);
//                PieAngleAnimation animation = new PieAngleAnimation(pieView_hum);
//                animation.setDuration(2000); //This is the duration of the animation in millis
//                pieView_hum.startAnimation(animation);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//        mRef2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                anhsang.setText(value);
//                if (anhsang.getText().toString().equals("Trời sáng")) {
//                    imageView.setImageResource(R.drawable.sun);
//                } else if (anhsang.getText().toString().equals("Trời tối")) {
//                    imageView.setImageResource(R.drawable.moon);
//                }
//
//
//            }
//
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//
//        mRef1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                textView.setText(value+"°C");
//                int i=Integer.parseInt(value.replaceAll("[\\D]", ""));
//                customGauge.setValue(i*10);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//        mRef3.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                float i = Float.parseFloat(value);
//                pieView_soil.setPieInnerPadding(30);
//                pieView_soil.setPercentageTextSize(20);
//                pieView_soil.setPercentage(i);
//                PieAngleAnimation animation = new PieAngleAnimation(pieView_soil);
//                animation.setDuration(2000); //This is the duration of the animation in millis
//                pieView_soil.startAnimation(animation);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });


        return view;
    }

    private void Alarmtrigger() {
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, PendingIntent.FLAG_MUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Channel Name";
            String channelDescription = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", channelName, importance);
            channel.setDescription(channelDescription);

            // Register the notification channel with the system
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "channel_id")
                .setSmallIcon(R.drawable.warning)

                .setContentTitle("Alert!")
                .setContentText(Alert)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

// Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(1, builder.build());
// Set the alarm to trigger after 5 seconds (adjust as needed)
        long triggerTime = SystemClock.elapsedRealtime() + 5000;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);

    }

}

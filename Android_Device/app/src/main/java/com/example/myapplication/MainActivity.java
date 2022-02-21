package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements LocationListener {

    boolean proceed;
    public Button stopButton;
    public RadioButton autoButton;
    public RadioButton manualButton;
    public TextView coords;
    int samplesToSend;
    TextView sampleView;
    public LocationManager loc;
    public final String DEVICE_ID= UUID.randomUUID().toString();
    Button startButton;
    Button exitButton;
    EditText ipText;
    boolean workdone;
    EditText portText;
    AlertDialog.Builder builder;
    AndroidSub sub;
    EditText samples;
    Vibrator v ;
    final int port=1883;
    MediaPlayer player;
    MediaPlayer player2;
     MqttAndroidClient client;
    final String brokerID= "tcp://localhost:1883";
    Button okButton;
    ArrayList<String []> manCords;
    private final MemoryPersistence persistence = new MemoryPersistence();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proceed=false;
        setContentView(R.layout.activity_main);
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setVisibility(View.INVISIBLE);
        autoButton = (RadioButton) findViewById(R.id.autoButton);
        exitButton=(Button)findViewById(R.id.exitButton);
        manualButton = (RadioButton) findViewById(R.id.manualButton);
        coords = (TextView) findViewById(R.id.coords);
        builder = new AlertDialog.Builder(MainActivity.this);
        ipText=(EditText) findViewById(R.id.ipSet);
        portText=(EditText)findViewById(R.id.portSet);
        startButton=(Button)findViewById(R.id.startButton);
        okButton=(Button)findViewById(R.id.okButton);
        sampleView=(TextView)findViewById(R.id.sampleview);
        v= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        samples=(EditText)findViewById(R.id.samplesText);
        samples.setInputType(InputType.TYPE_CLASS_NUMBER );
        workdone=false;
        samples.setVisibility(View.INVISIBLE);
        okButton.setVisibility(View.INVISIBLE);
        sampleView.setVisibility(View.INVISIBLE);

        CoordinatesFromCSV a=new CoordinatesFromCSV();
        File mydir = this.getFilesDir();
       String as=mydir.toString();

        manCords=new ArrayList<String[]>();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        Boolean isAnswered = true;
        boolean close=false;
























        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

        }

        getLocation();

        final Handler handler = new Handler();
        final int delay = 10000;
        handler.postDelayed(new Runnable() {
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                System.out.println("myHandler: here!"); // Do your work here
                if(activeNetworkInfo==null||!activeNetworkInfo.isConnected()){
                    builder.setMessage("Please enable internet access or close the app")
                            .setCancelable(true)
                            .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(intent);

                                }
                            })
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("");
                    alert.show();
                }

                handler.postDelayed(this, delay);
            }
        }, delay);

        final Handler autoHandler=new Handler();
        int autoDelay=1000;

        autoHandler.postDelayed(new Runnable() {
            public void run() {
                if(autoButton.isChecked()) {
                    if (client != null) {
                        if (client.isConnected()) {
                            @SuppressLint("MissingPermission") Location location = loc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            String msg = DEVICE_ID + "," + location.getLatitude() + "," + location.getLongitude();
                            MqttMessage message = new MqttMessage(msg.getBytes());
                            message.setQos(2);
                            try {
                                client.publish("android", message);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            } finally {
                                System.out.println("Message published");
                            }
                        }
                    }
                }






                autoHandler.postDelayed(this, autoDelay);
            }
        }, autoDelay);


        final Handler manualHandler=new Handler();
        final int[] counter = {0};

        manualHandler.postDelayed(new Runnable() {
            public void run() {
                if(samplesToSend==-1&&manualButton.isChecked()){

                }else{


                if(manualButton.isChecked()) {
                    if (client != null) {
                        if (client.isConnected() && counter[0] <samplesToSend) {

                            String [] currentPos=manCords.get(counter[0]);
                            String latitude=currentPos[0];
                            String longitude=currentPos[1];
                            String msg=DEVICE_ID+","+latitude+","+longitude;
                            counter[0]++;


                            MqttMessage message = new MqttMessage(msg.getBytes());
                            message.setQos(2);
                            try {
                                client.publish("android", message);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            } finally {
                                System.out.println("Message published");
                            }
                        }
                    }
                }

                }
                manualHandler.postDelayed(this, autoDelay);
            }
        }, autoDelay);









        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate();
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                if(client!=null){
                    if(client.isConnected()){
                        try {
                            client.disconnect();
                            System.out.println("Disconnected");
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    builder.setMessage("The app will close as we have no location access")
                            .setCancelable(true)
                            .setPositiveButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();

                                }
                            })
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("");
                    alert.show();
                }
            }
        });

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    builder.setMessage("The app will close as we have no write access")
                            .setCancelable(true)
                            .setPositiveButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();

                                }
                            })
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("");
                    alert.show();
                } else if(!workdone){
                    samples.setVisibility(View.VISIBLE);
                    okButton.setVisibility(View.VISIBLE);
                    sampleView.setVisibility(View.VISIBLE);

                    String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                    String fileName = "android.csv";
                    String filePath = baseDir + File.separator + fileName;
                    File f = new File(filePath);
                    CSVWriter writer = null;
                    try {
                        writer = new CSVWriter(new FileWriter(f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    XmlPullParser xpp = null;
                    Random rand = new Random();
                    int random = rand.nextInt(2);
                    if (random == 1) {
                        xpp = getResources().getXml(R.xml.android_1);
                    }
                    if (random == 0) {
                        xpp = getResources().getXml(R.xml.android_2);
                    }


                    try {
                        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                            if (xpp.getEventType() == XmlPullParser.START_TAG) {
                                if (xpp.getName().equals("vehicle")) {
                                    String coord = xpp.getAttributeValue(7) + "," + xpp.getAttributeValue(8);
                                    String[] data = new String[2];
                                    data[0] = xpp.getAttributeValue(7);
                                    data[1] = xpp.getAttributeValue(8);
                                    writer.writeNext(data);
                                }
                            }

                            xpp.next();
                            String xppp = xpp.getName();
                            System.out.println();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    File csv = new File(filePath);
                    System.out.println();
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    List myEntries = null;

                    CSVReader reader = null;
                    try {
                        reader = new CSVReader(new FileReader(csv));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        myEntries = reader.readAll();
                        System.out.println();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println();

                    for (int i = 0; i < myEntries.size(); i++) {
                        String[] current = (String[]) myEntries.get(i);
                        manCords.add(current);

                    }
                    System.out.println();
                    String size = String.valueOf(manCords.size());
                    samples.setText(size);
                    samplesToSend = -1;
                    workdone=true;
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Closing Activity")
                        .setMessage("Are you sure you want to close the app?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(samples.getText().toString())<=manCords.size()){
                   samplesToSend=Integer.parseInt(samples.getText().toString());
                   okButton.setVisibility(View.INVISIBLE);
                   samples.setVisibility(View.INVISIBLE);
                    sampleView.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Success",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Please input correct number of samples",
                            Toast.LENGTH_LONG).show();

                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.print("DEVICE ID IS");
                System.out.println(DEVICE_ID);

                startTransmitting();
            }
        });

    }

    public void playHighDangerSound() {
        if(player==null){
            player=MediaPlayer.create(this,R.raw.highwarning);
            player.setLooping(true);
        }

        if(!player.isPlaying()){
            player.start();
        }

    }

    public void playMediumDangerSound() {
        if(player2==null){
            player2=MediaPlayer.create(this,R.raw.medwarning);
            player2.setLooping(true);
        }

        if(!player2.isPlaying()){
            player2.start();
        }
    }

    private void stopSound(){
        if(player!=null && player.isPlaying()){
            player.stop();
        }
        if(player2!=null && player2.isPlaying()){
            player2.stop();
        }
    }


    public void getLocation() {
        try {
            loc = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
                return;
            }
            loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, MainActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    };

    public void startTransmitting(){
        String ip=ipText.getText().toString();
        String port=portText.getText().toString();
        String serverUri="tcp://"+ip+":"+port;
        client=new MqttAndroidClient(this.getApplicationContext(),serverUri,"androidId",persistence);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        try {
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connection Success!");
                    Toast.makeText(MainActivity.this, "Connection Successful",
                            Toast.LENGTH_LONG).show();
                    startButton.setVisibility(View.INVISIBLE);
                    stopButton.setVisibility(View.VISIBLE);
                    try {
                        System.out.println("Subscribing to /test");
                        client.subscribe("android", 0);
                    } catch (MqttException ex) {

                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Connection Failure!");
                    Toast.makeText(MainActivity.this, "Connection Unsuccessful,try again",
                            Toast.LENGTH_LONG).show();
                    System.out.println("throwable: " + exception.toString());
                }
            });
        } catch (MqttException ex) {
            System.out.println(ex.toString());
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                vibrate();
                String msg= new String(message.toString());
                System.out.println();
                if(msg.equals("High Danger")){
                    playHighDangerSound();
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("WARNING");
                    alertDialog.setMessage("POSSIBILITY OF HIGH DANGER");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    stopSound();
                                    player=null;
                                }
                            });
                    alertDialog.show();

                }

                if(msg.equals("Medium Danger")){
                    playMediumDangerSound();
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("WARNING");
                    alertDialog.setMessage("POSSIBILITY OF MEDIUM DANGER");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    stopSound();
                                    player2=null;
                                }
                            });
                    alertDialog.show();


                }



            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            proceed=true;

        }else{

        }
    }




    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        String lat=String.valueOf(location.getLatitude());
        String lon=String.valueOf(location.getLongitude());
        coords.setText(lat+","+lon);

    }
    public void vibrate(){
        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
    };


}

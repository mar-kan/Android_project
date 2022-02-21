package com.example.myapplicationlotr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class Settings extends Fragment {

    EditText ipText;
    EditText portText;
    MqttAndroidClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button exit = (Button) view.findViewById(R.id.Exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirm Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().moveTaskToBack(true);
                                getActivity().finish();
                                System.exit(0);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }

        });

        ipText=(EditText)view.findViewById(R.id.editIP);
        portText=(EditText)view.findViewById(R.id.editPort);


        Button sensor = (Button) view.findViewById(R.id.NSensor);
        sensor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.sensor_spinner, null);
                mBuilder.setTitle("Sensor Type");
                Spinner mSpiner = (Spinner) mView.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.Sensor_type));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpiner.setAdapter(adapter);

                EditText mLower = mView.findViewById(R.id.Lower);
                EditText mUpper = mView.findViewById(R.id.Upper);

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        float lower = Float.parseFloat(mLower.getText().toString());
                        float upper = Float.parseFloat(mUpper.getText().toString());
                        if (!mSpiner.getSelectedItem().toString().equalsIgnoreCase("Select Sensor")) {
                            if ((mSpiner.getSelectedItem().toString().equalsIgnoreCase("Smoke Sensor") && lower >= 0 && upper <= 0.25 && lower <= upper) ||
                                    (mSpiner.getSelectedItem().toString().equalsIgnoreCase("Gas Sensor") && lower >= 0 && upper <= 11 && lower <= upper) ||
                                    (mSpiner.getSelectedItem().toString().equalsIgnoreCase("Temp Sensor") && lower >= -5 && upper <= 80 && lower <= upper) ||
                                    (mSpiner.getSelectedItem().toString().equalsIgnoreCase("Ultraviolet Sensor") && lower >= 0 && upper <= 11 && lower <= upper)) {
                                Toast.makeText(getActivity(), mSpiner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "Out of Bounds", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });

        Button submit = (Button) view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemoryPersistence persistence=new MemoryPersistence();

                String ip=ipText.getText().toString();
                String port=portText.getText().toString();
                String serverUri="tcp://"+ip+":"+port;
                client=new MqttAndroidClient(Settings.this.getContext(),serverUri,"IotID",persistence);
                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setCleanSession(true);
                String message="hello";
                MqttMessage msg=new MqttMessage(message.getBytes());
                try {
                    client.publish("a",msg);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
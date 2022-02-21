package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class AndroidSub implements MqttCallback {
    MqttAndroidClient subClient;
    Context context;
    String topic;
    String broker;
    String clientId="";
    int qos=1;

    MemoryPersistence persistence = new MemoryPersistence();
    public AndroidSub(Context context,String topic,String broker) throws MqttException {
        this.context=context;
        this.topic=topic;
        this.broker=broker;
        subClient = new MqttAndroidClient(context,"tcp://10.0.2.2:1883","Androidclient",persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        subClient.setCallback(this);
        subClient.connect(connOpts, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                try {
                    subClient.subscribe(topic,qos);
                    System.out.println("connected");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                System.out.println("failed to connect");
                exception.printStackTrace();
            }
        });





    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        System.out.println("Asasa");

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}

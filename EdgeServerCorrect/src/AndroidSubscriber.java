import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.sql.Timestamp;

import static java.lang.System.exit;


public class AndroidSubscriber implements MqttCallback {
    private String topic;
    int qos;
    private String broker;
    private String clientId = "";
    MemoryPersistence persistence = new MemoryPersistence();
    MqttClient subClient;

    private int deviceID = -1;
    private double[] coordinates = new double[2];


    public AndroidSubscriber(String topic, String broker) {
        this.topic = topic;
        this.broker = broker;

        try {
            subClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            subClient.connect(connOpts);
            subClient.setCallback(this);
            subClient.subscribe(this.topic, this.qos);

        } catch (MqttException e) {
            e.printStackTrace();
            exit(-2);
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost");
    }

    public void disconnect() {
        try {
            subClient.disconnect();
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage var2) throws Exception {
        String var3 = (new Timestamp(System.currentTimeMillis())).toString();
        System.out.println("Time: " + var3 + "  Topic: " + topic + "  Message: " + new String(var2.getPayload()) + "  QoS: " + var2.getQos());


        this.splitMessage(var2.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Delivery completed");
    }

    public void splitMessage(String msg) {
        String[] splits = msg.split(",\s");

        // device ID
        try {
            deviceID = Integer.parseInt(splits[0]);
        } catch (NumberFormatException e) {
            System.out.println("Error in reading device ID from Android device.");
            e.printStackTrace();
        }

        // coordinates
        for (int i = 0; i < coordinates.length; i++)
        {
            try {
                coordinates[i] = Double.parseDouble(splits[i + 1]);
            } catch (NumberFormatException e) {
                System.out.println("Error in reading coordinates from Android device.");
                e.printStackTrace();
            }
        }
    }

    /** getters **/
    public double[] getCoordinates()
    {
        return coordinates;
    }

    public int getDeviceID()
    {
        return deviceID;
    }
}

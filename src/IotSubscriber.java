import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.sql.Timestamp;

import static java.lang.System.exit;


public class IotSubscriber implements MqttCallback {
    private String topic;
    int qos;
    private String broker;
    private String clientId = "";
    MemoryPersistence persistence = new MemoryPersistence();
    MqttClient subClient;

    private double[] measurements = new double[4];
    private double[] coordinates = new double[2];
    private int battery = -1;

    private String dangerMsg = "";
    private Timestamp time = null;


    public IotSubscriber(String topic, String broker)
    {
        this.topic = topic;
        this.broker = broker;

        try {
            subClient=new MqttClient(broker,clientId,persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            subClient.connect(connOpts);
            subClient.setCallback(this);
            subClient.subscribe(this.topic, this.qos);

        } catch (MqttException e) {
            e.printStackTrace();
            exit(-4);
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost");
    }

    public void disconnect()
    {
        try{
            subClient.disconnect();
        }
        catch(MqttException me){
            me.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage var2) throws Exception
    {
        time = (new Timestamp(System.currentTimeMillis()));
        System.out.println("Time: " + time.toString() + "  Topic: " + topic + "  Message: " + new String(var2.getPayload()) + "  QoS: " + var2.getQos());

        this.splitMessage(var2.toString());
        this.calculateDeviceDanger();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Delivery completed");
    }

    /** splits message to its corresponding values **/
    public void splitMessage(String msg)
    {
        String[] splits = msg.split(",\s");

        // measurements
        for (int i=0; i<measurements.length; i++)
        {
            try
            {
                measurements[i] = Double.parseDouble(splits[i]);
            } catch (NumberFormatException e)
            {
                System.out.println("Error in reading measurements from IOT device.");
                e.printStackTrace();
            }
        }

        // coordinates
        for (int i=0; i<coordinates.length; i++)
        {
            try
            {
                coordinates[i] = Double.parseDouble(splits[i+measurements.length]);
            } catch (NumberFormatException e)
            {
                System.out.println("Error in reading coordinates from IOT device.");
                e.printStackTrace();
            }
        }

        // battery
        try
        {
            battery = Integer.parseInt(splits[splits.length-1]);
        } catch (NumberFormatException e)
        {
            System.out.println("Error in reading battery from IOT device.");
            e.printStackTrace();
        }
    }

    /** checks if there is a danger and creates a danger message **/
    public void calculateDeviceDanger() throws Exception
    {
        DangerCalculator dangerCalculator = new DangerCalculator(measurements);
        dangerCalculator.calculateDanger();
        dangerMsg = dangerCalculator.createDangerMessage();
    }

    /** getters **/
    public String getDangerMsg()
    {
        return dangerMsg;
    }

    public double[] getCoordinates()
    {
        return coordinates;
    }

    public Timestamp getTime()
    {
        return time;
    }

    public double getMeasurementsAt(int pos) {
        return measurements[pos];
    }


}

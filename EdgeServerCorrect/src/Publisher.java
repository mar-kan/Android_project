import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static java.lang.System.exit;

public class Publisher {
    private String topic;
    int qos;
    private String broker;
    private String clientId = "";
    MemoryPersistence persistence = new MemoryPersistence();
    MqttClient pubClient;

    public Publisher(String topic, int qos, String broker) {
        this.topic = topic;
        this.qos = qos;
        this.broker = broker;

        try {
            pubClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            pubClient.connect(connOpts);

        } catch (MqttException e) {
            e.printStackTrace();
            exit(-5);
        }

    }

    public void publish(String cont){
       if(!cont.equals("No Danger")) {
           MqttMessage message = new MqttMessage(cont.getBytes());
           message.setQos(qos);
           try {
               pubClient.publish(topic, message);
           } catch (MqttException e) {
               e.printStackTrace();
           }
           //finally {
           //    System.out.println("Message published");
           //}
       }
    }

    public void disconnect(){
        try{
            pubClient.disconnect();
        }
        catch(MqttException me){
            me.printStackTrace();
        }
    }

    /** creates message that is going to be published **/
    public String createMessage(IotSubscriber iotSub1, IotSubscriber iotSub2, AndroidSubscriber aSub)
    {
        double dist = -1;
        String danger = "";

        if (!iotSub1.getDangerMsg().equals("No Danger") && !iotSub2.getDangerMsg().equals("No Danger"))
        {
            double[] center = DistanceCalculator.calculateCenter(iotSub1.getCoordinates()[0], iotSub1.getCoordinates()[1],
                    iotSub2.getCoordinates()[0], iotSub2.getCoordinates()[1]);
            dist = DistanceCalculator.distance(center[0], center[1], aSub.getCoordinates()[0], aSub.getCoordinates()[1], "m");

            // selects highest danger warning received to send to the android device
            if (iotSub1.getDangerMsg().equals("High Danger") || iotSub2.getDangerMsg().equals("Medium Danger"))
                danger = "High Danger";
            else
                danger = "Medium Danger";
        }
        else if (!iotSub1.getDangerMsg().equals("No Danger"))
        {
            dist = DistanceCalculator.distance(iotSub1.getCoordinates()[0], iotSub1.getCoordinates()[1],
                    aSub.getCoordinates()[0], aSub.getCoordinates()[1], "m");
            danger = iotSub1.getDangerMsg();
        }
        else if (!iotSub2.getDangerMsg().equals("No Danger"))
        {
            dist = DistanceCalculator.distance(iotSub2.getCoordinates()[0], iotSub2.getCoordinates()[1],
                    aSub.getCoordinates()[0], aSub.getCoordinates()[1], "m");
            danger = iotSub2.getDangerMsg();
        }
        return danger+", "+dist;
    }
}

import static java.lang.System.exit;
import java.io.IOException;
import java.sql.Timestamp;


public class EdgeServer {
    public static void main(String[] args) throws Exception {
        int qos = 2;
        int port = 1883;
        Runtime r = Runtime.getRuntime();

        String brokerID= "tcp://127.0.0.1:"+port;

        try {
            Process mosquitto = r.exec("C:\\Program Files\\mosquitto\\mosquitto.exe -v -p " + port);
        } catch (IOException e) {
            e.printStackTrace();
            exit(-1);
        }

        /* start publisher and one subscriber for each device and topic */
        Publisher pub = new Publisher("android", qos, brokerID);
        IotSubscriber iotSub1 = new IotSubscriber("tasos", brokerID);
        //IotSubscriber iotSub2 = new IotSubscriber("iot2", brokerID);
        //AndroidSubscriber aSub = new AndroidSubscriber("android", brokerID);

        while (true)
        {
            /* publish to android device */
            //String msg = pub.createMessage(iotSub1, iotSub2, aSub);
            //pub.publish(msg);

            /* post to database */
//            Database db = new Database();
//
//            if (!iotSub1.getDangerMsg().equals("No Danger") && !iotSub2.getDangerMsg().equals("No Danger"))
//            {
//                db.insert(aSub.getDeviceID(), iotSub1.getTime(), iotSub1.getMeasurementsAt(0),iotSub1.getMeasurementsAt(1), iotSub1.getMeasurementsAt(2), iotSub1.getMeasurementsAt(3), iotSub1.getDangerMsg());
//                db.insert(aSub.getDeviceID(), iotSub2.getTime(), iotSub2.getMeasurementsAt(0),iotSub2.getMeasurementsAt(1), iotSub2.getMeasurementsAt(2), iotSub2.getMeasurementsAt(3), iotSub2.getDangerMsg());
//            }
//            else if (!iotSub1.getDangerMsg().equals("No Danger"))
//            {
//                db.insert(aSub.getDeviceID(), iotSub1.getTime(), iotSub1.getMeasurementsAt(0),iotSub1.getMeasurementsAt(1), iotSub1.getMeasurementsAt(2), iotSub1.getMeasurementsAt(3), iotSub1.getDangerMsg());
//            }
//            else if (!iotSub2.getDangerMsg().equals("No Danger")
//            {
//                db.insert(aSub.getDeviceID(), iotSub2.getTime(), iotSub2.getMeasurementsAt(0),iotSub2.getMeasurementsAt(1), iotSub2.getMeasurementsAt(2), iotSub2.getMeasurementsAt(3), iotSub2.getDangerMsg());
//            }
//
//            db.insert(aSub.getDeviceID(), iotSub1.getTime(), iotSub1.getMeasurementsAt(0),iotSub1.getMeasurementsAt(1), iotSub1.getMeasurementsAt(2), iotSub1.getMeasurementsAt(3), iotSub1.getDangerMsg());


            // info window for iot and android
        }
    }
}




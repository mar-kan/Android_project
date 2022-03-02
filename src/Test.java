import java.sql.SQLException;

public class Test {

    public  static void main (String[] args) throws SQLException {

        int qos=2;
        String broker="tcp://127.0.0.1:"+"1883";
//        AndroidSubscriber sub = new AndroidSubscriber("android", broker);

        Publisher pub = new Publisher("android", qos, broker );  //start edgepublisher
        pub.publish("12234, 324.124, 124.908");

//        Publisher pub1 = new Publisher("iot1", qos,broker );    //start edgepublisher
//        pub1.publish("1.22, 2.34, 3.45, 4.56, 21.87654, 12.7654, 88");
//
//        Publisher pub2 = new Publisher("iot2", qos,broker );    //start edgepublisher
//        pub1.publish("9.21, 8.58, 7.45, 6.56, 87.87654, 76.7654, 22");

//        Database db = new Database();
//        db.insert(12, new Timestamp(System.currentTimeMillis()), 14.32323, 87.765432, 9.15, "HIGH DANGER");
//        System.out.println();
    }
}

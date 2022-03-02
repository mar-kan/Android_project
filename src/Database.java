import static java.lang.System.exit;
import java.sql.*;


public class Database {
    Connection conn;
    Statement st;


    public Database() {
        try {
            String url = "127.0.0.1";
            Connection conn = DriverManager.getConnection(url, "", "");
            Statement st = conn.createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
            exit(-3);
        }
    }

    /** inserts new entry in database **/
    public void insert(int id, Timestamp time, double lat, double lon, double danger, String sensor_val) throws SQLException
    {
        String query = "";
        try {
            query = String.valueOf(st.executeUpdate("INSERT INTO warnings (device_id, time, latitude, longitude, danger_level, sensor_value) "
                    + "VALUES (?, ?, ?, ?, ?, ?)"));
        }
        catch (Exception e) {
            e.printStackTrace();
            exit(-3);
        }
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, id);
        preparedStmt.setTimestamp(2, time);
        preparedStmt.setDouble(3, lat);
        preparedStmt.setDouble(4, lon);
        preparedStmt.setDouble(5, danger);
        preparedStmt.setString(6, sensor_val);
        preparedStmt.executeUpdate();
    }

    /** disconnects from the database **/
    public void disconnect() throws SQLException
    {
        conn.close();
    }
}

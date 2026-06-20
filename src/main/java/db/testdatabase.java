package db;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class testdatabase {

    public static Connection getKoneksi() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/db_drivora";
            String user = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException t) {
            System.out.println("Error Membuat Koneksi: " + t.getMessage());
            return null;
        } catch (ClassNotFoundException ex) {
            System.getLogger(testdatabase.class.getName()).log(System.Logger.Level.ERROR, "Driver not found", ex);
            return null;
        }
    }
}

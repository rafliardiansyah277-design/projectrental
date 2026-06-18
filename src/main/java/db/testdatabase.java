package db;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class testdatabase {
private static Connection koneksi;

    public static Connection getKoneksi() {
        // cek apakah koneksi
        if (koneksi == null) {

            try {                String url = "jdbc:mysql://127.0.0.1:3306/db_drivora";
                String user = "root";
                String password = "";
                Class.forName("com.mysql.cj.jdbc.Driver");
                koneksi = DriverManager.getConnection(url, user, password);
            } catch (SQLException t) {
                System.out.println("Error Membuat Koneksi");
            } catch (ClassNotFoundException ex) {
                System.getLogger(testdatabase.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        return koneksi;
    }
}

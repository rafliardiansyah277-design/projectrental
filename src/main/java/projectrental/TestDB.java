package projectrental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import db.testdatabase;

public class TestDB {
    public static void main(String[] args) {
        try {
            Connection conn = testdatabase.getKoneksi();
            System.out.println("Connected!");
            String sql = "UPDATE rentals SET status = 'rejected', rejection_reason = ?, admin_id = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "Test rejection reason");
            stmt.setInt(2, 4); // admin_id
            stmt.setInt(3, 1); // rental id
            int updated = stmt.executeUpdate();
            System.out.println("Rows updated: " + updated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

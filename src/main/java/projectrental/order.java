/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package projectrental;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Cursor;

/**
 *
 * @author user dell 7420
 */
public class order extends javax.swing.JPanel {

    private int currentAdminId = 1; // Default PIC Admin
    private int selectedRentalId = -1;
    private int selectedUserId = -1;
    private int selectedCarId = -1;
    private double originalPrice = 0;
    private String selectedKtpPath = "";

    public void setAdminId(int adminId) {
        this.currentAdminId = adminId;
    }

    
    /**
     * Creates new form order
     */
    
    public void loadPendingOrders() {
        listPanel.removeAll();
        boolean hasOrders = false;
        
        String sql = "SELECT r.id, r.user_id, c.id as car_id, c.brand, c.transmission, r.total_price, " +
                     "TIMESTAMPDIFF(HOUR, r.start_time, r.end_time) as duration " +
                     "FROM rentals r JOIN cars c ON r.car_id = c.id " +
                     "WHERE r.status = 'pending'";
                     
        try (Connection conn = db.testdatabase.getKoneksi();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                hasOrders = true;
                int rentalId = rs.getInt("id");
                int userId = rs.getInt("user_id");
                int carId = rs.getInt("car_id");
                String brand = rs.getString("brand");
                String trans = rs.getString("transmission");
                double price = rs.getDouble("total_price");
                int duration = rs.getInt("duration");

                JPanel item = createOrderItem(rentalId, userId, carId, brand, trans, price, duration);
                listPanel.add(item);
                listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            if (!hasOrders) {
                javax.swing.JLabel emptyLabel = new javax.swing.JLabel("Belum ada pesanan masuk", javax.swing.SwingConstants.CENTER);
                emptyLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                emptyLabel.setForeground(java.awt.Color.GRAY);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                listPanel.add(Box.createRigidArea(new Dimension(0, 50)));
                listPanel.add(emptyLabel);
                detailPanel.setVisible(false);
            } else {
                detailPanel.setVisible(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createOrderItem(int rentalId, int userId, int carId, String brand, String trans, double price, int duration) {
        JPanel panel = new JPanel(new java.awt.BorderLayout(10, 10));

        
        panel.setMaximumSize(new Dimension(320, 80));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        javax.swing.JLabel iconLabel = new javax.swing.JLabel("🚗", javax.swing.SwingConstants.CENTER);
        iconLabel.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 32));

        JPanel textPanel = new JPanel(new java.awt.GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(new javax.swing.JLabel(brand));
        textPanel.add(new javax.swing.JLabel(trans));

        JPanel rightPanel = new JPanel(new java.awt.GridLayout(2, 1));
        rightPanel.setOpaque(false);
        rightPanel.add(new javax.swing.JLabel(duration + " Jam", javax.swing.SwingConstants.RIGHT));
        
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        javax.swing.JLabel priceLabel = new javax.swing.JLabel(nf.format(price), javax.swing.SwingConstants.RIGHT);
        priceLabel.setForeground(new java.awt.Color(22, 163, 74));
        rightPanel.add(priceLabel);

        panel.add(iconLabel, java.awt.BorderLayout.WEST);
        panel.add(textPanel, java.awt.BorderLayout.CENTER);
        panel.add(rightPanel, java.awt.BorderLayout.EAST);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showOrderDetails(rentalId, userId, carId, brand, trans, price, duration);
            }
        });

        return panel;
    }

    private void showOrderDetails(int rentalId, int userId, int carId, String brand, String trans, double price, int duration) {
        selectedRentalId = rentalId;
        selectedUserId = userId;
        selectedCarId = carId;
        originalPrice = price;
        selectedKtpPath = "";
        
        ktpPathLabel.setText("Belum ada file dipilih");
        
        String userName = "", userPhone = "", userAddress = "", userNik = "";
        try (Connection conn = db.testdatabase.getKoneksi()) {
            String sql = "SELECT name, phone, address, nik FROM users WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        userName = rs.getString("name");
                        userPhone = rs.getString("phone");
                        userAddress = rs.getString("address");
                        userNik = rs.getString("nik");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        namaKTP.setText(userName != null ? userName : "");
        noTelp.setText(userPhone != null ? userPhone : "");
        alamat.setText(userAddress != null ? userAddress : "");
        noKTP.setText(userNik != null ? userNik : "");
        
        detailPanel.setVisible(true);
        detailPanel.revalidate();
        detailPanel.repaint();
    }


    private void clearForm() {
        namaKTP.setText("");
        noTelp.setText("");
        alamat.setText("");
        noKTP.setText("");
        ktpPathLabel.setText("Belum ada file dipilih");
        selectedRentalId = -1;
        selectedUserId = -1;
        selectedCarId = -1;
        selectedKtpPath = "";
    }

    public order() {
        initComponents();
        
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        clearForm();
        loadPendingOrders();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        detailPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        namaKTP = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        noTelp = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        alamat = new javax.swing.JTextField();
        noKTP = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        konfirmasi = new javax.swing.JButton();
        tolak = new javax.swing.JButton();
        uploadKTP = new javax.swing.JButton();
        ktpPathLabel = new javax.swing.JLabel();
        jScrollPaneLlist = new javax.swing.JScrollPane();
        listPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 153, 51));

        detailPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setText("mobil (transmisi)");

        jLabel7.setText("harga");

        jLabel8.setText("Nama sesuai KTP");

        jLabel9.setText("Nomor Telepon");

        noTelp.addActionListener(this::noTelpActionPerformed);

        jLabel10.setText("Alamat Lengkap");

        noKTP.addActionListener(this::noKTPActionPerformed);

        jLabel11.setText("Nomor KTP");

        konfirmasi.setText("Konfirmasi");
        konfirmasi.addActionListener(this::konfirmasiActionPerformed);

        tolak.setText("Tolak");
        tolak.addActionListener(this::tolakActionPerformed);

        uploadKTP.setText("+ Upload KTP");
        uploadKTP.addActionListener(this::uploadKTPActionPerformed);

        ktpPathLabel.setText("file belum diupload");

        javax.swing.GroupLayout detailPanelLayout = new javax.swing.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailPanelLayout.createSequentialGroup()
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(detailPanelLayout.createSequentialGroup()
                                .addComponent(konfirmasi, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tolak, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(detailPanelLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7)))
                        .addGap(14, 14, 14))
                    .addGroup(detailPanelLayout.createSequentialGroup()
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(noKTP, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(noTelp, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(alamat, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(namaKTP, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addContainerGap(115, Short.MAX_VALUE))
                    .addGroup(detailPanelLayout.createSequentialGroup()
                        .addComponent(uploadKTP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ktpPathLabel)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(8, 8, 8)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(namaKTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noTelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noKTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uploadKTP)
                    .addComponent(ktpPathLabel))
                .addGap(18, 18, 18)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(konfirmasi)
                    .addComponent(tolak)))
        );

        listPanel.setBackground(new java.awt.Color(255, 255, 255));
        listPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel1.setText("foto");

        jLabel2.setText("transmisi");

        jLabel3.setText("merk");

        jLabel4.setText("waktu");

        jLabel5.setText("harga");

        javax.swing.GroupLayout listPanelLayout = new javax.swing.GroupLayout(listPanel);
        listPanel.setLayout(listPanelLayout);
        listPanelLayout.setHorizontalGroup(
            listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addGroup(listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(21, 21, 21))
        );
        listPanelLayout.setVerticalGroup(
            listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(listPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addGap(19, 19, 19))
        );

        jScrollPaneLlist.setViewportView(listPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneLlist, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneLlist))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void noTelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noTelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noTelpActionPerformed

    private void noKTPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noKTPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noKTPActionPerformed

    private void konfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_konfirmasiActionPerformed
     if (selectedRentalId == -1 || selectedUserId == -1) return;
        
        String address = alamat.getText().trim();
        String nik = noKTP.getText().trim();
        if (address.isEmpty() || nik.isEmpty() || selectedKtpPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi Alamat, NIK, dan unggah Foto KTP!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection conn = db.testdatabase.getKoneksi()) {
            conn.setAutoCommit(false); // Transaksi Database
            try {

                String userSql = "UPDATE users SET address = ?, nik = ?, ktp_photo = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
                    stmt.setString(1, address);
                    stmt.setString(2, nik);
                    stmt.setString(3, selectedKtpPath);
                    stmt.setInt(4, selectedUserId);
                    stmt.executeUpdate();
                }
                
                String rentalSql = "UPDATE rentals SET status = 'active', admin_id = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(rentalSql)) {
                    stmt.setInt(1, currentAdminId);
                    stmt.setInt(2, selectedRentalId);
                    stmt.executeUpdate();
                }
                String transSql = "INSERT INTO transactions (rental_id, car_id, transaction_type, amount, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
                try (PreparedStatement stmt = conn.prepareStatement(transSql)) {
                    stmt.setInt(1, selectedRentalId);
                    stmt.setInt(2, selectedCarId);
                    stmt.setString(3, "Sewa Baru");
                    stmt.setDouble(4, originalPrice);
                    stmt.executeUpdate();
                }

                String carSql = "UPDATE cars SET status = 'rented' WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(carSql)) {
                    stmt.setInt(1, selectedCarId);
                    stmt.executeUpdate();
                }
                conn.commit();
                
                JOptionPane.showMessageDialog(this, "Pesanan Berhasil Dikonfirmasi!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadPendingOrders();
                
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengonfirmasi pesanan:\n" + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_konfirmasiActionPerformed

    private void uploadKTPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadKTPActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            selectedKtpPath = file.getAbsolutePath();
            ktpPathLabel.setText(file.getName());
        }
    }//GEN-LAST:event_uploadKTPActionPerformed

    private void tolakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tolakActionPerformed
    if (selectedRentalId == -1) return;
        String reason = JOptionPane.showInputDialog(this, "Masukkan alasan penolakan:", "Tolak Pesanan", JOptionPane.QUESTION_MESSAGE);
        if (reason == null || reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alasan penolakan tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection conn = db.testdatabase.getKoneksi()) {
            String sql = "UPDATE rentals SET status = 'rejected', rejection_reason = ?, admin_id = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, reason);
                stmt.setInt(2, currentAdminId);
                stmt.setInt(3, selectedRentalId);
                stmt.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Pesanan berhasil ditolak.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadPendingOrders();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menolak pesanan:\n" + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_tolakActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField alamat;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPaneLlist;
    private javax.swing.JButton konfirmasi;
    private javax.swing.JLabel ktpPathLabel;
    private javax.swing.JPanel listPanel;
    private javax.swing.JTextField namaKTP;
    private javax.swing.JTextField noKTP;
    private javax.swing.JTextField noTelp;
    private javax.swing.JButton tolak;
    private javax.swing.JButton uploadKTP;
    // End of variables declaration//GEN-END:variables
}

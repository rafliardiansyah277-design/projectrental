/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package projectrental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;

/**
 *
 * @author user dell 7420
 */
public class return1 extends javax.swing.JPanel {
    
    private int currentAdminId = 1; 
    private int selectedRentalId = -1;
    private int selectedCarId = -1;
    private double originalPrice = 0;
    private double penaltyAmount = 100000;
    private boolean isOverdue = false;

    private Timer countdownTimer;
    private List<RentalCardInfo> activeCards = new ArrayList<>();
    private RentalCardInfo selectedClassCard = null;

    private static class RentalCardInfo {
        int rentalId;
        int userId;
        int carId;
        String brand;
        String trans;
        String imagePath;
        double price;
        long timeDiffSec;
        javax.swing.JLabel timeLabel;
        javax.swing.JLabel priceLabel;
        JPanel panel;
    }

    public void setAdminId(int adminId) {
        this.currentAdminId = adminId;
    }

    /**
     * Creates new form return1
     */
    public return1() {
        initComponents();

        jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.Y_AXIS));

        jPanel3.setVisible(false);
        
        loadActiveOrders();
        
        countdownTimer = new Timer(1000, e -> updateCountdownTimers());
        countdownTimer.start();
    }
        // 1. Memuat daftar sewa yang sedang aktif dari database
    public void loadActiveOrders() {
        jPanel2.removeAll();
        activeCards.clear();
        
        String sql = "SELECT r.id, r.user_id, c.id as car_id, c.brand, c.transmission, c.image, r.total_price, " +
                     "TIMESTAMPDIFF(SECOND, NOW(), r.end_time) as time_diff_sec " +
                     "FROM rentals r JOIN cars c ON r.car_id = c.id " +
                     "WHERE r.status = 'active'";
                     
        try (Connection conn = db.testdatabase.getKoneksi();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            boolean hasOrders = false;
            while (rs.next()) {
                String brand = rs.getString("brand");
                String trans = rs.getString("transmission");
                String image = rs.getString("image");
                
                int rentalId = rs.getInt("id");
                hasOrders = true;
                int userId = rs.getInt("user_id");
                int carId = rs.getInt("car_id");
                double price = rs.getDouble("total_price");
                long timeDiffSec = rs.getLong("time_diff_sec");

                RentalCardInfo cardInfo = createReturnItem(rentalId, userId, carId, brand, trans, image, price, timeDiffSec);
                activeCards.add(cardInfo);
                jPanel2.add(cardInfo.panel);
                jPanel2.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            if (!hasOrders) {
                javax.swing.JLabel emptyLabel = new javax.swing.JLabel("Tidak ada unit yang aktif disewa", javax.swing.SwingConstants.CENTER);
                emptyLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                emptyLabel.setForeground(java.awt.Color.GRAY);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                jPanel2.add(Box.createRigidArea(new Dimension(0, 50)));
                jPanel2.add(emptyLabel);
                jPanel3.setVisible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        jPanel2.revalidate();
        jPanel2.repaint();
    }

    // 2. Format Detik menjadi Jam:Menit:Detik
    private String formatTimeDiff(long totalSecs) {
        boolean isNegative = totalSecs < 0;
        long absSecs = Math.abs(totalSecs);
        long hours = absSecs / 3600;
        long minutes = (absSecs % 3600) / 60;
        long seconds = absSecs % 60;
        
        String timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return isNegative ? "-" + timeStr : timeStr;
    }

    // 3. Membuat Item Visual Kartu Pengembalian secara dinamis
    private RentalCardInfo createReturnItem(int rentalId, int userId, int carId, String brand, String trans, String imagePath, double price, long timeDiffSec) {
        boolean overdue = timeDiffSec < 0;
        
        RentalCardInfo card = new RentalCardInfo();
        card.rentalId = rentalId;
        card.userId = userId;
        card.carId = carId;
        card.brand = brand;
        card.trans = trans;
        card.imagePath = imagePath;
        card.price = price;
        card.timeDiffSec = timeDiffSec;

        card.panel = new JPanel(new java.awt.BorderLayout(10, 10));
        card.panel.setBackground(overdue ? new Color(255, 180, 180) : Color.WHITE);
        card.panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.panel.setMaximumSize(new Dimension(320, 80));
        card.panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        javax.swing.JLabel iconLabel;
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            iconLabel = new javax.swing.JLabel("", javax.swing.SwingConstants.CENTER);
            try {
                String imgPath = "d:/coding/Documentation/coolyeah/UAS/rental-mobil/storage/app/public/" + imagePath;
                java.io.File file = new java.io.File(imgPath);
                if (file.exists()) {
                    java.awt.Image img = javax.imageio.ImageIO.read(file);
                    java.awt.Image scaledImg = img.getScaledInstance(50, 35, java.awt.Image.SCALE_SMOOTH);
                    iconLabel.setIcon(new javax.swing.ImageIcon(scaledImg));
                } else {
                    iconLabel.setText("🚗");
                    iconLabel.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 32));
                }
            } catch (Exception ex) {
                iconLabel.setText("🚗");
                iconLabel.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 32));
            }
        } else {
            iconLabel = new javax.swing.JLabel("🚗", javax.swing.SwingConstants.CENTER);
            iconLabel.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 32));
        }

        JPanel textPanel = new JPanel(new java.awt.GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(new javax.swing.JLabel(brand));
        textPanel.add(new javax.swing.JLabel(trans));

        JPanel rightPanel = new JPanel(new java.awt.GridLayout(2, 1));
        rightPanel.setOpaque(false);
        
        card.timeLabel = new javax.swing.JLabel(formatTimeDiff(timeDiffSec), javax.swing.SwingConstants.CENTER);
        card.timeLabel.setOpaque(true);
        card.timeLabel.setBackground(overdue ? new Color(200, 0, 0) : new Color(0, 150, 0));
        card.timeLabel.setForeground(Color.WHITE);
        card.timeLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        
        rightPanel.add(card.timeLabel);
        
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        card.priceLabel = new javax.swing.JLabel(nf.format(price), javax.swing.SwingConstants.RIGHT);
        rightPanel.add(card.priceLabel);

        card.panel.add(iconLabel, java.awt.BorderLayout.WEST);
        card.panel.add(textPanel, java.awt.BorderLayout.CENTER);
        card.panel.add(rightPanel, java.awt.BorderLayout.EAST);

        card.panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showReturnDetails(card);
            }
        });

        return card;
    }

    // 4. Update Countdown Setiap Detik di Memory & Detail Panel
    private void updateCountdownTimers() {
        for (RentalCardInfo card : activeCards) {
            card.timeDiffSec--;
            boolean overdue = card.timeDiffSec < 0;
            card.timeLabel.setText(formatTimeDiff(card.timeDiffSec));
            
            card.timeLabel.setBackground(overdue ? new Color(200, 0, 0) : new Color(0, 150, 0));
            card.panel.setBackground(overdue ? new Color(255, 180, 180) : Color.WHITE);
            
            // Jika detail panel sedang aktif menampilkan kartu ini, update detak detailnya juga secara live
            if (selectedClassCard != null && selectedClassCard.rentalId == card.rentalId) {
                jLabel4.setText(formatTimeDiff(card.timeDiffSec));
                jLabel4.setBackground(overdue ? new Color(200, 0, 0) : new Color(0, 150, 0));
                jPanel3.setBackground(overdue ? new Color(255, 180, 180) : Color.WHITE);
                
                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                if (overdue) {
                    jLabel6.setText("+ " + nf.format(penaltyAmount) + " (Denda)");
                    jLabel6.setForeground(new Color(200, 0, 0));
                    jLabel6.setVisible(true);
                } else {
                    jLabel6.setText("");
                    jLabel6.setVisible(false);
                }
            }
        }
    }

    // 5. Menampilkan Info Detail Penyewa ke Form Kanan
    private void showReturnDetails(RentalCardInfo card) {
        selectedClassCard = card;
        selectedRentalId = card.rentalId;
        selectedCarId = card.carId;
        originalPrice = card.price;
        isOverdue = card.timeDiffSec < 0;
        
        jPanel3.setBackground(isOverdue ? new Color(255, 180, 180) : Color.WHITE);
        
        // Set info header detail
        jLabel2.setText(card.brand);
        jLabel3.setText(card.trans);
        jLabel4.setText(formatTimeDiff(card.timeDiffSec));
        jLabel4.setOpaque(true);
        jLabel4.setBackground(isOverdue ? new Color(200, 0, 0) : new Color(0, 150, 0));
        jLabel4.setForeground(Color.WHITE);
        
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        jLabel5.setText(nf.format(card.price));
        
        if (isOverdue) {
            jLabel6.setText("+ " + nf.format(penaltyAmount) + " (Denda)");
            jLabel6.setForeground(new Color(200, 0, 0));
            jLabel6.setVisible(true);
        } else {
            jLabel6.setText("");
            jLabel6.setVisible(false);
        }
        
        // Load Car Image
        if (card.imagePath != null && !card.imagePath.trim().isEmpty()) {
            try {
                String imgPath = "d:/coding/Documentation/coolyeah/UAS/rental-mobil/storage/app/public/" + card.imagePath;
                java.io.File file = new java.io.File(imgPath);
                if (file.exists()) {
                    java.awt.Image img = javax.imageio.ImageIO.read(file);
                    java.awt.Image scaledImg = img.getScaledInstance(80, 50, java.awt.Image.SCALE_SMOOTH);
                    jLabel1.setIcon(new javax.swing.ImageIcon(scaledImg));
                    jLabel1.setText("");
                } else {
                    jLabel1.setIcon(null);
                    jLabel1.setText("No Image");
                }
            } catch (Exception ex) {
                jLabel1.setIcon(null);
                jLabel1.setText("Error Image");
                ex.printStackTrace();
            }
        } else {
            jLabel1.setIcon(null);
            jLabel1.setText("No Image");
        }
        
        // Ambil Data Penyewa dari Database
        String userName = "", userPhone = "", userAddress = "", userNik = "";
        try (Connection conn = db.testdatabase.getKoneksi()) {
            String sql = "SELECT name, phone, address, nik FROM users WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, card.userId);
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

        nama.setText(userName != null ? userName : "");
        nomorTelp.setText(userPhone != null ? userPhone : "");
        Alamat.setText(userAddress != null ? userAddress : "");
        noKTP.setText(userNik != null ? userNik : "");

        jPanel3.setVisible(true);
        jPanel3.revalidate();
        jPanel3.repaint();
    }

    private void clearForm() {
        nama.setText("");
        nomorTelp.setText("");
        Alamat.setText("");
        noKTP.setText("");
        selectedRentalId = -1;
        selectedCarId = -1;
        selectedClassCard = null;
        jLabel1.setIcon(null);
        jLabel1.setText("Foto Mobil");
        jPanel3.setVisible(false);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        nama = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        nomorTelp = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        Alamat = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        noKTP = new javax.swing.JTextField();
        konfirmasi = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 153, 51));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 223, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel2);

        jLabel1.setText("Foto Mobil");

        jLabel2.setText("Merk");

        jLabel3.setText("Transmisi");

        jLabel4.setText("Waktu");

        jLabel5.setText("Harga sewa");

        jLabel6.setText("Denda");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel4))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel5)))
                .addContainerGap(39, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(51, 51, 51))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setText("Nama Lengkap");

        nama.addActionListener(this::namaActionPerformed);

        jLabel8.setText("Nomor Telepon");

        nomorTelp.addActionListener(this::nomorTelpActionPerformed);

        jLabel9.setText("Alamat Lengkap");

        Alamat.addActionListener(this::AlamatActionPerformed);

        jLabel10.setText("Nomor KTP");

        noKTP.addActionListener(this::noKTPActionPerformed);

        konfirmasi.setText("Konfirmasi");
        konfirmasi.addActionListener(this::konfirmasiActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nama, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(nomorTelp, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(Alamat, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(noKTP, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(konfirmasi, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 14, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(21, 21, 21)
                        .addComponent(jLabel3)))
                .addGap(16, 16, 16)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nomorTelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Alamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noKTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(konfirmasi, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void namaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaActionPerformed

    private void nomorTelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nomorTelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nomorTelpActionPerformed

    private void AlamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlamatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AlamatActionPerformed

    private void noKTPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noKTPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noKTPActionPerformed

    private void konfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_konfirmasiActionPerformed
    if (selectedRentalId == -1) return;
        double finalPrice = originalPrice;
        if (isOverdue) {
            finalPrice += penaltyAmount;
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        int confirmResult = JOptionPane.showConfirmDialog(this, 
            "Selesaikan pengembalian?\nTotal Tagihan Akhir: " + nf.format(finalPrice) + 
            (isOverdue ? "\n(Sudah termasuk denda terlambat " + nf.format(penaltyAmount) + ")" : ""), 
            "Konfirmasi Pengembalian", JOptionPane.YES_NO_OPTION);
            
        if (confirmResult != JOptionPane.YES_OPTION) return;
        try (Connection conn = db.testdatabase.getKoneksi()) {
            conn.setAutoCommit(false); // Transaksi Database
            try {
                // 1. Update status sewa di rentals menjadi 'completed' dan simpan total_price akhir + admin_id PIC
                String rentalSql = isOverdue 
                    ? "UPDATE rentals SET status = 'completed', total_price = ?, admin_id = ?, penalty_status = 'paid' WHERE id = ?"
                    : "UPDATE rentals SET status = 'completed', total_price = ?, admin_id = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(rentalSql)) {
                    stmt.setDouble(1, finalPrice);
                    stmt.setInt(2, currentAdminId);
                    stmt.setInt(3, selectedRentalId);
                    stmt.executeUpdate();
                }
                // 2. Kembalikan status unit mobil menjadi 'available' (siap disewa kembali)
                String carSql = "UPDATE cars SET status = 'available' WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(carSql)) {
                    stmt.setInt(1, selectedCarId);
                    stmt.executeUpdate();
                }
                // 3. Catat transaksi denda jika ada ke riwayat transaksi
                if (isOverdue) {
                    String transSql = "INSERT INTO transactions (rental_id, car_id, transaction_type, amount, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
                    try (PreparedStatement stmt = conn.prepareStatement(transSql)) {
                        stmt.setInt(1, selectedRentalId);
                        stmt.setInt(2, selectedCarId);
                        stmt.setString(3, "Denda");
                        stmt.setDouble(4, penaltyAmount);
                        stmt.executeUpdate();
                    }
                }
                conn.commit(); // Eksekusi sukses
                
                JOptionPane.showMessageDialog(this, "Mobil berhasil dikembalikan ke garasi!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadActiveOrders();
                
            } catch (Exception e) {
                conn.rollback(); // Batalkan transaksi jika gagal
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengonfirmasi pengembalian:\n" + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_konfirmasiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Alamat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton konfirmasi;
    private javax.swing.JTextField nama;
    private javax.swing.JTextField noKTP;
    private javax.swing.JTextField nomorTelp;
    // End of variables declaration//GEN-END:variables
}

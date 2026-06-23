package projectrental;

import db.testdatabase;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class masterdata extends javax.swing.JPanel {

    public masterdata() {
        initComponents();
        setupDummyCatalog();
    }

    private void setupDummyCatalog() {
        // Ubah layout panel masterdata utama menjadi BorderLayout
        this.setLayout(new BorderLayout(15, 15));
        this.setBackground(new Color(245, 247, 250)); // Background abu-abu muda premium
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Master Data - Katalog Mobil");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Subtitle / Info
        JLabel subtitleLabel = new JLabel("Menampilkan unit rental mobil aktif dari Database");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        JPanel textHeader = new JPanel();
        textHeader.setLayout(new BoxLayout(textHeader, BoxLayout.Y_AXIS));
        textHeader.setOpaque(false);
        textHeader.add(titleLabel);
        textHeader.add(Box.createVerticalStrut(4));
        textHeader.add(subtitleLabel);
        headerPanel.add(textHeader, BorderLayout.WEST);

        // Pencarian Sederhana (Mockup)
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchBarPanel.setOpaque(false);
        JTextField searchField = new JTextField("Cari mobil...", 15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton searchBtn = new JButton("Cari");
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBarPanel.add(searchField);
        searchBarPanel.add(searchBtn);
        headerPanel.add(searchBarPanel, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);

        // --- 2. GRID PANEL (DAFTAR MOBIL) ---
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 Kolom
        gridPanel.setBackground(new Color(245, 247, 250));

        // Coba ambil data dari Database
        boolean loadedFromDb = false;
        try (Connection conn = testdatabase.getKoneksi()) {
            String sql = "SELECT brand, transmission, price_per_hour, plate_number, status, image FROM cars";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String brand = rs.getString("brand");
                    String trans = rs.getString("transmission");
                    double price = rs.getDouble("price_per_hour");
                    String plate = rs.getString("plate_number");
                    String status = rs.getString("status");
                    String image = rs.getString("image");
                    
                    gridPanel.add(createCarCard(brand, trans, price, plate, status, image));
                    loadedFromDb = true;
                }
            }
        } catch (Exception ex) {
            System.err.println("Gagal memuat database, fallback ke dummy data: " + ex.getMessage());
        }

        // Agar bisa di-scroll jika datanya banyak
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    // Fungsi pembantu untuk membuat JPanel berbentuk Card Mobil
    private JPanel createCarCard(String brand, String transmission, double price, String plate, String status, String imagePath) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        
        // Border rounded tipis & padding dalam
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // 1. Gambar Mobil (Load dinamis)
        JLabel imgPlaceholder = new JLabel("", SwingConstants.CENTER);
        imgPlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgPlaceholder.setPreferredSize(new Dimension(140, 90));
        imgPlaceholder.setMaximumSize(new Dimension(140, 90));
        imgPlaceholder.setMinimumSize(new Dimension(140, 90));

        boolean imageLoaded = false;
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                String fullPath = "d:/coding/Documentation/coolyeah/UAS/rental-mobil/storage/app/public/" + imagePath;
                java.io.File file = new java.io.File(fullPath);
                if (file.exists()) {
                    java.awt.Image img = javax.imageio.ImageIO.read(file);
                    java.awt.Image scaledImg = img.getScaledInstance(140, 90, java.awt.Image.SCALE_SMOOTH);
                    imgPlaceholder.setIcon(new ImageIcon(scaledImg));
                    imageLoaded = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (!imageLoaded) {
            // Placeholder clean jika gambar tidak ditemukan agar tidak memunculkan kotak kosong
            imgPlaceholder.setText("No Photo");
            imgPlaceholder.setFont(new Font("Segoe UI", Font.BOLD, 12));
            imgPlaceholder.setForeground(new Color(150, 150, 150));
            imgPlaceholder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }

        // 2. Nama Brand Mobil
        JLabel brandLabel = new JLabel(brand);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandLabel.setForeground(new Color(44, 62, 80));
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Detail Transmisi & Plat Nomor
        JLabel detailLabel = new JLabel(transmission + " | " + plate);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailLabel.setForeground(new Color(127, 140, 141));
        detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4. Badge Status (Available / Rented)
        JLabel statusLabel = new JLabel(status.toUpperCase());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusLabel.setOpaque(true);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        
        if (status.equalsIgnoreCase("Available")) {
            statusLabel.setBackground(new Color(220, 245, 220)); // Hijau muda
            statusLabel.setForeground(new Color(39, 174, 96));   // Hijau tua
        } else {
            statusLabel.setBackground(new Color(253, 235, 235)); // Merah muda
            statusLabel.setForeground(new Color(192, 57, 43));   // Merah tua
        }

        // 5. Harga per Jam (Format Rupiah)
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedPrice = formatter.format(price).replace(",00", "");
        JLabel priceLabel = new JLabel(formattedPrice + " / Hari");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setOpaque(true);
        priceLabel.setBackground(new Color(41, 128, 185)); // Biru
        priceLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Gabungkan komponen ke dalam card dengan jarak (spacing)
        card.add(imgPlaceholder);
        card.add(Box.createVerticalStrut(10));
        card.add(brandLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(detailLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(priceLabel);

        return card;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 571, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 366, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

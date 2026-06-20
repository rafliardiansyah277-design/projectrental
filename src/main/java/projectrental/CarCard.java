package projectrental;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class CarCard extends JPanel {

    public CarCard(String brand, String transmission, double price, String plate, String status) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        
        // Desain Border dan Padding Card
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // 1. Icon / Gambar Mobil Placeholder
        JLabel iconLabel = new JLabel("🚗", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setPreferredSize(new Dimension(100, 70));

        // 2. Nama Brand Mobil
        JLabel brandLabel = new JLabel(brand);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brandLabel.setForeground(new Color(44, 62, 80));
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Detail Transmisi & Plat Nomor
        JLabel detailLabel = new JLabel(transmission + " | " + plate);
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailLabel.setForeground(new Color(127, 140, 141));
        detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 4. Badge Status (Available / Rented)
        JLabel statusLabel = new JLabel(status.toUpperCase());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        statusLabel.setOpaque(true);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        
        if (status.equalsIgnoreCase("Available")) {
            statusLabel.setBackground(new Color(220, 245, 220)); // Hijau muda
            statusLabel.setForeground(new Color(39, 174, 96));   // Hijau tua
        } else {
            statusLabel.setBackground(new Color(253, 235, 235)); // Merah muda
            statusLabel.setForeground(new Color(192, 57, 43));   // Merah tua
        }

        // 5. Harga Sewa (Format Rupiah)
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedPrice = formatter.format(price).replace(",00", "");
        JLabel priceLabel = new JLabel(formattedPrice + " / Hari");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setOpaque(true);
        priceLabel.setBackground(new Color(41, 128, 185)); // Biru
        priceLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Satukan komponen ke dalam box card
        add(iconLabel);
        add(Box.createVerticalStrut(10));
        add(brandLabel);
        add(Box.createVerticalStrut(4));
        add(detailLabel);
        add(Box.createVerticalStrut(8));
        add(statusLabel);
        add(Box.createVerticalStrut(10));
        add(priceLabel);
    }
}

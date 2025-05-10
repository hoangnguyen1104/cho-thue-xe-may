package com.chothuexemay.view;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.model.DonThue;
import com.chothuexemay.model.User;
import com.chothuexemay.model.XeMay;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TaoDonThueDialog extends JDialog {
    private JTextField hoTenField, soDienThoaiField;
    private JDateChooser ngayTraDateChooser;
    private JButton taoBtn, huyBtn;
    private DataController dataController;
    private QuanLyXePanel quanLyXePanel;
    private XeMay xeMay;

    public TaoDonThueDialog(Window parent, DataController dataController, QuanLyXePanel quanLyXePanel, XeMay xeMay) {
        super(parent, "Tạo đơn thuê xe", ModalityType.APPLICATION_MODAL);
        this.dataController = dataController;
        this.quanLyXePanel = quanLyXePanel;
        this.xeMay = xeMay;
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đơn thuê"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Thông tin xe
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Biển số xe:"), gbc);
        gbc.gridx = 1;
        JTextField bienSoField = new JTextField(xeMay.getBienSo());
        bienSoField.setEditable(false);
        formPanel.add(bienSoField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tên xe:"), gbc);
        gbc.gridx = 1;
        JTextField tenXeField = new JTextField(xeMay.getTenXe());
        tenXeField.setEditable(false);
        formPanel.add(tenXeField, gbc);

        // Thông tin khách hàng
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Họ tên khách hàng:"), gbc);
        gbc.gridx = 1;
        hoTenField = new JTextField(20);
        formPanel.add(hoTenField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        soDienThoaiField = new JTextField(20);
        // Thêm sự kiện khi số điện thoại thay đổi
        soDienThoaiField.addCaretListener(e -> {
            String soDienThoai = soDienThoaiField.getText().trim();
            if (!soDienThoai.isEmpty()) {
                User existingUser = dataController.getUserByPhoneNumber(soDienThoai);
                if (existingUser != null) {
                    hoTenField.setText(existingUser.getHoTen());
                    hoTenField.setEditable(false);
                } else {
                    hoTenField.setEditable(true);
                }
            }
        });
        formPanel.add(soDienThoaiField, gbc);

        // Ngày trả xe
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Ngày trả xe:"), gbc);
        gbc.gridx = 1;
        ngayTraDateChooser = new JDateChooser();
        ngayTraDateChooser.setDate(new Date());
        formPanel.add(ngayTraDateChooser, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        taoBtn = new JButton("Tạo đơn");
        huyBtn = new JButton("Hủy");
        
        // Style buttons
        taoBtn.setBackground(new Color(46, 204, 113));
        taoBtn.setForeground(Color.WHITE);
        huyBtn.setBackground(new Color(231, 76, 60));
        huyBtn.setForeground(Color.WHITE);
        
        buttonPanel.add(taoBtn);
        buttonPanel.add(huyBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        taoBtn.addActionListener(e -> taoDonThue());
        huyBtn.addActionListener(e -> dispose());
    }

    private void taoDonThue() {
        String hoTen = hoTenField.getText().trim();
        String soDienThoai = soDienThoaiField.getText().trim();
        Date ngayTra = ngayTraDateChooser.getDate();

        if (hoTen.isEmpty() || soDienThoai.isEmpty() || ngayTra == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng điền đầy đủ thông tin!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra số điện thoại đã tồn tại chưa
        User khachHang = dataController.getUserByPhoneNumber(soDienThoai);
        if (khachHang == null) {
            // Tạo user mới cho khách hàng
            khachHang = new User(0, soDienThoai + "@gmail.com", "user123", hoTen, "user", soDienThoai);
            int userId = dataController.addUser(khachHang);
            
            if (userId == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi tạo thông tin khách hàng!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            khachHang = dataController.getUserByPhoneNumber(soDienThoai);
        }

        // Tạo đơn thuê
        LocalDate ngayThue = LocalDate.now();
        LocalDate ngayTraLocal = ngayTra.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Kiểm tra ngày trả phải sau ngày thuê
        if (!ngayTraLocal.isAfter(ngayThue)) {
            JOptionPane.showMessageDialog(this, 
                "Ngày trả phải sau ngày thuê!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Tính tổng tiền
        long soNgay = java.time.temporal.ChronoUnit.DAYS.between(ngayThue, ngayTraLocal);
        double tongTien = xeMay.getGiaThue() * soNgay;

        DonThue donThue = new DonThue(
            0, // ID sẽ được tự động tạo
            khachHang.getId(),
            xeMay.getBienSo(),
            ngayThue,
            ngayTraLocal,
            false,
            tongTien
        );

        dataController.addDonThue(donThue);
        xeMay.setDaThue(true);
        dataController.updateXeMay(xeMay);
        
        quanLyXePanel.loadData();
        dispose();
        JOptionPane.showMessageDialog(this, 
            String.format("Tạo đơn thuê thành công!\nTổng tiền: %.2f VND", tongTien), 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE);
    }
} 
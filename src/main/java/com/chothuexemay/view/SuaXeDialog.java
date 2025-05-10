package com.chothuexemay.view;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.model.XeMay;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SuaXeDialog extends JDialog {
    private JTextField bienSoField, tenXeField, hangXeField, giaThueField;
    private JButton luuBtn, huyBtn;
    private DataController dataController;
    private QuanLyXePanel quanLyXePanel;
    private XeMay xeMay;

    public SuaXeDialog(Window parent, DataController dataController, QuanLyXePanel quanLyXePanel, XeMay xeMay) {
        super(parent, "Sửa thông tin xe", ModalityType.APPLICATION_MODAL);
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin xe"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Biển số
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Biển số:"), gbc);
        gbc.gridx = 1;
        bienSoField = new JTextField(20);
        bienSoField.setText(xeMay.getBienSo());
        bienSoField.setEditable(false); // Không cho phép sửa biển số
        formPanel.add(bienSoField, gbc);

        // Tên xe
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tên xe:"), gbc);
        gbc.gridx = 1;
        tenXeField = new JTextField(20);
        tenXeField.setText(xeMay.getTenXe());
        formPanel.add(tenXeField, gbc);

        // Hãng xe
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Hãng xe:"), gbc);
        gbc.gridx = 1;
        hangXeField = new JTextField(20);
        hangXeField.setText(xeMay.getHangXe());
        formPanel.add(hangXeField, gbc);

        // Giá thuê
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Giá thuê:"), gbc);
        gbc.gridx = 1;
        giaThueField = new JTextField(20);
        giaThueField.setText(String.valueOf(xeMay.getGiaThue()));
        formPanel.add(giaThueField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        luuBtn = new JButton("Lưu");
        huyBtn = new JButton("Hủy");
        
        // Style buttons
        luuBtn.setBackground(new Color(46, 204, 113));
        luuBtn.setForeground(Color.WHITE);
        huyBtn.setBackground(new Color(231, 76, 60));
        huyBtn.setForeground(Color.WHITE);
        
        buttonPanel.add(luuBtn);
        buttonPanel.add(huyBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        luuBtn.addActionListener(e -> updateXe());
        huyBtn.addActionListener(e -> dispose());
    }

    private void updateXe() {
        String tenXe = tenXeField.getText();
        String hangXe = hangXeField.getText();
        String giaThueStr = giaThueField.getText();

        if (tenXe.isEmpty() || hangXe.isEmpty() || giaThueStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double giaThue = Double.parseDouble(giaThueStr);
            xeMay.setTenXe(tenXe);
            xeMay.setHangXe(hangXe);
            xeMay.setGiaThue(giaThue);
            
            dataController.updateXeMay(xeMay);
            quanLyXePanel.loadData();
            dispose();
            JOptionPane.showMessageDialog(this, "Cập nhật xe thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá thuê không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
} 
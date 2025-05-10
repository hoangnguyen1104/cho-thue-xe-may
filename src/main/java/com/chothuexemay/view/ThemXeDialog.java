package com.chothuexemay.view;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.model.XeMay;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ThemXeDialog extends JDialog {
    private JTextField bienSoField, tenXeField, loaiXeField, moTaField, giaThueField;
    private JButton themBtn, huyBtn;
    private DataController dataController;
    private QuanLyXePanel quanLyXePanel;

    public ThemXeDialog(Window parent, DataController dataController, QuanLyXePanel quanLyXePanel) {
        super(parent, "Thêm xe mới", ModalityType.APPLICATION_MODAL);
        this.dataController = dataController;
        this.quanLyXePanel = quanLyXePanel;
        
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
        formPanel.add(bienSoField, gbc);

        // Tên xe
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tên xe:"), gbc);
        gbc.gridx = 1;
        tenXeField = new JTextField(20);
        formPanel.add(tenXeField, gbc);

        // Loại xe
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Loại xe:"), gbc);
        gbc.gridx = 1;
        loaiXeField = new JTextField(20);
        formPanel.add(loaiXeField, gbc);

        // Mô tả
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        moTaField = new JTextField(20);
        formPanel.add(moTaField, gbc);

        // Giá thuê
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Giá thuê:"), gbc);
        gbc.gridx = 1;
        giaThueField = new JTextField(20);
        formPanel.add(giaThueField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        themBtn = new JButton("Thêm");
        huyBtn = new JButton("Hủy");
        
        // Style buttons
        themBtn.setBackground(new Color(46, 204, 113));
        themBtn.setForeground(Color.WHITE);
        huyBtn.setBackground(new Color(231, 76, 60));
        huyBtn.setForeground(Color.WHITE);
        
        buttonPanel.add(themBtn);
        buttonPanel.add(huyBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        themBtn.addActionListener(e -> addXe());
        huyBtn.addActionListener(e -> dispose());
    }

    private void addXe() {
        String bienSo = bienSoField.getText();
        String tenXe = tenXeField.getText();
        String hangXe = loaiXeField.getText();
        String giaThueStr = giaThueField.getText();

        if (bienSo.isEmpty() || tenXe.isEmpty() || hangXe.isEmpty() || giaThueStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double giaThue = Double.parseDouble(giaThueStr);
            XeMay xeMay = new XeMay(bienSo, tenXe, hangXe, giaThue);
            dataController.addXeMay(xeMay);
            quanLyXePanel.loadData();
            dispose();
            JOptionPane.showMessageDialog(this, "Thêm xe thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá thuê không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
} 
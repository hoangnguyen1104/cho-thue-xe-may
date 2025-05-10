package com.chothuexemay.view;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.model.DonThue;
import com.chothuexemay.model.User;
import com.chothuexemay.model.XeMay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class ThueXePanel extends JPanel {
    private User currentUser;
    private DataController dataController;
    private JTable table;
    private DefaultTableModel tableModel;

    public ThueXePanel(User currentUser, DataController dataController) {
        this.currentUser = currentUser;
        this.dataController = dataController;
        setLayout(new BorderLayout());

        // Tạo bảng hiển thị danh sách xe
        tableModel = new DefaultTableModel(new Object[]{"Biển số", "Tên xe", "Hãng xe", "Giá thuê", "Trạng thái"}, 0);
        table = new JTable(tableModel);
        loadData();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tạo nút thuê xe
        JButton thueBtn = new JButton("Thuê xe");
        thueBtn.addActionListener(e -> thueXe());
        JPanel btnPanel = new JPanel();
        btnPanel.add(thueBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        for (XeMay xe : dataController.getXeMays()) {
            if (!xe.isDaThue()) {
                tableModel.addRow(new Object[]{
                    xe.getBienSo(), 
                    xe.getTenXe(), 
                    xe.getHangXe(), 
                    xe.getGiaThue(), 
                    xe.isDaThue() ? "Đã thuê" : "Chưa thuê"
                });
            }
        }
    }

    private void thueXe() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn xe để thuê", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bienSo = (String) table.getValueAt(selectedRow, 0);
        XeMay selectedXe = null;
        for (XeMay xe : dataController.getXeMays()) {
            if (xe.getBienSo().equals(bienSo)) {
                selectedXe = xe;
                break;
            }
        }

        if (selectedXe != null && !selectedXe.isDaThue()) {
            // Hiển thị dialog để nhập thông tin thuê xe
            JTextField ngayTraField = new JTextField(10);
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Ngày trả (yyyy-MM-dd):"));
            panel.add(ngayTraField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Thông tin thuê xe",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    LocalDate ngayTra = LocalDate.parse(ngayTraField.getText());
                    LocalDate ngayThue = LocalDate.now();
                    
                    // Tính số ngày thuê và tổng tiền
                    long soNgayThue = java.time.temporal.ChronoUnit.DAYS.between(ngayThue, ngayTra);
                    double tongTien = soNgayThue * selectedXe.getGiaThue();

                    DonThue donThue = new DonThue(0, currentUser.getId(), selectedXe.getBienSo(), ngayThue, ngayTra, false, tongTien);
                    dataController.addDonThue(donThue);
                    
                    selectedXe.setDaThue(true);
                    dataController.updateXeMay(selectedXe);
                    
                    loadData();
                    JOptionPane.showMessageDialog(this, "Thuê xe thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Ngày không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
} 
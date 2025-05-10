package com.chothuexemay.view;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.model.XeMay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuanLyXePanel extends JPanel {
    private DataController dataController;
    private JTable table;
    private DefaultTableModel tableModel;

    public QuanLyXePanel(DataController dataController) {
        this.dataController = dataController;
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"Biển số", "Tên xe", "Hãng xe", "Giá thuê", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells read-only
            }
        };
        table = new JTable(tableModel);
        loadData();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Thêm xe");
        JButton editBtn = new JButton("Sửa xe");
        JButton delBtn = new JButton("Xóa xe");
        JButton reloadBtn = new JButton("Làm mới");
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(reloadBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Chức năng thêm xe
        addBtn.addActionListener(e -> {
            ThemXeDialog dialog = new ThemXeDialog(SwingUtilities.getWindowAncestor(this), dataController, this);
            dialog.setVisible(true);
        });
        
        // Chức năng sửa xe
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xe để sửa", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bienSo = (String) table.getValueAt(selectedRow, 0);
            XeMay selectedXe = dataController.getXeMayByBienSo(bienSo);
            if (selectedXe != null) {
                SuaXeDialog dialog = new SuaXeDialog(SwingUtilities.getWindowAncestor(this), dataController, this, selectedXe);
                dialog.setVisible(true);
            }
        });

        // Chức năng xóa xe
        delBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xe để xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bienSo = (String) table.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa xe này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                dataController.deleteXeMay(bienSo);
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa xe thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Chức năng làm mới danh sách
        reloadBtn.addActionListener(e -> {
            loadData();
            JOptionPane.showMessageDialog(this, "Đã cập nhật danh sách xe!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        // Chức năng tạo đơn thuê
        JButton taoDonBtn = new JButton("Tạo đơn thuê");
        btnPanel.add(taoDonBtn);
        taoDonBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn xe để tạo đơn thuê", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bienSo = (String) table.getValueAt(selectedRow, 0);
            XeMay selectedXe = dataController.getXeMayByBienSo(bienSo);
            if (selectedXe != null) {
                if (selectedXe.isDaThue()) {
                    JOptionPane.showMessageDialog(this, "Xe này đã được thuê!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                TaoDonThueDialog dialog = new TaoDonThueDialog(SwingUtilities.getWindowAncestor(this), dataController, this, selectedXe);
                dialog.setVisible(true);
            }
        });
    }

    public void loadData() {
        tableModel.setRowCount(0);
        for (XeMay xe : dataController.getXeMays()) {
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
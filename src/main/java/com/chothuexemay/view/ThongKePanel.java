package com.chothuexemay.view;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.model.DonThue;
import com.chothuexemay.model.User;
import com.chothuexemay.model.XeMay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ThongKePanel extends JPanel {
    private DataController dataController;
    private JTable thongKeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> loaiThongKeCombo;
    private JLabel tongDonThueLabel;
    private JLabel tongDoanhThuLabel;

    public ThongKePanel(DataController dataController) {
        this.dataController = dataController;
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Panel chọn loại thống kê
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Chọn loại thống kê"));

        String[] loaiThongKe = {"Theo ngày", "Theo tuần", "Theo tháng"};
        loaiThongKeCombo = new JComboBox<>(loaiThongKe);
        loaiThongKeCombo.addActionListener(e -> loadData());
        controlPanel.add(new JLabel("Loại thống kê:"));
        controlPanel.add(loaiThongKeCombo);

        // Panel hiển thị tổng số
        JPanel tongSoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tongDonThueLabel = new JLabel("Tổng số đơn thuê: 0");
        tongDoanhThuLabel = new JLabel("Tổng doanh thu: 0 VNĐ");
        tongSoPanel.add(tongDonThueLabel);
        tongSoPanel.add(Box.createHorizontalStrut(20));
        tongSoPanel.add(tongDoanhThuLabel);

        // Panel chứa cả control và tổng số
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.CENTER);
        topPanel.add(tongSoPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Bảng thống kê
        String[] columns = {"Thời gian", "Số đơn thuê", "Doanh thu"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        thongKeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(thongKeTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<DonThue> donThueList = dataController.getAllDonThue();
        String selectedType = (String) loaiThongKeCombo.getSelectedItem();
        
        Map<String, ThongKeData> thongKeMap = new TreeMap<>();
        DateTimeFormatter formatter;

        switch (selectedType) {
            case "Theo ngày":
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (DonThue donThue : donThueList) {
                    String ngay = donThue.getNgayThue().format(formatter);
                    thongKeMap.computeIfAbsent(ngay, k -> new ThongKeData())
                            .addDonThue(donThue.getTongTien());
                }
                break;

            case "Theo tuần":
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (DonThue donThue : donThueList) {
                    LocalDate ngayThue = donThue.getNgayThue();
                    // Tính ngày đầu và cuối của tuần
                    LocalDate startOfWeek = ngayThue.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
                    LocalDate endOfWeek = startOfWeek.plusDays(6);
                    String tuan = String.format("%s - %s", 
                        startOfWeek.format(formatter),
                        endOfWeek.format(formatter));
                    thongKeMap.computeIfAbsent(tuan, k -> new ThongKeData())
                            .addDonThue(donThue.getTongTien());
                }
                break;

            case "Theo tháng":
                formatter = DateTimeFormatter.ofPattern("MM/yyyy");
                for (DonThue donThue : donThueList) {
                    String thang = donThue.getNgayThue().format(formatter);
                    thongKeMap.computeIfAbsent(thang, k -> new ThongKeData())
                            .addDonThue(donThue.getTongTien());
                }
                break;
        }

        // Cập nhật bảng
        int tongDonThue = 0;
        double tongDoanhThu = 0;

        for (Map.Entry<String, ThongKeData> entry : thongKeMap.entrySet()) {
            ThongKeData data = entry.getValue();
            tableModel.addRow(new Object[]{
                entry.getKey(),
                data.getSoDonThue(),
                String.format("%,.0f VNĐ", data.getDoanhThu())
            });
            tongDonThue += data.getSoDonThue();
            tongDoanhThu += data.getDoanhThu();
        }

        // Cập nhật tổng số
        tongDonThueLabel.setText(String.format("Tổng số đơn thuê: %d", tongDonThue));
        tongDoanhThuLabel.setText(String.format("Tổng doanh thu: %,.0f VNĐ", tongDoanhThu));
    }

    private static class ThongKeData {
        private int soDonThue;
        private double doanhThu;

        public void addDonThue(double tien) {
            soDonThue++;
            doanhThu += tien;
        }

        public int getSoDonThue() {
            return soDonThue;
        }

        public double getDoanhThu() {
            return doanhThu;
        }
    }
} 
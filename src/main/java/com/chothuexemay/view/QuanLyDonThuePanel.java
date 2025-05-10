package com.chothuexemay.view;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.model.DonThue;
import com.chothuexemay.model.User;
import com.chothuexemay.model.XeMay;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class QuanLyDonThuePanel extends JPanel {
    private DataController dataController;
    private JTable donThueTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JDateChooser fromDateChooser;
    private JDateChooser toDateChooser;
    private JComboBox<String> statusCombo;

    public QuanLyDonThuePanel(DataController dataController) {
        this.dataController = dataController;
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));

        // Tên khách hàng
        searchField = new JTextField(20);
        searchPanel.add(new JLabel("Tên khách hàng:"));
        searchPanel.add(searchField);

        // Từ ngày
        fromDateChooser = new JDateChooser();
        fromDateChooser.setPreferredSize(new Dimension(120, 25));
        searchPanel.add(new JLabel("Ngày thuê:"));
        searchPanel.add(fromDateChooser);

        // Đến ngày
        toDateChooser = new JDateChooser();
        toDateChooser.setPreferredSize(new Dimension(120, 25));
        searchPanel.add(new JLabel("Ngày trả:"));
        searchPanel.add(toDateChooser);

        // Trạng thái
        String[] statuses = {"Tất cả", "Đang thuê", "Đã trả", "Quá hạn"};
        statusCombo = new JComboBox<>(statuses);
        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(statusCombo);

        // Nút tìm kiếm
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> search());
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Bảng đơn thuê
        String[] columns = {"ID", "Tên khách hàng", "Số điện thoại", "Xe thuê", "Ngày thuê", "Ngày trả", "Trạng thái", "Tổng tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        donThueTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(donThueTable);
        add(scrollPane, BorderLayout.CENTER);

        // Thêm nút trả xe
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton traXeButton = new JButton("Trả xe");
        traXeButton.addActionListener(e -> traXe());
        buttonPanel.add(traXeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Thêm double click listener
        donThueTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    traXe();
                }
            }
        });
    }

    private void traXe() {
        int selectedRow = donThueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn thuê cần trả xe!");
            return;
        }

        // Kiểm tra trạng thái từ bảng
        String trangThai = (String) tableModel.getValueAt(selectedRow, 6);
        if ("Đã trả".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, 
                "Đơn thuê này đã được trả xe trước đó!\nKhông thể trả xe lần nữa.", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        DonThue donThue = dataController.getDonThueById(id);
        if (donThue == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy đơn thuê!");
            return;
        }

        if (donThue.isDaTra()) {
            JOptionPane.showMessageDialog(this, 
                "Đơn thuê này đã được trả xe trước đó!\nKhông thể trả xe lần nữa.", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận trả xe cho đơn thuê #" + id + "?",
            "Xác nhận trả xe",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Cập nhật trạng thái đơn thuê
                donThue.setDaTra(true);
                dataController.updateDonThue(donThue);

                // Cập nhật trạng thái xe
                XeMay xe = dataController.getXeMayByBienSo(donThue.getXeMayId());
                if (xe != null) {
                    xe.setDaThue(false);
                    dataController.updateXeMay(xe);
                }

                JOptionPane.showMessageDialog(this, "Trả xe thành công!");
                
                // Cập nhật trạng thái trên bảng ngay lập tức
                tableModel.setValueAt("Đã trả", selectedRow, 6);
                
                // Reload toàn bộ dữ liệu để đảm bảo đồng bộ
                loadData();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi trả xe: " + e.getMessage());
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<DonThue> donThueList = dataController.getAllDonThue();
        
        // Sort the list by return date in descending order
        donThueList.sort((d1, d2) -> d2.getNgayTra().compareTo(d1.getNgayTra()));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (DonThue donThue : donThueList) {
            User user = dataController.getUserById(donThue.getUserId());
            String status = getStatusText(donThue);
            
            Object[] rowData = {
                donThue.getId(),
                user != null ? user.getHoTen() : "N/A",
                user != null ? user.getSoDienThoai() : "N/A",
                donThue.getXeMayId(),
                donThue.getNgayThue().format(formatter),
                donThue.getNgayTra().format(formatter),
                status,
                donThue.getTongTien()
            };
            tableModel.addRow(rowData);
        }
    }

    private String getStatusText(DonThue donThue) {
        if (donThue == null) {
            return "N/A";
        }
        
        // Kiểm tra trạng thái đã trả trước
        if (donThue.isDaTra()) {
            return "Đã trả";
        }
        
        // Nếu chưa trả, kiểm tra quá hạn
        LocalDate now = LocalDate.now();
        if (donThue.getNgayTra().isBefore(now)) {
            return "Quá hạn";
        }
        
        return "Đang thuê";
    }

    private void search() {
        String searchName = searchField.getText().trim();
        LocalDate fromDate = fromDateChooser.getDate() != null ? 
            fromDateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate toDate = toDateChooser.getDate() != null ? 
            toDateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null;
        String selectedStatus = (String) statusCombo.getSelectedItem();

        List<DonThue> filteredList = dataController.getAllDonThue();

        // Lọc theo tên
        if (!searchName.isEmpty()) {
            filteredList = filteredList.stream()
                .filter(donThue -> {
                    User user = dataController.getUserById(donThue.getUserId());
                    return user != null && user.getHoTen().toLowerCase().contains(searchName.toLowerCase());
                })
                .collect(Collectors.toList());
        }

        // Lọc theo ngày thuê
        if (fromDate != null) {
            filteredList = filteredList.stream()
                .filter(donThue -> {
                    LocalDate ngayThue = donThue.getNgayThue();
                    return ngayThue.equals(fromDate);
                })
                .collect(Collectors.toList());
        }

        // Lọc theo ngày trả
        if (toDate != null) {
            filteredList = filteredList.stream()
                .filter(donThue -> {
                    LocalDate ngayTra = donThue.getNgayTra();
                    return ngayTra.equals(toDate);
                })
                .collect(Collectors.toList());
        }

        // Lọc theo trạng thái
        if (!selectedStatus.equals("Tất cả")) {
            filteredList = filteredList.stream()
                .filter(donThue -> {
                    String status = getStatusText(donThue);
                    return status.equals(selectedStatus);
                })
                .collect(Collectors.toList());
        }

        if (filteredList.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy đơn thuê nào phù hợp với điều kiện tìm kiếm!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        }

        updateTable(filteredList);
    }

    private void updateTable(List<DonThue> donThueList) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (DonThue donThue : donThueList) {
            User user = dataController.getUserById(donThue.getUserId());
            String status = getStatusText(donThue);
            tableModel.addRow(new Object[]{
                donThue.getId(),
                user != null ? user.getHoTen() : "N/A",
                user != null ? user.getSoDienThoai() : "N/A",
                donThue.getXeMayId(),
                donThue.getNgayThue().format(formatter),
                donThue.getNgayTra().format(formatter),
                status,
                donThue.getTongTien()
            });
        }
    }
} 
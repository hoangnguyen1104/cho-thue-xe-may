package com.chothuexemay.controller;

import com.chothuexemay.model.User;
import com.chothuexemay.model.XeMay;
import com.chothuexemay.model.DonThue;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class DataController {
    private DatabaseManager databaseManager;
    private List<User> users;
    private List<XeMay> xeMays;
    private List<DonThue> donThues;

    public DataController() {
        databaseManager = new DatabaseManager();
        loadData();
    }

    private void loadData() {
        users = databaseManager.getUsers();
        xeMays = databaseManager.getXeMays();
        donThues = databaseManager.getDonThues();

        // Thêm dữ liệu mẫu nếu chưa có
        if (users.isEmpty()) {
            User admin = new User(1, "admin@gmail.com", "admin123", "Admin", "admin", "0123456789");
            User user = new User(2, "user@gmail.com", "user123", "User", "user", "0987654321");
            databaseManager.addUser(admin);
            databaseManager.addUser(user);
            users.add(admin);
            users.add(user);
        }

        if (xeMays.isEmpty()) {
            XeMay xe1 = new XeMay("59A1-12345", "Honda Wave", "Honda", 80000);
            XeMay xe2 = new XeMay("59B2-67890", "Yamaha Sirius", "Yamaha", 90000);
            XeMay xe3 = new XeMay("59C3-11111", "Honda AirBlade", "Honda", 150000);
            databaseManager.addXeMay(xe1);
            databaseManager.addXeMay(xe2);
            databaseManager.addXeMay(xe3);
            xeMays.add(xe1);
            xeMays.add(xe2);
            xeMays.add(xe3);
        }
    }

    public List<User> getUsers() { return users; }
    public List<XeMay> getXeMays() { return xeMays; }
    public List<DonThue> getDonThues() { return donThues; }

    public boolean isPhoneNumberExists(String phoneNumber) {
        return users.stream()
                .anyMatch(user -> user.getSoDienThoai().equals(phoneNumber));
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return users.stream()
                .filter(user -> user.getSoDienThoai().equals(phoneNumber))
                .findFirst()
                .orElse(null);
    }

    public int addUser(User user) { 
        int id = databaseManager.addUser(user);
        if (id != -1) {
            user = new User(id, user.getUsername(), user.getPassword(), user.getHoTen(), user.getRole(), user.getSoDienThoai());
            users.add(user);
        }
        return id;
    }
    
    public void addXeMay(XeMay xe) { 
        databaseManager.addXeMay(xe);
        xeMays.add(xe);
    }
    
    public void addDonThue(DonThue don) { 
        databaseManager.addDonThue(don);
        donThues.add(don);
    }

    public void updateXeMay(XeMay xe) {
        databaseManager.updateXeMay(xe);
        for (int i = 0; i < xeMays.size(); i++) {
            if (xeMays.get(i).getBienSo().equals(xe.getBienSo())) {
                xeMays.set(i, xe);
                break;
            }
        }
    }

    public void deleteXeMay(String bienSo) {
        databaseManager.deleteXeMay(bienSo);
        xeMays.removeIf(xe -> xe.getBienSo().equals(bienSo));
    }

    public List<DonThue> getAllDonThue() {
        donThues = databaseManager.getDonThues();
        return new ArrayList<>(donThues);
    }

    public List<DonThue> searchDonThueByName(String name) {
        return donThues.stream()
                .filter(donThue -> {
                    User user = getUserById(donThue.getUserId());
                    return user != null && user.getHoTen().toLowerCase().contains(name.toLowerCase());
                })
                .collect(Collectors.toList());
    }

    public List<DonThue> searchDonThueByDateRange(LocalDate fromDate, LocalDate toDate) {
        return donThues.stream()
                .filter(donThue -> {
                    LocalDate ngayThue = donThue.getNgayThue();
                    return !ngayThue.isBefore(fromDate) && !ngayThue.isAfter(toDate);
                })
                .collect(Collectors.toList());
    }

    public List<DonThue> searchDonThueByStatus(String status) {
        LocalDate now = LocalDate.now();
        return donThues.stream()
                .filter(donThue -> {
                    switch (status) {
                        case "Đang thuê":
                            return !donThue.isDaTra() && !donThue.getNgayTra().isBefore(now);
                        case "Đã trả":
                            return donThue.isDaTra();
                        case "Quá hạn":
                            return !donThue.isDaTra() && donThue.getNgayTra().isBefore(now);
                        default:
                            return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public List<DonThue> getDonThueByDate(LocalDate date) {
        return donThues.stream()
                .filter(donThue -> donThue.getNgayThue().equals(date))
                .collect(Collectors.toList());
    }

    public User getUserById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public XeMay getXeMayByBienSo(String bienSo) {
        return xeMays.stream()
                .filter(xe -> xe.getBienSo().equals(bienSo))
                .findFirst()
                .orElse(null);
    }

    public DonThue getDonThueById(int id) {
        return databaseManager.getDonThueById(id);
    }

    public void updateDonThue(DonThue donThue) {
        databaseManager.updateDonThue(donThue);
    }

    public User authenticateUser(String username, String password) {
        return databaseManager.authenticateUser(username, password);
    }
} 
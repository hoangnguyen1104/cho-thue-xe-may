package com.chothuexemay.controller;

import com.chothuexemay.model.User;
import com.chothuexemay.model.XeMay;
import com.chothuexemay.model.DonThue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/chothuexemay?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";
    
    private final Connection connection;
    
    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            initializeDatabase();
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            createTablesIfNotExist();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }
    
    private void initializeDatabase() throws SQLException {
        try (Connection tempConn = DriverManager.getConnection("jdbc:mysql://localhost:3306", USER, PASSWORD);
             Statement stmt = tempConn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS chothuexemay");
        }
    }
    
    private void createTablesIfNotExist() throws SQLException {
        String[] createTableQueries = {
            """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(100) UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL,
                ho_ten VARCHAR(100) NOT NULL,
                role VARCHAR(20) NOT NULL,
                so_dien_thoai VARCHAR(15)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS xe_may (
                bien_so VARCHAR(20) PRIMARY KEY,
                ten_xe VARCHAR(100) NOT NULL,
                hang_xe VARCHAR(50) NOT NULL,
                gia_thue DOUBLE NOT NULL,
                da_thue BOOLEAN DEFAULT FALSE
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS don_thue (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                xe_may_id VARCHAR(20) NOT NULL,
                ngay_thue DATE NOT NULL,
                ngay_tra DATE NOT NULL,
                da_tra BOOLEAN DEFAULT FALSE,
                tong_tien DOUBLE NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (xe_may_id) REFERENCES xe_may(bien_so)
            )
            """
        };
        
        try (Statement stmt = connection.createStatement()) {
            for (String query : createTableQueries) {
                stmt.execute(query);
            }
        }
    }
    
    // User methods
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users: " + e.getMessage(), e);
        }
        return users;
    }
    
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("ho_ten"),
            rs.getString("role"),
            rs.getString("so_dien_thoai")
        );
    }
    
    public int addUser(User user) {
        String query = "INSERT INTO users (username, password, ho_ten, role, so_dien_thoai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getHoTen());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getSoDienThoai());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user: " + e.getMessage(), e);
        }
    }
    
    // XeMay methods
    public List<XeMay> getXeMays() {
        List<XeMay> xeMays = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM xe_may")) {
            while (rs.next()) {
                xeMays.add(createXeMayFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching motorcycles: " + e.getMessage(), e);
        }
        return xeMays;
    }
    
    private XeMay createXeMayFromResultSet(ResultSet rs) throws SQLException {
        XeMay xe = new XeMay(
            rs.getString("bien_so"),
            rs.getString("ten_xe"),
            rs.getString("hang_xe"),
            rs.getDouble("gia_thue")
        );
        xe.setDaThue(rs.getBoolean("da_thue"));
        return xe;
    }
    
    public void addXeMay(XeMay xe) {
        String query = "INSERT INTO xe_may (bien_so, ten_xe, hang_xe, gia_thue, da_thue) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, xe.getBienSo());
            pstmt.setString(2, xe.getTenXe());
            pstmt.setString(3, xe.getHangXe());
            pstmt.setDouble(4, xe.getGiaThue());
            pstmt.setBoolean(5, xe.isDaThue());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding motorcycle: " + e.getMessage(), e);
        }
    }
    
    public void updateXeMay(XeMay xe) {
        String query = "UPDATE xe_may SET ten_xe=?, hang_xe=?, gia_thue=?, da_thue=? WHERE bien_so=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, xe.getTenXe());
            pstmt.setString(2, xe.getHangXe());
            pstmt.setDouble(3, xe.getGiaThue());
            pstmt.setBoolean(4, xe.isDaThue());
            pstmt.setString(5, xe.getBienSo());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Motorcycle not found with license plate: " + xe.getBienSo());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating motorcycle: " + e.getMessage(), e);
        }
    }
    
    public void deleteXeMay(String bienSo) {
        String query = "DELETE FROM xe_may WHERE bien_so=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, bienSo);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Motorcycle not found with license plate: " + bienSo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting motorcycle: " + e.getMessage(), e);
        }
    }
    
    // DonThue methods
    public List<DonThue> getDonThues() {
        List<DonThue> donThues = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM don_thue")) {
            while (rs.next()) {
                donThues.add(createDonThueFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching rental orders: " + e.getMessage(), e);
        }
        return donThues;
    }
    
    private DonThue createDonThueFromResultSet(ResultSet rs) throws SQLException {
        return new DonThue(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("xe_may_id"),
            rs.getDate("ngay_thue").toLocalDate(),
            rs.getDate("ngay_tra").toLocalDate(),
            rs.getBoolean("da_tra"),
            rs.getDouble("tong_tien")
        );
    }
    
    public void addDonThue(DonThue don) {
        String query = "INSERT INTO don_thue (user_id, xe_may_id, ngay_thue, ngay_tra, da_tra, tong_tien) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, don.getUserId());
            pstmt.setString(2, don.getXeMayId());
            pstmt.setDate(3, Date.valueOf(don.getNgayThue()));
            pstmt.setDate(4, Date.valueOf(don.getNgayTra()));
            pstmt.setBoolean(5, don.isDaTra());
            pstmt.setDouble(6, don.getTongTien());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to create rental order");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    don.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Failed to get generated ID for rental order");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding rental order: " + e.getMessage(), e);
        }
    }

    public DonThue getDonThueById(int id) {
        String query = "SELECT * FROM don_thue WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createDonThueFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching rental order: " + e.getMessage(), e);
        }
    }

    public void updateDonThue(DonThue donThue) {
        String query = "UPDATE don_thue SET da_tra = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, donThue.isDaTra());
            pstmt.setInt(2, donThue.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Rental order not found with ID: " + donThue.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating rental order: " + e.getMessage(), e);
        }
    }

    public User authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error authenticating user: " + e.getMessage(), e);
        }
    }
} 
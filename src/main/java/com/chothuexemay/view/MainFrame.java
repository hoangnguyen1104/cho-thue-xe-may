package com.chothuexemay.view;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private User user;
    private DataController dataController;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;

    public MainFrame(User user, DataController dataController) {
        this.user = user;
        this.dataController = dataController;
        setTitle("Hệ thống cho thuê xe máy");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Chức năng");
        JMenuItem quanLyXeItem = new JMenuItem("Quản lý xe");
        JMenuItem thueXeItem = new JMenuItem("Thuê xe");
        JMenuItem thongKeItem = new JMenuItem("Thống kê");
        JMenuItem dangXuatItem = new JMenuItem("Đăng xuất");

        // Phân quyền
        if (user.getRole().equals("admin")) {
            menu.add(quanLyXeItem);
            menu.add(thongKeItem);
        }
        menu.add(thueXeItem);
        menu.add(dangXuatItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        tabbedPane = new JTabbedPane();
        
        // Thêm các tab dựa vào role của user
        if (user.getRole().equals("admin")) {
            tabbedPane.addTab("Quản lý xe", new QuanLyXePanel(dataController));
            tabbedPane.addTab("Quản lý đơn thuê", new QuanLyDonThuePanel(dataController));
            tabbedPane.addTab("Thống kê", new ThongKePanel(dataController));
        } else {
            tabbedPane.addTab("Thuê xe", new ThueXePanel(user, dataController));
        }

        add(tabbedPane);

        quanLyXeItem.addActionListener(e -> showQuanLyXePanel());
        thueXeItem.addActionListener(e -> showThueXePanel());
        thongKeItem.addActionListener(e -> showThongKePanel());
        dangXuatItem.addActionListener(e -> {
            dispose();
            new LoginFrame(dataController).setVisible(true);
        });
    }

    private void showQuanLyXePanel() {
        mainPanel.removeAll();
        mainPanel.add(new QuanLyXePanel(dataController), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showThueXePanel() {
        mainPanel.removeAll();
        mainPanel.add(new ThueXePanel(user, dataController), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showThongKePanel() {
        mainPanel.removeAll();
        mainPanel.add(new ThongKePanel(dataController), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
} 
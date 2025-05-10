package com.chothuexemay.model;

public class XeMay {
    private String bienSo;
    private String tenXe;
    private String hangXe;
    private double giaThue;
    private boolean daThue;

    public XeMay(String bienSo, String tenXe, String hangXe, double giaThue) {
        this.bienSo = bienSo;
        this.tenXe = tenXe;
        this.hangXe = hangXe;
        this.giaThue = giaThue;
        this.daThue = false;
    }

    public String getBienSo() {
        return bienSo;
    }

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe;
    }

    public String getHangXe() {
        return hangXe;
    }

    public void setHangXe(String hangXe) {
        this.hangXe = hangXe;
    }

    public double getGiaThue() {
        return giaThue;
    }

    public void setGiaThue(double giaThue) {
        this.giaThue = giaThue;
    }

    public boolean isDaThue() {
        return daThue;
    }

    public void setDaThue(boolean daThue) {
        this.daThue = daThue;
    }
} 
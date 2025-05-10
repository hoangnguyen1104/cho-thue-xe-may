package com.chothuexemay.model;

import java.time.LocalDate;

public class DonThue {
    private int id;
    private int userId;
    private String xeMayId;
    private LocalDate ngayThue;
    private LocalDate ngayTra;
    private boolean daTra;
    private double tongTien;

    public DonThue(int id, int userId, String xeMayId, LocalDate ngayThue, LocalDate ngayTra, boolean daTra, double tongTien) {
        this.id = id;
        this.userId = userId;
        this.xeMayId = xeMayId;
        this.ngayThue = ngayThue;
        this.ngayTra = ngayTra;
        this.daTra = daTra;
        this.tongTien = tongTien;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public String getXeMayId() {
        return xeMayId;
    }

    public LocalDate getNgayThue() {
        return ngayThue;
    }

    public LocalDate getNgayTra() {
        return ngayTra;
    }

    public boolean isDaTra() {
        return daTra;
    }

    public void setDaTra(boolean daTra) {
        this.daTra = daTra;
    }

    public double getTongTien() {
        return tongTien;
    }
} 
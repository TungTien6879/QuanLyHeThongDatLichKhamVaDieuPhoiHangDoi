package com.benhvien.quanlylichkham.service;

import org.springframework.stereotype.Component;

@Component("trangThaiHienThi")
public class TrangThaiHienThi {
    public String ten(String ma) {
        if (ma == null) return "Không xác định";
        return switch (ma) {
            case "CHO_XAC_NHAN" -> "Chờ xác nhận";
            case "DA_XAC_NHAN" -> "Đã xác nhận";
            case "DANG_KHAM" -> "Đang khám";
            case "DA_KHAM" -> "Đã khám";
            case "DA_HUY" -> "Đã hủy";
            default -> ma;
        };
    }
}

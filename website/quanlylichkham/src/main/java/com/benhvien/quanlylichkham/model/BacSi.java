package com.benhvien.quanlylichkham.model;
import jakarta.persistence.*;
@Entity @Table(name="bac_si")
public class BacSi {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @OneToOne(optional=false) @JoinColumn(name="tai_khoan_id",nullable=false,unique=true) public TaiKhoan taiKhoan;
 @Column(nullable=false,length=100) public String chuyenKhoa;
 @Column(nullable=false,length=100) public String phongKham;
 public BacSi() {}
 public BacSi(TaiKhoan t,String ck,String pk){taiKhoan=t;chuyenKhoa=ck;phongKham=pk;}
}

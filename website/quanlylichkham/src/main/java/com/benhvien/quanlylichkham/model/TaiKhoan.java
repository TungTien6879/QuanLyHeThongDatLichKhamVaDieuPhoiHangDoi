package com.benhvien.quanlylichkham.model;
import jakarta.persistence.*;
@Entity @Table(name="tai_khoan")
public class TaiKhoan {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true,length=150) public String email;
 @Column(nullable=false) public String matKhau;
 @Column(nullable=false,length=120) public String hoTen;
 @Column(nullable=false,length=20) public String vaiTro;
 public TaiKhoan() {}
 public TaiKhoan(String e,String p,String h,String v){email=e;matKhau=p;hoTen=h;vaiTro=v;}
}

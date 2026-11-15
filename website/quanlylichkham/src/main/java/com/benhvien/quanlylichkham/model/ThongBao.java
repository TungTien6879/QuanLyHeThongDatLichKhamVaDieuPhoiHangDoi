package com.benhvien.quanlylichkham.model;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="thong_bao")
public class ThongBao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public TaiKhoan nguoiNhan;
 @Column(nullable=false,length=500) public String noiDung;
 public LocalDateTime taoLuc=LocalDateTime.now();
 public ThongBao(){}
 public ThongBao(TaiKhoan u,String s){nguoiNhan=u;noiDung=s;}
}

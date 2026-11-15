package com.benhvien.quanlylichkham.model;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="lich_hen", uniqueConstraints=@UniqueConstraint(name="uq_bacsi_khunggio",columnNames={"bac_si_id","gio_kham"}))
public class LichHen {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) @JoinColumn(name="benh_nhan_id") public TaiKhoan benhNhan;
 @ManyToOne(optional=false) @JoinColumn(name="bac_si_id") public BacSi bacSi;
 @Column(name="gio_kham",nullable=false) public LocalDateTime gioKham;
 @Column(nullable=false,length=24) public String trangThai="CHO_XAC_NHAN";
 @Column(nullable=false) public boolean daCheckin=false;
 @Column(nullable=false) public boolean uuTien=false;
 @Column(length=250) public String lyDoUuTien;
 @ManyToOne public TaiKhoan nguoiXacNhan;
 public LocalDateTime gioXacNhan;
 public LocalDateTime gioCheckin;
 @Column(length=500) public String ghiChu;
 public LichHen() {}
 public LichHen(TaiKhoan bn,BacSi bs,LocalDateTime gio,String note){benhNhan=bn;bacSi=bs;gioKham=gio;ghiChu=note;}
}

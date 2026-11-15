package com.benhvien.quanlylichkham.service;
import com.benhvien.quanlylichkham.model.*;
import com.benhvien.quanlylichkham.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
@Service
public class NghiepVu {
 private final LichHenRepo lich; private final ThongBaoRepo tb;
 public NghiepVu(LichHenRepo l,ThongBaoRepo t){lich=l;tb=t;}
 public void thongBao(TaiKhoan u,String s){tb.save(new ThongBao(u,s));}
 @Transactional public LichHen dat(TaiKhoan u,BacSi bs,LocalDateTime gio,String ghiChu){
  if(gio.isBefore(LocalDateTime.now().plusMinutes(10))||gio.isAfter(LocalDateTime.now().plusMonths(3)))throw new IllegalArgumentException("Chon lich tu 10 phut sau hien tai den 3 thang toi.");
  if(gio.getMinute()%30!=0||gio.getSecond()!=0||gio.getHour()<7||gio.getHour()>=17)throw new IllegalArgumentException("Khung gio moi 30 phut, tu 07:00 den 16:30.");
  if(lich.existsByBacSiIdAndGioKham(bs.id,gio))throw new IllegalArgumentException("Bac si da co lich o khung gio nay.");
  LichHen h=lich.saveAndFlush(new LichHen(u,bs,gio,ghiChu));
  thongBao(u,"Da gui yeu cau dat lich #"+h.id+". Cho nhan vien xac nhan.");return h;
 }
 @Transactional public void doiTrangThai(LichHen h,String trangThai){h.trangThai=trangThai;lich.save(h);thongBao(h.benhNhan,"Lich #"+h.id+" chuyen sang trang thai: "+trangThai);}
 @Transactional public void checkIn(LichHen h){if(!"DA_XAC_NHAN".equals(h.trangThai))throw new IllegalArgumentException("Lich chua duoc xac nhan.");h.daCheckin=true;h.gioCheckin=LocalDateTime.now();lich.save(h);thongBao(h.benhNhan,"Da tiep nhan lich #"+h.id+". Theo doi hang doi tren trang ca nhan.");}
 @Transactional public void xacNhanUuTien(LichHen h,TaiKhoan nhanVien,boolean uuTien,String lyDo){
  if(!h.daCheckin||!"DA_XAC_NHAN".equals(h.trangThai))throw new IllegalArgumentException("Chi cap nhat uu tien khi da tiep nhan va chua kham.");
  if(uuTien&&(lyDo==null||lyDo.isBlank()))throw new IllegalArgumentException("Phai ghi ly do khi xac nhan uu tien.");
  h.uuTien=uuTien;h.lyDoUuTien=uuTien?lyDo.trim():null;h.nguoiXacNhan=nhanVien;h.gioXacNhan=LocalDateTime.now();lich.save(h);
  thongBao(h.benhNhan,"Nhan vien da cap nhat muc uu tien cho lich #"+h.id+".");
 }
 public List<LichHen> hangDoi(){List<LichHen> ds=new ArrayList<>(lich.findByDaCheckinTrueAndTrangThaiInOrderByGioCheckinAsc(List.of("DA_XAC_NHAN")));
  ds.sort(Comparator.comparing((LichHen h)->!h.uuTien).thenComparing(h->h.gioCheckin,Comparator.nullsLast(Comparator.naturalOrder())));return ds;}
 public int duKienPhut(LichHen x){int count=0;for(LichHen h:hangDoi()){if(h.id.equals(x.id))return count*15; if(h.bacSi.id.equals(x.bacSi.id))count++;}return -1;}
}

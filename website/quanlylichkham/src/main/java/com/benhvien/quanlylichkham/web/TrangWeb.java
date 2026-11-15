package com.benhvien.quanlylichkham.web;

import com.benhvien.quanlylichkham.model.*;
import com.benhvien.quanlylichkham.repo.*;
import com.benhvien.quanlylichkham.service.NghiepVu;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.*;
import java.util.*;

@Controller
public class TrangWeb {

  @org.springframework.web.bind.annotation.ModelAttribute
  public void trangThaiDangNhap(
      Model model,
      Authentication authentication) {

    boolean daDangNhap = authentication != null
        && authentication.isAuthenticated()
        && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);

    model.addAttribute("daDangNhap", daDangNhap);
  }

  private final TaiKhoanRepo taiKhoan;
  private final BacSiRepo bacSi;
  private final LichHenRepo lich;
  private final ThongBaoRepo tb;
  private final NghiepVu nv;
  private final PasswordEncoder encoder;

  public TrangWeb(TaiKhoanRepo t, BacSiRepo b, LichHenRepo l, ThongBaoRepo thong, NghiepVu n, PasswordEncoder e) {
    taiKhoan = t;
    bacSi = b;
    lich = l;
    tb = thong;
    nv = n;
    encoder = e;
  }

  private TaiKhoan me(Authentication a) {
    return taiKhoan.findByEmailIgnoreCase(a.getName()).orElseThrow();
  }

  private String done(RedirectAttributes r, String path, String msg) {
    r.addFlashAttribute("message", msg);
    return "redirect:" + path;
  }

  private String bad(RedirectAttributes r, String path, Exception ex) {
    r.addFlashAttribute("error", ex.getMessage() == null ? "Thao tac khong thanh cong" : ex.getMessage());
    return "redirect:" + path;
  }

  @GetMapping("/")
  String home(Model m, Authentication authentication) {
    m.addAttribute("doctors", bacSi.findAll());

    boolean daDangNhap = authentication != null
        && authentication.isAuthenticated()
        && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);

    m.addAttribute("daDangNhap", daDangNhap);

    return "index";
  }

  @GetMapping("/dang-nhap")
  String login() {
    return "dang-nhap";
  }

  @GetMapping("/cam-truy-cap")
  String denied() {
    return "cam-truy-cap";
  }

  @GetMapping("/dang-ky")
  String registration() {
    return "dang-ky";
  }

  @PostMapping("/dang-ky")
  String register(@RequestParam String hoTen, @RequestParam String email, @RequestParam String password,
      RedirectAttributes r) {
    try {
      if (hoTen.isBlank() || email.isBlank() || password.length() < 8 || !email.matches("^[^@ ]+@[^@ ]+\\.[^@ ]+$"))
        throw new IllegalArgumentException("Nhap ho ten, email hop le va mat khau toi thieu 8 ky tu.");
      if (taiKhoan.existsByEmailIgnoreCase(email))
        throw new IllegalArgumentException("Email da duoc su dung.");
      taiKhoan.save(new TaiKhoan(email.trim().toLowerCase(), encoder.encode(password), hoTen.trim(), "BENH_NHAN"));
      return done(r, "/dang-nhap", "Dang ky thanh cong. Hay dang nhap.");
    } catch (Exception e) {
      return bad(r, "/dang-ky", e);
    }
  }

  @GetMapping("/bang-dieu-khien")
  String dashboard(Authentication auth) {
    return switch (me(auth).vaiTro) {
      case "ADMIN" -> "redirect:/admin";
      case "NHAN_VIEN" -> "redirect:/nhan-vien";
      case "BAC_SI" -> "redirect:/bac-si";
      default -> "redirect:/lich-cua-toi";
    };
  }

  @GetMapping("/dat-lich")
  String booking(Model m) {
    m.addAttribute("doctors", bacSi.findAll());
    return "dat-lich";
  }

  @PostMapping("/dat-lich")
  String book(@RequestParam Long bacSiId, @RequestParam String gioKham, @RequestParam(defaultValue = "") String ghiChu,
      Authentication a, RedirectAttributes r) {
    try {
      nv.dat(me(a), bacSi.findById(bacSiId).orElseThrow(), LocalDateTime.parse(gioKham), ghiChu);
      return done(r, "/lich-cua-toi", "Da gui yeu cau dat lich.");
    } catch (DataIntegrityViolationException e) {
      return bad(r, "/dat-lich", new IllegalArgumentException("Khung gio vua duoc dat, hay chon gio khac."));
    } catch (Exception e) {
      return bad(r, "/dat-lich", e);
    }
  }

  @GetMapping("/lich-cua-toi")
  String mine(Model m, Authentication a) {
    TaiKhoan u = me(a);
    var ds = lich.findByBenhNhanEmailIgnoreCaseOrderByGioKhamDesc(u.email);
    m.addAttribute("appointments", ds);
    m.addAttribute("notifications", tb.findTop20ByNguoiNhanEmailIgnoreCaseOrderByTaoLucDesc(u.email));
    Map<Long, Integer> waits = new HashMap<>();
    for (var h : ds)
      waits.put(h.id, nv.duKienPhut(h));
    m.addAttribute("waits", waits);
    return "lich-cua-toi";
  }

  @PostMapping("/lich-cua-toi/{id}/huy")
  String cancel(@PathVariable Long id, Authentication a, RedirectAttributes r) {
    var h = lich.findById(id).orElseThrow();
    if (!h.benhNhan.id.equals(me(a).id) || !("CHO_XAC_NHAN".equals(h.trangThai) || "DA_XAC_NHAN".equals(h.trangThai))
        || h.daCheckin)
      return bad(r, "/lich-cua-toi", new IllegalArgumentException("Khong the huy lich nay."));
    nv.doiTrangThai(h, "DA_HUY");
    return done(r, "/lich-cua-toi", "Da huy lich.");
  }

  @GetMapping("/nhan-vien")
  String staff(Model m) {
    m.addAttribute("appointments", lich.findAllByOrderByGioKhamDesc());
    m.addAttribute("queue", nv.hangDoi());
    return "nhan-vien";
  }

  @PostMapping("/nhan-vien/{id}/xac-nhan")
  String confirm(@PathVariable Long id, RedirectAttributes r) {
    try {
      var h = lich.findById(id).orElseThrow();
      if (!"CHO_XAC_NHAN".equals(h.trangThai))
        throw new IllegalArgumentException("Lich khong o trang thai cho.");
      nv.doiTrangThai(h, "DA_XAC_NHAN");
      return done(r, "/nhan-vien", "Da xac nhan lich.");
    } catch (Exception e) {
      return bad(r, "/nhan-vien", e);
    }
  }

  @PostMapping("/nhan-vien/{id}/check-in")
  String checkin(@PathVariable Long id, RedirectAttributes r) {
    try {
      nv.checkIn(lich.findById(id).orElseThrow());
      return done(r, "/nhan-vien", "Da tiep nhan benh nhan.");
    } catch (Exception e) {
      return bad(r, "/nhan-vien", e);
    }
  }

  @PostMapping("/nhan-vien/{id}/uu-tien")
  String priority(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean uuTien,
      @RequestParam(defaultValue = "") String lyDo, Authentication a, RedirectAttributes r) {
    try {
      nv.xacNhanUuTien(lich.findById(id).orElseThrow(), me(a), uuTien, lyDo);
      return done(r, "/nhan-vien", "Da luu muc uu tien do nhan vien xac nhan.");
    } catch (Exception e) {
      return bad(r, "/nhan-vien", e);
    }
  }

  @GetMapping("/bac-si")
  String doctor(Model m, Authentication a) {
    var b = bacSi.findByTaiKhoanEmailIgnoreCase(a.getName());
    m.addAttribute("appointments", b.map(x -> lich.findByBacSiIdOrderByGioKhamAsc(x.id)).orElse(List.of()));
    return "bac-si";
  }

  @PostMapping("/bac-si/{id}/bat-dau")
  String start(@PathVariable Long id, Authentication a, RedirectAttributes r) {
    try {
      var h = lich.findById(id).orElseThrow();
      if (!h.bacSi.taiKhoan.email.equalsIgnoreCase(a.getName()) || !h.daCheckin || !"DA_XAC_NHAN".equals(h.trangThai))
        throw new IllegalArgumentException("Lich nay khong du dieu kien goi kham.");
      nv.doiTrangThai(h, "DANG_KHAM");
      return done(r, "/bac-si", "Da goi benh nhan.");
    } catch (Exception e) {
      return bad(r, "/bac-si", e);
    }
  }

  @PostMapping("/bac-si/{id}/hoan-tat")
  String finish(@PathVariable Long id, Authentication a, RedirectAttributes r) {
    try {
      var h = lich.findById(id).orElseThrow();
      if (!h.bacSi.taiKhoan.email.equalsIgnoreCase(a.getName()) || !"DANG_KHAM".equals(h.trangThai))
        throw new IllegalArgumentException("Lich chua trong qua trinh kham.");
      nv.doiTrangThai(h, "DA_KHAM");
      return done(r, "/bac-si", "Da hoan tat kham.");
    } catch (Exception e) {
      return bad(r, "/bac-si", e);
    }
  }

  @GetMapping("/admin")
  String admin(Model m) {
    m.addAttribute("users", taiKhoan.findAll());
    m.addAttribute("doctors", bacSi.findAll());
    m.addAttribute("appointmentCount", lich.count());
    return "admin";
  }

  @PostMapping("/admin/tao-tai-khoan")
  String addUser(@RequestParam String hoTen, @RequestParam String email, @RequestParam String password,
      @RequestParam String vaiTro, @RequestParam(defaultValue = "") String chuyenKhoa,
      @RequestParam(defaultValue = "") String phongKham, RedirectAttributes r) {
    try {
      if (!List.of("BAC_SI", "NHAN_VIEN").contains(vaiTro))
        throw new IllegalArgumentException("Vai tro khong hop le.");
      if (password.length() < 10 || hoTen.isBlank() || !email.matches("^[^@ ]+@[^@ ]+\\.[^@ ]+$"))
        throw new IllegalArgumentException("Nhap ten, email va mat khau it nhat 10 ky tu.");
      if (taiKhoan.existsByEmailIgnoreCase(email))
        throw new IllegalArgumentException("Email da ton tai.");
      if (vaiTro.equals("BAC_SI") && (chuyenKhoa.isBlank() || phongKham.isBlank()))
        throw new IllegalArgumentException("Bac si can co chuyen khoa va phong kham.");
      var u = taiKhoan.save(new TaiKhoan(email.trim().toLowerCase(), encoder.encode(password), hoTen.trim(), vaiTro));
      if (vaiTro.equals("BAC_SI"))
        bacSi.save(new BacSi(u, chuyenKhoa.trim(), phongKham.trim()));
      return done(r, "/admin", "Da tao tai khoan.");
    } catch (Exception e) {
      return bad(r, "/admin", e);
    }
  }
}

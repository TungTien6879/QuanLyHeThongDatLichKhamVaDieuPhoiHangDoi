package com.benhvien.quanlylichkham.repo;
import com.benhvien.quanlylichkham.model.*;
import org.springframework.data.jpa.repository.*;
import java.time.*;
import java.util.*;
public interface LichHenRepo extends JpaRepository<LichHen,Long> {
 List<LichHen> findByBenhNhanEmailIgnoreCaseOrderByGioKhamDesc(String email);
 List<LichHen> findByBacSiIdOrderByGioKhamAsc(Long id);
 List<LichHen> findAllByOrderByGioKhamDesc();
 boolean existsByBacSiIdAndGioKham(Long bs,LocalDateTime gio);
 List<LichHen> findByDaCheckinTrueAndTrangThaiInOrderByGioCheckinAsc(Collection<String> states);
}

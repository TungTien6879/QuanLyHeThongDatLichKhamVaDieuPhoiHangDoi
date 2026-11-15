package com.benhvien.quanlylichkham.repo;
import com.benhvien.quanlylichkham.model.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ThongBaoRepo extends JpaRepository<ThongBao,Long>{ List<ThongBao> findTop20ByNguoiNhanEmailIgnoreCaseOrderByTaoLucDesc(String e); }

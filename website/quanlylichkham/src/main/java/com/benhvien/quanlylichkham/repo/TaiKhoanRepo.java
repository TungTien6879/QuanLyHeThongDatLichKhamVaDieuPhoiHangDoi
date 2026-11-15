package com.benhvien.quanlylichkham.repo;
import com.benhvien.quanlylichkham.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface TaiKhoanRepo extends JpaRepository<TaiKhoan,Long> { Optional<TaiKhoan> findByEmailIgnoreCase(String email); boolean existsByEmailIgnoreCase(String email); }

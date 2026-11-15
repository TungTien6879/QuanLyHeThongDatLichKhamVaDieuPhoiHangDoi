package com.benhvien.quanlylichkham.repo;
import com.benhvien.quanlylichkham.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BacSiRepo extends JpaRepository<BacSi,Long> { Optional<BacSi> findByTaiKhoanEmailIgnoreCase(String email); }

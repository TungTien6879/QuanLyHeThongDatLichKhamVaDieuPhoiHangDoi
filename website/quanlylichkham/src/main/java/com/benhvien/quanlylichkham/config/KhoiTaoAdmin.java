package com.benhvien.quanlylichkham.config;
import com.benhvien.quanlylichkham.model.TaiKhoan;
import com.benhvien.quanlylichkham.repo.TaiKhoanRepo;
import org.springframework.boot.*;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class KhoiTaoAdmin {
 @Bean CommandLineRunner taoAdmin(TaiKhoanRepo repo,PasswordEncoder encoder){return args->{
  String email=System.getenv("ADMIN_EMAIL"),pass=System.getenv("ADMIN_PASSWORD");
  if(email!=null&&!email.isBlank()&&pass!=null&&pass.length()>=10&&!repo.existsByEmailIgnoreCase(email)){
   repo.save(new TaiKhoan(email.trim().toLowerCase(),encoder.encode(pass),"Quan tri he thong","ADMIN"));
   System.out.println("Da tao tai khoan quan tri tu bien moi truong ADMIN_EMAIL.");
  }
 };}
}

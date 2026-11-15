package com.benhvien.quanlylichkham.config;
import com.benhvien.quanlylichkham.repo.TaiKhoanRepo;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import java.util.List;
@Configuration
public class BaoMat {
 @Bean PasswordEncoder encoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService users(TaiKhoanRepo repo){return email -> {var u=repo.findByEmailIgnoreCase(email).orElseThrow(()->new UsernameNotFoundException("Khong tim thay tai khoan"));return new User(u.email,u.matKhau,List.of(new SimpleGrantedAuthority("ROLE_"+u.vaiTro)));};}
 @Bean SecurityFilterChain chain(HttpSecurity http)throws Exception{
  return http.authorizeHttpRequests(a->a
   .requestMatchers("/","/dang-ky","/dang-nhap","/css/**","/favicon.ico","/error").permitAll()
   .requestMatchers("/admin/**").hasRole("ADMIN")
   .requestMatchers("/nhan-vien/**").hasAnyRole("NHAN_VIEN","ADMIN")
   .requestMatchers("/bac-si/**").hasRole("BAC_SI")
   .requestMatchers("/dat-lich/**","/lich-cua-toi/**").hasRole("BENH_NHAN")
   .anyRequest().authenticated())
   .formLogin(f->f.loginPage("/dang-nhap").loginProcessingUrl("/dang-nhap").usernameParameter("email").passwordParameter("password").defaultSuccessUrl("/bang-dieu-khien",true).permitAll())
   .logout(l->l.logoutUrl("/dang-xuat").logoutSuccessUrl("/?logout"))
   .exceptionHandling(e->e.accessDeniedPage("/cam-truy-cap"))
   .build();
 }
}

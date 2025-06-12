package hahaha.service;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import hahaha.model.Account;
import hahaha.model.KhachHang;
import hahaha.model.RoleGroup;
import hahaha.repository.AccountRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.repository.RoleGroupRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private RoleGroupRepository roleGroupRepository;

   @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Kiểm tra tài khoản đã tồn tại
        Account account = accountRepository.findAccountByEmail(email);
        if (account == null) {
            // Kiểm tra KhachHang có tồn tại không
            KhachHang khachHang = khachHangRepository.findByEmail(email);
            // Nếu chưa tồn tại thì tạo mới
            if (khachHang == null) {
                khachHang = new KhachHang();
                khachHang.setMaKH(khachHangService.generateNextMaKH());
                khachHang.setEmail(email);
                khachHang.setHoTen(name != null ? name : "No Name");
                khachHang.setSoDienThoai("");
                khachHang.setDiaChi("");
                khachHang.setReferralCode(khachHangService.generateNextReferralCode());
                khachHang = khachHangRepository.save(khachHang);
            }

            // Nếu đã có account thì lấy lại từ khachHang
            if (khachHang.getAccount() != null) {
                account = khachHang.getAccount();
            } else {
                // Tạo mới account
                account = new Account();
                account.setUserName(email);
                account.setPasswordHash("google-auth");
                account.setIsDeleted(0);
                account.setCreatedAt(LocalDateTime.now());
                account.setUpdatedAt(LocalDateTime.now());
                account.setStatus("ACTIVE");
                account.setKhachHang(khachHang);

                RoleGroup roleGroup = roleGroupRepository.findById(3L).orElseThrow(() -> 
                    new OAuth2AuthenticationException("ROLE_USER không tồn tại"));
                account.setRoleGroup(roleGroup);

                account = accountRepository.save(account);

                // Gán lại vào khachHang nếu cần
                khachHang.setAccount(account);
                khachHangRepository.save(khachHang);
            }
        }

        // Trả về OAuth2User
        String roleGroupName = account.getRoleGroup().getNameRoleGroup();
        System.out.println(">>ROLE: "+ roleGroupName);
        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleGroupName)),
            oAuth2User.getAttributes(),
            "email"
        );
    }

}


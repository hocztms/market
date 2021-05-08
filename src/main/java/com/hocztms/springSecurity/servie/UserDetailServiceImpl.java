package com.hocztms.springSecurity.servie;

import com.hocztms.entity.Role;
import com.hocztms.entity.Users;
import com.hocztms.service.UserService;
import com.hocztms.springSecurity.entity.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users usersByUsername = userService.findUsersByUsername(username);
        if (usersByUsername==null){
            throw new RuntimeException("用户不存在");
        }
        if (usersByUsername.getStatus()==0){
            throw new RuntimeException("账户已冻结");
        }

        List<Role> roles = userService.getUserRoles(username);
        List<GrantedAuthority> authoritys = new ArrayList<>();

        for (Role role:roles){
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getRole());
            authoritys.add(simpleGrantedAuthority);
        }

        return new MyUserDetails(username,usersByUsername.getPassword(),authoritys);
    }
}

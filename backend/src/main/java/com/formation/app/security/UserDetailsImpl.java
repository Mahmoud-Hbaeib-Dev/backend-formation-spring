package com.formation.app.security;

import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implémentation de UserDetails pour Spring Security
 */
@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {
    
    private User user;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertir le rôle en GrantedAuthority
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRoles().name()));
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getLogin();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    /**
     * Obtient le rôle de l'utilisateur
     */
    public Role getRole() {
        return user.getRoles();
    }
    
    /**
     * Obtient l'ID de l'utilisateur
     */
    public String getUserId() {
        return user.getId();
    }
}


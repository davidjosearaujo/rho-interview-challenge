package com.example.rho_interview_challenge.userDocument;

import java.util.Collection;
import java.util.Collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mongodb.lang.NonNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Document
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    private String id;

    @NonNull
    private String userName;

    @NonNull
    private String password;

    public User(String id, String userName, String password) {
        super();
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    // Methods required by Spring Security for user details
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsername'");
    }

}

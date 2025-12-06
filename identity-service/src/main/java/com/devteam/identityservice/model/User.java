package com.devteam.identityservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "USERS")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

//    @Column(name = "PROFILE_PICTURE", nullable = false)
//    private String profilePicture = "https://www.gravatar.com/avatar/";

//    @Column(name = "PHONE_NUMBER", nullable = false, unique = true)
//    private String phoneNumber;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "IS_ENABLED", nullable = false)
    private boolean enabled = true;

    @Column(name = "IS_LOCKED", nullable = false)
    private boolean locked = false;

    @Column(name = "IS_CREDENTIALS_EXPIRED", nullable = false)
    private boolean expired = false;

    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean active = false;

    @Column(name = "CREDENTIALS_EXPIRED", nullable = false)
    private boolean credentialsExpired = false;

//    @Column(name = "IS_PHONE_VERIFIED", nullable = false)
//    private boolean phoneVerified = false;

    @Column(name = "IS_EMAIL_VERIFIED", nullable = false)
    private boolean emailVerified = false;

    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE", insertable = false)
    private LocalDateTime lastModifiedDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) return List.of();
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

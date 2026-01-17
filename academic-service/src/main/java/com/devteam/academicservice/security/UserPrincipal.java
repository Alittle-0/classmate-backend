package com.devteam.academicservice.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Represents the authenticated user principal in the security context.
 * This class holds user information extracted from HTTP headers sent by the API Gateway.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal {
    
    private String userId;
    private String email;
    private String firstname;
    private String lastname;
    private String role;
    
    /**
     * Converts comma-separated roles string to Spring Security authorities
     * @return Collection of GrantedAuthority
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_"+this.role));
    }
    
    /**
     * Get full name of the user
     * @return firstname + lastname
     */
    public String getFullName() {
        if (firstname == null && lastname == null) {
            return email;
        }
        return String.format("%s %s", 
            firstname != null ? firstname : "", 
            lastname != null ? lastname : "").trim();
    }
}

package com.devteam.academicservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class to easily access current authenticated user information.
 * This provides a convenient way to get user details throughout the application.
 */
public class SecurityUtils {
    
    /**
     * Get the current authenticated user principal
     * @return UserPrincipal or null if not authenticated
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        
        return null;
    }
    
    /**
     * Check if user is authenticated
     * @return true if user is authenticated
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
}

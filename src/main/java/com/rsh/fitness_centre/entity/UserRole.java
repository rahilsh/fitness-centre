package com.rsh.fitness_centre.entity;

/**
 * Enumeration of user roles for role-based access control (RBAC).
 */
public enum UserRole {
    /**
     * Anonymous user - no authentication required.
     * Can access public endpoints only.
     */
    ANONYMOUS("ROLE_ANONYMOUS"),
    
    /**
     * Regular user - can book slots and view fitness centres.
     */
    USER("ROLE_USER"),
    
    /**
     * Admin user - can manage fitness centres and slots.
     */
    ADMIN("ROLE_ADMIN"),
    
    /**
     * Super admin - full system access.
     */
    SUPER_ADMIN("ROLE_SUPER_ADMIN");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}

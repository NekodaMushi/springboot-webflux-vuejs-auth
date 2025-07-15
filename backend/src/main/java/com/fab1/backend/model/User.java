package com.fab1.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.annotation.Transient;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Table("users")
public class User implements UserDetails {

    @Id
    private Long id;

    private String username;
    private String password;
    @Builder.Default
    private boolean enabled = true;

    @Transient
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.enabled = true;
        this.roles = new HashSet<>();
    }

    public User withRole(Role role) {
        Set<Role> newRoles = new HashSet<>(this.roles);
        newRoles.add(role);
        return User.builder()
                .id(this.id)
                .username(this.username)
                .password(this.password)
                .enabled(this.enabled)
                .roles(newRoles)
                .build();
    }

    public User withoutRole(Role role) {
        Set<Role> newRoles = new HashSet<>(this.roles);
        newRoles.remove(role);
        return User.builder()
                .id(this.id)
                .username(this.username)
                .password(this.password)
                .enabled(this.enabled)
                .roles(newRoles)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
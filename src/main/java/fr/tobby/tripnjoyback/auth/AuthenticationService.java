package fr.tobby.tripnjoyback.auth;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class AuthenticationService implements UserDetailsService {

    private final UserRepository repository;

    public AuthenticationService(final UserRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException
    {
        UserEntity user = repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(
                user.getEmail(),
                user.getPassword(),
                user.getRoles());
    }
}

class UserDetailsImpl implements UserDetails
{
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> roles;

    public UserDetailsImpl(final String username, final String password,
                           final Collection<? extends GrantedAuthority> roles)
    {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Collections.unmodifiableCollection(roles);
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
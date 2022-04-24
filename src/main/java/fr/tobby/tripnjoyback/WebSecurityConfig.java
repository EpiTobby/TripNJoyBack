package fr.tobby.tripnjoyback;

import fr.tobby.tripnjoyback.auth.AuthenticationService;
import fr.tobby.tripnjoyback.auth.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationService service;
    private final JwtFilter jwtFilter;

    public WebSecurityConfig(final JwtFilter jwtFilter)
    {
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception
    {
        http.authorizeHttpRequests()
            .antMatchers("/auth/**").permitAll()
            .antMatchers("/auth/{id}/updatepassword").authenticated()
            .antMatchers("/auth/{id}/updateemail").authenticated()
            .antMatchers("/swagger-ui.html/**").permitAll()
            .antMatchers("/swagger-ui/**").permitAll()
            .antMatchers("/v2/api-docs").permitAll()
            .antMatchers("/v3/api-docs/**").permitAll()
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/actuator/**").permitAll()
            // FIXME: remove below permissions
            .antMatchers("/index.html").permitAll()
            .antMatchers("/app.js").permitAll()
            .antMatchers("/main.css").permitAll()
            .antMatchers("/webjars/**").permitAll()
            .antMatchers("/chat/**").permitAll()
            .antMatchers("/wbsocket/**").permitAll()
            .antMatchers("/wbsocket").permitAll()
            .anyRequest().permitAll()
            //                .anyRequest().authenticated()
            .and()
            // Do not persist session. Auth is done via jwt and checked at each request
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .csrf().disable();
        // jwt filter will authenticate the user at each request if jwt is valid
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected UserDetailsService userDetailsService()
    {
        return service;
    }

    @Bean
    public PasswordEncoder encoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "myAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }
}

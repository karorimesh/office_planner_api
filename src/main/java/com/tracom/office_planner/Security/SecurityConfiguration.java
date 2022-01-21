package com.tracom.office_planner.Security;
/*
General security configuration class
 */

import com.tracom.office_planner.Api.CustomAuthenticationFilter;
import com.tracom.office_planner.Api.CustomAuthorizationFilter;
import com.tracom.office_planner.User.CustomUserService;
import com.tracom.office_planner.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginFailure loginFailure;
    @Autowired
    private LoginSuccess loginSuccess;
    @Autowired
    private UserRepository userRepo;



    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider  = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    // TODO: 12/29/2021 Do more polishing of the ant matchers
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter =  new CustomAuthenticationFilter(authenticationManagerBean());
        http    .csrf().disable();
        http    .sessionManagement().sessionCreationPolicy(STATELESS);
        http    .authorizeRequests()
                /*API ant matchers*/
                .antMatchers(HttpMethod.GET,"/api/meeting/**","/api/meetings","/api/boardrooms","/api/boardroom/**",
                        "/api/user/**","/api/users").authenticated()
                .antMatchers(HttpMethod.POST,"/api/meeting/save").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/meeting/delete/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/meeting/update/**","/api/user/edit/**").authenticated()
                .antMatchers(HttpMethod.GET,"/api/user").hasAuthority("admin")
                .antMatchers(HttpMethod.POST,"/api/user/save","/api/boardroom/save").hasAuthority("admin")
                .antMatchers(HttpMethod.DELETE,"/api/boardroom/delete/**","/api/user/delete/**").hasAuthority("admin")
                .antMatchers(HttpMethod.PUT,"/api/boardroom/edit/**").hasAuthority("admin")
                /* End of API ant matchers */
                /* Browser Ant matchers */
                .antMatchers(HttpMethod.GET,
                        "/edit_user/**","/add_user",
                        "/new_board",
                        "/edit/**",
                        "/reschedule/**")
                .hasAuthority("admin")
                .antMatchers(HttpMethod.DELETE,"/delete_user/**", "/delete_meet/**", "/delete_board/**")
                .hasAuthority("admin")
                .antMatchers("/list_users","/new_meet",
                        "/meeting/**","/home",
                        "/boardroom","/my_meeting",
                        "/boardroom/page/**","meeting/page/**",
                        "/my_meeting/page/**","/list_users/page/**",
                        "/calendar","/my_profile")
                .authenticated()
                /*
                End of browser ant matchers
                 */
                .anyRequest()
                .permitAll();
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .usernameParameter("username")
//                .failureHandler(loginFailure)
//                .successHandler(loginSuccess)
//                .defaultSuccessUrl("/home")
//                .permitAll()
//                .and()
//                .logout()
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    Principal principal = request.getUserPrincipal();
//                    String name = principal.getName();
//                    User user = userRepo.findUserByName(name);
//                    PlannerLogger.loggedOutUser(user);
//                })
//                .logoutSuccessUrl("/landing")
//                .permitAll()
//                .and()
//                .exceptionHandling()
//                .accessDeniedPage("/403");
        http.   addFilter(customAuthenticationFilter);
        http.   addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/js/**");
        web.ignoring().antMatchers("/images/**");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

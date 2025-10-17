package project.iw3.iw3.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
   @Bean
   SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// CORS: https://developer.mozilla.org/es/docs/Web/HTTP/CORS
		// CSRF: https://developer.mozilla.org/es/docs/Glossary/CSRF
		http.cors(CorsConfigurer::disable);
		http.csrf(AbstractHttpConfigurer::disable);
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/**").permitAll()
			    .anyRequest().authenticated()
		);
		return http.build();
   }
}

/*
 function sumauno(a) {
 	return a+1;
 }

 sumauno= a-> { return a+1; }
 */

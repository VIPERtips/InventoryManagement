package com.tips.InventoryManagement.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class SecurityConfig {
	
	public static String  hashPassword(String password) throws NoSuchAlgorithmException  {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
		byte[] hashBytes = mDigest.digest(password.getBytes(StandardCharsets.UTF_8));
		
		return bytesToHex(hashBytes);
		
	}
	/*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/resources/**").permitAll() 
                .anyRequest().authenticated() 
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .defaultSuccessUrl("/dashboard", true) 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // Redirect to login page after logout
                .permitAll()
            );

        return http.build();
    }*/

	private static String bytesToHex(byte[] hashBytes) {
		StringBuilder hexString = new StringBuilder();
		for(byte b : hashBytes) {
			String hex = Integer.toHexString(0xff & b);
			if(hex.length() == 1) {
				hexString.append("0");
				
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
	
	public static boolean verifyPassword(String inputPassword,String storedHashedPassword) throws NoSuchAlgorithmException {
		String hashedInputPassword = hashPassword(inputPassword);
		return hashedInputPassword.equals(storedHashedPassword);
		
	}
}

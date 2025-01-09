package com.nathanlucas.nscommerce;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class NscommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NscommerceApplication.class, args);
	}

}

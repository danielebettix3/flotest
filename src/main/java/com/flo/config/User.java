package com.flo.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "user")
@Data
public class User
{

	String username;
	String password;
	List<String> authorities;
}

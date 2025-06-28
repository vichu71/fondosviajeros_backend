package com.vhs.fondosviajeros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@SpringBootApplication
//@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class FondosviajerosBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FondosviajerosBackendApplication.class, args);
	}

}

package ru.practicum.shareit;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(
		info = @Info(
				title = "ShareIt API definition",
				version = "0.0.1",
				description = "REST API of items short time share service"
			)
		)
@SpringBootApplication
public class ShareItGateway {
	public static void main(String[] args) {
		SpringApplication.run(ShareItGateway.class, args);
	}

}

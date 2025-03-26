package task_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	
	
	    @Bean
	    public OpenAPI openAPI() {
	        return new OpenAPI()
	            .info(new Info()
	                .title("Task Management API")
	                .version("1.0")
	                .description("REST API for managing tasks, users, and roles with JWT authentication."));
	    }
	}



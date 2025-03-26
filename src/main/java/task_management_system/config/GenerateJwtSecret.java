package task_management_system.config;

import java.util.Base64;

import javax.crypto.SecretKey;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class GenerateJwtSecret {
	
	
	 public static void main(String[] args) {
	        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
	        System.out.println("Generated JWT Secret: " + base64Key);
	    }
	}



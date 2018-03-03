package com.ioannisgk.evsharing.utils;

import java.net.Socket;

import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.stereotype.Component;

// We use jasypt for encryption/decryption, we need to install the policy jar file to the JVM
// Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7 Download

@Component
public class StrongTextEncryptorHelper {
	
	// Class attributes
	private StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
	private BasicTextEncryptor basicEncryptor = new BasicTextEncryptor();
	
	// Class constructor
	public StrongTextEncryptorHelper() {
		textEncryptor.setPassword("evsharingPassEncryptor");
		basicEncryptor.setPassword("evsharingPassEncryptor");
	}
	
	// Method to encrypt a password
	
	public String encryptPassword(String plainPassword) {
		
		return textEncryptor.encrypt(plainPassword);
	}
	
	// Method to decrypt a password
	
	public String decryptPassword(String encryptedPassword) {
		
		return textEncryptor.decrypt(encryptedPassword);
	}
	
	// Method to encrypt a password for Android app use
	
	public String encryptPasswordBasic(String plainPassword) {
			
		return basicEncryptor.encrypt(plainPassword);
	}
		
	// Method to decrypt a password for Android app use
		
	public String decryptPasswordBasic(String encryptedPassword) {
			
		return basicEncryptor.decrypt(encryptedPassword);
	}
}
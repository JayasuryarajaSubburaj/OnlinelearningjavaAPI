package com.onlinelearning.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class SignUpEntity {

	@Id
	private String id;
	private String firstName;
	private String lastName;
	//private String address;
	private List<Address> addresses=new ArrayList<>();; 
	
	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses =new ArrayList<>();
	}

	private String gender;
	private String dateofbirth;
	private String phoneNumber;
	private String email;
	private String password;

	private String generatedId;

	private String resetToken;
	private LocalDateTime tokenExpiryDate;
	private String newPassword;
	
	//Arun useremail,userpassword
	 private String userEmail;
	 private String userPassword;

	// Getters and Setters

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getfirstName() {
		return firstName;
	}

	public void setfirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getlastName() {
		return lastName;
	}

	public void setlastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateofbirth() {
		return dateofbirth;
	}

	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	public String getphoneNumber() {
		return phoneNumber;
	}

	public void setphoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static String Generatedid() {
		String generatedId = UUID.randomUUID().toString();
		return generatedId;
	}
////
//	public String getGeneratedId() {
//		return Generatedid();
//	}

	public String getgeneratedId() {
		return generatedId;
	}

	public void setGeneratedId(String generatedId) {
		generatedId = generatedId;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public LocalDateTime getTokenExpiryDate() {
		return tokenExpiryDate;
	}

	public void setTokenExpiryDate(LocalDateTime tokenExpiryDate) {
		this.tokenExpiryDate = tokenExpiryDate;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	// Default Constructor

	public SignUpEntity() {
		this.generatedId = Generatedid();
	}
	
	public static class Address {
		
		private String id = UUID.randomUUID().toString();
	    private String doorNo;
	    private String street;
	    private String city;
	    private String state;
	    private String country;
	    
	    // Getters, Setters, Constructors omitted for brevity...
	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }
		public String getDoorNo() {
			return doorNo;
		}
		public void setDoorNo(String doorNo) {
			this.doorNo = doorNo;
		}
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
	}

}

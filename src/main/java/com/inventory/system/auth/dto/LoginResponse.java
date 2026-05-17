package com.inventory.system.auth.dto;

public class LoginResponse {

	private String token;
	private String name;
	private Long id;
	private String email;
	private String role;
    private String sessionId;

    public LoginResponse(
            String token,
            String name,
            Long id,
            String email,
            String role,
            String sessionId
    ) {

        this.token = token;
        this.name = name;
        this.id = id;
        this.email = email;
        this.role = role;
        this.sessionId = sessionId;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
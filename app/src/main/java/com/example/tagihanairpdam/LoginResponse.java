package com.example.tagihanairpdam;

public class LoginResponse {
    private String message;
    private User user;

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter and Setter for user
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Nested class for User
    public static class User {
        private String id;
        private String name;
        private String username;
        private String role;
        private String email;
        private String phone;
        private String address;
        private boolean isFirstLogin;

        // Getter and Setter for id
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        // Getter and Setter for name
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // Getter and Setter for username
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        // Getter and Setter for role
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        // Getter and Setter for email
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        // Getter and Setter for phone
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        // Getter and Setter for address
        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        // Getter and Setter for isFirstLogin
        public boolean isFirstLogin() {
            return isFirstLogin;
        }

        public void setFirstLogin(boolean firstLogin) {
            isFirstLogin = firstLogin;
        }
    }
}
package vttp.project.Models;

import jakarta.validation.constraints.NotNull;

public class Login {

    @NotNull(message="Username cannot be null")
    String username;

    @NotNull(message="Password cannot be null")
    String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Login [username=" + username + ", password=" + password + "]";
    }

    
    
}

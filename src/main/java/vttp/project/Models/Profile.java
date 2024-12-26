package vttp.project.Models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Profile {

    @NotNull(message="Username cannot be null")
    String username;

    @NotNull(message="Password cannot be null")
    @Size(min=5, max=32, message="password must be between 5 and 32 characters")
    String password;

    @NotNull(message="Gender cannot be null")
    String gender;

    @NotNull(message="Age cannot be null")
    @Min(value=10, message="Age cannot be less than 10yrs")
    @Max(value=120, message="Age cannot be more than 120yrs")
    Integer age;

    @NotNull(message="Height cannot be null")
    @Min(value=60, message="Height cannot be below 60cm")
    @Max(value=270, message="Height cannot be above 270cm")
    Integer height;

    @NotNull(message="Weight cannot be null")
    @Min(value=30, message="Weight cannot be below 30kg")
    @Max(value=500, message="Weight cannot be above 500kg")
    Integer weight;


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

    

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Profile [username=" + username + ", password=" + password + ", gender=" + gender + ", age=" + age
                + ", height=" + height + ", weight=" + weight + "]";
    }

    

    
    
    
}

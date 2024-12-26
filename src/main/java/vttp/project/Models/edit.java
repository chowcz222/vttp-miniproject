package vttp.project.Models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class edit {

    @NotNull(message="Age cannot be null")
    @Min(value=10, message="Age cannot be less than 10yrs")
    @Max(value=120, message="Age cannot be more than 120yrs")
    private int age;

    @NotNull(message="Height cannot be null")
    @Min(value=60, message="Height cannot be below 60cm")
    @Max(value=270, message="Height cannot be above 270cm")
    private int height;

    @NotNull(message="Weight cannot be null")
    @Min(value=30, message="Weight cannot be below 30kg")
    @Max(value=500, message="Weight cannot be above 500kg")
    private int weight;


    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    
    
}

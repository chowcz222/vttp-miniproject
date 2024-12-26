package vttp.project.Models;

import java.util.LinkedList;
import java.util.List;

public class dish {

    private String dishName;
	private List<Ingredients> contents = new LinkedList<>();
    private int totalCalorie;

    public void addContent(Ingredients ingredient) { this.contents.add(ingredient); }

    public List<Ingredients> getContents() { return this.contents; }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public int getTotalCalorie() {
        return totalCalorie;
    }

    public void setTotalCalorie(int totalCalorie) {
        this.totalCalorie = totalCalorie;
    }

    @Override
    public String toString() {
        return "dish [dishName=" + dishName + ", contents=" + contents + ", totalCalorie=" + totalCalorie + "]";
    }

    
    
}

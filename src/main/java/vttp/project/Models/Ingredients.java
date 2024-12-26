package vttp.project.Models;

public class Ingredients {

    private String name;
	private int quantity;
	public String unit;
	public int calorie;

	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }

	public void setQuantity(int quantity) { this.quantity = quantity; }
	public int getQuantity() { return this.quantity; }

	public void setCalorie(int calorie) { this.calorie = calorie; }
	public int getCalorie() { return this.calorie; }




	@Override
	public String toString() {
		return "Ingredients{name=%s, quantity=%d, calorie=%d}".formatted(name, quantity, calorie);
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
    
}

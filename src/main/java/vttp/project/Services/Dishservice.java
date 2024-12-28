package vttp.project.Services;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import vttp.project.Models.Ingredients;
import vttp.project.Models.dish;
import vttp.project.Repositries.Dishrepositry;

@Service
public class Dishservice {

    public static final String CALORIE_URL = "https://api.calorieninjas.com/v1/nutrition?query=";

    @Autowired
    private Dishrepositry dishrepo;

    //Retrieving Dish information

    public String getDishinstruction(String dishName) {

        String instructions= dishrepo.retrieveDishinstruction(dishName);

        return instructions;
    }

    public dish getDishingredients(String dishName) {

        dish dishinfo = dishrepo.retrieveDishingredients(dishName);
        
        return dishinfo;

    }

    public int getDishCalorie(String dishName) {

        return dishrepo.getCaloriefromRepo(dishName);
    }

    //Calling api to get individual calories for each ingredient

    public Integer getCalorieCount(String name, int quantity, String unit) {

        int calorie = 1;

        if(unit.equals("tbsp") || unit.equals("pieces")){
            unit = "";
        }

        String url = CALORIE_URL + String.valueOf(quantity) + unit + " " + name;

        RequestEntity<Void> req = RequestEntity
            .get(url)
            .header("X-Api-Key", "q/Yd+dDllx1n02Mwfui2Iw==uhadRLmEn0tRAqo1")
            .accept(MediaType.APPLICATION_JSON)
            .build();
        
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject result = reader.readObject();
        JsonArray items = result.getJsonArray("items");
        if (!items.isEmpty()) {
            JsonObject firstItem = items.getJsonObject(0);

            JsonValue calorieValue = firstItem.get("calories");
            
            calorie = ((JsonNumber) calorieValue).intValue();
        } else {
            calorie = 0;
        }
        
        return calorie;
    }


    //Storing, checking existence of, deleting dish information

    public void storeDishInstruction(String dishname, String instruction) {

        dishrepo.storeInstruction(dishname, instruction);
    }

    public void storeDishIngredients(String dishname, dish dishinfo) {

        dishrepo.storeIngredients(dishname, dishinfo);
    }

    public void storeDishCalorie(String dishname, int calorie) {

        dishrepo.storeCalorie(dishname, calorie);
    }

    public void storeBasedonUser(String dishname, String username) {

        dishrepo.storedishUser(dishname, username);
    }

    public void deleteDish(String dishname, String username) {

        dishrepo.deleteDishfromRepo(dishname, username);
    }

    public boolean checkNameExists(String dishname) {

        return dishrepo.checkNameExistinRepo(dishname);
    }


    //Services for Rest Controllers

    public List<String> getDishesUnderCalories(int calorie) {
        Map<String, Integer> allDishes = dishrepo.getAllDishandCalories();
        return allDishes.entrySet().stream()
                .filter(entry -> entry.getValue() < calorie)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public String getDishRecipe(String dishName) {

        String instructions = dishrepo.retrieveDishinstruction(dishName);
        dish dishInfo = dishrepo.retrieveDishingredients(dishName);

        if (instructions == null) {
            return null;
        }

        JsonArrayBuilder ingredientJson = Json.createArrayBuilder();
        for (Ingredients ingredient : dishInfo.getContents()) {
            ingredientJson.add(Json.createObjectBuilder()
                    .add("name", ingredient.getName())
                    .add("quantity", ingredient.getQuantity())
                    .add("calorie", ingredient.getCalorie()));
        }

        JsonObject dishRecipeJson = Json.createObjectBuilder()
                .add("Instructions", instructions)
                .add("Ingredient list", ingredientJson.build())
                .build();


        return dishRecipeJson.toString();

    }


    
}

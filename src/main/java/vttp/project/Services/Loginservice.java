package vttp.project.Services;

import java.io.StringReader;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp.project.Models.edit;
import vttp.project.Repositries.Loginrepositry;

@Service
public class Loginservice {

    @Autowired
    private Loginrepositry loginrepo;

    public static final String API_URL = "https://nutrition-calculator.p.rapidapi.com/api/nutrition-info?";

    public boolean hasUsername(String username) {

        if(loginrepo.hasUserinRepo(username)){
            return true;
        } else {
            return false;
        }
    }

    public String getPassword(String username) {

        return loginrepo.getPasswordfromRepo(username);

    }

    public Integer getCalories(String username){

        String details = loginrepo.getUserDetails(username);

        JsonReader reader = Json.createReader(new StringReader(details));
        JsonObject result = reader.readObject();
        int totalcalorie = Integer.parseInt(result.getString("calorie"));

        int caloriePerMeal = totalcalorie-500;
        
        
        return caloriePerMeal;


    }


    public Map<String, Integer> getDishandCalories() {
        return loginrepo.getdishList();
    }

    public void storeUserDetails(String username, String gender, Integer age,
                                    Integer height, Integer weight) {

        String url = UriComponentsBuilder
            .fromUriString(API_URL)
            .queryParam("measurement_units", "met")
            .queryParam("sex", gender)
            .queryParam("age_value", age)
            .queryParam("age_type","yrs")
            .queryParam("cm",height)
            .queryParam("kilos",weight)
            .toUriString();

        RequestEntity<Void> req = RequestEntity
            .get(url)
            .header("x-rapidapi-key", "26ce2f63f3msh6202ec34fe0509dp1b144djsnd224bfb07138")
		    .header("x-rapidapi-host", "nutrition-calculator.p.rapidapi.com")
            .accept(MediaType.APPLICATION_JSON)
            .build();
        
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        String payload = resp.getBody();
        String decodedPayload = payload.replaceAll("\\\\u003b", ";");
        decodedPayload = decodedPayload.replaceAll("\\\\", "");
        JsonReader reader = Json.createReader(new StringReader(decodedPayload));
        JsonObject result = reader.readObject();
        JsonObject bmi = result.getJsonObject("BMI_EER");
        String calorieJson = bmi.getString("Estimated Daily Caloric Needs");
        String numericCalorieValue = calorieJson.replaceAll("[^0-9]", "");

        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        objBuilder.add("gender", gender)
          .add("age", age)
          .add("height", height)
          .add("weight", weight)
          .add("calorie", numericCalorieValue);

        JsonObject userDetails = objBuilder.build();  // Build the JsonObject

        String userDetailsString = userDetails.toString();


        System.out.println(userDetailsString);

        loginrepo.storeUserinRepo(username, userDetailsString);

    }

    public void storeLoginDetails(String username, String password) {

        loginrepo.storeLogininRepo(username,password);
    }


    public edit getHeightWeightAge(String username){

        String details = loginrepo.getUserDetails(username);

        JsonReader reader = Json.createReader(new StringReader(details));
        JsonObject result = reader.readObject();
        int age = result.getInt("age");
        int height = result.getInt("height");
        int weight = result.getInt("weight");
        
        return new edit(age, height, weight);
    }

    public void saveProfile(String username, int age, int height, int weight) {

        String details = loginrepo.getUserDetails(username);

        JsonReader reader = Json.createReader(new StringReader(details));
        JsonObject result = reader.readObject();

        String url = UriComponentsBuilder
            .fromUriString(API_URL)
            .queryParam("measurement_units", "met")
            .queryParam("sex", result.getString("gender"))
            .queryParam("age_value", age)
            .queryParam("age_type","yrs")
            .queryParam("cm",height)
            .queryParam("kilos",weight)
            .toUriString();

        RequestEntity<Void> req = RequestEntity
            .get(url)
            .header("x-rapidapi-key", "26ce2f63f3msh6202ec34fe0509dp1b144djsnd224bfb07138")
		    .header("x-rapidapi-host", "nutrition-calculator.p.rapidapi.com")
            .accept(MediaType.APPLICATION_JSON)
            .build();
        
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        String payload = resp.getBody();
        String decodedPayload = payload.replaceAll("\\\\u003b", ";");
        decodedPayload = decodedPayload.replaceAll("\\\\", "");
        JsonReader reader1 = Json.createReader(new StringReader(decodedPayload));
        JsonObject result1 = reader1.readObject();
        JsonObject bmi = result1.getJsonObject("BMI_EER");
        String calorieJson = bmi.getString("Estimated Daily Caloric Needs");
        String numericCalorieValue = calorieJson.replaceAll("[^0-9]", "");

        JsonObjectBuilder updatedBuilder = Json.createObjectBuilder(result);
        updatedBuilder.add("age", age);
        updatedBuilder.add("height", height);
        updatedBuilder.add("weight", weight);
        updatedBuilder.add("calorie", numericCalorieValue);

        JsonObject updatedResult = updatedBuilder.build();

        String updatedDetails = updatedResult.toString();

        loginrepo.storeUserinRepo(username, updatedDetails);
    }

    
    
}

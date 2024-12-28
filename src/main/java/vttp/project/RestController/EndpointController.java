package vttp.project.RestController;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp.project.Services.Dishservice;
import vttp.project.Services.Loginservice;

@RestController
@RequestMapping("/api")
public class EndpointController {

    @Autowired
    private Loginservice loginsvc;

    @Autowired
    private Dishservice dishsvc;

    @GetMapping("/dishlist/{username}")
     public ResponseEntity<List<String>> getUserDishes(@PathVariable String username) {
        List<String> dishes = loginsvc.getUserdishesfromRepo(username);
        if (dishes == null || dishes.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/under/{calorie}")
    public ResponseEntity<List<String>> getDishesUnderCalories(@PathVariable int calorie) {

        List<String> dishList = dishsvc.getDishesUnderCalories(calorie);

        if (dishList == null || dishList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        return ResponseEntity.ok(dishList);
    }

    @GetMapping("/recipe/{dishname}")
    public ResponseEntity<String> getDishInfo(@PathVariable String dishname) {

        String dishRecipe = dishsvc.getDishRecipe(dishname);

        if (dishRecipe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        return ResponseEntity.ok(dishRecipe);
    }


    
}

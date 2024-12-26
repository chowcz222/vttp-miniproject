package vttp.project.Controller;

import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpSession;
import vttp.project.Models.Ingredients;
import vttp.project.Models.dish;
import vttp.project.Services.Dishservice;

@Controller
public class DishesControler {

    @Autowired
    private Dishservice dishsvc;

    @GetMapping("/dishes/{name}")
    public String getDishPage(@PathVariable String name, Model model) throws JsonMappingException, JsonProcessingException {

        String instruction = dishsvc.getDishinstruction(name);
        instruction = instruction.replace("\\r\\n", "\n").replace("\\n", "\n");
        

        dish ingredientList = dishsvc.getDishingredients(name);

        model.addAttribute("name", name);
        model.addAttribute("dish", ingredientList);
        model.addAttribute("instruction", instruction);

        return "dish-info";
    }

    @GetMapping("/dishcreationpage")
    public String displaycreatedish(HttpSession session, Model model) {

        dish dishInfo = (dish)session.getAttribute("newDish");

        if (dishInfo == null) {
            model.addAttribute("dishinfo", new dish());
        } else {
            model.addAttribute("dishinfo", dishInfo);
        }
        Integer totalcalorie = (Integer)session.getAttribute("dishcalorie");

        if (totalcalorie == null || totalcalorie == 0) {
            model.addAttribute("dishcalorie", 0);
        } else {
            model.addAttribute("dishcalorie", totalcalorie);
        }
            model.addAttribute("Ingredients", new Ingredients());

        return "createdish";
    }

    @PostMapping("/addIngredients")
    public String addIngredientsinDish(@ModelAttribute("Ingredients") Ingredients ingredient, Model model, HttpSession session) {

        dish dishInfo = (dish)session.getAttribute("newDish");
        Integer totalcalorie = (Integer)session.getAttribute("dishcalorie");

        if (dishInfo == null) {
            dishInfo = new dish();  
            session.setAttribute("newDish", dishInfo); 
        }

        if (totalcalorie == null) {
            totalcalorie = 0; 
            session.setAttribute("dishcalorie", totalcalorie); 
        }

        int calorie = dishsvc.getCalorieCount(ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());

        if(calorie == 0) {
            model.addAttribute("errorMessage", "Ingredient not recognized");
            model.addAttribute("dishinfo", dishInfo);
            return "createdish";
        } else {
            model.addAttribute("errorMessage", "");
            ingredient.setCalorie(calorie);
            totalcalorie += calorie;
            dishInfo.addContent(ingredient);
            session.setAttribute("newDish", dishInfo);
            session.setAttribute("dishcalorie", totalcalorie);

            model.addAttribute("dishcalorie", totalcalorie);

            model.addAttribute("dishinfo", dishInfo);

            return "createdish";
        }

    }

    @PostMapping("/postdish")
    public String saveDish(@RequestParam("dishname") String dishName, 
                            @RequestParam("instruction") String instruction,Model model, HttpSession session) {

        dish dishInfo = (dish)session.getAttribute("newDish");
        Integer calorie = (Integer)session.getAttribute("dishcalorie");

        session.setAttribute("newDish", new dish());
        session.setAttribute("dishcalorie", 0);

        dishsvc.storeDishInstruction(dishName, instruction);
        dishsvc.storeDishIngredients(dishName, dishInfo);
        dishsvc.storeDishCalorie(dishName, calorie);

        model.addAttribute("dishname", dishName);

        return"posted";
    }

    @PostMapping("/deleteIngredient")
    public String deleteIngredient( @RequestParam("ingredientName") String ingredientName,
    @RequestParam("ingredientQuantity") int ingredientQuantity,
    @RequestParam("ingredientUnit") String ingredientUnit, HttpSession session, Model model) {
        dish dishInfo = (dish) session.getAttribute("newDish");

        if (dishInfo != null) {

            int removedCalorie = 0;
            Iterator<Ingredients> iterator = dishInfo.getContents().iterator();
            while (iterator.hasNext()) {
            Ingredients ingredient = iterator.next();
            if (ingredient.getName().equals(ingredientName) &&
                ingredient.getQuantity() == ingredientQuantity &&
                ingredient.getUnit().equals(ingredientUnit)) {
                iterator.remove();
                removedCalorie += ingredient.getCalorie();
            }
        }
            int totalcalorie = (int)session.getAttribute("dishcalorie");
            int newCalorie = totalcalorie - removedCalorie;
            
            session.setAttribute("newDish", dishInfo);
            session.setAttribute("dishcalorie", newCalorie);
        }

    return "redirect:/dishcreationpage";
}
    
}

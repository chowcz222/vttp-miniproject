package vttp.project.Controller;

import java.util.Iterator;

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
public class DishesController {

    @Autowired
    private Dishservice dishsvc;

    //Display dishinfo from mainpage Controller

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

    //Dish Creation page controller

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

    @PostMapping("/deleteIngredient")
    public String deleteIngredient( @RequestParam("ingredientName") String ingredientName,
    @RequestParam("ingredientQuantity") int ingredientQuantity,
    @RequestParam("ingredientUnit") String ingredientUnit, HttpSession session, Model model) {
        dish dishInfo = (dish) session.getAttribute("newDish");

        if (dishInfo != null) {
            int removedCalorie = 0;
            boolean itemRemoved = false;
        
            Iterator<Ingredients> iterator = dishInfo.getContents().iterator();
            while (iterator.hasNext()) {
                Ingredients ingredient = iterator.next();
                if (!itemRemoved && ingredient.getName().equals(ingredientName) &&
                    ingredient.getQuantity() == ingredientQuantity &&
                    ingredient.getUnit().equals(ingredientUnit)) {
                    
                    iterator.remove();
                    removedCalorie += ingredient.getCalorie();
                    itemRemoved = true;
                }
            }
        
            if (itemRemoved) {
                int totalCalorie = (int) session.getAttribute("dishcalorie");
                int newCalorie = totalCalorie - removedCalorie;
        
                session.setAttribute("newDish", dishInfo);
                session.setAttribute("dishcalorie", newCalorie);
            }
        }
        

        return "redirect:/dishcreationpage";
    }

    @PostMapping("/postdish")
    public String saveDish(@RequestParam("dishname") String dishName, 
                            @RequestParam("instruction") String instruction,Model model, HttpSession session) {

        Boolean boolean1 = dishsvc.checkNameExists(dishName);

        if(boolean1) {

            model.addAttribute("instruction", instruction);
            model.addAttribute("dishname", dishName);

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

            model.addAttribute("errorMessage1", "Dish Name Taken");

            return "createdish";

        }

        dish dishInfo = (dish)session.getAttribute("newDish");
        Integer calorie = (Integer)session.getAttribute("dishcalorie");

        session.setAttribute("newDish", new dish());
        session.setAttribute("dishcalorie", 0);

        dishsvc.storeDishInstruction(dishName, instruction);
        dishsvc.storeDishIngredients(dishName, dishInfo);
        dishsvc.storeDishCalorie(dishName, calorie);
        dishsvc.storeBasedonUser(dishName,(String)session.getAttribute("username"));

        model.addAttribute("dishname", dishName);

        return"posted";
    }


    //Editing and deleteing dishes from edit profile page controllers

    @PostMapping("/deleteDish")
    public String deleteDish(@RequestParam("dishName") String dishName, HttpSession session) {

        dishsvc.deleteDish(dishName, (String)session.getAttribute("username"));

        return"redirect:/editprofile";

    }

    @GetMapping("/editdishes/{name}")
    public String editDish(@PathVariable String name, Model model, HttpSession session) {

        String instruction = dishsvc.getDishinstruction(name);
        instruction = instruction.replace("\\r\\n", "\n").replace("\\n", "\n");
        
        dish ingredientList = dishsvc.getDishingredients(name);
        int calorie = dishsvc.getDishCalorie(name);

        model.addAttribute("dishname", name);
        model.addAttribute("dishinfo", ingredientList);
        model.addAttribute("instruction", instruction);
        model.addAttribute("dishcalorie", calorie);

        session.setAttribute("newDish2", ingredientList);
        session.setAttribute("dishcalorie2", calorie);
        session.setAttribute("dishName2", name);
        session.setAttribute("dishInstruction2", instruction);
 
        return"editdish";
    }

    @PostMapping("/deleteIngredient2")
    public String deleteIngredient2( @RequestParam("ingredientName") String ingredientName,
    @RequestParam("ingredientQuantity") int ingredientQuantity,
    @RequestParam("ingredientUnit") String ingredientUnit, HttpSession session, Model model) {
        dish dishInfo = (dish) session.getAttribute("newDish2");

        if (dishInfo != null) {
            int removedCalorie = 0;
            boolean itemRemoved = false;
        
            Iterator<Ingredients> iterator = dishInfo.getContents().iterator();
            while (iterator.hasNext()) {
                Ingredients ingredient = iterator.next();
                if (!itemRemoved && ingredient.getName().equals(ingredientName) &&
                    ingredient.getQuantity() == ingredientQuantity &&
                    ingredient.getUnit().equals(ingredientUnit)) {
                    
                    iterator.remove();
                    removedCalorie += ingredient.getCalorie();
                    itemRemoved = true;
                }
            }
        
            if (itemRemoved) {
                int totalCalorie = (int) session.getAttribute("dishcalorie2");
                int newCalorie = totalCalorie - removedCalorie;
        
                session.setAttribute("newDish2", dishInfo);
                session.setAttribute("dishcalorie2", newCalorie);
                model.addAttribute("dishcalorie", newCalorie);
            }
        }
        

        model.addAttribute("dishname", (String)session.getAttribute("dishName2"));
        model.addAttribute("dishinfo", dishInfo);
        model.addAttribute("instruction", (String)session.getAttribute("dishInstruction2"));

        return"editdish";
    }

    @PostMapping("/addIngredients2")
    public String addIngredientsinDish2(@ModelAttribute("Ingredients") Ingredients ingredient, Model model, HttpSession session) {

        dish dishInfo = (dish)session.getAttribute("newDish2");
        Integer totalcalorie = (Integer)session.getAttribute("dishcalorie2");

        if (dishInfo == null) {
            dishInfo = new dish();  
            session.setAttribute("newDish2", dishInfo); 
        }

        if (totalcalorie == null) {
            totalcalorie = 0; 
            session.setAttribute("dishcalorie2", totalcalorie); 
        }

        int calorie = dishsvc.getCalorieCount(ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());

        if(calorie == 0) {
            model.addAttribute("errorMessage", "Ingredient not recognized");
            model.addAttribute("dishinfo", dishInfo);
            model.addAttribute("dishname", (String)session.getAttribute("dishName2"));
            model.addAttribute("instruction", (String)session.getAttribute("dishInstruction2"));
            model.addAttribute("dishcalorie", (int)session.getAttribute("dishcalorie2"));
            return "editdish";
        } else {
            model.addAttribute("errorMessage", "");
            ingredient.setCalorie(calorie);
            totalcalorie += calorie;
            dishInfo.addContent(ingredient);
            session.setAttribute("newDish2", dishInfo);
            session.setAttribute("dishcalorie2", totalcalorie);

            model.addAttribute("dishcalorie", totalcalorie);

            model.addAttribute("dishinfo", dishInfo);
            model.addAttribute("dishname", (String)session.getAttribute("dishName2"));
            model.addAttribute("instruction", (String)session.getAttribute("dishInstruction2"));

            return "editdish";
        }
    }

    @PostMapping("/saveedit")
    public String saveEdit(@RequestParam("dishname") String dishName, 
                            @RequestParam("instruction") String instruction,Model model, HttpSession session) {

        dishsvc.deleteDish((String)session.getAttribute("dishName2"), (String)session.getAttribute("username"));

        Boolean boolean1 = dishsvc.checkNameExists(dishName);

        if(boolean1) {

            model.addAttribute("instruction", instruction);
            model.addAttribute("dishname", dishName);

            dish dishInfo = (dish)session.getAttribute("newDish2");

            if (dishInfo == null) {
                model.addAttribute("dishinfo", new dish());
            } else {
                model.addAttribute("dishinfo", dishInfo);
            }
            Integer totalcalorie = (Integer)session.getAttribute("dishcalorie2");

            if (totalcalorie == null || totalcalorie == 0) {
                model.addAttribute("dishcalorie", 0);
            } else {
                model.addAttribute("dishcalorie", totalcalorie);
            }
                model.addAttribute("Ingredients", new Ingredients());

            model.addAttribute("errorMessage1", "Dish Name Taken");

            return "editdish";

        }

        dish dishInfo = (dish)session.getAttribute("newDish2");
        Integer calorie = (Integer)session.getAttribute("dishcalorie2");

        session.setAttribute("newDish2", new dish());
        session.setAttribute("dishcalorie2", 0);

        dishsvc.storeDishInstruction(dishName, instruction);
        dishsvc.storeDishIngredients(dishName, dishInfo);
        dishsvc.storeDishCalorie(dishName, calorie);
        dishsvc.storeBasedonUser(dishName,(String)session.getAttribute("username"));

        model.addAttribute("dishname", dishName);

        return"edited";
    }


    
}

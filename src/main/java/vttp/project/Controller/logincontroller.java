package vttp.project.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.project.Models.Profile;
import vttp.project.Models.edit;
import vttp.project.Services.Loginservice;

@Controller
@RequestMapping
public class logincontroller {

    @Autowired
    private Loginservice loginsvc;

    @GetMapping("/loginpage")
    public String getloginpage(Model model) {

        model.addAttribute("login", new Profile());

        return"login";
    }

    @GetMapping("/login")
    public String getverification(@ModelAttribute("login") Profile loginDetails, Model model, HttpSession session) {

        if (!loginsvc.hasUsername(loginDetails.getUsername())) {
            model.addAttribute("errorMessage", "User does not exist!");
            return "login";
        }

        String password = loginsvc.getPassword(loginDetails.getUsername());

        if(password.equals(loginDetails.getPassword())) {

            session.setAttribute("username", loginDetails.getUsername() );
            return "redirect:/loginmainpage";

        } else {
            model.addAttribute("errorMessage", "Invalid Password!");
            return "login";
        }
    }

    @GetMapping("/createpage")
    public String getCreatepage(Model model) {
        model.addAttribute("profile", new Profile());
        return"createAcc";
    }

    @PostMapping("/create")
    public String storeUserDetails( @Valid @ModelAttribute("profile") Profile profiledetails, BindingResult bindings, Model model, HttpSession session) {

        if (bindings.hasErrors()) 
            return "createAcc";

        if (loginsvc.hasUsername(profiledetails.getUsername())) {
            model.addAttribute("errorMessage", "Username already exists!");
            return "createAcc";
        }

        loginsvc.storeUserDetails(profiledetails.getUsername(), profiledetails.getGender(), profiledetails.getAge(), profiledetails.getHeight(),
                                    profiledetails.getWeight());
        
        loginsvc.storeLoginDetails(profiledetails.getUsername(), profiledetails.getPassword());

        
        session.setAttribute("username", profiledetails.getUsername() );
        return"redirect:/loginmainpage";
    }





    @GetMapping("/loginmainpage")
    public String displayMainpage(@RequestParam(value = "meals", defaultValue = "3") int meals, Model model, HttpSession session) {

        String username = session.getAttribute("username").toString();
        session.setAttribute("dishcalorie", 0);

        Map<String, Integer> dishList = loginsvc.getDishandCalories();

        int calorie = loginsvc.getCalories(username);
        int adjustedcalorie = calorie/meals;

        List<Map.Entry<String, Integer>> sortedDishList = dishList.entrySet()
            .stream()
            .filter(entry -> entry.getValue() <= adjustedcalorie) 
            .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
            .collect(Collectors.toList());

        model.addAttribute("dishList", sortedDishList);
        model.addAttribute("calorieIntake", calorie);
        model.addAttribute("mealCalorie", adjustedcalorie);
        model.addAttribute("meals", meals);

        return"mainpage";
    }

    @GetMapping("/editprofile")
    public String showEditProfilePage(Model model, HttpSession session) {

        edit profileDetails = loginsvc.getHeightWeightAge((String)session.getAttribute("username"));
        model.addAttribute("name", (String)session.getAttribute("username"));
        model.addAttribute("age", profileDetails.getAge());
        model.addAttribute("height", profileDetails.getHeight());
        model.addAttribute("weight", profileDetails.getWeight());
        return "profile";
    }

    @PostMapping("/storeprofile")
    public String saveNewProfile(@RequestParam("age") int age, @RequestParam("height") int height, @RequestParam("weight") int weight, HttpSession session ) {


        loginsvc.saveProfile((String)session.getAttribute("username"), age, height, weight);

        return"saved";
    }

    @PostMapping("/logout")
    public String userLogout(HttpSession session) {

        session.invalidate();

        return"redirect:/index.html";
    }


    
}

package vttp.project.Repositries;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.project.Models.dish;

@Repository
public class Dishrepositry {
    
    @Autowired @Qualifier("redis-object2")
    RedisTemplate<String, Object> redisTemplate;

    public String retrieveDishinstruction(String dishName) {

        Object instruction = redisTemplate.opsForHash().get("instruction",dishName);
        if (instruction == null) {
            return null;
        }
        return instruction.toString();
    }

    public dish retrieveDishingredients(String dishName) {

        dish dishIngredients = (dish)redisTemplate.opsForHash().get("ingredients",dishName);
        return dishIngredients;
    }

    public int getCaloriefromRepo(String dishName) {

        return (int)redisTemplate.opsForHash().get("calorie",dishName);
    }

    

    public void storeInstruction(String dishname, String instruction) {

        redisTemplate.opsForHash().put("instruction", dishname, instruction);
    }

    public void storeIngredients(String dishname, dish dishinfo) {

        redisTemplate.opsForHash().put("ingredients", dishname, dishinfo);
    }

    public void storeCalorie(String dishname, int calorie) {

        redisTemplate.opsForHash().put("calorie", dishname, calorie);
    }

    public void storedishUser(String dishname, String username) {

        redisTemplate.opsForList().leftPush(username, dishname);

    }

    public void deleteDishfromRepo(String dishname, String username) {

        redisTemplate.opsForList().remove(username, 0, dishname);
        redisTemplate.opsForHash().delete("instruction", dishname);
        redisTemplate.opsForHash().delete("ingredients", dishname);
        redisTemplate.opsForHash().delete("calorie", dishname);
    }

    public boolean checkNameExistinRepo(String dishName) {
        return redisTemplate.opsForHash().hasKey("instruction", dishName);
    }

    public Map<String, Integer> getAllDishandCalories() {
        return redisTemplate.<String, Integer>opsForHash().entries("calorie");
    }

}

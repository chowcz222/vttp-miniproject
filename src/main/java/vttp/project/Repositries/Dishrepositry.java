package vttp.project.Repositries;

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
        return instruction.toString();
    }

    public dish retrieveDishingredients(String dishName) {

        dish dishIngredients = (dish)redisTemplate.opsForHash().get("ingredients",dishName);
        return dishIngredients;
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
}

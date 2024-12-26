package vttp.project.Repositries;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class Loginrepositry {
    
    @Autowired @Qualifier("redis-object")
    RedisTemplate<String, String> redisTemplate;

    public boolean hasUserinRepo(String username) {

        return redisTemplate.opsForHash().hasKey("user", username);
    }

    public String getPasswordfromRepo(String username) {

        return redisTemplate.opsForHash().get("user",username).toString();
    }

    public String getUserDetails(String username) {
        
        return redisTemplate.opsForHash().get("detail",username).toString();
    }


    public Map<String, Integer> getdishList() {

         Map<String, Integer> dishList = new HashMap<>();
         Map<Object, Object> redisData = redisTemplate.opsForHash().entries("calorie");

        for (Map.Entry<Object, Object> entry : redisData.entrySet()) {
            String key = (String) entry.getKey();
            int value = Integer.valueOf(entry.getValue().toString());

            dishList.put(key, value);
        }
        return dishList;
    }

    public void storeUserinRepo(String username, String userDetails) {

        redisTemplate.opsForHash().put("detail", username, userDetails);
    }

    public void storeLogininRepo(String username, String password) {

        redisTemplate.opsForHash().put("user", username, password);
    }
}

package flashgrid.service;



import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;



@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate; // high level redis client for string operations 
    private final DefaultRedisScript<Long> stockScript; // Java wrapper object contain lua source code , expected return type and metadata

    public RedisService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate; // store reffernce to our shared redis client 
        this.stockScript = new DefaultRedisScript<>(); // lua script holder just a java object that will describe the script
        this.stockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/stock_check.lua"))); //now redis knows where the script for lua is 
        this.stockScript.setResultType(Long.class); // whatever redis returns after executing convert it into long of java


    }

    //helper function to intiliase stock data for the product 
    
    public void setStock(Long productId , int stock){
        redisTemplate.opsForValue().set("product_stock:" + productId, String.valueOf(stock));
    }

    // The Gatekeeper Function
    public boolean tryBuy(Long productId) {
        String key = "product_stock:" + productId;

        // Execute Lua Script
        // returns: 1 = Success, 0 = Sold Out, -1 = Error
        Long result = redisTemplate.execute(stockScript, Collections.singletonList(key));// here our script will run wherenever try buy is called

        return result != null && result == 1;
    }
    
}

// redisservice bean is created and shared accross application again application context me store 
// redistemplateexccute provides the lua script to redis and it provide the collection or list of keys if only 1 key then use singleton 
// if mulptle keys then list all the keys the reason is that cause lua needs list of keys hence collection and not single key data is passed .

// core logic is that - our gatekeeper lua+ redis will check if we have 100 product and our queue has 100 users it wont allow next all users so that all users wont hit our queue and would disrupt them 
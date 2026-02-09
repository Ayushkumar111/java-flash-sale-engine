
-- check if product exist in cache
local stock = redis.call("get" , KEYS[1]) --checking

if(not stock) then -- product not preesent in cache then return -1
    return -1
end

if(tonumber(stock)>0)then -- if stock is avaiblae and greater than 0 , then decrement
    redis.call("decr" , KEYS[1])
    return 1 -- on success 
else
    return 0 -- failure product sold out 
end


-- KEYS[1] : product ID key it is 


-- redis own function such as get call decr are atomic in nature 
-- but suppose we did 
-- get stock 
-- decr stock 
-- in b.w these 2 atomic operation any other operation bby the user can be done which can lead to state change 

-- lua says i will check the logic and combine the logic + redis commands as one atomic executable unit and i will execute it atomically so that no other user can interrupt in b/w the logic and redis command execution
-- this user A runs then user B runs . And as redis is singlel threaded 
-- lua runs inside redis 
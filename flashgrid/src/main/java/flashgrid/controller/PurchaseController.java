package flashgrid.controller;


import flashgrid.dto.PurchaseRequest;
import flashgrid.service.OrderProducer;
import flashgrid.service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/purchase")// all endpoints in this controller will start with /purchase for example /purchase/buy
public class PurchaseController {
    private final RedisService redisService;
    private final OrderProducer orderProducer;

    public PurchaseController(RedisService redisService, OrderProducer orderProducer) {
        this.redisService = redisService;
        this.orderProducer = orderProducer;
    }

    @PostMapping("/init")
    public String initStock(@RequestParam Long productId, @RequestParam int stock){
        redisService.setStock(productId, stock);
        return "Stock initialized for product " + productId + " with stock " + stock;
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buy(@RequestBody PurchaseRequest request) { // request comes in json format converted to java object via jackson
        
        // redis gatekeeper layer
        boolean acquired = redisService.tryBuy(request.productId());

        if (!acquired) {
            //straight up block the request
            return ResponseEntity.status(429).body("SOLD OUT (Rejected by Gatekeeper)");
        }

        // valid then send to queue for processing
        orderProducer.sendOrder(request);
        
        return ResponseEntity.ok("Request Accepted! You are in the queue.");
    }


    

    
}


//purchase controller is a rest controller that will handle the http requests related to purchase and it will be managed by spring container as a bean and it will be used to send messages to the queue when order is placed
// rest controller annotation is used to specify that this class is a rest controller and it will be used to handle the http requests and it will be managed by spring container as a bean and it will be used to send messages to the queue when order is placed
// request mapping annotation is used to specify that all endpoints in this controller will start with /purchase

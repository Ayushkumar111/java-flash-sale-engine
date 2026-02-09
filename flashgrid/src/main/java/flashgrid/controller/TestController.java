package flashgrid.controller;

import flashgrid.service.OrderProducer;
import flashgrid.dto.PurchaseRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController{

    private final OrderProducer orderproducer;

    public TestController(OrderProducer orderproducer){
        this.orderproducer = orderproducer;

    }

    @PostMapping("/test/send")
    public String buy(@RequestBody PurchaseRequest request){
        orderproducer.sendOrder(request);
        return "Order request sent";
    }





}

//rest controller annotation is used to specify that this class is a rest controller and it will be used to handle the http requests and it will be managed by spring container as a bean and it will be used to send messages to the queue when order is placed
// get mapping annotation is used to specify that this method will handle the get requests and it will be used to send messages to the queue when order is placed
// request param annotation is used to specify that this method will take a parameter from the request and it will be used to send messages to the queue when order is placed
// so when we hit the endpoint /test/send?msg=hello then this method will be called and it will send the message hello to the queue and return the response message sent to rabbitmq
// rest controller automatically appplies response body annotation to all methods in the class and store test controller in application context 
// responsebody means that the return value will not be "view name" but actuall response
// view name is just a template file which spring renders and send to browser so in return it will look for 
// file with name message sent to rabbitmq.html and render  but in each case we wont have template for every response .
//a view resolver check if we dont have response body annotation to check in template folder and tell spring to render 
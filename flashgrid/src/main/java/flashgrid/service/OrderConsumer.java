package flashgrid.service;

import flashgrid.config.RabbitMQConfig;
import flashgrid.dto.PurchaseRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
public class OrderConsumer{


    private final InventoryService inventoryService;

    public OrderConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void processOrder(PurchaseRequest order){
        System.out.println(" [x] Received '" + order + "' from Queue. Processing...");

        
        

        // calling the logic

        boolean success = inventoryService.attemptPurchase(order.productId());

        if(success){
            System.out.println(" Order Confirmed for User: " + order.userId());

        }else{
            System.out.println("  Order Failed for User: " + order.userId() + " - Out of Stock");
        }
       
    }
}

// service annotation classficy this class is a service class and create a bean of this class and manage it in spring container 
// rabbit listenr annotation creates a background listner which listen to the queue and invokes the process order when order arrivce
// so in case of producer side we manually create the order and PUSH happens
// in case of consumer side it is event driven when message in queue arrives then order processs is called - this is managed by rabbitlisner
// and message is converted back to string 
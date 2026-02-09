package flashgrid.service;

import flashgrid.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import flashgrid.dto.PurchaseRequest;



@Service
public class OrderProducer {

    private  final RabbitTemplate  rabbitTemplate;

    public OrderProducer(RabbitTemplate rabbitTemplate){

        this.rabbitTemplate = rabbitTemplate; // this will be used to send messages to the queue 
    }
    

    
    public void sendOrder(PurchaseRequest order){
        // convert the java object to a message and send it to a queue
        rabbitTemplate.convertAndSend(flashgrid.config.RabbitMQConfig.QUEUE_NAME, order);
        System.out.println(" [x] Sent '" + order + "' to Queue");
    }


    
}

// rabbit  template - this is a helper class provided by spring amqp to send and receive messages from rabbit mq it provides methods like convert and send to send messages to the queue and convert and receive to receive messages from the queue
// @service annotation is used to specify that this class is a service class and it will be used to define the business logic of the application and it will be managed by spring container as a bean and it will be used to send messages to the queue when order is placed
// so order producer object will be created during component scanning -> stored in application context 
// application context has 
// rabbittemplate (bean)
// orderproducer (bean)
//now order producer is a wrapper around rabbite template to send messages to queue
// now it exposes a send order method which take string obejct
// we convert the java string object to a message 
// here we can see that rabbitemplate is used for convertand send function 
// convert will convert the string object into bytes and headers are added sended 
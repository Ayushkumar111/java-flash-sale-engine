package flashgrid.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;




@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "flashgrid_orders";


    // this method will create a quueue in rabbit mq with flashgrid name 

    @Bean
    public Queue orderQueue(){
        return new Queue(QUEUE_NAME , true);// name flashgrid order , durable = true . save messages if server restart 
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter(); // this will convert java object to json and json to java object when we send and receive messages from rabbit mq 
    }
    
}

// flow - 
// config annotation this annotation is used to specify that this class is a configuration class and it will be used to define beans and other configuration settings for the application as the project start
// bean - tells that run this method and return the object created in singleton use case like create once use everywhere in the project
// this document is creation 

// jack2json convertor flow 
// if we dont use it our native byte convertor convert out java object into byte stream and send it into queue whcih is needed
// but this byte stream is java speicific and if other service want to decode it they cant 
// so we use jack2kson it convert java object into json string- that convert itno bytes - now bytes to json string here jack2json convert back json to  java objects so consumer can use it 
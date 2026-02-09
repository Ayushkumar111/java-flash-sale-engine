package flashgrid.dto;

import java.io.Serializable; // this tells that this class can be converted into byte stream 

public record PurchaseRequest(Long userId , Long productId) implements Serializable {

    
}


// dto - data transfer obejct - a plain data container whose only job is to move data b/w layers or services 
// we dont send raw strings because it has 
// no structure + no validation + consumer must guess the meaning og what the string means
// eg - "user-123" - raw string - no validation have to guesss shit
// but if we have 
//{
 // "userId": 1,
  //"productId": 1

//}

// now record - is a data only class which create immutable data only object 
// it on its own create
// constructors + getters + equals hascode tostring methods 
// the core reason is that records are immutable which means when we seralize it and covert this data into bytes and deconstruct it data wont change

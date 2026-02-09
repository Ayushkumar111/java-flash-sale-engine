package flashgrid;

import jakarta.persistence.*;

@Entity // this annotataion is used to to specify that this class is linked to data base tabel 
@Table(name= "products") // this annotation is used to specify the name of table 

public class Product {

    @Id // unique identifier for the prodcut 
    @GeneratedValue(strategy = GenerationType.IDENTITY)// autoincrement the id  
    private Long id;

    private String name;

    private int stock;

    @Version // this is done to handle the concurrent updates to the same product to prevent conflict 
    private Long version;

    protected Product(){
        
    };

    public Product(String name , int stock){
        this.name = name ; 
        this.stock = stock ;

    };


    public Long getId(){
        return id; // this will return the id 
    };

    public String getName(){
        return name; // this will return the name of the product 
    };

    public  void setName(String name){
        this.name = name; // set the product name
        
    };

    public int getStock(){
        return stock; // return the stock data
    }

    public void setStock(int stock){
        this.stock = stock; // set the stock data 
    }

    public Long getVersion(){
        return version; // return the version of the product 

    }

}

// version 
// db -> stock = 10 , version = 0 
// user 1 -> read product -> stock = 10 , version = 0 
// user 2 -> read product -> stock = 10 , version = 0 

// user 1 updates and set product stock as 9 , and version is updated to 1 
// now user 2 has stale data of stock and version when user 2 tries to update , with old version data and it fails cause in where clause 
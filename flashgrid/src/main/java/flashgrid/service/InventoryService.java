package flashgrid.service;

import flashgrid.Product;
import flashgrid.repository.ProductRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class InventoryService {

    private final ProductRepository productRepository;

    public InventoryService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Transactional
    public boolean attemptPurchase(Long productId){
        try{

            Product product = productRepository.findById(productId).orElse(null);

            if(product!= null&& product.getStock() > 0){
                product.setStock(product.getStock() - 1);//changes the heap memory.
                productRepository.save(product);//.save() command tells Hibernate to generate the SQL changes the hard drive data.
                return true;
            }else{
                return false;
            }
            


    
        }catch(ObjectOptimisticLockingFailureException e){
            System.out.println(" Race Condition blocked! User missed the item.");
            return false;
        }
    }
}



// inventory service is a service class that will handle the business logic related to inventory management and it will be managed by spring container as a bean and it will be used to handle the business logic related to inventory management
// transactional annotation is used to specify that this method should be executed within a transaction and if any exception occurs then the transaction will be rolled back and it will also handle the concurrent updates to the same product to prevent conflict by using optimistic locking and it will throw ObjectOptimisticLockingFailureException if there is a conflict and we can catch that exception to handle
// in transactional annotation everytime it runs at one big atomic function either all checklist pass or if one even fail rollback


//The Application Context is a "Warehouse": Spring holds the ProductRepository instance (the tool) in its warehouse (Context).
//Your Class is a "Worker": InventoryService is a worker that needs that tool.
//The Constructor is the "Order Form": When you define the constructor arguments, you are telling Spring: "When you hire (create) me, please hand me a ProductRepository."
//The Field (private final...) is the "Pocket": Even if Spring hands you the tool in the constructor, you need a place to put it so you can use it later in other methods (like attemptPurchase).
//If you didn't write declaration #1 (the field), you would receive the repo in the constructor, but you would immediately drop it and forget it! You assign it to this.productRepository to keep it safe in your "pocket" for later use.

//Detailed Flow:

//Spring sees InventoryService.
//Spring sees the Constructor requesting ProductRepository.
//Spring grabs the repo from the Context (Warehouse).
//Spring runs the constructor, passing the repo in.
//You save that repo into your class field (this.productRepository).
//Later, inside attemptPurchase, you use that saved field.
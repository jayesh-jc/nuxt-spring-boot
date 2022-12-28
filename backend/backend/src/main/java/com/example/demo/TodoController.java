package com.example.demo;



import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import javax.management.relation.RelationNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController

public class TodoController {
	@Autowired
    CustomerRepository repository;
	@Autowired
	ApplicationEventPublisher publisher;

    @GetMapping("/addtask")
    public Mono<Long> addCustomers(){
    	return 
    	repository.save(new Customer("services", "Bauer")).doOnSuccess(item -> publishCatalogueItemEvent("Created", item)).flatMap(item -> Mono.just(item.getId()));
        
    }
    
    @PostMapping("/updatetask")
    public Flux<Customer> updateCatalogueItem(
        
         @RequestBody Customer catalogueItem)  {
    	Mono<Customer> catalogueItemfromDB = getItemById(catalogueItem.getId());

        catalogueItemfromDB.subscribe(
            value -> {
                value.setFirstName(catalogueItem.getFirstName());
                value.setLastName(catalogueItem.getLastName());

                repository
                    .save(value)
                    .doOnSuccess(item -> publishCatalogueItemEvent("UPDATED", item))
                    .subscribe();
            });
        return repository.findAll();
    }
    
    @GetMapping("/task")
    public Flux<Customer> getCustomers(){
    	
       return repository.findAll();
    }
    
    private final void publishCatalogueItemEvent(String eventType, Customer item) {
        this.publisher.publishEvent(new TaskItemEvent(eventType, item));
    }
    private Mono<Customer> getItemById(Long skuNumber) {
        return repository.findById(skuNumber).switchIfEmpty(Mono.defer(() -> Mono.error(new RelationNotFoundException(
                String.format("Catalogue Item not found for the provided SKU :: %s" , skuNumber)))));
    }
}
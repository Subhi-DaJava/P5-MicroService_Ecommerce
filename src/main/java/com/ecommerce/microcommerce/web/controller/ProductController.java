package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @GetMapping("/Produits")
    public String listeProduits(){
       return "Un exemple de produits";
    }

    /**
     * URI "/Produits/{id}", renvoyer un produit au format JSON qui correspond à la classe Product.
     * @param id
     * @return
     */
    //Récupérer un produit par son Id
    @GetMapping(value = "Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id){
        Product product = new Product(id,"Aspirateur",100);
        return product;
        //return product = new Product(id, new String("Aspirateur"), 100);
    }
}

package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDAO;
import com.ecommerce.microcommerce.model.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    //Même pas besoin de spécifier son implémentation(ProductDAO -> ProductDAOImpl)
    /**
     * Tout d'abord, nous avons créé une variable de type ProductDao, que nous avons définie en private final afin que
     * Spring se charge d'en fabriquer une instance que nous injectons dans le constructeur. ProductDao a désormais accès à toutes les méthodes
     * que nous avons définies.
     */
    private final ProductDAO productDAO;

    public ProductController(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @GetMapping("/Produits")
    public List<Product> listeProduits(){
       return productDAO.findAll();
    }

    /**
     * URI "/Produits/{id}", renvoyer un produit au format JSON qui correspond à la classe Product.
     * @param id
     * @return
     */
    //Récupérer un produit par son Id
    @GetMapping(value = "/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id){
        return productDAO.findById(id);
    }
    @PostMapping("/Produits")
    public void ajouteProduit(@RequestBody Product product) {
        productDAO.save(product);
    }
}

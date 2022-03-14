package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDAO;
import com.ecommerce.microcommerce.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

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
     */
    //Récupérer un produit par son Id
    @GetMapping(value = "/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id){
        return productDAO.findById(id);
    }

    /**
     *Dans un premier temps, nous faisons appel à la DAO pour ajouter le produit. Dans le cas où le produit ajouté est vide ou n'existe pas, nous retournons le code 204 No Content.
     *  Pour cela, la méthode noContent() est utilisée. Cette méthode est chaînée avec la méthode build() qui construit le header, et y ajoute le code choisi.
     * Dans le cas où tout s'est bien passé et que productAdded n'est donc pas null, nous avons besoin, en plus du code 201, d'ajouter l'URI vers cette nouvelle ressource créée,
     * afin d'être conformes au protocole HTTP.
     * Nous déclarons donc une instance de la classe URI afin de la passer ensuite comme argument de ResponseEntity. Nous instancions cette URI à partir de l'URL de la requête reçue.
     * Nous ajoutons ensuite l'Id du produit à l'URI à l'aide de la méthode buildAndExpand. Nous retrouvons l'Id dans l'instance de Product que nous avons reçu : productAdded.getId().
     * Enfin, nous invoquons la méthode created de ResponseEntity, qui accepte comme argument l'URI de la ressource nouvellement créée, et renvoie le code de statut 201.
     */
    @PostMapping(value = "/Produits")
    public ResponseEntity<Product> ajouterProduit(@RequestBody Product product) {
        Product productAdded = productDAO.save(product);
        if (Objects.isNull(productAdded)) {
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}

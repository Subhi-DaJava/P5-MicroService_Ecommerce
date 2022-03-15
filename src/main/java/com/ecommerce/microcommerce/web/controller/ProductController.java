package com.ecommerce.microcommerce.web.controller;


import com.ecommerce.microcommerce.web.dao.ProductDAO;
import com.ecommerce.microcommerce.web.model.Product;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
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

    /**
     * SimpleBeanPropertyFilter est une implémentation de PropertyFilter qui permet d'établir les règles de filtrage sur un Bean donné.
     * La règle serializeAllExcept("") qui exclut uniquement les propriétés que nous souhaitons ignorer.
     * Inversement, vous pouvez procéder avec la méthode filterOutAllExcept qui marque toutes les propriétés comme étant à ignorer,
     * sauf celles passées en argument.
     * Maintenant que nous avons établi notre règle de filtrage, la ligne suivante nous permet d'indiquer à Jackson à quel Bean
     * l'appliquer. Nous utilisons SimpleFilterProvider pour déclarer que les règles de filtrage que nous avons créées (monFiltre)
     * peuvent s'appliquer à tous les Beans qui sont annotés avec monFiltreDynamique.
     * Jusqu'à présent, nous n'avons encore rien filtré concrètement ! Nous avons établi la règle et indiqué que cette règle
     * ne s'applique qu'aux Beans qui sont annotés avec monFiltreDynamique, mais nous ne l'avons pas encore appliquée.
     * Pour cela, nous les mettons au format MappingJacksonValue. Cela permet de donner accès aux méthodes qui nous intéressent,
     * comme setFilters qui applique les filtres que nous avons établis à la liste de Product.
     * Nous retournons ensuite la liste filtrée. Ne vous inquiétez pas si vous devez renvoyer MappingJacksonValue,
     * car ce n'est qu'un simple "conteneur" qui ne change absolument rien au contenu. MappingJacksonValue est donc exactement
     * comme produits, avec des méthodes de filtrage en plus.
     */
    //Récupérer la liste des produits par filtre
    //Nous savons appliquer des filtres sur notre API grâce à Jackson.
    @GetMapping("/Produits")
    public MappingJacksonValue listeProduits(){
        List<Product> produits = productDAO.findAll();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat","id");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;
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
    //Nous avons remplacé les types de retour pour rendre notre application cohérente avec la norme, grâce à ResponseEntity.
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

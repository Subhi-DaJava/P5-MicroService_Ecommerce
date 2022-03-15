package com.ecommerce.microcommerce.web.dao;

import com.ecommerce.microcommerce.web.model.Product;

import java.util.List;

public interface ProductDAO {
    List<Product> findAll();
    Product findById(int id);
    Product save(Product product);
}

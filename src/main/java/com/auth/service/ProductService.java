package com.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth.model.Product;
import com.auth.repository.ProductRepo;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepository;

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    // Save product (Create/Update)
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Delete product by ID
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}

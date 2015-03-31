/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author Ryan
 */
public class Product {
    private int productId;
    private String name;
    private String description;
    private int quantity;
    
    public Product() {
        
    }

    public Product(int productId, String name, String description, int quantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }
    
    public Product (JsonObject json) {
        productId = json.getInt("productId");
        name = json.getString("name");
        description = json.getString("description");
        quantity = json.getInt("quantity");
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("productId" , productId)
                .add("name", name)
                .add("description", description)
                .add("quantity", quantity)
                .build();
    }
    
            
    
}

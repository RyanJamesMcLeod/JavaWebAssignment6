/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author Ryan
 */
@ApplicationScoped
public class ProductList {

    private List<Product> productList;

    public ProductList() {
        productList = new ArrayList<>();

        try (Connection conn = Database.database.getConnection()) {
            String query = "SELECT * from products";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("ProductId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("quantity")
                );
                productList.add(p);
            }
        } catch (SQLException ex) {

        }
    }

    public JsonArray toJSON() {
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Product p : productList) {
            json.add(p.toJSON());
        }
        return json.build();
    }

    public void add(Product product) throws Exception {

        int rowsUpdated = Database.database.doUpdate("INSERT INTO products (Name, Description, Quantity) VALUES(?, ?, ?)", product.getName(), product.getDescription(), String.valueOf(product.getQuantity()));

        if (rowsUpdated == 1) {
            productList.add(product);
        } else {
            throw new Exception("Insert into Products failed");
        }
    }

    public void remove(Product product) throws Exception {
        remove(product.getProductId());
    }

    public void remove(int id) throws Exception {
        int rowsUpdated = Database.database.doUpdate("DELETE FROM products WHERE ProductId = ?", String.valueOf(id));

        if (rowsUpdated > 0) {
            Product original = get(id);
            productList.remove(original);
        } else {
            throw new Exception("Delete Products failed");
        }
    }

    public void set(int id, Product product) throws Exception {
        int rowsUpdated = Database.database.doUpdate("UPDATE SET Name = ?, Description = ?, Quantity = ? WHERE ProductId = ?", product.getName(), product.getDescription(), String.valueOf(product.getQuantity()), String.valueOf(id));

        if (rowsUpdated == 1) {
            Product original = get(id);
            int productId = original.getProductId();
            original.setName(product.getName());
            original.setDescription(product.getDescription());
            original.setQuantity(product.getQuantity());
        } else {
            throw new Exception("Update Products failed");
        }
    }

    public Product get(int id) {
        Product product = null;
        for (int i = 0; i < productList.size() && product == null; i++) {
            Product p = productList.get(i);
            if (p.getProductId() == id) {
                product = p;
            }
        }
        return product;
    }

}

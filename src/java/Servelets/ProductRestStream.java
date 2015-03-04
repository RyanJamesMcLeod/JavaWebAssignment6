/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servelets;

import credentials.Credentials;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author Ryan
 */
@Path("/stream")
public class ProductRestStream {
    
    @GET
    public String doGet() throws IOException{
//        JsonObject json = Json.createObjectBuilder()
//                .add("productID", 4)
//                .add("description", "thing")
//                .build();
        return getResultsArray("SELECT * FROM product");
    }
    
    private String getResultsArray(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            sb.append("[ ");
            while (rs.next()) {
                sb.append(String.format("{ \"productId\" : %s, \"name\" : %s, \"description\" : %s, \"quantity\" : %s },\n", rs.getInt("ProductID"), rs.getString("Name"), rs.getString("Description"), rs.getInt("Quantity")));
            }
            sb.setLength(Math.max(sb.length() - 2, 0));
            sb.append("]");
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
    
    @GET
    @Path("{id}")
    public String doGetById (@PathParam("id") int id) {
        return "World";
    }
    
    @POST
    public String doPost () {
        return "";
    }
    
    @PUT
    @Path ("{id}")
    public String doPut (@PathParam("id") int id) {
        return "";
    }
    
    @DELETE
    @Path("{id}")
    public String doDelete (@PathParam("id") int id) {
        return "";
    }
    
}

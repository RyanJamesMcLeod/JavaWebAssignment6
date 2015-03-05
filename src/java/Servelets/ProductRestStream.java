/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servelets;

import credentials.Credentials;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
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
    public String doGet() throws IOException {
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
    public String doGetById(@PathParam("id") int id) throws IOException {
        return getResults("SELECT * FROM product WHERE ProductID = ?", String.valueOf(id));
    }

    private String getResults(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(String.format("{ \"productId\" : %s, \"name\" : %s, \"description\" : %s, \"quantity\" : %s }", rs.getInt("ProductID"), rs.getString("Name"), rs.getString("Description"), rs.getInt("Quantity")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    @POST
    @Consumes("application/json")
    public String doPost(String data) throws SQLException {
        JsonReader reader = Json.createReader(new StringReader(data));
        JsonObject json = reader.readObject();
        String returnString = "";
        try (Connection conn = Credentials.getConnection()) {
            String parameters[] = {json.getString("Name"), json.getString("Description"), String.valueOf(json.getInt("Quantity"))};
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO product (Name, Description, Quantity) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= parameters.length; i++) {
                pstmt.setString(i, parameters[i - 1]);
            }
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                returnString = ("<a>http://localhost:8080/Assignment3/stream/" + keys.getInt(1) + "</a>");
            }
        } catch (SQLException ex) {
            returnString = ex.getMessage();
        }
        return returnString;
    }

    @PUT
    @Path("{id}")
    public String doPut(@PathParam("id") int id) {
        return "";
    }

    @DELETE
    @Path("{id}")
    public String doDelete(@PathParam("id") int id) throws IOException {
        performDelete("DELETE FROM product WHERE ProductId = ?", String.valueOf(id));
        return "";
    }

    private String performDelete(String query, String... params) {
        String queryString;

        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            pstmt.executeUpdate();
            queryString = "";
        } catch (SQLException ex) {
            queryString = "BAD";
        }
        return queryString;
    }

}

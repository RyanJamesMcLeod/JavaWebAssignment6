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
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author Ryan
 */
@Path("/stream")
public class ProductRestStream {

    @GET
    @Produces("application/json")
    public String doGet() {

        String returnString;

        try (Connection conn = Credentials.getConnection()) {
            JsonArrayBuilder jsonArray = Json.createArrayBuilder();
            JsonObjectBuilder json = Json.createObjectBuilder();

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM product");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                json.add("ProductId", rs.getInt("ProductId"))
                        .add("Name", rs.getString("Name"))
                        .add("Description", rs.getString("Description"))
                        .add("Quantity", rs.getInt("Quantity"));
                jsonArray.add(json);
            }

            JsonArray completeJson = jsonArray.build();
            returnString = completeJson.toString();
        } catch (SQLException ex) {
            returnString = "SQL Error: " + ex.getMessage();
        }
        return returnString;
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public String doGetById(@PathParam("id") int id) {
        String returnString;

        try (Connection conn = Credentials.getConnection()) {
            JsonObjectBuilder json = Json.createObjectBuilder();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM product WHERE ProductId = " + String.valueOf(id));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                json.add("ProductId", rs.getInt("ProductId"))
                        .add("Name", rs.getString("Name"))
                        .add("Description", rs.getString("Description"))
                        .add("Quantity", rs.getInt("Quantity"));

            }
            JsonObject completeJson = json.build();
            returnString = completeJson.toString();
        } catch (SQLException ex) {
            returnString = "SQL Error: " + ex.getMessage();
        }

        return returnString;
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
                returnString = ("<a>http://localhost:8080/JavaWebAssignment/stream/" + keys.getInt(1) + "</a>");
            }
        } catch (SQLException ex) {
            returnString = "SQL Error: " + ex.getMessage();
        }
        return returnString;
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public String doPut(@PathParam("id") int id, String data) {
        JsonReader reader = Json.createReader(new StringReader(data));
        JsonObject json = reader.readObject();
        String returnString = "";
        try (Connection conn = Credentials.getConnection()) {
            String parameters[] = {json.getString("Name"), json.getString("Description"), String.valueOf(json.getInt("Quantity"))};
            PreparedStatement pstmt = conn.prepareStatement("UPDATE product SET name = ?, description = ?, quantity = ? WHERE ProductId = ?;", Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= parameters.length; i++) {
                pstmt.setString(i, parameters[i - 1]);
            }
            pstmt.setInt(4, id);
            pstmt.executeUpdate();

            returnString = ("<a>http://localhost:8080/JavaWebAssignment/stream/" + id + "</a>");

        } catch (SQLException ex) {
            returnString = "SQL Error: " + ex.getMessage();
        }
        return returnString;
    }

    @DELETE
    @Path("{id}")
    @Produces("application/json")
    public String doDelete(@PathParam("id") int id) {
        String returnString = "";
        
        try (Connection conn = credentials.Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM product WHERE ProductId = ?");
            pstmt.setString(1, String.valueOf(id));
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            returnString = "SQL Error: " + ex.getMessage();
        }
        return returnString;
    }

}

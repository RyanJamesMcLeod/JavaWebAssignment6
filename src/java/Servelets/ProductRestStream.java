/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servelets;

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
    public String doGet() {
        return "Hello";
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

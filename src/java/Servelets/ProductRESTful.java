/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servelets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import model.Product;
import model.ProductList;

/**
 *
 * @author Ryan
 */
@Path("/products")
@ApplicationScoped
public class ProductRESTful {

    @Inject
    ProductList productList;

    @GET
    @Produces("application/json")
    public Response getAll() {
        return Response.ok(productList.toJSON()).build();
    }

    @GET
    @Path("{id}")
    public Response getOneProduct(@PathParam("id") int id) {
        return Response.ok(productList.get(id).toJSON()).build();
    }

    @POST
    @Consumes("application/json")
    public Response add(JsonObject json) {
        Response result;
        try {
            productList.add(new Product(json));
            result = Response.ok().build();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            result = Response.status(500).build();
        }
        return result;
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response put(@PathParam("id") int id, JsonObject json) {
        Response result;
        try {
            productList.set(id, new Product(json));
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).build();
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        Response result;
        try {
            productList.remove(id);
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).build();
        }
        return result;
    }

}

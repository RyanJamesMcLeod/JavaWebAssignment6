/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servelets;

import credentials.Credentials;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author c0565705
 */
@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    /**
     * Performs the GET request, retrieving the information from the database
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "text/plain-text");
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                // There are no parameters at all
                out.println(getResultsArray("SELECT * FROM product"));
            } else {
                // There are some parameters
                String id = request.getParameter("id");
                out.println(getResults("SELECT * FROM product WHERE ProductID = ?", id));
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Performs the action that creates a return statement in JSON format when
     * there is only one JSON object
     *
     * @param query
     * @param params
     * @return stringbuilder sb
     */
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

    /**
     * Performs the action that return a JSON array with all information stored
     * in it as objects
     *
     * @param query
     * @param params
     * @return stringbuilder sb
     */
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

    /**
     * Performs the POST method, adding a new element to the database
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String result;
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {

                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                result = (doUpdate("INSERT INTO product (Name, Description, Quantity) VALUES (?, ?, ?)", name, description, quantity));

                if (result.equalsIgnoreCase("BAD")) {
                    response.setStatus(500);
                } else {
                    out.println(result);
                }
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?name=XXX&age=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Performs the method that returns a URL of the information inserted
     *
     * @param query
     * @param params
     * @return queryString
     */
    private String doUpdate(String query, String... params) {
        String queryString = "";
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                queryString = ("<a>http://localhost:8080/JavaWebAssignment/products/" + keys.getInt(1) + "</a>");
            } else if (params.length == 4) {
                queryString = "<a>http://localhost:8080/JavaWebAssignment/products/" + String.valueOf(params[4 - 1]) + "</a>";
            }
        } catch (SQLException ex) {
            queryString = "BAD";
        }
        return queryString;

    }

    /**
     * Performs the PUT method, updating an element of the database, based on
     * inserted ID
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String result;
        Set<String> keySet = request.getParameterMap().keySet();

        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("id") && keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                // There are some parameters
                String id = request.getParameter("id");
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                result = (doUpdate("UPDATE product SET Name = ?, Description = ?, Quantity = ? WHERE ProductId = ?", name, description, quantity, id));
                out.println(result);
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?name=XXX&age=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Performs the DELETE method, which deletes an element from the database
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String result;
        Set<String> keySet = request.getParameterMap().keySet();

        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("id")) {
                String id = request.getParameter("id");
                result = performDelete("DELETE FROM product WHERE ProductId = ?", id);
                if (result.equalsIgnoreCase("BAD")) {
                    response.setStatus(500);
                }
                out.println(result);
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Performs the methods that deletes the element from the database, given
     * the ID
     *
     * @param query
     * @param params
     * @return queryString
     */
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package B_servlets;

import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Chermaine Tan
 */
@WebServlet(name = "ECommerce_AddFurnitureToListServlet", urlPatterns = {"/ECommerce_AddFurnitureToListServlet"})
public class ECommerce_AddFurnitureToListServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            //get the session
            HttpSession session;
            session = request.getSession();

            ArrayList<ShoppingCartLineItem> shoppingCart = (ArrayList<ShoppingCartLineItem>) session.getAttribute("shoppingCart");

            Long countryID = (Long) session.getAttribute("countryID");
            String sku = request.getParameter("SKU");
            String name = request.getParameter("name");
            String id = request.getParameter("id");
            String imageURL = request.getParameter("imageURL");
            double price = Double.parseDouble(request.getParameter("price"));
            int quantityOfItem;

            Client client = ClientBuilder.newClient();
            WebTarget target = client
                    .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.storeentity")
                    .path("getQuantity")
                    .queryParam("storeID", 59)
                    .queryParam("SKU", sku);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response resp = invocationBuilder.get();
            System.out.println("status: " + response.getStatus());
            if (response.getStatus() != 200) {
                quantityOfItem = 0;
            }
            String result = (String) resp.readEntity(String.class);
            System.out.println("Result returned from ws: " + result);
            quantityOfItem = Integer.parseInt(result);

            ShoppingCartLineItem item = new ShoppingCartLineItem();
            item.setId(id);
            item.setImageURL(imageURL);
            item.setName(name);
            item.setPrice(price);
            item.setSKU(sku);
            item.setCountryID(countryID);

            if (shoppingCart == null) {
                shoppingCart = new ArrayList<>();
            }
            if (shoppingCart.contains(item) && (quantityOfItem - shoppingCart.get(shoppingCart.indexOf(item)).getQuantity() >= 0)) {
                int itemIndex = shoppingCart.indexOf(item);
                shoppingCart.get(itemIndex).setQuantity(shoppingCart.get(itemIndex).getQuantity() + 1);
            } else if(quantityOfItem-1 >= 0) {
                item.setQuantity(1);
                shoppingCart.add(item);
            } else {
                String failResult = "Item is currently out of stock!";
                response.sendRedirect("B/SG/shoppingCart.jsp?errMsg=" + failResult);
            }
            session.setAttribute("shoppingCart", shoppingCart);
            String sucessResult = "Item successfully added into the cart!";
            response.sendRedirect("B/SG/shoppingCart.jsp?goodMsg=" + sucessResult);
        } catch (Exception e) {
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

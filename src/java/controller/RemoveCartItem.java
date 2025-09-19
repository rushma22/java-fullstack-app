package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.*;

@WebServlet(name = "RemoveCartItem", urlPatterns = {"/RemoveCartItem"})
public class RemoveCartItem extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject res = new JsonObject();
        res.addProperty("status", false);

        String cartIdStr = request.getParameter("cartId");
        String productIdStr = request.getParameter("productId");

        User user = (User) request.getSession().getAttribute("user");

        try {
            if (user != null) {
                // Logged-in: remove by cartId belonging to this user
                if (cartIdStr == null || cartIdStr.isEmpty()) {
                    res.addProperty("message", "Missing cartId.");
                } else {
                    int cartId = Integer.parseInt(cartIdStr);
                    SessionFactory sf = HibernateUtil.getSessionFactory();
                    Session s = sf.openSession();
                    Transaction tx = s.beginTransaction();

                    // Ensure the cart row belongs to the user
                    Criteria c = s.createCriteria(Cart.class)
                            .add(org.hibernate.criterion.Restrictions.eq("id", cartId))
                            .add(org.hibernate.criterion.Restrictions.eq("user", user));

                    Cart cart = (Cart) c.uniqueResult();
                    if (cart == null) {
                        res.addProperty("message", "Cart item not found.");
                    } else {
                        s.delete(cart);
                        tx.commit();
                        res.addProperty("status", true);
                        res.addProperty("message", "Item removed.");
                    }
                    s.close();
                }
            } else {
                // Guest: remove from sessionCart by productId
                if (productIdStr == null || productIdStr.isEmpty()) {
                    res.addProperty("message", "Missing productId.");
                } else {
                    int productId = Integer.parseInt(productIdStr);
                    ArrayList<Cart> sessionCarts = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");
                    if (sessionCarts == null || sessionCarts.isEmpty()) {
                        res.addProperty("message", "Your cart is empty.");
                    } else {
                        boolean removed = sessionCarts.removeIf(c -> c.getProduct() != null && c.getProduct().getId() == productId);
                        request.getSession().setAttribute("sessionCart", sessionCarts);
                        if (removed) {
                            res.addProperty("status", true);
                            res.addProperty("message", "Item removed.");
                        } else {
                            res.addProperty("message", "Item not found.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            res.addProperty("message", "Error: " + e.getMessage());
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(res));
    }
}

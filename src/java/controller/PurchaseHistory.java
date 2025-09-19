/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Rushma
 */
@WebServlet(name = "PurchaseHistory", urlPatterns = {"/PurchaseHistory"})
public class PurchaseHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJsonObject = new JsonObject();

        responseJsonObject.addProperty("status", Boolean.FALSE);

        User u = (User) req.getSession().getAttribute("user");
        System.out.println("userrr" + u);

        if (u == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        } else {
            Session s = HibernateUtil.getSessionFactory().openSession();
            Criteria c = s.createCriteria(Orders.class);
            c.add(Restrictions.eq("user", u));
            List<Orders> ordersList = c.list();

            System.out.println("lsittt  " + ordersList);
            if (c.list().isEmpty()) {
                responseJsonObject.addProperty("message",
                        "You have not purchased anything yet");
            } else {
                for (Orders o : ordersList) {
                    Criteria c1 = s.createCriteria(OrderItems.class);
                    List<OrderItems> orderItems = c1.list();
                    responseJsonObject.add("productList", gson.toJsonTree(orderItems));
                    responseJsonObject.addProperty("status", true);
                }


            }
        }
        resp.setContentType("application/json");
        String toJson = gson.toJson(responseJsonObject);
        resp.getWriter().write(toJson);

    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Model;
import hibernate.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Rushma
 */
@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        System.out.println("idddd" + req.getParameter("id"));

        Gson gson = new Gson();
        JsonObject resObj = new JsonObject();
        resObj.addProperty("status", Boolean.FALSE);
        if (Util.isInteger(id)) {
            System.out.println("came inside integer");
//            search the product 
            try {
                Session s = HibernateUtil.getSessionFactory().openSession();
                Product p = (Product) s.get(Product.class, Integer.parseInt(id));

                if (p.getStatus().getValue().equals("Active")) {
                    p.getUser().setEmail(null);
                    p.getUser().setPassword(null);
                    p.getUser().setVerification(null);
                    p.getUser().setCreated_at(null);
                    p.getUser().setId(-1);

                    Criteria c1 = s.createCriteria(Model.class);
                    c1.add(Restrictions.eq("brand", p.getModel().getBrand()));
                    List<Model> modelList = c1.list();
                    
                    
                    Criteria c2 = s.createCriteria(Product.class);
                    c2.add(Restrictions.in("model", modelList)); //subquery
                    c2.add(Restrictions.ne("id", p.getId())); //not equal to curent product
                    c2.setMaxResults(6);
                    List<Product> productList = c2.list();

                    for (Product pr : productList) {
                        pr.getUser().setEmail(null);
                        pr.getUser().setPassword(null);
                        pr.getUser().setVerification(null);
                        pr.getUser().setCreated_at(null);
                        pr.getUser().setId(-1);
                    }

                    resObj.add("product", gson.toJsonTree(p));
                    resObj.add("productList", gson.toJsonTree(productList));
                    System.out.println("product detailsssss   " + p);
                    resObj.addProperty("status", Boolean.TRUE);
                } else {
                    resObj.addProperty("message", "Product not found 1");
                }

            } catch (Exception e) {
                e.printStackTrace();
                resObj.addProperty("message", "Product not found 2");
            }
        } else {
            resObj.addProperty("message", "Product not found 3");
        }

        resp.setContentType("application/json");
        String json = gson.toJson(resObj);
        resp.getWriter().write(json);

    }

}

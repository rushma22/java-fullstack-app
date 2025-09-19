/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Brand;
import hibernate.Color;
import hibernate.HibernateUtil;
import hibernate.Model;
import hibernate.Product;
import hibernate.Quality;
import hibernate.Status;
import hibernate.Storage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
@WebServlet(name = "LoadData", urlPatterns = {"/LoadData"})
public class LoadData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session s = HibernateUtil.getSessionFactory().openSession();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        Criteria c1 = s.createCriteria(Brand.class);
        List<Brand> brandList = c1.list();

        Criteria c2 = s.createCriteria(Model.class);
        List<Model> modelList = c2.list();

        Criteria c3 = s.createCriteria(Storage.class);
        List<Storage> storageList = c3.list();

        Criteria c4 = s.createCriteria(Quality.class);
        List<Quality> qualityList = c4.list();

        Criteria c5 = s.createCriteria(Color.class);
        List<Color> colorList = c5.list();

//        Criteria c6 = s.createCriteria(Status.class);
//        List<Status> statusList = c6.list();

        
        ////load product data
        Criteria c6 = s.createCriteria(Product.class);
        c6.addOrder(Order.desc("id"));
        
        Status status = (Status) s.get(Status.class,2);
        
        c6.add(Restrictions.eq("status",status));
        responseObject.addProperty("allProductCount",c6.list().size());
        c6.setFirstResult(0);
        c6.setMaxResults(6);
        
        List<Product> productList = c6.list();
        for(Product product : productList){
            product.setUser(null);
        }
        
        
        Gson gson = new Gson();
        
        responseObject.addProperty("status", Boolean.TRUE);
        responseObject.add("brandList", gson.toJsonTree(brandList));
        responseObject.add("modelList", gson.toJsonTree(modelList));
        responseObject.add("storageList", gson.toJsonTree(storageList));
        responseObject.add("qualityList", gson.toJsonTree(qualityList));
        responseObject.add("colorList", gson.toJsonTree(colorList));
        responseObject.add("productList", gson.toJsonTree(productList));
        

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));

        //to send the list to front end we need to use add() and convert to json tree
        s.close();

    }

}

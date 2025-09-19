/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import hibernate.City;
import hibernate.HibernateUtil;
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

/**
 *
 * @author Rushma
 */
@WebServlet(name = "CityData", urlPatterns = {"/CityData"})
public class CityData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(City.class);
        List<City> cityList = c.list();

        Gson gson = new Gson();
        String json = gson.toJson(cityList);
        resp.setContentType("application/json");
        resp.getWriter().write(json);
        
        s.close();
    }

}

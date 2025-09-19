/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Rushma
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject signInObj = gson.fromJson(req.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", Boolean.FALSE);

        String email = signInObj.get("email").getAsString();
        String password = signInObj.get("password").getAsString();

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email cannot be empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password cannot be empty");
        } else {
            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));
            c.add(Restrictions.eq("password", password));
            

            if (c.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid Email or Password");
            } else {
                User u = (User) c.list().get(0);
                HttpSession ses = req.getSession();
                responseObject.addProperty("status", true);// because there is a user
                
                if (!u.getVerification().equals("Verified")) {
                    ses.setAttribute("email", email);

                    responseObject.addProperty("message", "1");
                } else {

                    responseObject.addProperty("message", "2");
                    ses.setAttribute("user", u);
                }

            }
            s.close();
        }
        String responseJson = gson.toJson(responseObject);
        System.out.println(responseJson);
        resp.setContentType("application/json");
        resp.getWriter().write(responseJson);

    }



    
}

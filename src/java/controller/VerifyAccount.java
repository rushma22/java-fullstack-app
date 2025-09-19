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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Rushma
 */
@WebServlet(name = "VerifyAccount", urlPatterns = {"/VerifyAccount"})
public class VerifyAccount extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession ses = req.getSession();

        if (ses.getAttribute("email") == null) {
            responseObject.addProperty("message", "Email not found");
        } else {

            JsonObject codeObj = gson.fromJson(req.getReader(), JsonObject.class);

            String verificationCode = codeObj.get("code").getAsString();

            //connect to database
            Session s = HibernateUtil.getSessionFactory().openSession();

            // search all from user
            Criteria criteria = s.createCriteria(User.class);
            //where ...
            criteria.add(Restrictions.eq("verification", verificationCode));// where email is email
            criteria.add(Restrictions.eq("email", ses.getAttribute("email")));// where email is email

            if (criteria.list().isEmpty()) {
                responseObject.addProperty("message", "Wrong verification code");
            } else {
                User user = (User) criteria.list().get(0);
                user.setVerification("Verified");

                s.update(user);
                s.beginTransaction().commit();
                
                //save the user object in session
                ses.setAttribute("user", user);

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Verification Success!");
            }

        }

        String responseText = gson.toJson(responseObject);
        resp.setContentType("application/json");
        resp.getWriter().write(responseText);

    }

}

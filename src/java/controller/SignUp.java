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
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Rushma
 */
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson g = new Gson();

        JsonObject user = g.fromJson(req.getReader(), JsonObject.class);

        System.out.println(user);
        String firstName = user.get("firstName").getAsString();
        String lastName = user.get("lastName").getAsString();
        final String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();

        JsonObject responJsonObject = new JsonObject();
        responJsonObject.addProperty("status", false);

        if (firstName.isEmpty()) {
            responJsonObject.addProperty("message", "First name cannot be empty");
        } else if (lastName.isEmpty()) {
            responJsonObject.addProperty("message", "Last name cannot be empty");
        } else if (email.isEmpty()) {
            responJsonObject.addProperty("message", "Email name cannot be empty");
        } else if (!Util.isEmailValid(email)) {
            responJsonObject.addProperty("message", "Please enter a valid email");
        } else if (password.isEmpty()) {
            responJsonObject.addProperty("message", "Password cannot be empty");
        } else if (!Util.isPasswordValid(password)) {
            responJsonObject.addProperty("message", "Please enter a valid password with upper case, lower case, number and special character and 8 characters");
        } else {
//            SessionFactory sf = HibernateUtil.getSessionFactory();
//            Session s = sf.openSession();

            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria = s.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));

            if (!criteria.list().isEmpty()) {
                responJsonObject.addProperty("message", "User with this Email already Exists");
            } else {
                User u = new User();
                u.setFirst_name(firstName);
                u.setLast_name(lastName);
                u.setEmail(email);
                u.setPassword(password);

                final String verificationCode = Util.generateCode();
                u.setVerification(verificationCode);
                u.setCreated_at(new Date());

                s.save(u);
                s.beginTransaction().commit();

                //send email
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "SmartTrade - Verification", "<h1>" + verificationCode + "</h1> I pranked you sheruuu!!");
                    }

                }).start();
                
                //create session to rememebr the user(it will dstroy the session when deploying)
                HttpSession ses = req.getSession();
                ses.setAttribute("email", email);
                
                
                responJsonObject.addProperty("status", Boolean.TRUE);
                responJsonObject.addProperty("message", "Registration success. Please check your email for the verification code");
            }
            s.close();

        }

        String responseText = g.toJson(responJsonObject);
        resp.setContentType("application/json");
        resp.getWriter().write(responseText);
        System.out.println("response" + responseText);
//        
//

    }

}

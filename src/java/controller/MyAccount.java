/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Rushma
 */
@WebServlet(name = "MyAccount", urlPatterns = {"/MyAccount"})
public class MyAccount extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession ses = req.getSession(false);
        Gson gson = new Gson();

        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");

            JsonObject responseObject = new JsonObject();

            responseObject.addProperty("firstName", user.getFirst_name());
            responseObject.addProperty("lastName", user.getLast_name());
            responseObject.addProperty("currentPassword", user.getPassword());

            String year = new SimpleDateFormat("MMM yyyy").format(user.getCreated_at());

            responseObject.addProperty("since", year);

            Session s = HibernateUtil.getSessionFactory().openSession();
            Criteria c = s.createCriteria(Address.class);
            c.add(Restrictions.eq("user", user));
            if (!c.list().isEmpty()) {
                List<Address> addressList = c.list();
                responseObject.add("addressList", gson.toJsonTree(addressList));
               
            }

            String json = gson.toJson(responseObject);
            resp.setContentType("application/json");
            resp.getWriter().write(json);

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(req.getReader(), JsonObject.class);

        String firstName = userData.get("firstName").getAsString();
        String lastName = userData.get("lastName").getAsString();
        String postalCode = userData.get("postalCode").getAsString();
        String lineOne = userData.get("lineOne").getAsString();
        String lineTwo = userData.get("lineTwo").getAsString();
        int cityId = userData.get("cityId").getAsInt();
        String currentPassword = userData.get("currentPassword").getAsString();
        String newPassword = userData.get("newPassword").getAsString();
        String confirmPassword = userData.get("confirmPassword").getAsString();

        JsonObject responJsonObject = new JsonObject();
        responJsonObject.addProperty("status", Boolean.FALSE);

        if (firstName.isEmpty()) {
            responJsonObject.addProperty("message", "First name cannot be empty");
        } else if (lastName.isEmpty()) {
            responJsonObject.addProperty("message", "Last name cannot be empty");
        } else if (postalCode.isEmpty()) {
            responJsonObject.addProperty("message", "Postal code cannot be empty");
        } else if (lineOne.isEmpty()) {
            responJsonObject.addProperty("message", "Address line 1 cannot be empty");
        } else if (lineTwo.isEmpty()) {
            responJsonObject.addProperty("message", "Address line 2 cannot be empty");
        } else if (!Util.isCodeValid(postalCode)) {
            responJsonObject.addProperty("message", "Invalid postal code");
        } else if (cityId == 0) {
            responJsonObject.addProperty("message", "Please select a city");
        } else if (currentPassword.isEmpty()) {
            responJsonObject.addProperty("message", "Current password cannot be empty");
        } else if (!newPassword.isEmpty() && !Util.isPasswordValid(newPassword)) {
            responJsonObject.addProperty("message", "New password must include uppercase, lowercase, number, special character, and be at least 8 characters long");
        } else if (!confirmPassword.isEmpty() && !Util.isPasswordValid(confirmPassword)) {
            responJsonObject.addProperty("message", "Confirm password must include uppercase, lowercase, number, special character, and be at least 8 characters long");
        } else if (!confirmPassword.equals(newPassword)) {
            responJsonObject.addProperty("message", "Confirm password does not match the new password");
        } else if (!newPassword.isEmpty() && newPassword.equals(currentPassword)) {
            responJsonObject.addProperty("message", "New password is same as current password");
        } else {
            HttpSession ses = req.getSession(false);
            if (ses != null && ses.getAttribute("user") != null) {
                User sessionUser = (User) ses.getAttribute("user");

                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                hibernateSession.beginTransaction();

                Criteria userCriteria = hibernateSession.createCriteria(User.class);
                userCriteria.add(Restrictions.eq("email", sessionUser.getEmail()));

                if (!userCriteria.list().isEmpty()) {
                    User dbUser = (User) userCriteria.list().get(0);

                    // Update user fields
                    dbUser.setFirst_name(firstName);
                    dbUser.setLast_name(lastName);
                    if (!confirmPassword.isEmpty()) {
                        dbUser.setPassword(confirmPassword);
                    } else {
                        dbUser.setPassword(newPassword);
                    }

                    // Load city
                    City city = (City) hibernateSession.load(City.class, cityId);

                    // Find or create address
                    Criteria addressCriteria = hibernateSession.createCriteria(Address.class);
                    addressCriteria.add(Restrictions.eq("user", dbUser));
                    Address address;

                    if (!addressCriteria.list().isEmpty()) {
                        address = (Address) addressCriteria.list().get(0); // update
                    } else {
                        address = new Address(); // new
                        address.setUser(dbUser);
                    }

                    // Set address values
                    address.setLineOne(lineOne);
                    address.setLineTwo(lineTwo);
                    address.setPostalCode(postalCode);
                    address.setCity(city);

                    // Save updates
                    hibernateSession.merge(dbUser);
                    hibernateSession.merge(address);

                    hibernateSession.getTransaction().commit();
                    hibernateSession.close();

                    // ✅ Update session user object too
                    dbUser.setPassword(null); // optional: remove password before session update
                    ses.setAttribute("user", dbUser);

                    responJsonObject.addProperty("status", Boolean.TRUE);
                    responJsonObject.addProperty("message", "Profile updated successfully!");
                } else {
                    hibernateSession.getTransaction().rollback();
                    responJsonObject.addProperty("message", "User not found.");
                }
            }
        }

        String json = gson.toJson(responJsonObject);
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }

}

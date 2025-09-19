/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Rushma
 */
@WebFilter(filterName = "SessionFilter", urlPatterns = {"/admin.html"})
public class AdminSessionFilter implements Filter{
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException{
    }

   @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest  req =(HttpServletRequest) request;
        HttpServletResponse  res =(HttpServletResponse) response;
        
        HttpSession s = req.getSession(false); // do not craete a new session if there is no available session
        
        //to prevent going back to the page using back button
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");
      
        if(s!= null &&  s.getAttribute("user") != null){
            res.sendRedirect("admin.html");
            System.out.println("okayyyyyyy there is a useerrrrr");
        }else{
             System.out.println("oooppsss no userrr");
            chain.doFilter(request, response);
        }
    }
    

    @Override
    public void destroy() {
    }
    
   
}

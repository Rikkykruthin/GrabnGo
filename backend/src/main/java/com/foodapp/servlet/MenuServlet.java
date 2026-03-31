package com.foodapp.servlet;

import com.foodapp.dao.MenuDAO;
import com.foodapp.model.MenuItem;
import com.foodapp.util.Logger;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuServlet extends HttpServlet {
    private MenuDAO menuDAO = new MenuDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        Logger.logAPICall("GET", "/api/menu-items" + (pathInfo != null ? pathInfo : ""));
        
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                handleGetMenuItemById(request, response, pathInfo);
            } else {
                sendError(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            Logger.logError("Error in MenuServlet", e);
            sendError(response, 500, "Failed to fetch menu item: " + e.getMessage());
        }
    }
    
    private void handleGetMenuItemById(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws IOException {
        try {
            int menuItemId = Integer.parseInt(pathInfo.substring(1));
            MenuItem menuItem = menuDAO.getMenuItemById(menuItemId);
            
            if (menuItem == null) {
                sendError(response, 404, "Menu item not found");
                return;
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("menuItem", menuItem);
            
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (NumberFormatException e) {
            sendError(response, 400, "Invalid menu item ID");
        } catch (Exception e) {
            Logger.logError("Error fetching menu item", e);
            sendError(response, 500, "Failed to fetch menu item: " + e.getMessage());
        }
    }
    
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        response.getWriter().write(gson.toJson(error));
    }
}

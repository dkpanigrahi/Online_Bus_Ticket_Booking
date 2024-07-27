package com.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

public class CustomErrorController implements ErrorController {
	
	@RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            String message;

            switch (statusCode) {
                case 400:
                    message = "Bad Request";
                    break;
                case 401:
                    message = "Unauthorized";
                    break;
                case 403:
                    message = "Forbidden";
                    break;
                case 404:
                    message = "Not Found";
                    break;
                case 500:
                    message = "Internal Server Error";
                    break;
                default:
                    message = "Unexpected Error";
                    break;
            }

            model.addAttribute("status", statusCode);
            model.addAttribute("message", message);
        } else {
            model.addAttribute("message", "Unexpected Error");
        }
        
        return "error"; 
    }

    
    public String getErrorPath() {
        return "/error";
    }
}

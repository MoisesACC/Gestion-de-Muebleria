package com.muebleria.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Aquí puedes agregar lógica para manejar diferentes tipos de errores
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404";  // Redirige a una página de error 404 personalizada
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error-500";  // Redirige a una página de error 500 personalizada
            }
        }
        return "error";  
    }
}
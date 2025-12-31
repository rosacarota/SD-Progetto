package control.ordine;

import control.utente.Login;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/CheckoutRedirect")
public class CheckoutRedirect extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       Integer tipoUtente = (Integer) req.getSession().getAttribute("tipoUtente");

       if (tipoUtente == null || !tipoUtente.equals(Login.REGISTRATO)) {
           resp.sendRedirect("pages/login.jsp");
       }
       else {
           resp.sendRedirect("pages/checkout.jsp");
       }
    }
}

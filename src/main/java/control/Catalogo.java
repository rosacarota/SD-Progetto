package control;

import control.utente.Login;
import model.maglietta.MagliettaDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/Catalogo")
public class Catalogo extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        MagliettaDAO magliettaDAO = new MagliettaDAO();
        String targetJsp;

        try {
            req.setAttribute(
                "magliette",
                magliettaDAO.doRetriveAll(req.getParameter("ordine"))
            );

            Integer tipoUtente =
                (Integer) req.getSession().getAttribute("tipoUtente");

            if (tipoUtente != null && tipoUtente.equals(Login.ADMIN)) {
                targetJsp = "/catalogoAdmin.jsp";
            } else {
                targetJsp = "/catalogo.jsp";
            }

        } catch (SQLException e) {
            targetJsp = "/pages/errorpage.jsp";
        }

        req.getRequestDispatcher(targetJsp).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}

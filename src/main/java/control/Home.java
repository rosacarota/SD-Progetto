package control;

import model.maglietta.MagliettaDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/Home")
public class Home extends HttpServlet {
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
            targetJsp = "/index.jsp";

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

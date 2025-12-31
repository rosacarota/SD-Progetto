package control.maglietta;

import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;
import model.misura.MisuraDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/ModificaMaglietta")
public class ModificaMaglietta extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int id;

        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            return;
        }

        MagliettaDAO magliettaDAO = new MagliettaDAO();
        MisuraDAO misuraDAO = new MisuraDAO();

        try {
            MagliettaBean magliettaBean = magliettaDAO.doRetrieveByKey(id);
            req.setAttribute("maglietta", magliettaBean);
            req.setAttribute("misure", misuraDAO.doRetrieveAll(id));
        } catch (SQLException e) {
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            return;
        }

        req.getRequestDispatcher("pages/modifica.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}

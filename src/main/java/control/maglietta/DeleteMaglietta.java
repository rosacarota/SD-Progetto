package control.maglietta;

import model.maglietta.MagliettaDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/DeleteMaglietta")
public class DeleteMaglietta extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int ID = Integer.parseInt(req.getParameter("ID"));

        MagliettaDAO magliettaDAO = new MagliettaDAO();

        try {
            if (!magliettaDAO.deleteMaglietta(ID))
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
        }

        RequestDispatcher requestDispatcher = req.getRequestDispatcher("catalogoAdmin.jsp");
        requestDispatcher.forward(req, resp);
    }
}
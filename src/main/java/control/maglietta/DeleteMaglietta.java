package control.maglietta;

import model.maglietta.MagliettaDAO;

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        MagliettaDAO magliettaDAO = new MagliettaDAO();

        try {
            int ID = Integer.parseInt(req.getParameter("ID"));

            boolean deleted = magliettaDAO.deleteMaglietta(ID);
            if (!deleted) {
                try {
                    req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
                } catch (ServletException | IOException e) {
                    req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
                }
                return;
            }

            try {
                req.getRequestDispatcher("catalogoAdmin.jsp").forward(req, resp);
            } catch (ServletException | IOException e) {
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            }

        } catch (NumberFormatException | SQLException e) {
            try {
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            } catch (ServletException | IOException ex) {
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            }
        }
    }
}


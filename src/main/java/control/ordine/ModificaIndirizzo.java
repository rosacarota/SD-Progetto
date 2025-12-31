package control.ordine;

import model.utente.UtenteBean;
import model.utente.UtenteDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/ModificaIndirizzo")
public class ModificaIndirizzo extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UtenteBean utenteBean = (UtenteBean) req.getSession().getAttribute("utente");

        String viaNuova = req.getParameter("viaNuova");
        String cittaNuova = req.getParameter("cittaNuova");
        String capNuova = req.getParameter("capNuova");

        utenteBean.setVia(viaNuova);
        utenteBean.setCitta(cittaNuova);
        utenteBean.setCap(capNuova);

        UtenteDAO utenteDAO = new UtenteDAO();

        try {
            utenteDAO.doUpdate(utenteBean);
        } catch (SQLException e) {
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
        }

        resp.sendRedirect("StoricoOrdini");

    }
}

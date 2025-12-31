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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebServlet("/ModificaPagamento")
public class ModificaPagamento extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UtenteBean utenteBean = (UtenteBean) req.getSession().getAttribute("utente");

        String nomeCarta = req.getParameter("nomeCartaNuova");
        String cognomeCarta = req.getParameter("cognomeCartaNuova");
        String numeroCarta = req.getParameter("numCartaNuova");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dataScadenza = LocalDate.parse(req.getParameter("dataScadNuova"), formatter);
        String cvv = req.getParameter("CVVNuovo");

        utenteBean.setNomeCarta(nomeCarta);
        utenteBean.setCognomeCarta(cognomeCarta);
        utenteBean.setNumCarta(numeroCarta);
        utenteBean.setDataScadenza(dataScadenza);
        utenteBean.setCVV(cvv);

        UtenteDAO utenteDAO = new UtenteDAO();
        try {
            utenteDAO.doUpdate(utenteBean);
        } catch (SQLException e) {
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
        }

        if (req.getParameter("modificaPagamento").equals("profilo"))
            resp.sendRedirect("StoricoOrdini");
        else if (req.getParameter("modificaPagamento").equals("checkout"))
            resp.sendRedirect("pages/checkout.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doPost(req,resp);
    }
}

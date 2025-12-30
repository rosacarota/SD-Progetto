package control;

import model.acquisto.AcquistoBean;
import model.acquisto.AcquistoDAO;
import model.ordine.OrdineBean;
import model.ordine.OrdineDAO;
import model.utente.UtenteBean;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/StoricoOrdini")
public class StoricoOrdini extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtenteBean utenteBean = (UtenteBean) req.getSession().getAttribute("utente");

        OrdineDAO ordineDAO = new OrdineDAO();
        AcquistoDAO acquistoDAO = new AcquistoDAO();

        try {
            Collection<OrdineBean> ordini =
                    ordineDAO.doRetrieveByKey(utenteBean.getUsername());

            Map<OrdineBean, Collection<AcquistoBean>> map = new HashMap<>();

            for (OrdineBean o : ordini) {
                try {
                    map.put(o, acquistoDAO.doRetrieveByOrdine(o.getID()));
                } catch (SQLException e) {
                    try {
                        req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
                    } catch (ServletException | IOException ex) {
                        req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
                    }
                    return;
                }
            }

            req.setAttribute("ordini", map);

            try {
                req.getRequestDispatcher("pages/profilo.jsp").forward(req, resp);
            } catch (ServletException | IOException e) {
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            try {
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            } catch (ServletException | IOException ex) {
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}

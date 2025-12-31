package control;

import model.acquisto.AcquistoBean;
import model.acquisto.AcquistoDAO;
import model.ordine.OrdineBean;
import model.ordine.OrdineDAO;
import model.utente.UtenteBean;

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
        final String ERROR_PAGE = "/pages/errorpage.jsp";
        UtenteBean utenteBean = (UtenteBean) req.getSession().getAttribute("utente");

        OrdineDAO ordineDAO = new OrdineDAO();
        AcquistoDAO acquistoDAO = new AcquistoDAO();

        try {
            Collection<OrdineBean> ordini = ordineDAO.doRetrieveByKey(utenteBean.getUsername());

            Map<OrdineBean, Collection<AcquistoBean>> map =
                    buildOrdiniAcquistiMap(ordini, acquistoDAO);

            req.setAttribute("ordini", map);
            req.getRequestDispatcher("pages/profilo.jsp").forward(req, resp);

        } catch (SQLException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
        }
    }

    private Map<OrdineBean, Collection<AcquistoBean>> buildOrdiniAcquistiMap(
            Collection<OrdineBean> ordini,
            AcquistoDAO acquistoDAO
    ) throws SQLException {

        Map<OrdineBean, Collection<AcquistoBean>> map = new HashMap<>();

        for (OrdineBean o : ordini) {
            map.put(o, acquistoDAO.doRetrieveByOrdine(o.getID()));
        }

        return map;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}

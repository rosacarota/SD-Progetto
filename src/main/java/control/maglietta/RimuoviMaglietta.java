package control.maglietta;

import model.CarrelloModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/RimuoviMaglietta")
public class RimuoviMaglietta extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();
        CarrelloModel carrello;

        synchronized (session) {
            carrello = (CarrelloModel) session.getAttribute("carrello");
        }

        int ID;
        try {
            ID = Integer.parseInt(req.getParameter("ID"));
        } catch (NumberFormatException e) {
            resp.sendRedirect("pages/errorpage.jsp");
            return;
        }

        String taglia = req.getParameter("taglia");

        carrello.rimuovi(ID, taglia);
    }
}

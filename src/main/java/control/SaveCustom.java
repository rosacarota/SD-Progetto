package control;

import model.CarrelloModel;
import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/SaveCustom")
public class SaveCustom extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        final String PATH = req.getServletContext().getRealPath("/images/grafiche/");
        String imgData = req.getParameter("imgData");

        if (imgData == null || imgData.isEmpty()) {
            try {
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            } catch (ServletException | IOException e) {
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            }
            return;
        }

        MagliettaDAO magliettaDAO = new MagliettaDAO();

        try {
            String base64Data = imgData.substring(imgData.indexOf(",") + 1);
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);

            String nomeFile = magliettaDAO.getMaxID() + "PersonalizzataCustom.png";
            String relativePath = "images/grafiche/" + nomeFile;
            String filePath = PATH + nomeFile;

            java.nio.file.Files.write(
                    java.nio.file.Paths.get(filePath),
                    imageBytes
            );

            MagliettaBean maglietta = new MagliettaBean();
            maglietta.setNome("Custom");
            maglietta.setColore(req.getParameter("colore"));
            maglietta.setTipo("Personalizzata");
            maglietta.setPrezzo(20);
            maglietta.setIVA(3);
            maglietta.setDescrizione("Maglietta custom");
            maglietta.setGrafica(relativePath);

            magliettaDAO.doSave(maglietta);

            HttpSession session = req.getSession();
            CarrelloModel carrello;

            synchronized (session) {
                carrello = (CarrelloModel) session.getAttribute("carrello");
                if (carrello == null) {
                    carrello = new CarrelloModel();
                    session.setAttribute("carrello", carrello);
                }
            }

            int idMaglietta = magliettaDAO.getMaxID() - 1;
            String taglia = req.getParameter("taglia");

            carrello.aggiungi(idMaglietta, taglia);
            resp.sendRedirect("pages/carrello.jsp");

        } catch (SQLException | IOException e) {
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

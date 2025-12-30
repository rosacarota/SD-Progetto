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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/SaveCustom")
public class SaveCustom extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String PATH = req.getServletContext().getRealPath("/images/grafiche/");
        final String ERROR_PAGE = "/pages/errorpage.jsp";

        String imgData = req.getParameter("imgData");

        if (imgData != null && !imgData.isEmpty()) {
            // Rimuovi l'intestazione "data:image/png;base64," dal dato dell'immagine
            String base64Data = imgData.substring(imgData.indexOf(",") + 1);

            // Decodifica l'immagine Base64 in bytes
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);

            // DAO per getIDMax
            MagliettaDAO magliettaDAO = new MagliettaDAO();
            try {
                String nomeFile = magliettaDAO.getMaxID() + "Personalizzata " + "Custom" + ".png";
                String relativePath = "images" + File.separator + "grafiche" + File.separator + nomeFile;

                // Imposta il percorso del file in cui salvare l'immagine sul server
                String filePath = PATH + nomeFile;

                // Salva l'immagine sul server
                java.nio.file.Files.write(java.nio.file.Paths.get(filePath), imageBytes);

                // Creazione magliettaBean
                MagliettaBean maglietta = new MagliettaBean();
                maglietta.setNome("Custom");
                maglietta.setColore(req.getParameter("colore"));
                maglietta.setTipo("Personalizzata");
                maglietta.setPrezzo(20);
                maglietta.setIVA(3);
                maglietta.setDescrizione("Maglietta custom");
                maglietta.setGrafica(relativePath);

                // Salvataggio maglietta
                magliettaDAO.doSave(maglietta);
            } catch (SQLException e) {
                req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            }

            HttpSession session = req.getSession();
            CarrelloModel carrello;

            synchronized (session) {
                carrello = (CarrelloModel) session.getAttribute("carrello");
                if (carrello == null) {
                    carrello = new CarrelloModel();
                    session.setAttribute("carrello", carrello);
                }
            }

            try {
                int ID = magliettaDAO.getMaxID() - 1;
                String taglia = req.getParameter("taglia");

                carrello.aggiungi(ID, taglia);
                resp.sendRedirect("pages/carrello.jsp");
            } catch (SQLException e) {
                req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            }
        } else {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}

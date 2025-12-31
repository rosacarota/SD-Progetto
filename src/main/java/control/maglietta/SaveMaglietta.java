package control.maglietta;

import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;
import model.misura.MisuraBean;
import model.misura.MisuraDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebServlet("/SaveMaglietta")
@MultipartConfig
public class SaveMaglietta extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        final String ERROR_PAGE = "/pages/errorpage.jsp";
        final String PATH = req.getServletContext().getRealPath("/images/grafiche/");
        Path uploadDir = Paths.get(PATH).toAbsolutePath().normalize();

        String nome = req.getParameter("nome");
        String colore = req.getParameter("colore");
        String tipo = req.getParameter("tipo");
        int iva;
        try {
            iva = (int) Float.parseFloat(req.getParameter("IVA"));
        } catch (NumberFormatException | NullPointerException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        float prezzo;
        try {
            prezzo = Float.parseFloat(req.getParameter("prezzo"));
        } catch (NumberFormatException | NullPointerException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }
        String descrizione = req.getParameter("descrizione");

        Part grafica;
        try {
            grafica = req.getPart("grafica");
        } catch (IOException | ServletException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        MagliettaDAO magliettaDAO = new MagliettaDAO();

        String nomeFile;
        int extensionIndex = grafica.getSubmittedFileName().lastIndexOf(".");

        try {
            String estensione = grafica.getSubmittedFileName().substring(extensionIndex);
            nomeFile = magliettaDAO.getMaxID() + tipo + estensione;
        } catch (SQLException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        nomeFile = nomeFile.replaceAll("[^a-zA-Z0-9._-]", "_");

        String relativePath = "images/grafiche/" + nomeFile;

        Path destinationFile = uploadDir.resolve(nomeFile).normalize();

        if (!destinationFile.startsWith(uploadDir)) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        try (InputStream inputStream = grafica.getInputStream()) {
            Files.copy(inputStream, destinationFile);
        } catch (IOException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        MagliettaBean maglietta = new MagliettaBean();
        maglietta.setNome(nome);
        maglietta.setColore(colore);
        maglietta.setTipo(tipo);
        maglietta.setPrezzo(prezzo);
        maglietta.setIVA(iva);
        maglietta.setDescrizione(descrizione);
        maglietta.setGrafica(relativePath);

        String taglia = req.getParameter("taglia");
        int quantita;
        try {
            quantita = Integer.parseInt(req.getParameter("quantita"));
        } catch (NumberFormatException | NullPointerException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        MisuraDAO misuraDAO = new MisuraDAO();

        try {
            magliettaDAO.doSave(maglietta);
            MisuraBean misuraBean =
                    new MisuraBean(magliettaDAO.getMaxID() - 1, quantita, taglia);
            misuraDAO.doSave(misuraBean);
        } catch (SQLException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        resp.sendRedirect("./Catalogo");
    }
}

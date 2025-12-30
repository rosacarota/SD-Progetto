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
import java.util.stream.Stream;

@WebServlet("/UpdateMaglietta")
@MultipartConfig
public class UpdateMaglietta extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final String ERROR_PAGE = "/pages/errorpage.jsp";
        final String PATH = req.getServletContext().getRealPath("/images/grafiche/");
        Path uploadDir = Paths.get(PATH).toAbsolutePath().normalize();

        int ID;
        try {
            ID = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException | NullPointerException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        String nome = req.getParameter("nome");

        float prezzo;
        try {
            prezzo = Float.parseFloat(req.getParameter("prezzo"));
        } catch (NumberFormatException | NullPointerException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        int IVA;
        try {
            IVA = (int) Float.parseFloat(req.getParameter("IVA"));
        } catch (NumberFormatException | NullPointerException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        String colore = req.getParameter("colore");
        String descrizione = req.getParameter("descrizione");
        String pathGrafica = req.getParameter("path");
        Part grafica = req.getPart("grafica");
        try {
            grafica = req.getPart("grafica");
        } catch (IOException | ServletException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        if (colore == null)
            colore = req.getParameter("coloreVecchio");

        String tipoTmp = req.getParameter("tipo");
        if (tipoTmp == null)
            tipoTmp = req.getParameter("tipoVecchio");

        final String tipo = tipoTmp;

        MagliettaDAO magliettaDAO = new MagliettaDAO();

        if (grafica != null && !grafica.getSubmittedFileName().isEmpty()) {

            int extensionIndex = grafica.getSubmittedFileName().lastIndexOf(".");
            String estensione = grafica.getSubmittedFileName().substring(extensionIndex);
            String nomeFile = ID + tipo + estensione;

            nomeFile = nomeFile.replaceAll("[^a-zA-Z0-9._-]", "_");

            pathGrafica = "images/grafiche/" + nomeFile;

            try (Stream<Path> files = Files.list(uploadDir)) {
                files
                        .filter(p -> p.getFileName().toString().startsWith(ID + tipo))
                        .forEach(p -> {
                            try {
                                if (p.normalize().startsWith(uploadDir)) {
                                    Files.deleteIfExists(p);
                                }
                            } catch (IOException ignored) {
                                // Intentionally ignored:
                                // failure to delete a previous graphic file must not prevent
                                // the upload of the new graphic or the update of the product.
                            }
                        });
            } catch (IOException e) {
                req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
                return;
            }

            Path destinationFile = uploadDir.resolve(nomeFile).normalize();

            if (!destinationFile.startsWith(uploadDir)) {
                req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
                return;
            }

            try (InputStream inputStream = grafica.getInputStream()) {
                Files.copy(inputStream, destinationFile);
            } catch (IOException e) {
                req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
                return;
            }
        }

        MagliettaBean maglietta = new MagliettaBean();
        maglietta.setID(ID);
        maglietta.setNome(nome);
        maglietta.setPrezzo(prezzo);
        maglietta.setIVA(IVA);
        maglietta.setColore(colore);
        maglietta.setTipo(tipo);
        maglietta.setDescrizione(descrizione);
        maglietta.setGrafica(pathGrafica);


        int quantita;
        try {
            quantita = Integer.parseInt(req.getParameter("quantita"));
        } catch (NumberFormatException | NullPointerException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        String taglia = req.getParameter("taglia");

        MisuraBean misuraBean = new MisuraBean(ID, quantita, taglia);
        MisuraDAO misuraDAO = new MisuraDAO();

        try {
            magliettaDAO.doUpdate(maglietta);
            misuraDAO.doUpdate(misuraBean);
        } catch (SQLException e) {
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect("Catalogo");
    }
}

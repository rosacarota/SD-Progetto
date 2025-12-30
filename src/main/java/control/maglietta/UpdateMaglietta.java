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

        int ID, IVA, quantita;
        float prezzo;

        try {
            ID = Integer.parseInt(req.getParameter("id"));
            prezzo = Float.parseFloat(req.getParameter("prezzo"));
            IVA = (int) Float.parseFloat(req.getParameter("IVA"));
            quantita = Integer.parseInt(req.getParameter("quantita"));
        } catch (NumberFormatException | NullPointerException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        String nome = req.getParameter("nome");
        String descrizione = req.getParameter("descrizione");
        String taglia = req.getParameter("taglia");

        String colore = req.getParameter("colore");
        colore = (colore != null) ? colore : req.getParameter("coloreVecchio");

        String tipo = req.getParameter("tipo");
        tipo = (tipo != null) ? tipo : req.getParameter("tipoVecchio");

        String pathGrafica = req.getParameter("path");

        Part grafica;
        try {
            grafica = req.getPart("grafica");
            if (grafica != null && grafica.getSubmittedFileName().isEmpty()) {
                grafica = null;
            }
        } catch (IOException | ServletException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        if (grafica != null) {

            int extensionIndex = grafica.getSubmittedFileName().lastIndexOf(".");
            String estensione = grafica.getSubmittedFileName().substring(extensionIndex);
            String nomeFile = (ID + tipo + estensione)
                    .replaceAll("[^a-zA-Z0-9._-]", "_");

            pathGrafica = "images/grafiche/" + nomeFile;

            try (Stream<Path> files = Files.list(uploadDir)) {
                String finalTipo = tipo;
                files.filter(p -> p.normalize().startsWith(uploadDir))
                        .filter(p -> p.getFileName().toString().startsWith(ID + finalTipo))
                        .forEach(p -> {
                            try {
                                Files.deleteIfExists(p);
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
                req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
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

        MisuraBean misuraBean = new MisuraBean(ID, quantita, taglia);

        try {
            new MagliettaDAO().doUpdate(maglietta);
            new MisuraDAO().doUpdate(misuraBean);
        } catch (SQLException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
            return;
        }

        resp.sendRedirect("Catalogo");
    }
}

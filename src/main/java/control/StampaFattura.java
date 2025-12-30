package control;

import model.acquisto.AcquistoBean;
import model.ordine.OrdineBean;
import model.utente.UtenteBean;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Map;

@WebServlet("/StampaFattura")
public class StampaFattura extends HttpServlet {

    private static final String FILE_NAME = "fattura.pdf";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        final String PATH = req.getServletContext().getRealPath("/pdf/");

        PDDocument document = null;
        PDPageContentStream contentStream = null;

        try {
            int idOrdine = Integer.parseInt(req.getParameter("IDOrdine"));

            document = PDDocument.load(new File(PATH + FILE_NAME));

            UtenteBean utenteBean = (UtenteBean) req.getSession().getAttribute("utente");
            Map<OrdineBean, Collection<AcquistoBean>> ordini =
                    (Map<OrdineBean, Collection<AcquistoBean>>) req.getSession().getAttribute("ordini");

            Map.Entry<OrdineBean, Collection<AcquistoBean>> result =
                    findOrder(ordini, idOrdine);

            if (result == null) {
                throw new ServletException();
            }

            OrdineBean ordine = result.getKey();
            Collection<AcquistoBean> acquisti = result.getValue();


            PDPage page = document.getPage(0);
            contentStream = new PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true
            );

            contentStream.setFont(PDType1Font.HELVETICA, 8);

            contentStream.beginText();
            contentStream.newLineAtOffset(446.609f, 767.3385f);
            contentStream.showText(utenteBean.getNumCarta());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(430.864f, 635.7037f);
            contentStream.showText(LocalDate.now()
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(430.864f, 618.963f);
            contentStream.showText(ordine.getDataOrdine()
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(46.6203f, 642.2537f);
            contentStream.showText(utenteBean.getNome());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(46.6203f, 631.2537f);
            contentStream.showText(utenteBean.getCognome());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(46.6203f, 620.2537f);
            contentStream.showText(utenteBean.getVia() + " " + utenteBean.getCap());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(46.6203f, 609.2537f);
            contentStream.showText(utenteBean.getCitta());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(405.1517f, 591.2341f);
            contentStream.showText(ordine.getPrezzoTotale() + " euro");
            contentStream.endText();

            float coord = 448.0316f;

            for (AcquistoBean a : acquisti) {
                contentStream.beginText();
                contentStream.newLineAtOffset(92.7409f, coord);
                contentStream.showText(String.valueOf(a.getIDMaglietta()));
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(288.2813f, coord);
                contentStream.showText(String.valueOf(a.getQuantita()));
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(344.4793f, coord);
                contentStream.showText(a.getPrezzoAq() + " euro");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(515.6068f, coord);
                contentStream.showText((a.getPrezzoAq() * a.getQuantita()) + " euro");
                contentStream.endText();

                coord -= 15;
            }

            contentStream.close();
            document.save(new File(PATH + "output.pdf"));
            document.close();

            resp.setContentType("application/pdf");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=output.pdf");
            resp.sendRedirect("pdf/output.pdf");

        } catch (NumberFormatException | IOException | ServletException e) {
            if (contentStream != null) {
                try {
                    contentStream.close();
                } catch (IOException ignored) {
                    System.err.println("Failed to close PDF content stream");
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException ignored) {
                    System.err.println("Failed to close PDF content stream");

                }
            }
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    private Map.Entry<OrdineBean, Collection<AcquistoBean>> findOrder(
            Map<OrdineBean, Collection<AcquistoBean>> ordini,
            int idOrdine) {

        for (Map.Entry<OrdineBean, Collection<AcquistoBean>> entry : ordini.entrySet()) {
            if (entry.getKey().getID() == idOrdine) {
                return entry;
            }
        }
        return null;
    }

}

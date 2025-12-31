package control;

import model.acquisto.AcquistoBean;
import model.ordine.OrdineBean;
import model.utente.UtenteBean;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class StampaFatturaTest {

    private StampaFattura servlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;
    private ServletContext context;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setup() {
        servlet = new StampaFattura();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        context = mock(ServletContext.class);
        dispatcher = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);
        when(req.getServletContext()).thenReturn(context);
        when(context.getRealPath("/pdf/")).thenReturn("/tmp/");
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcher);
    }

    // -------- Test doPost() --------

    // {id_valido, ordine_trovato, pdf_ok, scrittura_pdf = effettuata}
    @Test
    void doPost_ok_pdfWritingVerified() throws Exception {
        when(req.getParameter("IDOrdine")).thenReturn("1");

        UtenteBean utente = mock(UtenteBean.class);
        when(utente.getNumCarta()).thenReturn("1234");
        when(utente.getNome()).thenReturn("Mario");
        when(utente.getCognome()).thenReturn("Rossi");
        when(utente.getVia()).thenReturn("Via Mango");
        when(utente.getCap()).thenReturn("80000");
        when(utente.getCitta()).thenReturn("Casotto");

        OrdineBean ordine = mock(OrdineBean.class);
        when(ordine.getID()).thenReturn(1);
        when(ordine.getPrezzoTotale()).thenReturn(10f);
        when(ordine.getDataOrdine()).thenReturn(LocalDate.now());

        AcquistoBean acquisto = mock(AcquistoBean.class);
        when(acquisto.getIDMaglietta()).thenReturn(1);
        when(acquisto.getQuantita()).thenReturn(1);
        when(acquisto.getPrezzoAq()).thenReturn(10f);

        Map<OrdineBean, Collection<AcquistoBean>> ordini = new HashMap<>();
        ordini.put(ordine, List.of(acquisto));

        when(session.getAttribute("utente")).thenReturn(utente);
        when(session.getAttribute("ordini")).thenReturn(ordini);

        PDDocument documentMock = mock(PDDocument.class);
        when(documentMock.getPage(0)).thenReturn(mock(PDPage.class));

        List<PDPageContentStream> createdStreams = new ArrayList<>();

        try (
                MockedStatic<PDDocument> mockedStatic = mockStatic(PDDocument.class);
                MockedConstruction<PDPageContentStream> mockedStream =
                        mockConstruction(PDPageContentStream.class,
                                (cs, ctx) -> createdStreams.add(cs))
        ) {
            mockedStatic.when(() -> PDDocument.load(any(File.class)))
                    .thenReturn(documentMock);

            servlet.doPost(req, resp);

            PDPageContentStream cs = createdStreams.get(0);

            verify(resp).sendRedirect("pdf/output.pdf");
            verify(dispatcher, never()).forward(req, resp);
        }
    }

    // {id_valido, ordine_non_trovato}
    @Test
    void doPost_ordineNonTrovato() throws Exception {
        when(req.getParameter("IDOrdine")).thenReturn("5");
        when(session.getAttribute("utente")).thenReturn(mock(UtenteBean.class));
        when(session.getAttribute("ordini")).thenReturn(new HashMap<>());

        try (MockedStatic<PDDocument> mockedStatic = mockStatic(PDDocument.class)) {
            mockedStatic.when(() -> PDDocument.load(any(File.class)))
                    .thenReturn(mock(PDDocument.class));

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
        }
    }

    // {id_non_numerico}
    @Test
    void doPost_idNonNumerico() throws Exception {
        when(req.getParameter("IDOrdine")).thenReturn("abc");

        servlet.doPost(req, resp);

        verify(dispatcher).forward(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    // {pdf_exception}
    @Test
    void doPost_pdfException() throws Exception {
        when(req.getParameter("IDOrdine")).thenReturn("1");

        try (MockedStatic<PDDocument> mocked = mockStatic(PDDocument.class)) {
            mocked.when(() -> PDDocument.load(any(File.class)))
                    .thenThrow(new IOException("PDF error"));

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
        }
    }

    // {id_valido, ordine_trovato, eccezione_dopo_creazione, cleanup_eseguito}
    @Test
    void doPost_cleanup_contentStreamAndDocumentCovered() throws Exception {
        when(req.getParameter("IDOrdine")).thenReturn("1");

        UtenteBean utente = mock(UtenteBean.class);
        when(utente.getNumCarta()).thenReturn("1234");
        when(utente.getNome()).thenReturn("Mario");
        when(utente.getCognome()).thenReturn("Rossi");
        when(utente.getVia()).thenReturn("Via Mango");
        when(utente.getCap()).thenReturn("80000");
        when(utente.getCitta()).thenReturn("Casotto");

        OrdineBean ordine = mock(OrdineBean.class);
        when(ordine.getID()).thenReturn(1);
        when(ordine.getPrezzoTotale()).thenReturn(10f);
        when(ordine.getDataOrdine()).thenReturn(LocalDate.now());

        AcquistoBean acquisto = mock(AcquistoBean.class);
        when(acquisto.getIDMaglietta()).thenReturn(1);
        when(acquisto.getQuantita()).thenReturn(1);
        when(acquisto.getPrezzoAq()).thenReturn(10f);

        Map<OrdineBean, Collection<AcquistoBean>> ordini = new HashMap<>();
        ordini.put(ordine, List.of(acquisto));

        when(session.getAttribute("utente")).thenReturn(utente);
        when(session.getAttribute("ordini")).thenReturn(ordini);

        PDDocument documentMock = mock(PDDocument.class);
        when(documentMock.getPage(0)).thenReturn(mock(PDPage.class));

        doThrow(new IOException("save failed"))
                .when(documentMock).save(any(File.class));

        try (
                MockedStatic<PDDocument> mockedStatic = mockStatic(PDDocument.class);
                MockedConstruction<PDPageContentStream> mockedStream =
                        mockConstruction(PDPageContentStream.class)
        ) {
            mockedStatic.when(() -> PDDocument.load(any(File.class)))
                    .thenReturn(documentMock);

            servlet.doPost(req, resp);

            verify(documentMock).close();
            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
        }
    }

    // {id_valido, ordine_trovato, eccezione_dopo_creazione, contentStream_close_throws_IOException}
    @Test
    void doPost_cleanup_contentStreamCloseThrowsIOException() throws Exception {
        when(req.getParameter("IDOrdine")).thenReturn("1");

        UtenteBean utente = mock(UtenteBean.class);
        when(utente.getNumCarta()).thenReturn("1234");
        when(utente.getNome()).thenReturn("Mario");
        when(utente.getCognome()).thenReturn("Rossi");
        when(utente.getVia()).thenReturn("Via Mango");
        when(utente.getCap()).thenReturn("80000");
        when(utente.getCitta()).thenReturn("Casotto");

        OrdineBean ordine = mock(OrdineBean.class);
        when(ordine.getID()).thenReturn(1);
        when(ordine.getPrezzoTotale()).thenReturn(10f);
        when(ordine.getDataOrdine()).thenReturn(LocalDate.now());

        AcquistoBean acquisto = mock(AcquistoBean.class);
        when(acquisto.getIDMaglietta()).thenReturn(1);
        when(acquisto.getQuantita()).thenReturn(1);
        when(acquisto.getPrezzoAq()).thenReturn(10f);

        Map<OrdineBean, Collection<AcquistoBean>> ordini = new HashMap<>();
        ordini.put(ordine, List.of(acquisto));

        when(session.getAttribute("utente")).thenReturn(utente);
        when(session.getAttribute("ordini")).thenReturn(ordini);

        PDDocument documentMock = mock(PDDocument.class);
        when(documentMock.getPage(0)).thenReturn(mock(PDPage.class));
        doThrow(new IOException("save failed")).when(documentMock).save(any(File.class));

        try (
                MockedStatic<PDDocument> mockedStatic = mockStatic(PDDocument.class);
                MockedConstruction<PDPageContentStream> mockedStream =
                        mockConstruction(PDPageContentStream.class,
                                (cs, ctx) -> doThrow(new IOException("close fail")).when(cs).close())
        ) {
            mockedStatic.when(() -> PDDocument.load(any(File.class)))
                    .thenReturn(documentMock);

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
        }
    }

    // {id_valido, ordine_trovato, eccezione_dopo_creazione, document_close_throws_IOException}
    @Test
    void doPost_cleanup_documentCloseThrowsIOException() throws Exception {
        when(req.getParameter("IDOrdine")).thenReturn("1");

        UtenteBean utente = mock(UtenteBean.class);
        when(utente.getNumCarta()).thenReturn("1234");
        when(utente.getNome()).thenReturn("Mario");
        when(utente.getCognome()).thenReturn("Rossi");
        when(utente.getVia()).thenReturn("Via Mango");
        when(utente.getCap()).thenReturn("80000");
        when(utente.getCitta()).thenReturn("Casotto");

        OrdineBean ordine = mock(OrdineBean.class);
        when(ordine.getID()).thenReturn(1);
        when(ordine.getPrezzoTotale()).thenReturn(10f);
        when(ordine.getDataOrdine()).thenReturn(LocalDate.now());

        AcquistoBean acquisto = mock(AcquistoBean.class);
        when(acquisto.getIDMaglietta()).thenReturn(1);
        when(acquisto.getQuantita()).thenReturn(1);
        when(acquisto.getPrezzoAq()).thenReturn(10f);

        Map<OrdineBean, Collection<AcquistoBean>> ordini = new HashMap<>();
        ordini.put(ordine, List.of(acquisto));

        when(session.getAttribute("utente")).thenReturn(utente);
        when(session.getAttribute("ordini")).thenReturn(ordini);

        PDDocument documentMock = mock(PDDocument.class);
        when(documentMock.getPage(0)).thenReturn(mock(PDPage.class));
        doThrow(new IOException("save failed")).when(documentMock).save(any(File.class));
        doThrow(new IOException("close doc fail")).when(documentMock).close();

        try (
                MockedStatic<PDDocument> mockedStatic = mockStatic(PDDocument.class);
                MockedConstruction<PDPageContentStream> mockedStream =
                        mockConstruction(PDPageContentStream.class)
        ) {
            mockedStatic.when(() -> PDDocument.load(any(File.class)))
                    .thenReturn(documentMock);

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
        }
    }

    // -------- Test doGet() --------

    // {doGet_delegates_to_doPost}
    @Test
    void doGet_callsDoPost() throws Exception {
        StampaFattura spyServlet = spy(new StampaFattura());
        doNothing().when(spyServlet).doPost(req, resp);

        spyServlet.doGet(req, resp);

        verify(spyServlet).doPost(req, resp);
    }

    // -------- Test findOrder() --------

    // {ordine_non_trovato}
    @Test
    void findOrder_nonTrovato() throws Exception {
        OrdineBean ordine = mock(OrdineBean.class);
        when(ordine.getID()).thenReturn(1);

        Map<OrdineBean, Collection<AcquistoBean>> ordini = new HashMap<>();
        ordini.put(ordine, List.of());

        Method m = StampaFattura.class.getDeclaredMethod("findOrder", Map.class, int.class);
        m.setAccessible(true);

        Object result = m.invoke(servlet, ordini, 2);

        assertNull(result);
    }
}

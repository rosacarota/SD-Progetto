package control.ordine;

import model.CarrelloModel;
import model.acquisto.AcquistoBean;
import model.acquisto.AcquistoDAO;
import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaOrdine;
import model.misura.MisuraDAO;
import model.ordine.OrdineBean;
import model.ordine.OrdineDAO;
import model.utente.UtenteBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CheckoutTest {

    private Checkout servlet;

    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;

    private RequestDispatcher dispatcher;

    private CarrelloModel carrello;
    private UtenteBean utente;

    @BeforeEach
    void setup() {
        servlet = new Checkout();

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        dispatcher = mock(RequestDispatcher.class);

        carrello = mock(CarrelloModel.class);
        utente = mock(UtenteBean.class);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("carrello")).thenReturn(carrello);
        when(session.getAttribute("utente")).thenReturn(utente);

        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcher);

        when(utente.getUsername()).thenReturn("mango");
    }

    // -------- Test doPost() --------

    // {data_consegna_null, forward_error}
    @Test
    void doPost_dataConsegnaNull_forwardError() throws Exception {
        when(req.getParameter("data-consegna")).thenReturn(null);
        when(req.getParameter("prezzo-totale")).thenReturn("10");

        servlet.doPost(req, resp);

        verify(dispatcher).forward(req, resp);
        verify(resp, never()).sendRedirect(anyString());
        verify(session, never()).removeAttribute("carrello");
    }

    // {prezzo_totale_non_numerico, forward_error}
    @Test
    void doPost_prezzoTotaleNonNumerico_forwardError() throws Exception {
        when(req.getParameter("data-consegna")).thenReturn("2030-01-01");
        when(req.getParameter("prezzo-totale")).thenReturn("abc");

        servlet.doPost(req, resp);

        verify(dispatcher).forward(req, resp);
        verify(resp, never()).sendRedirect(anyString());
        verify(session, never()).removeAttribute("carrello");
    }

    // {input_valido, carrello_una_riga, db_ok, redirect_ok}
    @Test
    void doPost_ok_carrelloUnaRiga_redirectOk() throws Exception {
        when(req.getParameter("data-consegna")).thenReturn("2030-01-01");
        when(req.getParameter("prezzo-totale")).thenReturn("20");
        when(req.getParameter("nome-spedizione")).thenReturn("Mario");
        when(req.getParameter("cognome-spedizione")).thenReturn("Rossi");
        when(req.getParameter("cap-spedizione")).thenReturn("80000");
        when(req.getParameter("via-spedizione")).thenReturn("Via Mango");
        when(req.getParameter("citta-spedizione")).thenReturn("Casotto");

        MagliettaOrdine p = mock(MagliettaOrdine.class);
        MagliettaBean m = mock(MagliettaBean.class);

        when(p.getMagliettaBean()).thenReturn(m);
        when(m.getID()).thenReturn(7);
        when(m.getGrafica()).thenReturn("img.png");
        when(m.getPrezzo()).thenReturn(15.0f);
        when(m.getIVA()).thenReturn(22);
        when(p.getQuantita()).thenReturn(2);
        when(p.getTaglia()).thenReturn("M");

        when(carrello.getCarrello()).thenReturn(Collections.singletonList(p));

        try (MockedConstruction<OrdineDAO> ordineCons = mockConstruction(OrdineDAO.class, (mock, ctx) -> {
                 if (ctx.getCount() == 1) {
                     doNothing().when(mock).doSave(any(OrdineBean.class));
                 } else {
                     when(mock.getMaxID()).thenReturn(5);
                 }
             });
             MockedConstruction<AcquistoDAO> acquistoCons = mockConstruction(AcquistoDAO.class, (mock, ctx) -> {
                 doAnswer(inv -> {
                     AcquistoBean a = inv.getArgument(0);
                     assertEquals(4, a.getIDOrdine());
                     assertEquals(7, a.getIDMaglietta());
                     assertEquals(2, a.getQuantita());
                     assertEquals("img.png", a.getImmagine());
                     assertEquals(15.0f, a.getPrezzoAq(), 0.0001f);
                     assertEquals(22, a.getIvaAq());
                     assertEquals("M", a.getTaglia());
                     return null;
                 }).when(mock).doSave(any(AcquistoBean.class));
             });
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class, (mock, ctx) -> {
                 doNothing().when(mock).doUpdateUtente(any(AcquistoBean.class), anyString());
             })) {

            servlet.doPost(req, resp);

            OrdineDAO ordineDAO_1 = ordineCons.constructed().get(0);
            verify(ordineDAO_1).doSave(any(OrdineBean.class));

            AcquistoDAO acquistoDAO = acquistoCons.constructed().get(0);
            verify(acquistoDAO).doSave(any(AcquistoBean.class));

            MisuraDAO misuraDAO = misuraCons.constructed().get(0);
            verify(misuraDAO).doUpdateUtente(any(AcquistoBean.class), eq("M"));

            verify(session).removeAttribute("carrello");
            verify(resp).sendRedirect("pages/acquisto.jsp");
            verify(dispatcher, never()).forward(req, resp);
        }
    }

    // {input_valido, ordineDAO_throws, forward_error, redirect_called}
    @Test
    void doPost_ordineDoSaveThrows_forwardError_thenRedirect() throws Exception {
        when(req.getParameter("data-consegna")).thenReturn("2030-01-01");
        when(req.getParameter("prezzo-totale")).thenReturn("20");
        when(req.getParameter("nome-spedizione")).thenReturn("Mario");
        when(req.getParameter("cognome-spedizione")).thenReturn("Rossi");
        when(req.getParameter("cap-spedizione")).thenReturn("80000");
        when(req.getParameter("via-spedizione")).thenReturn("Via Mango");
        when(req.getParameter("citta-spedizione")).thenReturn("Casotto");

        when(carrello.getCarrello()).thenReturn(Collections.emptyList());

        try (MockedConstruction<OrdineDAO> ordineCons = mockConstruction(OrdineDAO.class, (mock, ctx) -> {
                 if (ctx.getCount() == 1) {
                     doThrow(new SQLException("fail")).when(mock).doSave(any(OrdineBean.class));
                 }
             })) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(session).removeAttribute("carrello");
            verify(resp).sendRedirect("pages/acquisto.jsp");
        }
    }

    // {input_valido, carrello_una_riga, acquistoDAO_throws, forward_error, redirect_called}
    @Test
    void doPost_acquistoDoSaveThrows_forwardError_thenRedirect() throws Exception {
        when(req.getParameter("data-consegna")).thenReturn("2030-01-01");
        when(req.getParameter("prezzo-totale")).thenReturn("20");
        when(req.getParameter("nome-spedizione")).thenReturn("Mario");
        when(req.getParameter("cognome-spedizione")).thenReturn("Rossi");
        when(req.getParameter("cap-spedizione")).thenReturn("80000");
        when(req.getParameter("via-spedizione")).thenReturn("Via Mango");
        when(req.getParameter("citta-spedizione")).thenReturn("Casotto");

        MagliettaOrdine p = mock(MagliettaOrdine.class);
        MagliettaBean m = mock(MagliettaBean.class);

        when(p.getMagliettaBean()).thenReturn(m);
        when(m.getID()).thenReturn(7);
        when(m.getGrafica()).thenReturn("img.png");
        when(m.getPrezzo()).thenReturn(15.0f);
        when(m.getIVA()).thenReturn(22);
        when(p.getQuantita()).thenReturn(2);
        when(p.getTaglia()).thenReturn("M");

        when(carrello.getCarrello()).thenReturn(Collections.singletonList(p));

        try (MockedConstruction<OrdineDAO> ordineCons = mockConstruction(OrdineDAO.class, (mock, ctx) -> {
                 if (ctx.getCount() == 1) {
                     doNothing().when(mock).doSave(any(OrdineBean.class));
                 } else {
                     when(mock.getMaxID()).thenReturn(5);
                 }
             });
             MockedConstruction<AcquistoDAO> acquistoCons = mockConstruction(AcquistoDAO.class, (mock, ctx) -> {
                 doThrow(new SQLException("fail")).when(mock).doSave(any(AcquistoBean.class));
             })) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(session).removeAttribute("carrello");
            verify(resp).sendRedirect("pages/acquisto.jsp");
        }
    }
}

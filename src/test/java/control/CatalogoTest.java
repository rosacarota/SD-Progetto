package control;

import control.utente.Login;
import model.maglietta.MagliettaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CatalogoTest {

    private Catalogo servlet;

    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;

    private RequestDispatcher dispatcherOk;
    private RequestDispatcher dispatcherErr;

    @BeforeEach
    void setup() {
        servlet = new Catalogo();

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        dispatcherOk = mock(RequestDispatcher.class);
        dispatcherErr = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);
        when(req.getParameter("ordine")).thenReturn("prezzo");
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcherErr);
    }

    // -------- Test doGet() --------

    // {tipoUtente_admin, db_ok, forward_catalogoAdmin_ok}
    @Test
    void doGet_tipoUtenteAdmin_dbOk_forwardCatalogoAdmin() throws Exception {
        when(session.getAttribute("tipoUtente")).thenReturn(Login.ADMIN);
        when(req.getRequestDispatcher("/catalogoAdmin.jsp")).thenReturn(dispatcherOk);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(eq("prezzo"))).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(req).setAttribute(eq("magliette"), any());
            verify(dispatcherOk).forward(req, resp);
            verify(dispatcherErr, never()).forward(req, resp);

            MagliettaDAO dao = magliettaCons.constructed().get(0);
            verify(dao).doRetriveAll("prezzo");
        }
    }

    // {tipoUtente_non_admin, db_ok, forward_catalogo_ok}
    @Test
    void doGet_tipoUtenteNonAdmin_dbOk_forwardCatalogo() throws Exception {
        when(session.getAttribute("tipoUtente")).thenReturn(Login.REGISTRATO);
        when(req.getRequestDispatcher("/catalogo.jsp")).thenReturn(dispatcherOk);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(eq("prezzo"))).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(req).setAttribute(eq("magliette"), any());
            verify(dispatcherOk).forward(req, resp);
            verify(dispatcherErr, never()).forward(req, resp);
        }
    }

    // {tipoUtente_null, db_ok, forward_catalogo_ok}
    @Test
    void doGet_tipoUtenteNull_dbOk_forwardCatalogo() throws Exception {
        when(session.getAttribute("tipoUtente")).thenReturn(null);
        when(req.getRequestDispatcher("/catalogo.jsp")).thenReturn(dispatcherOk);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(eq("prezzo"))).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(req).setAttribute(eq("magliette"), any());
            verify(dispatcherOk).forward(req, resp);
            verify(dispatcherErr, never()).forward(req, resp);
        }
    }

    // {db_exception, forward_errorpage_ok}
    @Test
    void doGet_dbException_forwardErrorPage() throws Exception {
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcherOk);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(any())).thenThrow(new SQLException("fail"));
        })) {

            servlet.doGet(req, resp);

            verify(dispatcherOk).forward(req, resp);
        }
    }

    // {db_ok, forward_throws_servletException, forward_errorpage}
    @Test
    void doGet_forwardThrowsServletException_thenForwardErrorPage() throws Exception {
        when(session.getAttribute("tipoUtente")).thenReturn(null);

        when(req.getRequestDispatcher("/catalogo.jsp")).thenReturn(dispatcherOk);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcherErr);

        doThrow(new ServletException("boom")).when(dispatcherOk).forward(req, resp);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(eq("prezzo"))).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(dispatcherOk).forward(req, resp);
            verify(dispatcherErr).forward(req, resp);
        }
    }

    // {db_ok, forward_throws_ioexception, forward_errorpage}
    @Test
    void doGet_forwardThrowsIOException_thenForwardErrorPage() throws Exception {
        when(session.getAttribute("tipoUtente")).thenReturn(null);

        when(req.getRequestDispatcher("/catalogo.jsp")).thenReturn(dispatcherOk);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcherErr);

        doThrow(new IOException("boom")).when(dispatcherOk).forward(req, resp);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(eq("prezzo"))).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(dispatcherOk).forward(req, resp);
            verify(dispatcherErr).forward(req, resp);
        }
    }

    // -------- Test doPost() --------

    // {delegazione_a_doGet}
    @Test
    void doPost_delegaDoGet() throws Exception {
        when(session.getAttribute("tipoUtente")).thenReturn(null);
        when(req.getRequestDispatcher("/catalogo.jsp")).thenReturn(dispatcherOk);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(eq("prezzo"))).thenReturn(Collections.emptyList());
        })) {

            servlet.doPost(req, resp);

            verify(dispatcherOk).forward(req, resp);
        }
    }
}

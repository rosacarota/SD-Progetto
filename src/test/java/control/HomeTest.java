package control;

import model.maglietta.MagliettaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HomeTest {

    private Home servlet;

    private HttpServletRequest req;
    private HttpServletResponse resp;

    private RequestDispatcher dispatcherIndex;
    private RequestDispatcher dispatcherError;

    @BeforeEach
    void setup() {
        servlet = new Home();

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);

        dispatcherIndex = mock(RequestDispatcher.class);
        dispatcherError = mock(RequestDispatcher.class);

        when(req.getRequestDispatcher("/index.jsp")).thenReturn(dispatcherIndex);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcherError);
    }

    // -------- Test doGet() --------

    // {ordine_null, db_ok, forward_index_ok}
    @Test
    void doGet_dbOk_forwardIndex() throws Exception {
        when(req.getParameter("ordine")).thenReturn(null);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(isNull())).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(req).setAttribute(eq("magliette"), any());
            verify(dispatcherIndex).forward(req, resp);
            verify(dispatcherError, never()).forward(req, resp);

            MagliettaDAO dao = magliettaCons.constructed().get(0);
            verify(dao).doRetriveAll(null);
        }
    }

    // {ordine_valido, db_ok, forward_index_ok}
    @Test
    void doGet_ordineValido_dbOk_forwardIndex() throws Exception {
        when(req.getParameter("ordine")).thenReturn("prezzo");

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(eq("prezzo"))).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(req).setAttribute(eq("magliette"), any());
            verify(dispatcherIndex).forward(req, resp);
            verify(dispatcherError, never()).forward(req, resp);

            MagliettaDAO dao = magliettaCons.constructed().get(0);
            verify(dao).doRetriveAll("prezzo");
        }
    }

    // {db_exception, forward_error_ok}
    @Test
    void doGet_dbException_forwardError() throws Exception {
        when(req.getParameter("ordine")).thenReturn("prezzo");

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(anyString())).thenThrow(new SQLException("fail"));
        })) {

            servlet.doGet(req, resp);

            verify(dispatcherError).forward(req, resp);
            verify(dispatcherIndex, never()).forward(req, resp);
        }
    }

    // {forward_index_throws, forward_error_ok}
    @Test
    void doGet_forwardIndexThrows_thenForwardError() throws Exception {
        when(req.getParameter("ordine")).thenReturn(null);
        doThrow(new ServletException("boom")).when(dispatcherIndex).forward(req, resp);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(isNull())).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(dispatcherIndex).forward(req, resp);
            verify(dispatcherError).forward(req, resp);
        }
    }

    // {forward_index_throws_io, forward_error_ok}
    @Test
    void doGet_forwardIndexThrowsIOException_thenForwardError() throws Exception {
        when(req.getParameter("ordine")).thenReturn(null);
        doThrow(new IOException("boom")).when(dispatcherIndex).forward(req, resp);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(isNull())).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(dispatcherIndex).forward(req, resp);
            verify(dispatcherError).forward(req, resp);
        }
    }

    // -------- Test doPost() --------

    // {delegazione_a_doGet}
    @Test
    void doPost_delegaDoGet() throws Exception {
        when(req.getParameter("ordine")).thenReturn(null);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetriveAll(isNull())).thenReturn(Collections.emptyList());
        })) {

            servlet.doPost(req, resp);

            verify(dispatcherIndex).forward(req, resp);
        }
    }
}

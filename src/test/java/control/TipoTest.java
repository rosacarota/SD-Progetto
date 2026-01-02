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

class TipoTest {

    private Tipo servlet;

    private HttpServletRequest req;
    private HttpServletResponse resp;

    private RequestDispatcher dispatcherTipo;
    private RequestDispatcher dispatcherErr;

    @BeforeEach
    void setup() {
        servlet = new Tipo();

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);

        dispatcherTipo = mock(RequestDispatcher.class);
        dispatcherErr = mock(RequestDispatcher.class);

        when(req.getRequestDispatcher("magliettaTipo.jsp")).thenReturn(dispatcherTipo);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcherErr);

        when(req.getParameter("tipo")).thenReturn("Film e Serie TV");
    }

    // -------- Test doGet() --------

    // {tipo_valido, db_ok, forward_magliettaTipo_ok}
    @Test
    void doGet_tipoValido_dbOk_forwardMagliettaTipo() throws Exception {
        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetrieveByTipo(eq("Film e Serie TV"))).thenReturn(Collections.emptyList());
        })) {

            servlet.doGet(req, resp);

            verify(req).setAttribute(eq("maglietteTipo"), any());
            verify(dispatcherErr, never()).forward(req, resp);
            verify(dispatcherTipo).forward(req, resp);

            MagliettaDAO dao = magliettaCons.constructed().get(0);
            verify(dao).doRetrieveByTipo("Film e Serie TV");
        }
    }

    // {tipo_valido, db_exception, forward_error_then_forward_magliettaTipo}
    @Test
    void doGet_dbException_forwardError_thenForwardMagliettaTipo() throws Exception {
        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetrieveByTipo(anyString())).thenThrow(new SQLException("fail"));
        })) {

            servlet.doGet(req, resp);

            verify(dispatcherErr).forward(req, resp);
            verify(dispatcherTipo).forward(req, resp);
        }
    }

    // {tipo_valido, forward_magliettaTipo_throws_servletException, propagate}
    @Test
    void doGet_forwardMagliettaTipoThrowsServletException_propagates() throws Exception {
        doThrow(new ServletException("boom")).when(dispatcherTipo).forward(req, resp);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetrieveByTipo(anyString())).thenReturn(Collections.emptyList());
        })) {

            org.junit.jupiter.api.Assertions.assertThrows(ServletException.class, () -> servlet.doGet(req, resp));
        }
    }

    // {tipo_valido, forward_magliettaTipo_throws_ioexception, propagate}
    @Test
    void doGet_forwardMagliettaTipoThrowsIOException_propagates() throws Exception {
        doThrow(new IOException("boom")).when(dispatcherTipo).forward(req, resp);

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetrieveByTipo(anyString())).thenReturn(Collections.emptyList());
        })) {

            org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> servlet.doGet(req, resp));
        }
    }

    // -------- Test doPost() --------

    // {delegazione_a_doGet}
    @Test
    void doPost_delegaDoGet() throws Exception {
        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
            when(mock.doRetrieveByTipo(anyString())).thenReturn(Collections.emptyList());
        })) {

            servlet.doPost(req, resp);

            verify(dispatcherTipo).forward(req, resp);
        }
    }
}

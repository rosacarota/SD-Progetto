package control;

import model.CarrelloModel;
import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SaveCustomTest {

    private SaveCustom servlet;

    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;
    private ServletContext servletContext;

    private RequestDispatcher dispatcher;

    @BeforeEach
    void setup() {
        servlet = new SaveCustom();

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        servletContext = mock(ServletContext.class);

        dispatcher = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);
        when(req.getServletContext()).thenReturn(servletContext);
        when(servletContext.getRealPath("/images/grafiche/")).thenReturn("/tmp/");

        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcher);
    }

    // -------- Test doPost() --------

    // {imgData_null, forward_error}
    @Test
    void doPost_imgDataNull_forwardError() throws Exception {
        when(req.getParameter("imgData")).thenReturn(null);

        servlet.doPost(req, resp);

        verify(dispatcher).forward(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    // {imgData_empty, forward_error}
    @Test
    void doPost_imgDataEmpty_forwardError() throws Exception {
        when(req.getParameter("imgData")).thenReturn("");

        servlet.doPost(req, resp);

        verify(dispatcher).forward(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }

    // {imgData_valida, carrello_null, db_ok, file_write_ok, redirect_ok}
    @Test
    void doPost_ok_creaMaglietta_salva_aggiungeAlCarrello_redirect() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3};
        String imgData = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

        when(req.getParameter("imgData")).thenReturn(imgData);
        when(req.getParameter("colore")).thenReturn("Bianco");
        when(req.getParameter("taglia")).thenReturn("M");

        when(session.getAttribute("carrello")).thenReturn(null);

        try (MockedStatic<java.nio.file.Files> files = mockStatic(java.nio.file.Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
                 when(mock.getMaxID()).thenReturn(10, 10);

                 doAnswer(inv -> {
                     MagliettaBean m = inv.getArgument(0);

                     assertEquals("Custom", m.getNome());
                     assertEquals("Bianco", m.getColore());
                     assertEquals("Personalizzata", m.getTipo());
                     assertEquals(20.0f, m.getPrezzo(), 0.0001f);
                     assertEquals(3, m.getIVA());
                     assertEquals("Maglietta custom", m.getDescrizione());
                     assertEquals("images/grafiche/10PersonalizzataCustom.png", m.getGrafica());

                     return null;
                 }).when(mock).doSave(any(MagliettaBean.class));
             })) {

            files.when(() -> java.nio.file.Files.write(any(Path.class), any(byte[].class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            servlet.doPost(req, resp);

            MagliettaDAO magliettaDAO = magliettaCons.constructed().get(0);
            verify(magliettaDAO, atLeastOnce()).getMaxID();
            verify(magliettaDAO).doSave(any(MagliettaBean.class));

            verify(session).setAttribute(eq("carrello"), any(CarrelloModel.class));

            verify(resp).sendRedirect("pages/carrello.jsp");
            verify(dispatcher, never()).forward(req, resp);
        }
    }

    // {imgData_valida, carrello_gia_presente, db_ok, file_write_ok, redirect_ok}
    @Test
    void doPost_ok_carrelloGiaPresente_noSetAttribute_redirect() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3};
        String imgData = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

        when(req.getParameter("imgData")).thenReturn(imgData);
        when(req.getParameter("colore")).thenReturn("Bianco");
        when(req.getParameter("taglia")).thenReturn("M");

        CarrelloModel existing = mock(CarrelloModel.class);
        when(session.getAttribute("carrello")).thenReturn(existing);

        try (MockedStatic<java.nio.file.Files> files = mockStatic(java.nio.file.Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
                 when(mock.getMaxID()).thenReturn(10, 10);
                 doNothing().when(mock).doSave(any(MagliettaBean.class));
             })) {

            files.when(() -> java.nio.file.Files.write(any(Path.class), any(byte[].class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            servlet.doPost(req, resp);

            verify(session, never()).setAttribute(eq("carrello"), any());
            verify(existing).aggiungi(eq(9), eq("M"));

            verify(resp).sendRedirect("pages/carrello.jsp");
            verify(dispatcher, never()).forward(req, resp);
        }
    }

    // {imgData_valida, doSave_throws, forward_error}
    @Test
    void doPost_doSaveThrows_forwardError() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3};
        String imgData = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

        when(req.getParameter("imgData")).thenReturn(imgData);
        when(req.getParameter("colore")).thenReturn("Bianco");
        when(req.getParameter("taglia")).thenReturn("M");

        when(session.getAttribute("carrello")).thenReturn(null);

        try (MockedStatic<java.nio.file.Files> files = mockStatic(java.nio.file.Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
                 when(mock.getMaxID()).thenReturn(10, 10);
                 doThrow(new SQLException("fail")).when(mock).doSave(any(MagliettaBean.class));
             })) {

            files.when(() -> java.nio.file.Files.write(any(Path.class), any(byte[].class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
        }
    }

    // {imgData_valida, file_write_throws, forward_error}
    @Test
    void doPost_fileWriteThrows_forwardError() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3};
        String imgData = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

        when(req.getParameter("imgData")).thenReturn(imgData);
        when(req.getParameter("colore")).thenReturn("Bianco");
        when(req.getParameter("taglia")).thenReturn("M");

        when(session.getAttribute("carrello")).thenReturn(null);

        try (MockedStatic<java.nio.file.Files> files = mockStatic(java.nio.file.Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
                 when(mock.getMaxID()).thenReturn(10, 10);
                 doNothing().when(mock).doSave(any(MagliettaBean.class));
             })) {

            files.when(() -> java.nio.file.Files.write(any(Path.class), any(byte[].class)))
                    .thenThrow(new IOException("fail"));

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
        }
    }

    // -------- Test doGet() --------

    // {delegazione_a_doPost}
    @Test
    void doGet_delegaDoPost() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3};
        String imgData = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

        when(req.getParameter("imgData")).thenReturn(imgData);
        when(req.getParameter("colore")).thenReturn("Bianco");
        when(req.getParameter("taglia")).thenReturn("M");

        when(session.getAttribute("carrello")).thenReturn(null);

        try (MockedStatic<java.nio.file.Files> files = mockStatic(java.nio.file.Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
                 when(mock.getMaxID()).thenReturn(10, 10);
                 doNothing().when(mock).doSave(any(MagliettaBean.class));
             })) {

            files.when(() -> java.nio.file.Files.write(any(Path.class), any(byte[].class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            servlet.doGet(req, resp);

            verify(resp).sendRedirect("pages/carrello.jsp");
            verify(dispatcher, never()).forward(req, resp);
        }
    }
}

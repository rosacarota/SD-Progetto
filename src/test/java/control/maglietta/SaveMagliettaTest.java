package control.maglietta;

import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;
import model.misura.MisuraBean;
import model.misura.MisuraDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SaveMagliettaTest {

    private SaveMaglietta servlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private ServletContext context;
    private RequestDispatcher dispatcher;
    private Part graficaPart;

    @BeforeEach
    void setup() throws Exception {
        servlet = new SaveMaglietta();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        context = mock(ServletContext.class);
        dispatcher = mock(RequestDispatcher.class);
        graficaPart = mock(Part.class);

        when(req.getServletContext()).thenReturn(context);
        when(context.getRealPath("/images/grafiche/")).thenReturn("/tmp/uploads");

        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcher);

        when(req.getParameter("nome")).thenReturn("Mercoledi");
        when(req.getParameter("colore")).thenReturn("Bianco");
        when(req.getParameter("tipo")).thenReturn("Film e Serie TV");
        when(req.getParameter("IVA")).thenReturn("22");
        when(req.getParameter("prezzo")).thenReturn("15.0");
        when(req.getParameter("descrizione")).thenReturn("Maglietta di Mercoledi");
        when(req.getParameter("taglia")).thenReturn("M");
        when(req.getParameter("quantita")).thenReturn("3");

        when(req.getPart("grafica")).thenReturn(graficaPart);
        when(graficaPart.getSubmittedFileName()).thenReturn("img.jpg");
        when(graficaPart.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));
    }

    // -------- Test doPost() --------

    // {IVA_valida, prezzo_valido, grafica_ok, getMaxID_ok, filename_con_estensione, path_ok, copy_ok, quantita_valida, DB_ok, tipo_con_spazi}
    @Test
    void doPost_success_redirectCatalogo() throws Exception {
        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons =
                     mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
                         when(mock.getMaxID()).thenReturn(9, 10);
                     });
             MockedConstruction<MisuraDAO> misuraCons =
                     mockConstruction(MisuraDAO.class)) {

            files.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            servlet.doPost(req, resp);

            MagliettaDAO magliettaDAO = magliettaCons.constructed().get(0);
            MisuraDAO misuraDAO = misuraCons.constructed().get(0);

            ArgumentCaptor<MagliettaBean> magCaptor = ArgumentCaptor.forClass(MagliettaBean.class);
            verify(magliettaDAO).doSave(magCaptor.capture());
            MagliettaBean saved = magCaptor.getValue();

            assertEquals("Mercoledi", saved.getNome());
            assertEquals("Bianco", saved.getColore());
            assertEquals("Film e Serie TV", saved.getTipo());
            assertEquals(15.0f, saved.getPrezzo(), 0.0001f);
            assertEquals(22, saved.getIVA());
            assertEquals("Maglietta di Mercoledi", saved.getDescrizione());
            assertEquals("images/grafiche/9Film_e_Serie_TV.jpg", saved.getGrafica());

            ArgumentCaptor<MisuraBean> misuraCaptor = ArgumentCaptor.forClass(MisuraBean.class);
            verify(misuraDAO).doSave(misuraCaptor.capture());
            MisuraBean misuraSaved = misuraCaptor.getValue();

            assertEquals(9, misuraSaved.getIDMaglietta());
            assertEquals(3, misuraSaved.getQuantita());
            assertEquals("M", misuraSaved.getTaglia());

            verify(resp).sendRedirect("./Catalogo");
            verify(dispatcher, never()).forward(any(), any());
        }
    }

    // {IVA_non_numerica}
    @Test
    void doPost_ivaNonNumerico_forwardError() throws Exception {
        when(req.getParameter("IVA")).thenReturn("abc");

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class)) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
            verify(req, never()).getPart(anyString());

            assertTrue(magliettaCons.constructed().isEmpty());
            assertTrue(misuraCons.constructed().isEmpty());
        }
    }

    // {prezzo_non_numerico}
    @Test
    void doPost_prezzoNonNumerico_forwardError() throws Exception {
        when(req.getParameter("prezzo")).thenReturn("abc");

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class)) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
            verify(req, never()).getPart(anyString());

            assertTrue(magliettaCons.constructed().isEmpty());
            assertTrue(misuraCons.constructed().isEmpty());
        }
    }

    // {grafica_getPart_throws_ServletException}
    @Test
    void doPost_getPartThrowsServletException_forwardError() throws Exception {
        when(req.getPart("grafica")).thenThrow(new ServletException("boom"));

        try (MockedConstruction<MagliettaDAO> magliettaCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class)) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
            assertTrue(magliettaCons.constructed().isEmpty());
            assertTrue(misuraCons.constructed().isEmpty());
        }
    }

    // {getMaxID_throws_SQLException_prima_del_copy}
    @Test
    void doPost_getMaxIDThrowsSQLException_forwardError() throws Exception {
        try (MockedConstruction<MagliettaDAO> magliettaCons =
                     mockConstruction(MagliettaDAO.class, (mock, ctx) ->
                             when(mock.getMaxID()).thenThrow(new SQLException("db"))
                     );
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class)) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            MagliettaDAO magliettaDAO = magliettaCons.constructed().get(0);
            verify(magliettaDAO, never()).doSave(any());

            assertTrue(misuraCons.constructed().isEmpty());
        }
    }

    // {path_traversal_detected_destination_not_startsWith_uploadDir}
    @Test
    void doPost_destinationNotStartsWithUploadDir_forwardError() throws Exception {
        Path uploadDirMock = mock(Path.class);
        Path destinationMock = mock(Path.class);

        try (MockedStatic<Paths> paths = mockStatic(Paths.class);
             MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons =
                     mockConstruction(MagliettaDAO.class, (mock, ctx) -> when(mock.getMaxID()).thenReturn(9, 10));
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class)) {

            paths.when(() -> Paths.get(anyString())).thenReturn(uploadDirMock);

            when(uploadDirMock.toAbsolutePath()).thenReturn(uploadDirMock);
            when(uploadDirMock.normalize()).thenReturn(uploadDirMock);

            when(uploadDirMock.resolve(anyString())).thenReturn(destinationMock);
            when(destinationMock.normalize()).thenReturn(destinationMock);

            when(destinationMock.startsWith(uploadDirMock)).thenReturn(false);

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
            files.verifyNoInteractions();

            MagliettaDAO magliettaDAO = magliettaCons.constructed().get(0);
            verify(magliettaDAO, never()).doSave(any());
            assertTrue(misuraCons.constructed().isEmpty());
        }
    }

    // {copy_throws_IOException}
    @Test
    void doPost_filesCopyThrowsIOException_forwardError() throws Exception {
        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons =
                     mockConstruction(MagliettaDAO.class, (mock, ctx) -> when(mock.getMaxID()).thenReturn(9, 10));
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class)) {

            files.when(() -> Files.copy(any(InputStream.class), any(Path.class)))
                    .thenThrow(new IOException("copy fail"));

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            MagliettaDAO magliettaDAO = magliettaCons.constructed().get(0);
            verify(magliettaDAO, never()).doSave(any());
            assertTrue(misuraCons.constructed().isEmpty());
        }
    }

    // {quantita_non_numerica}
    @Test
    void doPost_quantitaNonNumerica_forwardError() throws Exception {
        when(req.getParameter("quantita")).thenReturn("xyz");

        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons =
                     mockConstruction(MagliettaDAO.class, (mock, ctx) -> when(mock.getMaxID()).thenReturn(9, 10));
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class)) {

            files.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            MagliettaDAO magliettaDAO = magliettaCons.constructed().get(0);
            verify(magliettaDAO, never()).doSave(any());
            assertTrue(misuraCons.constructed().isEmpty());
        }
    }

    // {DB_throws_SQLException_su_doSave_maglietta}
    @Test
    void doPost_sqlExceptionOnDoSaveMaglietta_forwardError() throws Exception {
        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons =
                     mockConstruction(MagliettaDAO.class, (mock, ctx) -> {
                         when(mock.getMaxID()).thenReturn(9, 10);
                         doThrow(new SQLException("save fail")).when(mock).doSave(any(MagliettaBean.class));
                     });
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class)) {

            files.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            MagliettaDAO magliettaDAO = magliettaCons.constructed().get(0);
            verify(magliettaDAO).doSave(any(MagliettaBean.class));

            // MisuraDAO viene creato prima del try, ma doSave NON deve essere chiamato
            MisuraDAO misuraDAO = misuraCons.constructed().get(0);
            verify(misuraDAO, never()).doSave(any(MisuraBean.class));
        }
    }

    // {DB_throws_SQLException_su_doSave_misura}
    @Test
    void doPost_sqlExceptionOnDoSaveMisura_forwardError() throws Exception {
        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons =
                     mockConstruction(MagliettaDAO.class, (mock, ctx) -> when(mock.getMaxID()).thenReturn(9, 10));
             MockedConstruction<MisuraDAO> misuraCons =
                     mockConstruction(MisuraDAO.class, (mock, ctx) -> doThrow(new SQLException("misura fail"))
                             .when(mock).doSave(any(MisuraBean.class)))) {

            files.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            MagliettaDAO magliettaDAO = magliettaCons.constructed().get(0);
            verify(magliettaDAO).doSave(any(MagliettaBean.class));

            MisuraDAO misuraDAO = misuraCons.constructed().get(0);
            verify(misuraDAO).doSave(any(MisuraBean.class));
        }
    }

    // {DB_throws_SQLException_su_secondo_getMaxID}
    @Test
    void doPost_sqlExceptionOnSecondGetMaxID_forwardError() throws Exception {
        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magliettaCons =
                     mockConstruction(MagliettaDAO.class, (mock, ctx) ->
                             when(mock.getMaxID()).thenReturn(9).thenThrow(new SQLException("second max fail"))
                     );
             MockedConstruction<MisuraDAO> misuraCons = mockConstruction(MisuraDAO.class)) {

            files.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            MagliettaDAO magliettaDAO = magliettaCons.constructed().get(0);
            verify(magliettaDAO).doSave(any(MagliettaBean.class));

            MisuraDAO misuraDAO = misuraCons.constructed().get(0);
            verify(misuraDAO, never()).doSave(any(MisuraBean.class));
        }
    }
}

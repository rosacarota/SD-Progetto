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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UpdateMagliettaTest {

    private UpdateMaglietta servlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private ServletContext context;
    private RequestDispatcher dispatcher;
    private Part graficaPart;

    private static final String ERROR_PAGE = "/pages/errorpage.jsp";

    @BeforeEach
    void setup() throws Exception {
        servlet = new UpdateMaglietta();

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        context = mock(ServletContext.class);
        dispatcher = mock(RequestDispatcher.class);
        graficaPart = mock(Part.class);

        when(req.getServletContext()).thenReturn(context);
        when(context.getRealPath("/images/grafiche/")).thenReturn("/tmp/uploads");
        when(req.getRequestDispatcher(ERROR_PAGE)).thenReturn(dispatcher);

        // ---- default "valid" parameters ----
        when(req.getParameter("id")).thenReturn("9");
        when(req.getParameter("prezzo")).thenReturn("15.0");
        when(req.getParameter("IVA")).thenReturn("22");
        when(req.getParameter("quantita")).thenReturn("3");

        when(req.getParameter("nome")).thenReturn("Mercoledi");
        when(req.getParameter("descrizione")).thenReturn("Desc");
        when(req.getParameter("taglia")).thenReturn("M");

        when(req.getParameter("colore")).thenReturn("Bianco");
        when(req.getParameter("coloreVecchio")).thenReturn("Nero");

        when(req.getParameter("tipo")).thenReturn("Film e Serie TV");
        when(req.getParameter("tipoVecchio")).thenReturn("Anime");

        when(req.getParameter("path")).thenReturn("images/grafiche/vecchia.jpg");

        // default: part presente ma filename vuoto => grafica = null (skip upload)
        when(req.getPart("grafica")).thenReturn(graficaPart);
        when(graficaPart.getSubmittedFileName()).thenReturn("");
    }

    // -------------------------
    // Test doPost()
    // -------------------------

    // {numeri_validi, colore_presente, tipo_presente, grafica_filename_vuoto_skip_upload, DB_ok}
    @Test
    void doPost_success_noUpload_redirectCatalogo() throws Exception {
        try (MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons = mockConstruction(MisuraDAO.class)) {

            servlet.doPost(req, resp);

            MagliettaDAO magDAO = magCons.constructed().get(0);
            MisuraDAO misDAO = misCons.constructed().get(0);

            ArgumentCaptor<MagliettaBean> magCap = ArgumentCaptor.forClass(MagliettaBean.class);
            verify(magDAO).doUpdate(magCap.capture());
            MagliettaBean mag = magCap.getValue();

            assertEquals(9, mag.getID());
            assertEquals("Mercoledi", mag.getNome());
            assertEquals("Bianco", mag.getColore());
            assertEquals("Film e Serie TV", mag.getTipo());
            assertEquals(15.0f, mag.getPrezzo(), 0.0001f);
            assertEquals(22, mag.getIVA());
            assertEquals("Desc", mag.getDescrizione());
            assertEquals("images/grafiche/vecchia.jpg", mag.getGrafica());

            ArgumentCaptor<MisuraBean> misCap = ArgumentCaptor.forClass(MisuraBean.class);
            verify(misDAO).doUpdate(misCap.capture());
            MisuraBean misura = misCap.getValue();

            assertEquals(9, misura.getIDMaglietta());
            assertEquals(3, misura.getQuantita());
            assertEquals("M", misura.getTaglia());

            verify(resp).sendRedirect("Catalogo");
            verify(dispatcher, never()).forward(any(), any());
        }
    }

    // {numeri_validi, colore_null_fallback_vecchio, tipo_null_fallback_vecchio, grafica_filename_vuoto_skip_upload, DB_ok}
    @Test
    void doPost_fallbackColoreTipo_redirectCatalogo() throws Exception {
        when(req.getParameter("colore")).thenReturn(null);
        when(req.getParameter("tipo")).thenReturn(null);

        try (MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons = mockConstruction(MisuraDAO.class)) {

            servlet.doPost(req, resp);

            MagliettaDAO magDAO = magCons.constructed().get(0);

            ArgumentCaptor<MagliettaBean> magCap = ArgumentCaptor.forClass(MagliettaBean.class);
            verify(magDAO).doUpdate(magCap.capture());

            assertEquals("Nero", magCap.getValue().getColore());
            assertEquals("Anime", magCap.getValue().getTipo());

            verify(resp).sendRedirect("Catalogo");
            verify(dispatcher, never()).forward(any(), any());
        }
    }

    // {id_non_numerico}
    @Test
    void doPost_parseError_forwardError() throws Exception {
        when(req.getParameter("id")).thenReturn("abc");

        try (MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons = mockConstruction(MisuraDAO.class)) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            verify(req, never()).getPart(anyString());
            assertTrue(magCons.constructed().isEmpty());
            assertTrue(misCons.constructed().isEmpty());
        }
    }

    // {numeri_validi, getPart_throws_ServletException}
    @Test
    void doPost_getPartThrowsServletException_forwardError() throws Exception {
        when(req.getPart("grafica")).thenThrow(new ServletException("boom"));

        try (MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons = mockConstruction(MisuraDAO.class)) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
            assertTrue(magCons.constructed().isEmpty());
            assertTrue(misCons.constructed().isEmpty());
        }
    }

    // {numeri_validi, grafica_filename_non_vuoto, list_ok, delete_old_attempted, startsWith_ok, copy_ok, DB_ok}
    @Test
    void doPost_uploadOk_redirectCatalogo() throws Exception {
        when(graficaPart.getSubmittedFileName()).thenReturn("img.jpg");
        when(graficaPart.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        Path uploadDir = Paths.get("/tmp/uploads").toAbsolutePath().normalize();
        Path old1 = uploadDir.resolve("9Film e Serie TV_old.jpg");
        Path old2 = uploadDir.resolve("9Film e Serie TV_other.png");

        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons = mockConstruction(MisuraDAO.class)) {

            files.when(() -> Files.list(any(Path.class))).thenReturn(Stream.of(old1, old2));
            files.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);
            files.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            servlet.doPost(req, resp);

            files.verify(() -> Files.list(any(Path.class)));
            files.verify(() -> Files.copy(any(InputStream.class), any(Path.class)));

            MagliettaDAO magDAO = magCons.constructed().get(0);
            ArgumentCaptor<MagliettaBean> magCap = ArgumentCaptor.forClass(MagliettaBean.class);
            verify(magDAO).doUpdate(magCap.capture());

            assertEquals("images/grafiche/9Film_e_Serie_TV.jpg", magCap.getValue().getGrafica());

            verify(resp).sendRedirect("Catalogo");
            verify(dispatcher, never()).forward(any(), any());
        }
    }

    // {numeri_validi, grafica_filename_non_vuoto, list_mix_match_e_nonmatch, delete_only_matching, forEach_covered}
    @Test
    void doPost_upload_deletesOnlyMatchingOldFiles_killsLambdaMutants() throws Exception {
        when(graficaPart.getSubmittedFileName()).thenReturn("img.jpg");
        when(graficaPart.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        Path uploadDir = Paths.get("/tmp/uploads").toAbsolutePath().normalize();

        Path matchInside = uploadDir.resolve("9Film e Serie TV_old.jpg");
        Path nonMatchInside = uploadDir.resolve("9AltroTipo_old.jpg");
        Path matchOutside = Paths.get("/tmp/altro")
                .toAbsolutePath().normalize()
                .resolve("9Film e Serie TV_hacker.jpg");

        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons = mockConstruction(MisuraDAO.class)) {

            files.when(() -> Files.list(any(Path.class)))
                    .thenReturn(Stream.of(matchInside, nonMatchInside, matchOutside));

            files.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);
            files.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L);

            servlet.doPost(req, resp);

            files.verify(() -> Files.deleteIfExists(any(Path.class)), times(1));
            files.verify(() -> Files.deleteIfExists(eq(matchOutside)), never());
            files.verify(() -> Files.deleteIfExists(eq(nonMatchInside)), never());
            files.verify(() -> Files.deleteIfExists(eq(matchInside)), times(1));

            verify(resp).sendRedirect("Catalogo");
            verify(dispatcher, never()).forward(any(), any());
        }
    }

    // {numeri_validi, grafica_filename_non_vuoto, Files_list_throws_IOException}
    @Test
    void doPost_filesListThrowsIOException_forwardError_andReturnEarly() throws Exception {
        when(graficaPart.getSubmittedFileName()).thenReturn("img.jpg");
        when(graficaPart.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons = mockConstruction(MisuraDAO.class)) {

            files.when(() -> Files.list(any(Path.class))).thenThrow(new IOException("list fail"));

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            assertTrue(magCons.constructed().isEmpty());
            assertTrue(misCons.constructed().isEmpty());
        }
    }

    // {numeri_validi, grafica_filename_non_vuoto, destination_not_startsWith_uploadDir}
    @Test
    void doPost_destinationNotStartsWithUploadDir_forwardError_andReturnEarly() throws Exception {
        when(graficaPart.getSubmittedFileName()).thenReturn("img.jpg");

        Path uploadDirMock = mock(Path.class);
        Path destinationMock = mock(Path.class);

        try (MockedStatic<Paths> paths = mockStatic(Paths.class);
             MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons = mockConstruction(MisuraDAO.class)) {

            paths.when(() -> Paths.get(anyString())).thenReturn(uploadDirMock);

            when(uploadDirMock.toAbsolutePath()).thenReturn(uploadDirMock);
            when(uploadDirMock.normalize()).thenReturn(uploadDirMock);

            when(uploadDirMock.resolve(anyString())).thenReturn(destinationMock);
            when(destinationMock.normalize()).thenReturn(destinationMock);

            when(destinationMock.startsWith(uploadDirMock)).thenReturn(false);

            files.when(() -> Files.list(uploadDirMock)).thenReturn(Stream.empty());

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            assertTrue(magCons.constructed().isEmpty());
            assertTrue(misCons.constructed().isEmpty());
            files.verify(() -> Files.copy(any(InputStream.class), any(Path.class)), never());
        }
    }

    // {numeri_validi, grafica_filename_non_vuoto, list_ok, startsWith_ok, copy_throws_IOException}
    @Test
    void doPost_filesCopyThrowsIOException_forwardError_andReturnEarly() throws Exception {
        when(graficaPart.getSubmittedFileName()).thenReturn("img.jpg");
        when(graficaPart.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        try (MockedStatic<Files> files = mockStatic(Files.class);
             MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons = mockConstruction(MisuraDAO.class)) {

            files.when(() -> Files.list(any(Path.class))).thenReturn(Stream.empty());
            files.when(() -> Files.copy(any(InputStream.class), any(Path.class)))
                    .thenThrow(new IOException("copy fail"));

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            assertTrue(magCons.constructed().isEmpty());
            assertTrue(misCons.constructed().isEmpty());
        }
    }

    // {numeri_validi, grafica_filename_vuoto_skip_upload, SQLException_on_MagliettaDAO_doUpdate}
    @Test
    void doPost_sqlExceptionOnMagliettaUpdate_forwardError() throws Exception {

        try (MockedConstruction<MagliettaDAO> magCons =
                     mockConstruction(MagliettaDAO.class, (mock, ctx) ->
                             doThrow(new SQLException("fail"))
                                     .when(mock).doUpdate(any(MagliettaBean.class))
                     );
             MockedConstruction<MisuraDAO> misCons =
                     mockConstruction(MisuraDAO.class)) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            assertTrue(misCons.constructed().isEmpty());
        }
    }

    // {numeri_validi, grafica_filename_vuoto_skip_upload, SQLException_on_MisuraDAO_doUpdate}
    @Test
    void doPost_sqlExceptionOnMisuraUpdate_forwardError() throws Exception {
        try (MockedConstruction<MagliettaDAO> magCons = mockConstruction(MagliettaDAO.class);
             MockedConstruction<MisuraDAO> misCons =
                     mockConstruction(MisuraDAO.class, (mock, ctx) ->
                             doThrow(new SQLException("fail")).when(mock).doUpdate(any(MisuraBean.class))
                     )) {

            servlet.doPost(req, resp);

            verify(dispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            MagliettaDAO magDAO = magCons.constructed().get(0);
            verify(magDAO).doUpdate(any(MagliettaBean.class));

            MisuraDAO misDAO = misCons.constructed().get(0);
            verify(misDAO).doUpdate(any(MisuraBean.class));
        }
    }
}

package control.search;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchBarTest {

    private SearchBar servlet;
    private DataSource dsMock;
    private Connection connMock;
    private PreparedStatement psMock;
    private ResultSet rsMock;
    private ResultSetMetaData metaMock;

    private HttpServletRequest req;
    private HttpServletResponse resp;
    private StringWriter writer;

    @BeforeEach
    void setup() throws Exception {
        dsMock = mock(DataSource.class);
        connMock = mock(Connection.class);
        psMock = mock(PreparedStatement.class);
        rsMock = mock(ResultSet.class);
        metaMock = mock(ResultSetMetaData.class);

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);

        writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        servlet = new SearchBar(dsMock);

        when(dsMock.getConnection()).thenReturn(connMock);
        when(connMock.prepareStatement(anyString())).thenReturn(psMock);
        when(psMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.getMetaData()).thenReturn(metaMock);
    }

    // -------- Test doPost() --------

    // {search_normale, lista_vuota, db_ok}
    @Test
    void doPost_searchNormale_listaVuota_dbOk() throws Exception {
        when(req.getParameter("search")).thenReturn("mag");

        when(metaMock.getColumnCount()).thenReturn(2);
        when(rsMock.next()).thenReturn(false);

        servlet.doPost(req, resp);

        verify(psMock).setString(1, "%mag%");
        verify(resp).setContentType("application/json");
        verify(resp).setCharacterEncoding("UTF-8");

        assertEquals("[]", writer.toString());
    }

    // {search_normale, una_riga, db_ok}
    @Test
    void doPost_searchNormale_unaRiga_dbOk() throws Exception {
        when(req.getParameter("search")).thenReturn("mag");

        when(metaMock.getColumnCount()).thenReturn(2);
        when(metaMock.getColumnName(1)).thenReturn("ID");
        when(metaMock.getColumnName(2)).thenReturn("Nome");

        when(rsMock.next()).thenReturn(true, false);
        when(rsMock.getObject(1)).thenReturn(10);
        when(rsMock.getObject(2)).thenReturn("Maglia");

        servlet.doPost(req, resp);

        List<Map<String, Object>> parsed = new Gson().fromJson(writer.toString(), List.class);

        assertEquals(1, parsed.size());
        assertEquals(10.0, parsed.get(0).get("ID"));
        assertEquals("Maglia", parsed.get(0).get("Nome"));
    }

    // {search_normale, piu_righe, db_ok}
    @Test
    void doPost_searchNormale_piuRighe_dbOk() throws Exception {
        when(req.getParameter("search")).thenReturn("mag");

        when(metaMock.getColumnCount()).thenReturn(1);
        when(metaMock.getColumnName(1)).thenReturn("ID");

        when(rsMock.next()).thenReturn(true, true, false);
        when(rsMock.getObject(1)).thenReturn(1, 2);

        servlet.doPost(req, resp);

        List<Map<String, Object>> parsed = new Gson().fromJson(writer.toString(), List.class);

        assertEquals(2, parsed.size());
        assertEquals(1.0, parsed.get(0).get("ID"));
        assertEquals(2.0, parsed.get(1).get("ID"));
    }

    // {search_vuota, lista_vuota, db_ok}
    @Test
    void doPost_searchVuota_listaVuota_dbOk() throws Exception {
        when(req.getParameter("search")).thenReturn("");

        when(metaMock.getColumnCount()).thenReturn(2);
        when(rsMock.next()).thenReturn(false);

        servlet.doPost(req, resp);

        assertEquals("[]", writer.toString());
    }

    // {search_null, lista_vuota, db_ok}
    @Test
    void doPost_searchNull_listaVuota_dbOk() throws Exception {
        when(req.getParameter("search")).thenReturn(null);

        when(metaMock.getColumnCount()).thenReturn(2);
        when(rsMock.next()).thenReturn(false);

        servlet.doPost(req, resp);

        assertEquals("[]", writer.toString());
    }

    // {search_normale, db_exception}
    @Test
    void doPost_searchNormale_dbException_forwardErrorPage() throws Exception {
        when(req.getParameter("search")).thenReturn("mag");

        when(dsMock.getConnection()).thenThrow(new SQLException());

        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcher);

        servlet.doPost(req, resp);

        verify(dispatcher).forward(req, resp);
    }

    // {search_normale, writer_exception}
    @Test
    void doPost_writerException_forwardErrorPage() throws Exception {
        when(req.getParameter("search")).thenReturn("mag");

        when(metaMock.getColumnCount()).thenReturn(1);
        when(metaMock.getColumnName(1)).thenReturn("ID");
        when(rsMock.next()).thenReturn(true, false);
        when(rsMock.getObject(1)).thenReturn(1);

        when(resp.getWriter()).thenThrow(new IOException());

        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcher);

        servlet.doPost(req, resp);

        verify(dispatcher).forward(req, resp);
    }

    // -------- Test doGet() --------

    // {delegazione_a_doPost}
    @Test
    void doGet_delegaDoPost() throws Exception {
        when(req.getParameter("search")).thenReturn("mag");

        when(metaMock.getColumnCount()).thenReturn(1);
        when(metaMock.getColumnName(1)).thenReturn("ID");

        when(rsMock.next()).thenReturn(true, false);
        when(rsMock.getObject(1)).thenReturn(42);

        servlet.doGet(req, resp);

        List<Map<String, Object>> parsed = new Gson().fromJson(writer.toString(), List.class);

        assertEquals(1, parsed.size());
        assertEquals(42.0, parsed.get(0).get("ID"));
    }
}

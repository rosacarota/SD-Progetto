package control.maglietta;

import model.CarrelloModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

class AggiungiMagliettaTest {

    private AggiungiMaglietta servlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;
    private CarrelloModel carrelloMock;

    @BeforeEach
    void setup() {
        servlet = new AggiungiMaglietta();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        carrelloMock = mock(CarrelloModel.class);

        when(req.getSession()).thenReturn(session);
    }

    // -------- Test doPost() --------

    // {carrello_null, quantita_null}
    @Test
    void doPost_carrelloNull_quantitaNull() throws Exception {
        when(session.getAttribute("carrello")).thenReturn(null);
        when(req.getParameter("ID")).thenReturn("5");
        when(req.getParameter("quantita")).thenReturn(null);
        when(req.getParameter("taglia")).thenReturn("M");

        try (MockedConstruction<CarrelloModel> mocked =
                     mockConstruction(CarrelloModel.class)) {

            servlet.doPost(req, resp);

            CarrelloModel created = mocked.constructed().get(0);

            verify(session).setAttribute("carrello", created);
            verify(created).aggiungi(5, "M");
            verify(resp).sendRedirect("pages/carrello.jsp");
        }
    }

    // {carrello_presente, quantita_null}
    @Test
    void doPost_carrelloPresente_quantitaNull() throws Exception {
        when(session.getAttribute("carrello")).thenReturn(carrelloMock);
        when(req.getParameter("ID")).thenReturn("5");
        when(req.getParameter("quantita")).thenReturn(null);
        when(req.getParameter("taglia")).thenReturn("M");

        servlet.doPost(req, resp);

        verify(carrelloMock).aggiungi(5, "M");
        verify(resp).sendRedirect("pages/carrello.jsp");
        verify(session, never()).setAttribute(eq("carrello"), any());
    }

    // {carrello_presente, quantita_presente}
    @Test
    void doPost_carrelloPresente_quantitaPresente() throws Exception {
        when(session.getAttribute("carrello")).thenReturn(carrelloMock);
        when(req.getParameter("ID")).thenReturn("5");
        when(req.getParameter("quantita")).thenReturn("3");
        when(req.getParameter("taglia")).thenReturn("L");

        servlet.doPost(req, resp);

        verify(carrelloMock).setQuantita(5, 3, "L");
        verify(resp, never()).sendRedirect(anyString());
    }

    // {id_non_numerico}
    @Test
    void doPost_IDNonNumerico_redirectError() throws Exception {
        when(session.getAttribute("carrello")).thenReturn(carrelloMock);
        when(req.getParameter("ID")).thenReturn("abc");
        when(req.getParameter("quantita")).thenReturn(null);
        when(req.getParameter("taglia")).thenReturn("M");

        servlet.doPost(req, resp);

        verify(resp).sendRedirect("pages/errorpage.jsp");
        verify(carrelloMock, never()).aggiungi(anyInt(), any());
        verify(carrelloMock, never()).setQuantita(anyInt(), anyInt(), any());
    }
}

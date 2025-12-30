package control.maglietta;

import model.CarrelloModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class RimuoviMagliettaTest {

    private RimuoviMaglietta servlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;
    private CarrelloModel carrelloMock;

    @BeforeEach
    void setup() {
        servlet = new RimuoviMaglietta();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        carrelloMock = mock(CarrelloModel.class);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("carrello")).thenReturn(carrelloMock);
    }

    // -------- Test doPost() --------

    // {carrello_presente, id_valido, taglia_valida}
    @Test
    void doPost_carrelloPresente_rimuoveMaglietta() throws Exception {
        when(req.getParameter("ID")).thenReturn("5");
        when(req.getParameter("taglia")).thenReturn("M");

        servlet.doPost(req, resp);

        verify(carrelloMock).rimuovi(5, "M");
        verifyNoInteractions(resp);
    }

    // {id_non_numerico}
    @Test
    void doPost_idNonNumerico_redirectError() throws Exception {
        when(req.getParameter("ID")).thenReturn("abc");
        when(req.getParameter("taglia")).thenReturn("M");

        servlet.doPost(req, resp);

        verify(resp).sendRedirect("pages/errorpage.jsp");
        verify(carrelloMock, never()).rimuovi(anyInt(), any());
    }
}

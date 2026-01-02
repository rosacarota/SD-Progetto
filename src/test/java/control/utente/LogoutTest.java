package control.utente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LogoutTest {

    private Logout servlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;

    @BeforeEach
    void setup() {
        servlet = new Logout();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(req.getSession()).thenReturn(session);
    }

    // -------- Test doPost() --------

    // {POST, session_ok, invalidate_ok, redirect_ok}
    @Test
    void doPost_success_invalidateAndRedirectIndex() throws Exception {
        servlet.doPost(req, resp);

        verify(req).getSession();
        verify(session).invalidate();
        verify(resp).sendRedirect("index.jsp");
        verifyNoMoreInteractions(resp);
    }

    // -------- Test doGet() --------

    // {GET, delega_a_doPost, session_ok, invalidate_ok, redirect_ok}
    @Test
    void doGet_delegatesToDoPost_sameEffectsAsPost() throws Exception {
        servlet.doGet(req, resp);

        verify(req).getSession();
        verify(session).invalidate();
        verify(resp).sendRedirect("index.jsp");
        verifyNoMoreInteractions(resp);
    }

    // {POST, session_ok, invalidate_throws_RuntimeException}
    @Test
    void doPost_invalidateThrows_exceptionPropagates_noRedirect() throws Exception {
        doThrow(new IllegalStateException("already invalidated")).when(session).invalidate();

        assertThrows(IllegalStateException.class, () -> servlet.doPost(req, resp));

        verify(req).getSession();
        verify(session).invalidate();
        verify(resp, never()).sendRedirect(anyString());
    }
}

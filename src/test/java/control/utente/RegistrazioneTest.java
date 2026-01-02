package control.utente;

import model.utente.UtenteBean;
import model.utente.UtenteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrazioneTest {

    private Registrazione servlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;

    @BeforeEach
    void setup() {
        servlet = new Registrazione();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(req.getSession()).thenReturn(session);
    }

    private void stubValidParams(String username, String email, String tipo, String dataNascitaIso) {
        when(req.getParameter("usernameReg")).thenReturn(username);
        when(req.getParameter("passwordReg")).thenReturn("pwd123");
        when(req.getParameter("nomeReg")).thenReturn("Mario");
        when(req.getParameter("cognomeReg")).thenReturn("Rossi");
        when(req.getParameter("emailReg")).thenReturn(email);
        when(req.getParameter("tipo")).thenReturn(tipo);
        when(req.getParameter("dataNascitaReg")).thenReturn(dataNascitaIso);
    }

    // TC1: {utente_non_presente, tipo=user, data valida} => salva e redirect
    @Test
    void doPost_userNonPresente_salva_eRedirect() throws Exception {
        stubValidParams("agovenlockf", "agovenlockf@oracle.com", "user", "1970-10-24");

        try (MockedConstruction<UtenteDAO> utenteDAOMock =
                     mockConstruction(UtenteDAO.class, (mock, ctx) -> {
                         when(mock.doRetrieveByKey("agovenlockf")).thenReturn(null);
                         when(mock.doRetrieveByEmail("agovenlockf@oracle.com")).thenReturn(null);
                     })) {

            servlet.doPost(req, resp);

            UtenteDAO dao = utenteDAOMock.constructed().get(0);

            verify(session).setAttribute("utentePresente", false);

            ArgumentCaptor<UtenteBean> captor = ArgumentCaptor.forClass(UtenteBean.class);
            verify(dao).doSave(captor.capture());

            UtenteBean saved = captor.getValue();
            assertEquals("agovenlockf", saved.getUsername());
            assertEquals("pwd123", saved.getPwd());
            assertEquals("Mario", saved.getNome());
            assertEquals("Rossi", saved.getCognome());
            assertEquals("agovenlockf@oracle.com", saved.getEmail());
            assertEquals(LocalDate.parse("1970-10-24"), saved.getDataNascita());
            assertEquals("user", saved.getTipo());

            verify(resp).sendRedirect("pages/login.jsp");
            verify(req, never()).getRequestDispatcher("/pages/errorpage.jsp");
        }
    }

    // TC2: {utente_non_presente, tipo=admin, data valida} => salva admin e redirect
    @Test
    void doPost_adminNonPresente_salva_eRedirect() throws Exception {
        stubValidParams("admin01", "admin01@oracle.com", "admin", "1990-01-10");

        try (MockedConstruction<UtenteDAO> utenteDAOMock =
                     mockConstruction(UtenteDAO.class, (mock, ctx) -> {
                         when(mock.doRetrieveByKey("admin01")).thenReturn(null);
                         when(mock.doRetrieveByEmail("admin01@oracle.com")).thenReturn(null);
                     })) {

            servlet.doPost(req, resp);

            UtenteDAO dao = utenteDAOMock.constructed().get(0);

            verify(session).setAttribute("utentePresente", false);

            ArgumentCaptor<UtenteBean> captor = ArgumentCaptor.forClass(UtenteBean.class);
            verify(dao).doSave(captor.capture());
            assertEquals("admin", captor.getValue().getTipo());

            verify(resp).sendRedirect("pages/login.jsp");
            verify(req, never()).getRequestDispatcher("/pages/errorpage.jsp");
        }
    }

    // TC3: {username già presente} => utentePresente=true, NO doSave, NO doRetrieveByEmail (short-circuit), redirect
    @Test
    void doPost_usernamePresente_setTrue_noSave_noEmailLookup_redirect() throws Exception {
        stubValidParams("mario", "mario@oracle.com", "user", "2000-12-31");

        UtenteBean existing = new UtenteBean();
        existing.setUsername("mario");

        try (MockedConstruction<UtenteDAO> utenteDAOMock =
                     mockConstruction(UtenteDAO.class, (mock, ctx) -> {
                         when(mock.doRetrieveByKey("mario")).thenReturn(existing);
                     })) {

            servlet.doPost(req, resp);

            UtenteDAO dao = utenteDAOMock.constructed().get(0);

            verify(session).setAttribute("utentePresente", true);
            verify(dao, never()).doSave(any());

            verify(dao, never()).doRetrieveByEmail(anyString());

            verify(resp).sendRedirect("pages/login.jsp");
            verify(req, never()).getRequestDispatcher("/pages/errorpage.jsp");
        }
    }

    // TC4: {email già presente} => utentePresente=true, NO doSave, redirect
    @Test
    void doPost_emailPresente_setTrue_noSave_redirect() throws Exception {
        stubValidParams("nuovoUser", "dup@oracle.com", "user", "2001-01-01");

        UtenteBean existingEmail = new UtenteBean();
        existingEmail.setEmail("dup@oracle.com");

        try (MockedConstruction<UtenteDAO> utenteDAOMock =
                     mockConstruction(UtenteDAO.class, (mock, ctx) -> {
                         when(mock.doRetrieveByKey("nuovoUser")).thenReturn(null);
                         when(mock.doRetrieveByEmail("dup@oracle.com")).thenReturn(existingEmail);
                     })) {

            servlet.doPost(req, resp);

            UtenteDAO dao = utenteDAOMock.constructed().get(0);

            verify(session).setAttribute("utentePresente", true);
            verify(dao, never()).doSave(any());

            verify(resp).sendRedirect("pages/login.jsp");
            verify(req, never()).getRequestDispatcher("/pages/errorpage.jsp");
        }
    }

    // TC5: SQLException su doRetrieveByKey => forward errorpage, NO redirect
    @Test
    void doPost_sqlException_inRetrieve_forwardError_noRedirect() throws Exception {
        stubValidParams("u", "u@oracle.com", "user", "2000-01-01");

        RequestDispatcher errorDispatcher = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(errorDispatcher);

        try (MockedConstruction<UtenteDAO> utenteDAOMock =
                     mockConstruction(UtenteDAO.class, (mock, ctx) -> {
                         when(mock.doRetrieveByKey("u")).thenThrow(new SQLException());
                     })) {

            servlet.doPost(req, resp);

            verify(errorDispatcher).forward(req, resp);

            verify(session, never()).setAttribute(eq("utentePresente"), any());

            verify(resp, never()).sendRedirect(anyString());
        }
    }

    // TC6: SQLException su doSave => utentePresente=false, forward errorpage, NO redirect
    @Test
    void doPost_sqlException_inSave_forwardError_noRedirect() throws Exception {
        stubValidParams("u2", "u2@oracle.com", "user", "2000-01-01");

        RequestDispatcher errorDispatcher = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(errorDispatcher);

        try (MockedConstruction<UtenteDAO> utenteDAOMock =
                     mockConstruction(UtenteDAO.class, (mock, ctx) -> {
                         when(mock.doRetrieveByKey("u2")).thenReturn(null);
                         when(mock.doRetrieveByEmail("u2@oracle.com")).thenReturn(null);
                         doThrow(new SQLException()).when(mock).doSave(any(UtenteBean.class));
                     })) {

            servlet.doPost(req, resp);

            verify(session).setAttribute("utentePresente", false);
            verify(errorDispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());
        }
    }

    // TC7: data formato invalido => forward errorpage, NO redirect, DAO NON costruito
    @Test
    void doPost_dataInvalida_forwardError_noRedirect_noDAO() throws Exception {
        stubValidParams("u3", "u3@oracle.com", "user", "31/12/2000");

        RequestDispatcher errorDispatcher = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(errorDispatcher);

        try (MockedConstruction<UtenteDAO> utenteDAOMock =
                     mockConstruction(UtenteDAO.class)) {

            servlet.doPost(req, resp);

            verify(errorDispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            assertTrue(utenteDAOMock.constructed().isEmpty());
            verify(session, never()).setAttribute(eq("utentePresente"), any());
        }
    }

    // TC8: data null => forward errorpage, NO redirect, DAO NON costruito
    @Test
    void doPost_dataNull_forwardError_noRedirect_noDAO() throws Exception {
        stubValidParams("u4", "u4@oracle.com", "user", null);

        RequestDispatcher errorDispatcher = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(errorDispatcher);

        try (MockedConstruction<UtenteDAO> utenteDAOMock =
                     mockConstruction(UtenteDAO.class)) {

            servlet.doPost(req, resp);

            verify(errorDispatcher).forward(req, resp);
            verify(resp, never()).sendRedirect(anyString());

            assertTrue(utenteDAOMock.constructed().isEmpty());
            verify(session, never()).setAttribute(eq("utentePresente"), any());
        }
    }

    // TC9: doGet delega a doPost (uso caso "ok" minimo)
    @Test
    void doGet_delegaDoPost() throws Exception {
        stubValidParams("u5", "u5@oracle.com", "user", "1999-02-02");

        try (MockedConstruction<UtenteDAO> utenteDAOMock =
                     mockConstruction(UtenteDAO.class, (mock, ctx) -> {
                         when(mock.doRetrieveByKey("u5")).thenReturn(null);
                         when(mock.doRetrieveByEmail("u5@oracle.com")).thenReturn(null);
                     })) {

            servlet.doGet(req, resp);

            UtenteDAO dao = utenteDAOMock.constructed().get(0);
            verify(dao).doSave(any(UtenteBean.class));
            verify(resp).sendRedirect("pages/login.jsp");
        }
    }
}

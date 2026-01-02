package control;

import model.acquisto.AcquistoBean;
import model.acquisto.AcquistoDAO;
import model.ordine.OrdineBean;
import model.ordine.OrdineDAO;
import model.utente.UtenteBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StoricoOrdiniTest {

    private StoricoOrdini servlet;

    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;

    private RequestDispatcher dispatcherProfilo;
    private RequestDispatcher dispatcherError;

    private UtenteBean utente;

    @BeforeEach
    void setup() {
        servlet = new StoricoOrdini();

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        dispatcherProfilo = mock(RequestDispatcher.class);
        dispatcherError = mock(RequestDispatcher.class);

        utente = mock(UtenteBean.class);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(utente);

        when(req.getRequestDispatcher("pages/profilo.jsp")).thenReturn(dispatcherProfilo);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcherError);

        when(utente.getUsername()).thenReturn("mango");
    }

    // -------- Test doPost() --------

    // {utente_valido, ordini_vuoti, db_ok, forward_profilo_ok}
    @Test
    void doPost_ok_ordiniVuoti_forwardProfilo() throws Exception {
        try (MockedConstruction<OrdineDAO> ordineCons = mockConstruction(OrdineDAO.class, (mock, ctx) -> {
                 when(mock.doRetrieveByKey(eq("mango"))).thenReturn(Collections.emptyList());
             });
             MockedConstruction<AcquistoDAO> acquistoCons = mockConstruction(AcquistoDAO.class)) {

            servlet.doPost(req, resp);

            verify(req).setAttribute(eq("ordini"), argThat(m -> ((Map<?, ?>) m).isEmpty()));
            verify(dispatcherProfilo).forward(req, resp);
            verify(dispatcherError, never()).forward(req, resp);

            OrdineDAO ordineDAO = ordineCons.constructed().get(0);
            verify(ordineDAO).doRetrieveByKey("mango");

            assertEquals(1, acquistoCons.constructed().size());
        }
    }

    // {utente_valido, ordini_presenti, acquisti_presenti, db_ok, forward_profilo_ok}
    @Test
    void doPost_ok_ordiniPresenti_mappaCompleta_forwardProfilo() throws Exception {
        OrdineBean o1 = mock(OrdineBean.class);
        OrdineBean o2 = mock(OrdineBean.class);
        when(o1.getID()).thenReturn(1);
        when(o2.getID()).thenReturn(2);

        Collection<OrdineBean> ordini = Arrays.asList(o1, o2);

        Collection<AcquistoBean> a1 = Collections.singletonList(mock(AcquistoBean.class));
        Collection<AcquistoBean> a2 = Arrays.asList(mock(AcquistoBean.class), mock(AcquistoBean.class));

        try (MockedConstruction<OrdineDAO> ordineCons = mockConstruction(OrdineDAO.class, (mock, ctx) -> {
                 when(mock.doRetrieveByKey(eq("mango"))).thenReturn(ordini);
             });
             MockedConstruction<AcquistoDAO> acquistoCons = mockConstruction(AcquistoDAO.class, (mock, ctx) -> {
                 when(mock.doRetrieveByOrdine(1)).thenReturn(a1);
                 when(mock.doRetrieveByOrdine(2)).thenReturn(a2);
             })) {

            servlet.doPost(req, resp);

            verify(req).setAttribute(eq("ordini"), argThat(obj -> {
                Map<?, ?> map = (Map<?, ?>) obj;
                return map.size() == 2 && map.get(o1) == a1 && map.get(o2) == a2;
            }));

            verify(dispatcherProfilo).forward(req, resp);
            verify(dispatcherError, never()).forward(req, resp);

            verify(acquistoCons.constructed().get(0)).doRetrieveByOrdine(1);
            verify(acquistoCons.constructed().get(0)).doRetrieveByOrdine(2);
        }
    }

    // {ordineDAO_throws, forward_error_ok}
    @Test
    void doPost_ordineDAOThrows_forwardError() throws Exception {
        try (MockedConstruction<OrdineDAO> ordineCons = mockConstruction(OrdineDAO.class, (mock, ctx) -> {
                 when(mock.doRetrieveByKey(anyString())).thenThrow(new SQLException("fail"));
             });
             MockedConstruction<AcquistoDAO> acquistoCons = mockConstruction(AcquistoDAO.class)) {

            servlet.doPost(req, resp);

            verify(dispatcherError).forward(req, resp);
            verify(dispatcherProfilo, never()).forward(req, resp);
        }
    }

    // {acquistoDAO_throws_in_buildMap, forward_error_ok}
    @Test
    void doPost_acquistoDAOThrows_forwardError() throws Exception {
        OrdineBean o1 = mock(OrdineBean.class);
        when(o1.getID()).thenReturn(1);

        try (MockedConstruction<OrdineDAO> ordineCons = mockConstruction(OrdineDAO.class, (mock, ctx) -> {
                 when(mock.doRetrieveByKey(eq("mango"))).thenReturn(Collections.singletonList(o1));
             });
             MockedConstruction<AcquistoDAO> acquistoCons = mockConstruction(AcquistoDAO.class, (mock, ctx) -> {
                 when(mock.doRetrieveByOrdine(1)).thenThrow(new SQLException("fail"));
             })) {

            servlet.doPost(req, resp);

            verify(dispatcherError).forward(req, resp);
            verify(dispatcherProfilo, never()).forward(req, resp);
        }
    }

    // -------- Test buildOrdiniAcquistiMap() --------

    // {ordini_presenti, dao_ok, map_ok}
    @Test
    void buildOrdiniAcquistiMap_ok() throws Exception {
        OrdineBean o1 = mock(OrdineBean.class);
        OrdineBean o2 = mock(OrdineBean.class);
        when(o1.getID()).thenReturn(1);
        when(o2.getID()).thenReturn(2);

        AcquistoDAO acquistoDAO = mock(AcquistoDAO.class);
        Collection<AcquistoBean> a1 = Collections.singletonList(mock(AcquistoBean.class));
        Collection<AcquistoBean> a2 = Collections.singletonList(mock(AcquistoBean.class));

        when(acquistoDAO.doRetrieveByOrdine(1)).thenReturn(a1);
        when(acquistoDAO.doRetrieveByOrdine(2)).thenReturn(a2);

        Method m = StoricoOrdini.class.getDeclaredMethod("buildOrdiniAcquistiMap", Collection.class, AcquistoDAO.class);
        m.setAccessible(true);

        Object out = m.invoke(servlet, Arrays.asList(o1, o2), acquistoDAO);

        assertNotNull(out);
        Map<?, ?> map = (Map<?, ?>) out;
        assertEquals(2, map.size());
        assertSame(a1, map.get(o1));
        assertSame(a2, map.get(o2));

        verify(acquistoDAO).doRetrieveByOrdine(1);
        verify(acquistoDAO).doRetrieveByOrdine(2);
    }

    // {ordini_presenti, dao_throws}
    @Test
    void buildOrdiniAcquistiMap_daoThrows_propagatesCause() throws Exception {
        OrdineBean o1 = mock(OrdineBean.class);
        when(o1.getID()).thenReturn(1);

        AcquistoDAO acquistoDAO = mock(AcquistoDAO.class);
        when(acquistoDAO.doRetrieveByOrdine(1)).thenThrow(new SQLException("boom"));

        Method m = StoricoOrdini.class.getDeclaredMethod("buildOrdiniAcquistiMap", Collection.class, AcquistoDAO.class);
        m.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> m.invoke(servlet, Collections.singletonList(o1), acquistoDAO));
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof SQLException);
        assertTrue(ex.getCause().getMessage().contains("boom"));
    }

    // -------- Test doGet() --------

    // {delegazione_a_doPost}
    @Test
    void doGet_delegaDoPost() throws Exception {
        try (MockedConstruction<OrdineDAO> ordineCons = mockConstruction(OrdineDAO.class, (mock, ctx) -> {
                 when(mock.doRetrieveByKey(eq("mango"))).thenReturn(Collections.emptyList());
             });
             MockedConstruction<AcquistoDAO> acquistoCons = mockConstruction(AcquistoDAO.class)) {

            servlet.doGet(req, resp);

            verify(dispatcherProfilo).forward(req, resp);
        }
    }
}

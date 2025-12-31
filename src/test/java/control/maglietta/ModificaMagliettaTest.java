package control.maglietta;

import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;
import model.misura.MisuraBean;
import model.misura.MisuraDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;

class ModificaMagliettaTest {

    private ModificaMaglietta servlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setup() {
        servlet = new ModificaMaglietta();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
    }

    // -------- Test doGet() --------

    // {id_valido, dao_ok}
    @Test
    void doGet_idValido_daoOk() throws Exception {
        when(req.getParameter("id")).thenReturn("5");
        when(req.getRequestDispatcher("pages/modifica.jsp")).thenReturn(dispatcher);

        MagliettaBean magliettaMock = new MagliettaBean();
        magliettaMock.setID(5);

        Collection<MisuraBean> misureMock = List.of();

        try (
            MockedConstruction<MagliettaDAO> magliettaDAOMock =
                mockConstruction(MagliettaDAO.class, (mock, ctx) ->
                    when(mock.doRetrieveByKey(5)).thenReturn(magliettaMock)
                );
            MockedConstruction<MisuraDAO> misuraDAOMock =
                mockConstruction(MisuraDAO.class, (mock, ctx) ->
                    when(mock.doRetrieveAll(5)).thenReturn(misureMock)
                )
        ) {
            servlet.doGet(req, resp);

            verify(req).setAttribute("maglietta", magliettaMock);
            verify(req).setAttribute("misure", misureMock);
            verify(dispatcher).forward(req, resp);
        }
    }

    // {id_valido, magliettaDAO_exception}
    @Test
    void doGet_magliettaDAOException_forwardError() throws Exception {
        when(req.getParameter("id")).thenReturn("5");

        RequestDispatcher errorDispatcher = mock(RequestDispatcher.class);
        RequestDispatcher modificaDispatcher = mock(RequestDispatcher.class);

        when(req.getRequestDispatcher("/pages/errorpage.jsp"))
                .thenReturn(errorDispatcher);
        when(req.getRequestDispatcher("pages/modifica.jsp"))
                .thenReturn(modificaDispatcher);

        try (
                MockedConstruction<MagliettaDAO> magliettaDAOMock =
                        mockConstruction(MagliettaDAO.class, (mock, ctx) ->
                                when(mock.doRetrieveByKey(5)).thenThrow(new SQLException())
                        );
                MockedConstruction<MisuraDAO> misuraDAOMock =
                        mockConstruction(MisuraDAO.class)
        ) {
            servlet.doGet(req, resp);

            verify(errorDispatcher).forward(req, resp);
            verify(modificaDispatcher, never()).forward(req, resp);
        }
    }

    // {id_non_numerico}
    @Test
    void doGet_idNonNumerico_forwardError() throws Exception {
        when(req.getParameter("id")).thenReturn("abc");

        RequestDispatcher errorDispatcher = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher("/pages/errorpage.jsp"))
                .thenReturn(errorDispatcher);

        servlet.doGet(req, resp);

        verify(errorDispatcher).forward(req, resp);
    }

    // -------- Test doPost() --------

    // {delegazione_a_doGet}
    @Test
    void doPost_delegaDoGet() throws Exception {
        when(req.getParameter("id")).thenReturn("3");
        when(req.getRequestDispatcher("pages/modifica.jsp")).thenReturn(dispatcher);

        MagliettaBean magliettaMock = new MagliettaBean();
        magliettaMock.setID(3);

        Collection<MisuraBean> misureMock = List.of();

        try (
            MockedConstruction<MagliettaDAO> magliettaDAOMock =
                mockConstruction(MagliettaDAO.class, (mock, ctx) ->
                    when(mock.doRetrieveByKey(3)).thenReturn(magliettaMock)
                );
            MockedConstruction<MisuraDAO> misuraDAOMock =
                mockConstruction(MisuraDAO.class, (mock, ctx) ->
                    when(mock.doRetrieveAll(3)).thenReturn(misureMock)
                )
        ) {
            servlet.doPost(req, resp);

            verify(req).setAttribute("maglietta", magliettaMock);
            verify(req).setAttribute("misure", misureMock);
            verify(dispatcher).forward(req, resp);
        }
    }
}

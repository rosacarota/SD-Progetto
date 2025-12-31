package control.maglietta;

import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.SQLException;

import static org.mockito.Mockito.*;

class DescrizioneMagliettaTest {

    private DescrizioneMaglietta servlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private RequestDispatcher dispatcherOk;
    private RequestDispatcher dispatcherError;

    @BeforeEach
    void setup() {
        servlet = new DescrizioneMaglietta();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        dispatcherOk = mock(RequestDispatcher.class);
        dispatcherError = mock(RequestDispatcher.class);

        when(req.getParameter("id")).thenReturn("5");
        when(req.getRequestDispatcher("/pages/descrizione.jsp")).thenReturn(dispatcherOk);
        when(req.getRequestDispatcher("/pages/errorpage.jsp")).thenReturn(dispatcherError);
    }

    // -------- Test doGet() --------

    // {id_valido, dao_ok}
    @Test
    void doGet_idValido_daoOk() throws Exception {
        MagliettaBean bean = new MagliettaBean();
        bean.setID(5);

        try (MockedConstruction<MagliettaDAO> mocked =
                     mockConstruction(MagliettaDAO.class,
                             (dao, ctx) -> when(dao.doRetrieveByKey(5)).thenReturn(bean))) {

            servlet.doGet(req, resp);

            verify(req).setAttribute("magliettaBean", bean);
            verify(dispatcherOk).forward(req, resp);
            verify(dispatcherError, never()).forward(any(), any());
        }
    }

    // {id_valido, dao_exception}
    @Test
    void doGet_daoException_forwardError() throws Exception {
        try (MockedConstruction<MagliettaDAO> mocked =
                     mockConstruction(MagliettaDAO.class,
                             (dao, ctx) -> when(dao.doRetrieveByKey(5))
                                     .thenThrow(new SQLException()))) {

            servlet.doGet(req, resp);

            verify(dispatcherError).forward(req, resp);
            verify(dispatcherOk, never()).forward(any(), any());
        }
    }

    // {id_non_numerico}
    @Test
    void doGet_idNonNumerico_forwardError() throws Exception {
        when(req.getParameter("id")).thenReturn("abc");

        servlet.doGet(req, resp);

        verify(dispatcherError).forward(req, resp);
        verify(dispatcherOk, never()).forward(any(), any());
    }

    // -------- Test doPost() --------

    // {delegazione_a_doGet}
    @Test
    void doPost_delegaDoGet() throws Exception {
        MagliettaBean bean = new MagliettaBean();
        bean.setID(5);

        try (MockedConstruction<MagliettaDAO> mocked =
                     mockConstruction(MagliettaDAO.class,
                             (dao, ctx) -> when(dao.doRetrieveByKey(5)).thenReturn(bean))) {

            servlet.doPost(req, resp);

            verify(req).setAttribute("magliettaBean", bean);
            verify(dispatcherOk).forward(req, resp);
        }
    }
}

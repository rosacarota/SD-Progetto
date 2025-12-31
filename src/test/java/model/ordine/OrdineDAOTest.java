package model.ordine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdineDAOTest {

    private OrdineDAO dao;
    private DataSource dsMock;
    private Connection connMock;
    private Statement stmtMock;
    private PreparedStatement psMock;
    private ResultSet rsMock;

    @BeforeEach
    void setup() throws Exception {
        dsMock = mock(DataSource.class);
        connMock = mock(Connection.class);
        stmtMock = mock(Statement.class);
        psMock = mock(PreparedStatement.class);
        rsMock = mock(ResultSet.class);

        dao = new OrdineDAO(dsMock);

        when(dsMock.getConnection()).thenReturn(connMock);
        when(connMock.prepareStatement(anyString())).thenReturn(psMock);
        when(connMock.createStatement()).thenReturn(stmtMock);
        when(psMock.executeQuery()).thenReturn(rsMock);
        when(stmtMock.execute(anyString())).thenReturn(true);
    }

    // -------- Test doRetrieveByKey() --------

    // {id_valido, rs_con_riga, db_ok}
    @Test
    void doRetrieveByKey_idValido() throws Exception {
        LocalDate today = LocalDate.now();

        when(rsMock.next()).thenReturn(true);

        when(rsMock.getInt("ID")).thenReturn(1);
        when(rsMock.getString("username")).thenReturn("mango");
        when(rsMock.getFloat("prezzoTotale")).thenReturn(100f);
        when(rsMock.getDate("dataConsegna")).thenReturn(Date.valueOf(today));
        when(rsMock.getDate("dataOrdine")).thenReturn(Date.valueOf(today));
        when(rsMock.getString("nomeConsegna")).thenReturn("Mario");
        when(rsMock.getString("cognomeConsegna")).thenReturn("Rossi");
        when(rsMock.getString("cap")).thenReturn("80000");
        when(rsMock.getString("via")).thenReturn("Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto");

        OrdineBean o = dao.doRetrieveByKey(1);
        verify(psMock).setInt(1, 1);

        assertNotNull(o);
        assertEquals(1, o.getID());
        assertEquals("mango", o.getUsername());
        assertEquals(100f, o.getPrezzoTotale());
        assertEquals(today, o.getDataConsegna());
        assertEquals(today, o.getDataOrdine());
        assertEquals("Mario", o.getNomeConsegna());
        assertEquals("Rossi", o.getCognomeConsegna());
        assertEquals("80000", o.getCap());
        assertEquals("Via Mango", o.getVia());
        assertEquals("Casotto", o.getCitta());
    }

    // {id_valido, rs_vuoto, db_ok}
    @Test
    void doRetrieveByKey_rsVuoto() throws Exception {
        when(rsMock.next()).thenReturn(false);

        OrdineBean o = dao.doRetrieveByKey(1);

        assertNull(o);
    }

    // {db_exception}
    @Test
    void doRetrieveByKey_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.doRetrieveByKey(1));
    }

    // -------- Test doRetrieveByKey() --------

    // {username_valido, rs_vuoto, db_ok}
    @Test
    void doRetrieveByUsername_vuoto() throws Exception {
        when(rsMock.next()).thenReturn(false);

        Collection<OrdineBean> res = dao.doRetrieveByKey("mango");

        verify(psMock).setString(1, "mango");
        assertTrue(res.isEmpty());
    }

    // {username_valido, rs_una_riga, db_ok}
    @Test
    void doRetrieveByUsername_unaRiga() throws Exception {
        LocalDate today = LocalDate.now();

        when(rsMock.next()).thenReturn(true, false);

        when(rsMock.getInt("ID")).thenReturn(1);
        when(rsMock.getString("username")).thenReturn("mango");
        when(rsMock.getFloat("prezzoTotale")).thenReturn(50f);
        when(rsMock.getDate("dataConsegna")).thenReturn(Date.valueOf(today));
        when(rsMock.getDate("dataOrdine")).thenReturn(Date.valueOf(today));
        when(rsMock.getString("nomeConsegna")).thenReturn("Mario");
        when(rsMock.getString("cognomeConsegna")).thenReturn("Rossi");
        when(rsMock.getString("cap")).thenReturn("80000");
        when(rsMock.getString("via")).thenReturn("Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto");

        Collection<OrdineBean> res = dao.doRetrieveByKey("mango");

        assertEquals(1, res.size());
        verify(psMock).setString(1, "mango");

        OrdineBean o = res.iterator().next();
        assertEquals("mango", o.getUsername());
        assertEquals(50f, o.getPrezzoTotale());
        assertEquals(today, o.getDataConsegna());
        assertEquals(today, o.getDataOrdine());
        assertEquals("Mario", o.getNomeConsegna());
        assertEquals("Rossi", o.getCognomeConsegna());
        assertEquals("80000", o.getCap());
        assertEquals("Via Mango", o.getVia());
        assertEquals("Casotto", o.getCitta());
    }

    // -------- Test doRetriveAll() --------

    // {order_valido, rs_piu_righe, db_ok}
    @Test
    void doRetrieveAll_orderValido() throws Exception {
        LocalDate today = LocalDate.now();

        when(rsMock.next()).thenReturn(true, true, false);

        when(rsMock.getInt("ID")).thenReturn(1, 2);
        when(rsMock.getString("username")).thenReturn("mango", "mango");
        when(rsMock.getFloat("prezzoTotale")).thenReturn(10f, 20f);
        when(rsMock.getDate("dataConsegna")).thenReturn(Date.valueOf(today), Date.valueOf(today));
        when(rsMock.getDate("dataOrdine")).thenReturn(Date.valueOf(today), Date.valueOf(today));
        when(rsMock.getString("nomeConsegna")).thenReturn("Mario", "Mario");
        when(rsMock.getString("cognomeConsegna")).thenReturn("Rossi", "Rossi");
        when(rsMock.getString("cap")).thenReturn("80000", "80000");
        when(rsMock.getString("via")).thenReturn("Via Mango", "Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto", "Casotto");

        Collection<OrdineBean> res = dao.doRetriveAll("username");

        assertEquals(2, res.size());

        Iterator<OrdineBean> it = res.iterator();
        OrdineBean o = it.next();

        assertEquals("mango", o.getUsername());
        assertEquals(10f, o.getPrezzoTotale());
        assertEquals(today, o.getDataConsegna());
        assertEquals(today, o.getDataOrdine());

        verify(connMock).prepareStatement(contains("ORDER BY username"));
    }

    // {order_valido, prepareStatement_exception}
    @Test
    void doRetrieveAll_prepareStatementException() throws Exception {
        when(connMock.prepareStatement(anyString()))
                .thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.doRetriveAll("username"));
    }

    // {order_valido, getConnection_exception}
    @Test
    void doRetrieveAll_connectionException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.doRetriveAll("username"));
    }

    // {order_invalido, db_ok}
    @Test
    void doRetrieveAll_orderInvalido() throws Exception {
        when(rsMock.next()).thenReturn(false);

        Collection<OrdineBean> res = dao.doRetriveAll("nonValido");

        assertTrue(res.isEmpty());
        verify(connMock).prepareStatement(eq("SELECT * FROM Ordine"));
    }

    // -------- Test doSave() --------

    // {ordine_valido, db_ok}
    @Test
    void doSave_ok() throws Exception {
        OrdineBean o = new OrdineBean();
        o.setUsername("mango");
        o.setPrezzoTotale(99f);
        o.setNomeConsegna("Mario");
        o.setCognomeConsegna("Rossi");
        o.setCap("80000");
        o.setVia("Via Mango");
        o.setCitta("Casotto");

        dao.doSave(o);

        verify(psMock).setString(1, "mango");
        verify(psMock).setFloat(2, 99f);
        verify(psMock).setDate(eq(3), any(Date.class));
        verify(psMock).setDate(eq(4), any(Date.class));
        verify(psMock).setString(5, "Mario");
        verify(psMock).setString(6, "Rossi");
        verify(psMock).setString(7, "80000");
        verify(psMock).setString(8, "Via Mango");
        verify(psMock).setString(9, "Casotto");
        verify(psMock).executeUpdate();
    }

    // -------- Test getMaxID() --------

    // {db_ok, rs_con_riga}
    @Test
    void getMaxID_ok() throws Exception {
        when(rsMock.next()).thenReturn(true);
        when(rsMock.getInt("AUTO_INCREMENT")).thenReturn(8);

        assertEquals(8, dao.getMaxID());
        verify(psMock).setString(1, "Ordine");
    }

    // {db_ok, rs_vuoto}
    @Test
    void getMaxID_rsVuoto() throws Exception {
        when(rsMock.next()).thenReturn(false);

        assertThrows(SQLException.class, () -> dao.getMaxID());
    }

    // {db_exception}
    @Test
    void getMaxID_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.getMaxID());
    }
}

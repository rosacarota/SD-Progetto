package model.maglietta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MagliettaDAOTest {
    private MagliettaDAO dao;
    private DataSource dsMock;
    private Connection connMock;
    private Statement stmtMock;
    private PreparedStatement psMock;
    private ResultSet rsMock;
    private ResultSetMetaData metaMock;

    @BeforeEach
    void setup() throws Exception {
        dsMock = mock(DataSource.class);
        connMock = mock(Connection.class);
        stmtMock = mock(Statement.class);
        psMock = mock(PreparedStatement.class);
        rsMock = mock(ResultSet.class);
        metaMock = mock(ResultSetMetaData.class);

        dao = new MagliettaDAO(dsMock);

        when(dsMock.getConnection()).thenReturn(connMock);
        when(connMock.prepareStatement(anyString())).thenReturn(psMock);
        when(connMock.createStatement()).thenReturn(stmtMock);
        when(psMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.getMetaData()).thenReturn(metaMock);
    }

    // -------- Test doRetrieveByTipo() --------

    // {tipo_valido, rs_vuoto, db_ok}
    @Test
    void doRetrieveByTipo_vuoto() throws Exception {
        when(rsMock.next()).thenReturn(false);

        Collection<MagliettaBean> res = dao.doRetrieveByTipo("Film e Serie TV");
        verify(psMock).setString(1, "Film e Serie TV");

        assertTrue(res.isEmpty());
    }

    // {tipo_valido, rs_una_riga, db_ok}
    @Test
    void doRetrieveByTipo_unaRiga() throws Exception {
        when(rsMock.next()).thenReturn(true, false);

        when(rsMock.getInt("ID")).thenReturn(1);
        when(rsMock.getString("nome")).thenReturn("A");
        when(rsMock.getFloat("prezzo")).thenReturn(10f);
        when(rsMock.getInt("IVA")).thenReturn(22);
        when(rsMock.getString("colore")).thenReturn("Rosso");
        when(rsMock.getString("tipo")).thenReturn("Film e Serie TV");
        when(rsMock.getString("grafica")).thenReturn("X");
        when(rsMock.getString("descrizione")).thenReturn("Y");

        Collection<MagliettaBean> res = dao.doRetrieveByTipo("Film e Serie TV");

        assertEquals(1, res.size());
        verify(psMock).setString(1, "Film e Serie TV");
        MagliettaBean b = res.iterator().next();

        assertEquals(1, b.getID());
        assertEquals("A", b.getNome());
        assertEquals(10f, b.getPrezzo());
        assertEquals(22, b.getIVA());
        assertEquals("Rosso", b.getColore());
        assertEquals("Film e Serie TV", b.getTipo());
        assertEquals("X", b.getGrafica());
        assertEquals("Y", b.getDescrizione());
    }

    // {tipo_valido, rs_piu_righe, db_ok}
    @Test
    void doRetrieveByTipo_dueRighe() throws Exception {
        when(rsMock.next()).thenReturn(true, true, false);

        when(rsMock.getInt("ID")).thenReturn(1, 2);
        when(rsMock.getString("nome")).thenReturn("A", "B");
        when(rsMock.getFloat("prezzo")).thenReturn(10f, 20f);
        when(rsMock.getInt("IVA")).thenReturn(22, 22);
        when(rsMock.getString("colore")).thenReturn("Rosso", "Blu");
        when(rsMock.getString("tipo")).thenReturn("Film e Serie TV", "Film e Serie TV");
        when(rsMock.getString("grafica")).thenReturn("G1", "G2");
        when(rsMock.getString("descrizione")).thenReturn("D1", "D2");

        Collection<MagliettaBean> res = dao.doRetrieveByTipo("Film e Serie TV");

        assertEquals(2, res.size());
        verify(psMock).setString(1, "Film e Serie TV");
        Iterator<MagliettaBean> it = res.iterator();
        MagliettaBean b1 = it.next();
        MagliettaBean b2 = it.next();

        assertEquals("A", b1.getNome());
        assertEquals("B", b2.getNome());
    }

    // {tipo_valido, db_exception}
    @Test
    void doRetrieveByTipo_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.doRetrieveByTipo("Film e Serie TV"));
    }

    // -------- Test doRetrieveByKey() --------

    // {id_valido, db_ok}
    @Test
    void doRetrieveByKey_idValido() throws Exception {
        when(rsMock.next()).thenReturn(true);

        when(rsMock.getInt("ID")).thenReturn(5);
        when(rsMock.getString("nome")).thenReturn("Maglia");
        when(rsMock.getFloat("prezzo")).thenReturn(30f);
        when(rsMock.getInt("IVA")).thenReturn(22);
        when(rsMock.getString("colore")).thenReturn("Verde");
        when(rsMock.getString("tipo")).thenReturn("Casual");
        when(rsMock.getString("grafica")).thenReturn("G");
        when(rsMock.getString("descrizione")).thenReturn("DESC");

        MagliettaBean bean = dao.doRetrieveByKey(5);
        verify(psMock).setInt(1, 5);

        assertEquals(5, bean.getID());
        assertEquals("Maglia", bean.getNome());
        assertEquals(30f, bean.getPrezzo());
        assertEquals(22, bean.getIVA());
        assertEquals("Verde", bean.getColore());
        assertEquals("Casual", bean.getTipo());
        assertEquals("G", bean.getGrafica());
        assertEquals("DESC", bean.getDescrizione());
    }

    // {id_invalido, db_ok}
    @Test
    void doRetrieveByKey_nextFalse() throws Exception {
        when(rsMock.next()).thenReturn(false);
        when(rsMock.getInt(anyString())).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.doRetrieveByKey(999));
        verify(psMock).setInt(1, 999);
    }

    // {id_valido, db_exception}
    @Test
    void doRetrieveByKey_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.doRetrieveByKey(1));
    }

    // -------- Test doRetriveAll() --------

    // {order_valido, rs_piu_righe, db_ok}
    @Test
    void doRetrieveAll_orderValido() throws Exception {
        when(rsMock.next()).thenReturn(true, true, false);

        when(rsMock.getInt("ID")).thenReturn(1, 2);
        when(rsMock.getString("nome")).thenReturn("A", "B");
        when(rsMock.getFloat("prezzo")).thenReturn(10f, 20f);

        Collection<MagliettaBean> res = dao.doRetriveAll("nome");

        assertEquals(2, res.size());
        Iterator<MagliettaBean> it = res.iterator();
        assertEquals("A", it.next().getNome());
        assertEquals("B", it.next().getNome());

        verify(connMock).prepareStatement(contains("ORDER BY nome"));
        verify(connMock).close();
        verify(psMock).close();
    }

    // {order_invalido, rs_una_riga, db_ok}
    @Test
    void doRetrieveAll_orderInvalido() throws Exception {
        when(rsMock.next()).thenReturn(true, false);

        when(rsMock.getInt("ID")).thenReturn(1);
        when(rsMock.getString("nome")).thenReturn("A");

        Collection<MagliettaBean> res = dao.doRetriveAll("nonEsiste");

        assertEquals(1, res.size());
    }

    // {order_valido, db_exception}
    @Test
    void doRetrieveAll_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.doRetriveAll("nome"));
    }

    // -------- Test doSave() --------

    // {maglietta_valida, db_ok}
    @Test
    void doSave_ok() throws Exception {
        MagliettaBean b = new MagliettaBean();
        b.setNome("A");
        b.setPrezzo(10);
        b.setIVA(22);
        b.setColore("Rosso");
        b.setTipo("Film e Serie TV");
        b.setGrafica("G");
        b.setDescrizione("D");

        dao.doSave(b);

        verify(psMock).setString(1, "A");
        verify(psMock).setFloat(2, 10f);
        verify(psMock).setInt(3, 22);
        verify(psMock).setString(4, "Rosso");
        verify(psMock).setString(5, "Film e Serie TV");
        verify(psMock).setString(6, "G");
        verify(psMock).setString(7, "D");
        verify(psMock).executeUpdate();
    }

    // {maglietta_valida, db_exception}
    @Test
    void doSave_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        MagliettaBean b = new MagliettaBean();

        assertThrows(SQLException.class, () -> dao.doSave(b));
    }

    // -------- Test doUpdate() --------

    // {maglietta_valida, db_ok}
    @Test
    void doUpdate_ok() throws Exception {
        MagliettaBean b = new MagliettaBean();
        b.setID(5);
        b.setNome("A");
        b.setPrezzo(10);
        b.setIVA(22);
        b.setColore("Rosso");
        b.setTipo("Tipo");
        b.setGrafica("G");
        b.setDescrizione("D");

        dao.doUpdate(b);

        verify(psMock).setString(1, "A");
        verify(psMock).setFloat(2, 10f);
        verify(psMock).setInt(3, 22);
        verify(psMock).setString(4, "Rosso");
        verify(psMock).setString(5, "Tipo");
        verify(psMock).setString(6, "G");
        verify(psMock).setString(7, "D");
        verify(psMock).setInt(8, 5);
        verify(psMock).executeUpdate();
    }

    // {maglietta_valida, db_exception}
    @Test
    void doUpdate_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        MagliettaBean b = new MagliettaBean();

        assertThrows(SQLException.class, () -> dao.doUpdate(b));
    }

    // -------- Test doDelete() --------

    // {update_nonzero, db_ok}
    @Test
    void doDelete_true() throws Exception {
        when(psMock.executeUpdate()).thenReturn(1);

        assertTrue(dao.doDelete(5));
        verify(psMock).setInt(1, 5);
    }

    // {update_zero, db_ok}
    @Test
    void doDelete_false() throws Exception {
        when(psMock.executeUpdate()).thenReturn(0);

        assertFalse(dao.doDelete(5));
        verify(psMock).setInt(1, 5);
    }

    // {db_exception}
    @Test
    void doDelete_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.doDelete(5));
    }

    // -------- Test deleteMaglietta() --------

    // {update_nonzero, db_ok}
    @Test
    void deleteMaglietta_true() throws Exception {
        when(psMock.executeUpdate()).thenReturn(1);

        assertTrue(dao.deleteMaglietta(5));
        verify(psMock).setInt(1, 5);
    }

    // {update_zero, db_ok}
    @Test
    void deleteMaglietta_false() throws Exception {
        when(psMock.executeUpdate()).thenReturn(0);

        assertFalse(dao.deleteMaglietta(5));
        verify(psMock).setInt(1, 5);
    }

    // {db_exception}
    @Test
    void deleteMaglietta_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.deleteMaglietta(5));
    }

    // -------- Test getMaxID() --------

    // {db_ok, rs_con_riga}
    @Test
    void getMaxID_ok() throws Exception {
        when(rsMock.next()).thenReturn(true);
        when(rsMock.getInt("AUTO_INCREMENT")).thenReturn(42);

        assertEquals(42, dao.getMaxID());
        verify(psMock).setString(1, "Maglietta");
    }

    // {db_exception}
    @Test
    void getMaxID_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> dao.getMaxID());
    }

    // {db_ok, rs_vuoto}
    @Test
    void getMaxID_rsVuoto() throws Exception {
        when(rsMock.next()).thenReturn(false);

        assertThrows(SQLException.class, () -> dao.getMaxID());
    }
}

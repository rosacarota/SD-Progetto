package model.utente;

import model.security.CryptoUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UtenteDAOTest {

    private UtenteDAO dao;
    private DataSource dsMock;
    private SecretKey keyMock;

    private Connection connMock;
    private PreparedStatement psMock;
    private ResultSet rsMock;

    @BeforeEach
    void setup() throws Exception {
        dsMock = mock(DataSource.class);
        keyMock = mock(SecretKey.class);

        connMock = mock(Connection.class);
        psMock = mock(PreparedStatement.class);
        rsMock = mock(ResultSet.class);

        dao = new UtenteDAO(dsMock, keyMock);

        when(dsMock.getConnection()).thenReturn(connMock);
        when(connMock.prepareStatement(anyString())).thenReturn(psMock);
        when(psMock.executeQuery()).thenReturn(rsMock);
        when(psMock.executeUpdate()).thenReturn(1);
    }

    // -------- Test doRetrieveByKey() --------

    // {code_valido, rs_non_vuoto, decrypt_ok, birth_non_null, exp_non_null, db_ok}
    @Test
    void doRetrieveByKey_ok_decryptOk_birthAndExpPresent() throws Exception {
        when(rsMock.isBeforeFirst()).thenReturn(true);
        when(rsMock.next()).thenReturn(true);

        when(rsMock.getString("username")).thenReturn("mango");
        when(rsMock.getString("pwd")).thenReturn("hash");
        when(rsMock.getString("nome")).thenReturn("Mario");
        when(rsMock.getString("cognome")).thenReturn("Rossi");
        when(rsMock.getString("email")).thenReturn("m@x.it");
        when(rsMock.getDate("dataNascita")).thenReturn(Date.valueOf(LocalDate.of(2000, 1, 1)));

        when(rsMock.getString("nomeCarta")).thenReturn("ENC_NOME");
        when(rsMock.getString("cognomeCarta")).thenReturn("ENC_COGNOME");
        when(rsMock.getString("numCarta")).thenReturn("ENC_NUM");
        when(rsMock.getString("CVV")).thenReturn("ENC_CVV");
        when(rsMock.getString("dataScadenza")).thenReturn("ENC_EXP");

        when(rsMock.getString("cap")).thenReturn("80000");
        when(rsMock.getString("via")).thenReturn("Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto");
        when(rsMock.getString("tipo")).thenReturn("cliente");

        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_NOME"))).thenReturn("NomeCarta");
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_COGNOME"))).thenReturn("CognomeCarta");
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_NUM"))).thenReturn("1111");
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_CVV"))).thenReturn("999");
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_EXP"))).thenReturn("2030-01-01");

            UtenteBean u = dao.doRetrieveByKey("mango");

            assertNotNull(u);
            assertEquals("mango", u.getUsername());
            assertEquals("hash", u.getPwd());
            assertEquals("Mario", u.getNome());
            assertEquals("Rossi", u.getCognome());
            assertEquals("m@x.it", u.getEmail());
            assertEquals(LocalDate.of(2000, 1, 1), u.getDataNascita());
            assertEquals("NomeCarta", u.getNomeCarta());
            assertEquals("CognomeCarta", u.getCognomeCarta());
            assertEquals("1111", u.getNumCarta());
            assertEquals("999", u.getCVV());
            assertEquals(LocalDate.of(2030, 1, 1), u.getDataScadenza());
            assertEquals("80000", u.getCap());
            assertEquals("Via Mango", u.getVia());
            assertEquals("Casotto", u.getCitta());
            assertEquals("cliente", u.getTipo());

            verify(psMock).setString(1, "mango");
        }
    }

    // {code_valido, rs_vuoto, db_ok}
    @Test
    void doRetrieveByKey_rsVuoto_returnsNull() throws Exception {
        when(rsMock.isBeforeFirst()).thenReturn(false);

        UtenteBean u = dao.doRetrieveByKey("mango");

        assertNull(u);
        verify(psMock).setString(1, "mango");
    }

    // {db_exception}
    @Test
    void doRetrieveByKey_dbException() throws Exception {
        when(dsMock.getConnection()).thenThrow(new SQLException("db fail"));
        assertThrows(SQLException.class, () -> dao.doRetrieveByKey("mango"));
    }

    // {code_valido, rs_non_vuoto, decrypt_exception, db_ok}
    @Test
    void doRetrieveByKey_decryptException_wrapsSQLException() throws Exception {
        when(rsMock.isBeforeFirst()).thenReturn(true);
        when(rsMock.next()).thenReturn(true);

        when(rsMock.getString("username")).thenReturn("mango");
        when(rsMock.getString("pwd")).thenReturn("hash");
        when(rsMock.getString("nome")).thenReturn("Mario");
        when(rsMock.getString("cognome")).thenReturn("Rossi");
        when(rsMock.getString("email")).thenReturn("m@x.it");
        when(rsMock.getDate("dataNascita")).thenReturn(null);

        when(rsMock.getString("nomeCarta")).thenReturn("ENC_NOME");
        when(rsMock.getString("cognomeCarta")).thenReturn(null);
        when(rsMock.getString("numCarta")).thenReturn(null);
        when(rsMock.getString("CVV")).thenReturn(null);
        when(rsMock.getString("dataScadenza")).thenReturn(null);

        when(rsMock.getString("cap")).thenReturn("80000");
        when(rsMock.getString("via")).thenReturn("Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto");
        when(rsMock.getString("tipo")).thenReturn("cliente");

        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_NOME")))
                    .thenThrow(new GeneralSecurityException("boom"));

            SQLException ex = assertThrows(SQLException.class, () -> dao.doRetrieveByKey("mango"));
            assertTrue(ex.getMessage().contains("Decryption error"));
        }
    }

    // -------- Test doRetrieveByEmail() --------

    // {email_valida, rs_vuoto, db_ok}
    @Test
    void doRetrieveByEmail_rsVuoto_returnsNull() throws Exception {
        when(rsMock.isBeforeFirst()).thenReturn(false);

        UtenteBean u = dao.doRetrieveByEmail("m@x.it");

        assertNull(u);
        verify(psMock).setString(1, "m@x.it");
    }

    // {email_valida, rs_non_vuoto, decrypt_ok, birth_null, exp_null, db_ok}
    @Test
    void doRetrieveByEmail_ok_birthNull_expNull() throws Exception {
        when(rsMock.isBeforeFirst()).thenReturn(true);
        when(rsMock.next()).thenReturn(true);

        when(rsMock.getString("username")).thenReturn("u1");
        when(rsMock.getString("pwd")).thenReturn("hash");
        when(rsMock.getString("nome")).thenReturn("N");
        when(rsMock.getString("cognome")).thenReturn("C");
        when(rsMock.getString("email")).thenReturn("m@x.it");
        when(rsMock.getDate("dataNascita")).thenReturn(null);

        when(rsMock.getString("nomeCarta")).thenReturn(null);
        when(rsMock.getString("cognomeCarta")).thenReturn(null);
        when(rsMock.getString("numCarta")).thenReturn(null);
        when(rsMock.getString("CVV")).thenReturn(null);
        when(rsMock.getString("dataScadenza")).thenReturn(null);

        when(rsMock.getString("cap")).thenReturn("80000");
        when(rsMock.getString("via")).thenReturn("Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto");
        when(rsMock.getString("tipo")).thenReturn("cliente");

        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            UtenteBean u = dao.doRetrieveByEmail("m@x.it");

            assertNotNull(u);
            assertNull(u.getDataNascita());
            assertNull(u.getDataScadenza());
            assertEquals("80000", u.getCap());
            assertEquals("Via Mango", u.getVia());
            assertEquals("Casotto", u.getCitta());
            assertEquals("cliente", u.getTipo());

            verify(psMock).setString(1, "m@x.it");
        }
    }

    // -------- Test doRetriveAll() --------

    // {rs_piu_righe, decrypt_ok, birth_null, exp_null, db_ok}
    @Test
    void doRetrieveAll_dueRighe() throws Exception {
        when(connMock.prepareStatement(eq("SELECT * FROM Utente"))).thenReturn(psMock);
        when(psMock.executeQuery()).thenReturn(rsMock);

        AtomicInteger nextCalls = new AtomicInteger(0);
        when(rsMock.next()).thenAnswer(inv -> {
            int c = nextCalls.incrementAndGet();
            if (c == 1) return true;
            if (c == 2) return true;
            if (c == 3) return false;
            throw new AssertionError("too many next calls");
        });

        when(rsMock.getString("username")).thenReturn("u1", "u2");
        when(rsMock.getString("pwd")).thenReturn("p1", "p2");
        when(rsMock.getString("nome")).thenReturn("n1", "n2");
        when(rsMock.getString("cognome")).thenReturn("c1", "c2");
        when(rsMock.getString("email")).thenReturn("e1", "e2");
        when(rsMock.getDate("dataNascita")).thenReturn(null, null);

        when(rsMock.getString("nomeCarta")).thenReturn(null, null);
        when(rsMock.getString("cognomeCarta")).thenReturn(null, null);
        when(rsMock.getString("numCarta")).thenReturn(null, null);
        when(rsMock.getString("CVV")).thenReturn(null, null);
        when(rsMock.getString("dataScadenza")).thenReturn(null, null);

        when(rsMock.getString("cap")).thenReturn("x", "y");
        when(rsMock.getString("via")).thenReturn("v", "v");
        when(rsMock.getString("citta")).thenReturn("ct", "ct");
        when(rsMock.getString("tipo")).thenReturn("t", "t");

        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            Collection<UtenteBean> res = dao.doRetriveAll(null);
            assertEquals(2, res.size());

            ArrayList<UtenteBean> list = new ArrayList<>(res);
            assertEquals("u1", list.get(0).getUsername());
            assertEquals("x", list.get(0).getCap());
            assertEquals("v", list.get(0).getVia());
            assertEquals("ct", list.get(0).getCitta());
            assertEquals("t", list.get(0).getTipo());

            assertEquals("u2", list.get(1).getUsername());
            assertEquals("y", list.get(1).getCap());
            assertEquals("v", list.get(1).getVia());
            assertEquals("ct", list.get(1).getCitta());
            assertEquals("t", list.get(1).getTipo());
        }
    }

    // {rs_vuoto, db_ok}
    @Test
    void doRetrieveAll_zeroRighe() throws Exception {
        when(connMock.prepareStatement(eq("SELECT * FROM Utente"))).thenReturn(psMock);
        when(psMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(false);

        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            Collection<UtenteBean> res = dao.doRetriveAll(null);
            assertTrue(res.isEmpty());
        }
    }

    // -------- Test doSave() --------

    // {utente_valido, campi_carta_null_or_empty, encrypt_null, db_ok}
    @Test
    void doSave_ok_campiCartaNullOrEmpty_encryptNull() throws Exception {
        UtenteBean u = new UtenteBean();
        u.setUsername("mango");
        u.setPwd("plain");
        u.setNome("Mario");
        u.setCognome("Rossi");
        u.setEmail("m@x.it");
        u.setDataNascita(LocalDate.of(2000, 1, 1));
        u.setNomeCarta("");
        u.setCognomeCarta(null);
        u.setNumCarta("");
        u.setDataScadenza(null);
        u.setCVV("");
        u.setCap("80000");
        u.setVia("Via Mango");
        u.setCitta("Casotto");
        u.setTipo("cliente");

        try (MockedStatic<BCrypt> bc = mockStatic(BCrypt.class);
             MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {

            bc.when(BCrypt::gensalt).thenReturn("salt");
            bc.when(() -> BCrypt.hashpw(eq("plain"), eq("salt"))).thenReturn("HASHED");

            dao.doSave(u);

            verify(psMock).setString(1, "mango");
            verify(psMock).setString(2, "HASHED");
            verify(psMock).setString(3, "Mario");
            verify(psMock).setString(4, "Rossi");
            verify(psMock).setString(5, "m@x.it");
            verify(psMock).setDate(6, Date.valueOf(LocalDate.of(2000, 1, 1)));

            verify(psMock).setString(7, null);
            verify(psMock).setString(8, null);
            verify(psMock).setString(9, null);
            verify(psMock).setString(10, null);
            verify(psMock).setString(11, null);

            verify(psMock).setString(12, "80000");
            verify(psMock).setString(13, "Via Mango");
            verify(psMock).setString(14, "Casotto");
            verify(psMock).setString(15, "cliente");

            crypto.verifyNoInteractions();
        }
    }

    // {utente_valido, campi_carta_presenti, encrypt_ok, db_ok}
    @Test
    void doSave_ok_encryptOk() throws Exception {
        UtenteBean u = new UtenteBean();
        u.setUsername("mango");
        u.setPwd("plain");
        u.setNome("Mario");
        u.setCognome("Rossi");
        u.setEmail("m@x.it");
        u.setDataNascita(LocalDate.of(2000, 1, 1));
        u.setNomeCarta("NC");
        u.setCognomeCarta("CC");
        u.setNumCarta("1111");
        u.setDataScadenza(LocalDate.of(2030, 1, 1));
        u.setCVV("999");
        u.setCap("80000");
        u.setVia("Via Mango");
        u.setCitta("Casotto");
        u.setTipo("cliente");

        try (MockedStatic<BCrypt> bc = mockStatic(BCrypt.class);
             MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {

            bc.when(BCrypt::gensalt).thenReturn("salt");
            bc.when(() -> BCrypt.hashpw(eq("plain"), eq("salt"))).thenReturn("HASHED");

            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("NC"))).thenReturn("E_NC");
            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("CC"))).thenReturn("E_CC");
            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("1111"))).thenReturn("E_NUM");
            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("2030-01-01"))).thenReturn("E_EXP");
            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("999"))).thenReturn("E_CVV");

            dao.doSave(u);

            verify(psMock).setString(1, "mango");
            verify(psMock).setString(2, "HASHED");
            verify(psMock).setString(3, "Mario");
            verify(psMock).setString(4, "Rossi");
            verify(psMock).setString(5, "m@x.it");
            verify(psMock).setDate(6, Date.valueOf(LocalDate.of(2000, 1, 1)));

            verify(psMock).setString(7, "E_NC");
            verify(psMock).setString(8, "E_CC");
            verify(psMock).setString(9, "E_NUM");
            verify(psMock).setString(10, "E_EXP");
            verify(psMock).setString(11, "E_CVV");

            verify(psMock).setString(12, "80000");
            verify(psMock).setString(13, "Via Mango");
            verify(psMock).setString(14, "Casotto");
            verify(psMock).setString(15, "cliente");
        }
    }

    // {utente_valido, campi_carta_presenti, encrypt_exception, db_ok}
    @Test
    void doSave_encryptException_wrapsSQLException() throws Exception {
        UtenteBean u = new UtenteBean();
        u.setUsername("mango");
        u.setPwd("plain");
        u.setNome("Mario");
        u.setCognome("Rossi");
        u.setEmail("m@x.it");
        u.setDataNascita(LocalDate.of(2000, 1, 1));
        u.setNomeCarta("NC");
        u.setCognomeCarta(null);
        u.setNumCarta(null);
        u.setDataScadenza(null);
        u.setCVV(null);
        u.setCap("80000");
        u.setVia("Via Mango");
        u.setCitta("Casotto");
        u.setTipo("cliente");

        try (MockedStatic<BCrypt> bc = mockStatic(BCrypt.class);
             MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {

            bc.when(BCrypt::gensalt).thenReturn("salt");
            bc.when(() -> BCrypt.hashpw(eq("plain"), eq("salt"))).thenReturn("HASHED");

            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("NC")))
                    .thenThrow(new GeneralSecurityException("boom"));

            SQLException ex = assertThrows(SQLException.class, () -> dao.doSave(u));
            assertTrue(ex.getMessage().contains("Encryption error"));
        }
    }

    // -------- Test doUpdate() --------

    // {utente_valido, dataScadenza_null, encrypt_null, db_ok}
    @Test
    void doUpdate_ok_dataScadenzaNull_encryptNull() throws Exception {
        UtenteBean u = new UtenteBean();
        u.setUsername("mango");
        u.setPwd("plain");
        u.setNome("Mario");
        u.setCognome("Rossi");
        u.setEmail("m@x.it");
        u.setDataNascita(LocalDate.of(2000, 1, 1));
        u.setNumCarta("");
        u.setDataScadenza(null);
        u.setCVV("");
        u.setNomeCarta("");
        u.setCognomeCarta("");
        u.setCap("80000");
        u.setVia("Via Mango");
        u.setCitta("Casotto");
        u.setTipo("cliente");

        try (MockedStatic<BCrypt> bc = mockStatic(BCrypt.class);
             MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {

            bc.when(BCrypt::gensalt).thenReturn("salt");
            bc.when(() -> BCrypt.hashpw(eq("plain"), eq("salt"))).thenReturn("HASHED");

            dao.doUpdate(u);

            verify(psMock).setString(1, "HASHED");
            verify(psMock).setString(2, "Mario");
            verify(psMock).setString(3, "Rossi");
            verify(psMock).setString(4, "m@x.it");
            verify(psMock).setDate(5, Date.valueOf(LocalDate.of(2000, 1, 1)));

            verify(psMock).setString(6, null);
            verify(psMock).setString(7, null);
            verify(psMock).setString(8, null);
            verify(psMock).setString(9, null);
            verify(psMock).setString(10, null);
            verify(psMock).setString(11, "80000");
            verify(psMock).setString(12, "Via Mango");
            verify(psMock).setString(13, "Casotto");
            verify(psMock).setString(14, "cliente");
            verify(psMock).setString(15, "mango");

            crypto.verifyNoInteractions();
        }
    }

    // {utente_valido, dataScadenza_present, encrypt_ok, db_ok}
    @Test
    void doUpdate_ok_encryptOk() throws Exception {
        UtenteBean u = new UtenteBean();
        u.setUsername("mango");
        u.setPwd("plain");
        u.setNome("Mario");
        u.setCognome("Rossi");
        u.setEmail("m@x.it");
        u.setDataNascita(LocalDate.of(2000, 1, 1));
        u.setNumCarta("1111");
        u.setDataScadenza(LocalDate.of(2030, 1, 1));
        u.setCVV("999");
        u.setNomeCarta("NC");
        u.setCognomeCarta("CC");
        u.setCap("80000");
        u.setVia("Via Mango");
        u.setCitta("Casotto");
        u.setTipo("cliente");

        try (MockedStatic<BCrypt> bc = mockStatic(BCrypt.class);
             MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {

            bc.when(BCrypt::gensalt).thenReturn("salt");
            bc.when(() -> BCrypt.hashpw(eq("plain"), eq("salt"))).thenReturn("HASHED");

            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("1111"))).thenReturn("E_NUM");
            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("2030-01-01"))).thenReturn("E_EXP");
            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("999"))).thenReturn("E_CVV");
            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("NC"))).thenReturn("E_NC");
            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("CC"))).thenReturn("E_CC");

            dao.doUpdate(u);

            verify(psMock).setString(1, "HASHED");
            verify(psMock).setString(2, "Mario");
            verify(psMock).setString(3, "Rossi");
            verify(psMock).setString(4, "m@x.it");
            verify(psMock).setDate(5, Date.valueOf(LocalDate.of(2000, 1, 1)));

            verify(psMock).setString(6, "E_NUM");
            verify(psMock).setString(7, "E_EXP");
            verify(psMock).setString(8, "E_CVV");
            verify(psMock).setString(9, "E_NC");
            verify(psMock).setString(10, "E_CC");
            verify(psMock).setString(11, "80000");
            verify(psMock).setString(12, "Via Mango");
            verify(psMock).setString(13, "Casotto");
            verify(psMock).setString(14, "cliente");
            verify(psMock).setString(15, "mango");
        }
    }

    // {utente_valido, campi_carta_presenti, encrypt_exception, db_ok}
    @Test
    void doUpdate_encryptException_wrapsSQLException() throws Exception {
        UtenteBean u = new UtenteBean();
        u.setUsername("mango");
        u.setPwd("plain");
        u.setNome("Mario");
        u.setCognome("Rossi");
        u.setEmail("m@x.it");
        u.setDataNascita(LocalDate.of(2000, 1, 1));
        u.setNumCarta("1111");
        u.setDataScadenza(null);
        u.setCVV(null);
        u.setNomeCarta(null);
        u.setCognomeCarta(null);
        u.setCap("80000");
        u.setVia("Via Mango");
        u.setCitta("Casotto");
        u.setTipo("cliente");

        try (MockedStatic<BCrypt> bc = mockStatic(BCrypt.class);
             MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {

            bc.when(BCrypt::gensalt).thenReturn("salt");
            bc.when(() -> BCrypt.hashpw(eq("plain"), eq("salt"))).thenReturn("HASHED");

            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("1111")))
                    .thenThrow(new GeneralSecurityException("boom"));

            SQLException ex = assertThrows(SQLException.class, () -> dao.doUpdate(u));
            assertTrue(ex.getMessage().contains("Encryption error"));
        }
    }

    // -------- Test doDelete() --------

    // {executeUpdate_0, db_ok}
    @Test
    void doDelete_false_whenZeroRows() throws Exception {
        when(psMock.executeUpdate()).thenReturn(0);

        boolean res = dao.doDelete("mango");

        assertFalse(res);
        verify(psMock).setString(1, "mango");
    }

    // {executeUpdate_1, db_ok}
    @Test
    void doDelete_true_whenOneRow() throws Exception {
        when(psMock.executeUpdate()).thenReturn(1);

        boolean res = dao.doDelete("mango");

        assertTrue(res);
        verify(psMock).setString(1, "mango");
    }

    // -------- Test getUtenteBean() --------

    // {code_valido, rs_vuoto, db_ok}
    @Test
    void getUtenteBean_rsVuoto_returnsNull() throws Exception {
        when(rsMock.isBeforeFirst()).thenReturn(false);

        Method m = UtenteDAO.class.getDeclaredMethod("getUtenteBean", String.class, UtenteBean.class, String.class);
        m.setAccessible(true);

        Object out = m.invoke(dao, "mango", new UtenteBean(), "SELECT * FROM Utente WHERE username = ?");

        assertNull(out);
        verify(psMock).setString(1, "mango");
        verify(psMock).executeQuery();
    }

    // {code_valido, rs_non_vuoto, db_ok}
    @Test
    void getUtenteBean_rsNonVuoto_returnsUser() throws Exception {
        when(rsMock.isBeforeFirst()).thenReturn(true);
        when(rsMock.next()).thenReturn(true);

        when(rsMock.getString("username")).thenReturn("mango");
        when(rsMock.getString("pwd")).thenReturn("hash");
        when(rsMock.getString("nome")).thenReturn("Mario");
        when(rsMock.getString("cognome")).thenReturn("Rossi");
        when(rsMock.getString("email")).thenReturn("m@x.it");
        when(rsMock.getDate("dataNascita")).thenReturn(null);

        when(rsMock.getString("nomeCarta")).thenReturn(null);
        when(rsMock.getString("cognomeCarta")).thenReturn(null);
        when(rsMock.getString("numCarta")).thenReturn(null);
        when(rsMock.getString("CVV")).thenReturn(null);
        when(rsMock.getString("dataScadenza")).thenReturn(null);

        when(rsMock.getString("cap")).thenReturn("80000");
        when(rsMock.getString("via")).thenReturn("Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto");
        when(rsMock.getString("tipo")).thenReturn("cliente");

        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            Method m = UtenteDAO.class.getDeclaredMethod("getUtenteBean", String.class, UtenteBean.class, String.class);
            m.setAccessible(true);

            UtenteBean seed = new UtenteBean();
            Object out = m.invoke(dao, "mango", seed, "SELECT * FROM Utente WHERE username = ?");

            assertNotNull(out);
            assertSame(seed, out);
            assertEquals("mango", seed.getUsername());
            assertEquals("80000", seed.getCap());
            assertEquals("Via Mango", seed.getVia());
            assertEquals("Casotto", seed.getCitta());
            assertEquals("cliente", seed.getTipo());

            verify(psMock).setString(1, "mango");
            verify(rsMock).next();
        }
    }

    // -------- Test setUtente() --------

    // {birth_non_null, exp_non_null, decrypt_ok}
    @Test
    void setUtente_birthNonNull_expNonNull_decryptOk() throws Exception {
        when(rsMock.getString("username")).thenReturn("mango");
        when(rsMock.getString("pwd")).thenReturn("hash");
        when(rsMock.getString("nome")).thenReturn("Mario");
        when(rsMock.getString("cognome")).thenReturn("Rossi");
        when(rsMock.getString("email")).thenReturn("m@x.it");
        when(rsMock.getDate("dataNascita")).thenReturn(Date.valueOf(LocalDate.of(2000, 1, 1)));

        when(rsMock.getString("nomeCarta")).thenReturn("ENC_NOME");
        when(rsMock.getString("cognomeCarta")).thenReturn("ENC_COGNOME");
        when(rsMock.getString("numCarta")).thenReturn("ENC_NUM");
        when(rsMock.getString("CVV")).thenReturn("ENC_CVV");
        when(rsMock.getString("dataScadenza")).thenReturn("ENC_EXP");

        when(rsMock.getString("cap")).thenReturn("80000");
        when(rsMock.getString("via")).thenReturn("Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto");
        when(rsMock.getString("tipo")).thenReturn("cliente");

        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_NOME"))).thenReturn("NomeCarta");
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_COGNOME"))).thenReturn("CognomeCarta");
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_NUM"))).thenReturn("1111");
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_CVV"))).thenReturn("999");
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_EXP"))).thenReturn("2030-01-01");

            Method m = UtenteDAO.class.getDeclaredMethod("setUtente", ResultSet.class, UtenteBean.class);
            m.setAccessible(true);

            UtenteBean u = new UtenteBean();
            m.invoke(dao, rsMock, u);

            assertEquals(LocalDate.of(2000, 1, 1), u.getDataNascita());
            assertEquals(LocalDate.of(2030, 1, 1), u.getDataScadenza());
            assertEquals("NomeCarta", u.getNomeCarta());
            assertEquals("CognomeCarta", u.getCognomeCarta());
            assertEquals("1111", u.getNumCarta());
            assertEquals("999", u.getCVV());
            assertEquals("80000", u.getCap());
            assertEquals("Via Mango", u.getVia());
            assertEquals("Casotto", u.getCitta());
            assertEquals("cliente", u.getTipo());
        }
    }

    // {birth_null, exp_null, decrypt_null}
    @Test
    void setUtente_birthNull_expNull_allNulls() throws Exception {
        when(rsMock.getString("username")).thenReturn("mango");
        when(rsMock.getString("pwd")).thenReturn("hash");
        when(rsMock.getString("nome")).thenReturn("Mario");
        when(rsMock.getString("cognome")).thenReturn("Rossi");
        when(rsMock.getString("email")).thenReturn("m@x.it");
        when(rsMock.getDate("dataNascita")).thenReturn(null);

        when(rsMock.getString("nomeCarta")).thenReturn(null);
        when(rsMock.getString("cognomeCarta")).thenReturn(null);
        when(rsMock.getString("numCarta")).thenReturn(null);
        when(rsMock.getString("CVV")).thenReturn(null);
        when(rsMock.getString("dataScadenza")).thenReturn(null);

        when(rsMock.getString("cap")).thenReturn("80000");
        when(rsMock.getString("via")).thenReturn("Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto");
        when(rsMock.getString("tipo")).thenReturn("cliente");

        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            Method m = UtenteDAO.class.getDeclaredMethod("setUtente", ResultSet.class, UtenteBean.class);
            m.setAccessible(true);

            UtenteBean u = new UtenteBean();
            m.invoke(dao, rsMock, u);

            assertNull(u.getDataNascita());
            assertNull(u.getDataScadenza());
            assertNull(u.getNomeCarta());
            assertNull(u.getCognomeCarta());
            assertNull(u.getNumCarta());
            assertNull(u.getCVV());
            assertEquals("80000", u.getCap());
            assertEquals("Via Mango", u.getVia());
            assertEquals("Casotto", u.getCitta());
            assertEquals("cliente", u.getTipo());

            crypto.verifyNoInteractions();
        }
    }

    // {decrypt_exception, throws_sql_exception}
    @Test
    void setUtente_decryptException_wrapsSQLException() throws Exception {
        when(rsMock.getString("username")).thenReturn("mango");
        when(rsMock.getString("pwd")).thenReturn("hash");
        when(rsMock.getString("nome")).thenReturn("Mario");
        when(rsMock.getString("cognome")).thenReturn("Rossi");
        when(rsMock.getString("email")).thenReturn("m@x.it");
        when(rsMock.getDate("dataNascita")).thenReturn(null);

        when(rsMock.getString("nomeCarta")).thenReturn("ENC_NOME");
        when(rsMock.getString("cognomeCarta")).thenReturn(null);
        when(rsMock.getString("numCarta")).thenReturn(null);
        when(rsMock.getString("CVV")).thenReturn(null);
        when(rsMock.getString("dataScadenza")).thenReturn(null);

        when(rsMock.getString("cap")).thenReturn("80000");
        when(rsMock.getString("via")).thenReturn("Via Mango");
        when(rsMock.getString("citta")).thenReturn("Casotto");
        when(rsMock.getString("tipo")).thenReturn("cliente");

        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC_NOME")))
                    .thenThrow(new GeneralSecurityException("boom"));

            Method m = UtenteDAO.class.getDeclaredMethod("setUtente", ResultSet.class, UtenteBean.class);
            m.setAccessible(true);

            Exception ex = assertThrows(Exception.class, () -> m.invoke(dao, rsMock, new UtenteBean()));
            Throwable cause = ex.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof SQLException);
            assertTrue(cause.getMessage().contains("Decryption error"));
        }
    }

    // -------- Test encryptOrNull() --------

    // {v_null, returns_null}
    @Test
    void encryptOrNull_vNull_returnsNull() throws Exception {
        Method m = UtenteDAO.class.getDeclaredMethod("encryptOrNull", SecretKey.class, String.class);
        m.setAccessible(true);

        Object out = m.invoke(dao, keyMock, null);

        assertNull(out);
    }

    // {v_empty, returns_null}
    @Test
    void encryptOrNull_vEmpty_returnsNull() throws Exception {
        Method m = UtenteDAO.class.getDeclaredMethod("encryptOrNull", SecretKey.class, String.class);
        m.setAccessible(true);

        Object out = m.invoke(dao, keyMock, "");

        assertNull(out);
    }

    // {v_non_empty, encrypt_ok}
    @Test
    void encryptOrNull_vNonEmpty_encryptOk() throws Exception {
        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            crypto.when(() -> CryptoUtils.encrypt(eq(keyMock), eq("NC"))).thenReturn("E_NC");

            Method m = UtenteDAO.class.getDeclaredMethod("encryptOrNull", SecretKey.class, String.class);
            m.setAccessible(true);

            Object out = m.invoke(dao, keyMock, "NC");

            assertEquals("E_NC", out);
        }
    }

    // -------- Test decryptOrNull() --------

    // {v_null, returns_null}
    @Test
    void decryptOrNull_vNull_returnsNull() throws Exception {
        Method m = UtenteDAO.class.getDeclaredMethod("decryptOrNull", SecretKey.class, String.class);
        m.setAccessible(true);

        Object out = m.invoke(dao, keyMock, null);

        assertNull(out);
    }

    // {v_non_null, decrypt_ok}
    @Test
    void decryptOrNull_vNonNull_decryptOk() throws Exception {
        try (MockedStatic<CryptoUtils> crypto = mockStatic(CryptoUtils.class)) {
            crypto.when(() -> CryptoUtils.decrypt(eq(keyMock), eq("ENC"))).thenReturn("DEC");

            Method m = UtenteDAO.class.getDeclaredMethod("decryptOrNull", SecretKey.class, String.class);
            m.setAccessible(true);

            Object out = m.invoke(dao, keyMock, "ENC");

            assertEquals("DEC", out);
        }
    }
}

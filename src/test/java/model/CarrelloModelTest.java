package model;

import exception.GenericError;
import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;
import model.maglietta.MagliettaOrdine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarrelloModelTest {
    private CarrelloModel carrello;
    private MagliettaDAO daoMock;

    @BeforeEach
    void setup() {
        daoMock = mock(MagliettaDAO.class);
        carrello = new CarrelloModel(daoMock);
    }

    // -------- Test aggiungi() --------

    // {ID nuovo, carrello vuoto, taglia valida, DAO_OK}
    @Test
    void aggiungi_nuovoProdotto_carrelloVuoto_inserimentoCorretto() throws SQLException {
        MagliettaBean bean = new MagliettaBean();
        bean.setID(10);

        when(daoMock.doRetrieveByKey(10)).thenReturn(bean);

        carrello.aggiungi(10, "M");

        assertEquals(1, carrello.getCarrello().size());
        MagliettaOrdine o = carrello.getCarrello().get(0);
        assertEquals(10, o.getMagliettaBean().getID());
        assertEquals("M", o.getTaglia());
        assertEquals(1, o.getQuantita());
    }

    // {ID nuovo, carrello con altro ID, taglia valida, DAO_OK}
    @Test
    void aggiungi_nuovoProdotto_carrelloConAltroID_inserimentoCorretto() throws SQLException {
        MagliettaBean esistente = new MagliettaBean();
        esistente.setID(1);
        carrello.getCarrello().add(new MagliettaOrdine(esistente, "S"));

        MagliettaBean nuovo = new MagliettaBean();
        nuovo.setID(2);
        when(daoMock.doRetrieveByKey(2)).thenReturn(nuovo);

        carrello.aggiungi(2, "M");

        assertEquals(2, carrello.getCarrello().size());
    }

    // {ID nuovo, carrello vuoto, taglia valida, DAO_EXCEPTION}
    @Test
    void aggiungi_daoException_lanciaGenericError() throws SQLException {
        when(daoMock.doRetrieveByKey(5)).thenThrow(SQLException.class);

        assertThrows(GenericError.class, () -> carrello.aggiungi(5, "L"));
    }

    // {ID presente_stessa_taglia, carrello_con_stessa_ID_taglia, taglia valida}
    @Test
    void aggiungi_IDPresenteStessaTaglia_incrementaQuantita_eNonChiamaDAO() throws SQLException {
        MagliettaBean bean = new MagliettaBean();
        bean.setID(7);

        MagliettaOrdine ord = new MagliettaOrdine(bean, "S");
        carrello.getCarrello().add(ord);

        carrello.aggiungi(7, "S");

        assertEquals(2, ord.getQuantita());
        verify(daoMock, never()).doRetrieveByKey(anyInt());
    }

    // {ID presente_taglia_diversa, carrello_con_stessa_ID_taglia_diversa, taglia valida, DAO_OK}
    @Test
    void aggiungi_IDPresenteTagliaDiversa_creaNuovaEntry() throws SQLException {
        MagliettaBean bean = new MagliettaBean();
        bean.setID(5);

        carrello.getCarrello().add(new MagliettaOrdine(bean, "M"));
        when(daoMock.doRetrieveByKey(5)).thenReturn(bean);

        carrello.aggiungi(5, "L");

        assertEquals(2, carrello.getCarrello().size());
    }

    // -------- Test setQuantita() --------

    // {presente_stessa_taglia, quantita_corrente>0, quantita_nuova=0}
    @Test
    void setQuantita_zero_rimuoveProdotto() {
        MagliettaBean b = new MagliettaBean();
        b.setID(3);

        MagliettaOrdine o = new MagliettaOrdine(b, "M");
        carrello.getCarrello().add(o);

        carrello.setQuantita(3, 0, "M");

        assertTrue(carrello.getCarrello().isEmpty());
    }

    // {presente_stessa_taglia, quantita_corrente>0, quantita_nuova>0}
    @Test
    void setQuantita_valida_modificaQuantita() {
        MagliettaBean b = new MagliettaBean();
        b.setID(9);

        MagliettaOrdine o = new MagliettaOrdine(b, "L");
        carrello.getCarrello().add(o);

        carrello.setQuantita(9, 5, "L");

        assertEquals(5, o.getQuantita());
    }

    // {presente_stessa_taglia, quantita_corrente<=0, quantita_nuova>0}
    @Test
    void setQuantita_quantitaCorrenteZero_rimuoveProdotto() {
        MagliettaBean b = new MagliettaBean();
        b.setID(11);

        MagliettaOrdine o = new MagliettaOrdine(b, "S");
        o.setQuantita(0);
        carrello.getCarrello().add(o);

        carrello.setQuantita(11, 3, "S");

        assertTrue(carrello.getCarrello().isEmpty());
    }

    // {non_presente}
    @Test
    void setQuantita_idNonPresente_nessunaModifica() {
        MagliettaBean b = new MagliettaBean();
        b.setID(1);
        carrello.getCarrello().add(new MagliettaOrdine(b, "M"));

        carrello.setQuantita(999, 10, "M");

        assertEquals(1, carrello.getCarrello().size());
        assertEquals(1, carrello.getCarrello().get(0).getQuantita());
    }

    // {presente_taglia_diversa}
    @Test
    void setQuantita_tagliaDiversa_nonModifica() {
        MagliettaBean b = new MagliettaBean();
        b.setID(1);

        MagliettaOrdine o = new MagliettaOrdine(b, "S");
        carrello.getCarrello().add(o);

        carrello.setQuantita(1, 4, "XL");

        assertEquals(1, o.getQuantita());
    }

    // {presente_stessa_taglia, quantita_corrente>0, quantita_nuova<0}
    @Test
    void setQuantita_negativa_impostaQuantitaNegativa() {
        MagliettaBean b = new MagliettaBean();
        b.setID(13);

        MagliettaOrdine o = new MagliettaOrdine(b, "M");
        carrello.getCarrello().add(o);

        carrello.setQuantita(13, -2, "M");

        assertEquals(-2, o.getQuantita());
    }

    // -------- Test rimuovi() --------

    // {carrello_vuoto}
    @Test
    void rimuovi_carrelloVuoto_nessunaEccezione() {
        carrello.rimuovi(1, "M");
        assertTrue(carrello.getCarrello().isEmpty());
    }

    // {presente_stessa_taglia}
    @Test
    void rimuovi_presente_elimina() {
        MagliettaBean b = new MagliettaBean();
        b.setID(8);

        carrello.getCarrello().add(new MagliettaOrdine(b, "L"));

        carrello.rimuovi(8, "L");

        assertTrue(carrello.getCarrello().isEmpty());
    }

    // {presente_taglia_diversa}
    @Test
    void rimuovi_tagliaDiversa_nonRimuove() {
        MagliettaBean b = new MagliettaBean();
        b.setID(2);

        carrello.getCarrello().add(new MagliettaOrdine(b, "S"));

        carrello.rimuovi(2, "XL");

        assertEquals(1, carrello.getCarrello().size());
    }

    // {non_presente}
    @Test
    void rimuovi_idNonPresente_nessunaRimozione() {
        MagliettaBean b = new MagliettaBean();
        b.setID(2);

        carrello.getCarrello().add(new MagliettaOrdine(b, "M"));

        carrello.rimuovi(99, "M");

        assertEquals(1, carrello.getCarrello().size());
    }
}

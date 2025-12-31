package model.maglietta;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagliettaOrdineTest {

    // -------- Test incrementaQuantita() --------

    // {quantita_iniziale, operazione_incrementa}
    @Test
    void incrementaQuantita_da1_a2() {
        MagliettaBean bean = new MagliettaBean();
        bean.setPrezzo(10);

        MagliettaOrdine ordine = new MagliettaOrdine(bean, "M");

        ordine.incrementaQuantita();

        assertEquals(2, ordine.getQuantita());
    }

    // -------- Test decremenetaQuantita() --------

    // {quantita_iniziale, operazione_decrementa}
    @Test
    void decremenetaQuantita_da1_a0() {
        MagliettaBean bean = new MagliettaBean();
        bean.setPrezzo(10);

        MagliettaOrdine ordine = new MagliettaOrdine(bean, "M");

        ordine.decremenetaQuantita();

        assertEquals(0, ordine.getQuantita());
    }

    // -------- Test setQuantita() --------

    // {quantita_negativa, operazione_set}
    @Test
    void setQuantita_negativa_impostaValore() {
        MagliettaBean bean = new MagliettaBean();
        bean.setPrezzo(10);

        MagliettaOrdine ordine = new MagliettaOrdine(bean, "M");

        ordine.setQuantita(-5);

        assertEquals(-5, ordine.getQuantita());
    }

    // -------- Test getPrezzoTotale() --------

    // {quantita_incrementata, prezzo_positivo, operazione_getPrezzoTotale}
    @Test
    void getPrezzoTotale_quantita2_prezzo10() {
        MagliettaBean bean = new MagliettaBean();
        bean.setPrezzo(10);

        MagliettaOrdine ordine = new MagliettaOrdine(bean, "M");
        ordine.incrementaQuantita();

        assertEquals(20, ordine.getPrezzoTotale());
    }
}

package model;

import exception.GenericError;
import model.maglietta.MagliettaBean;
import model.maglietta.MagliettaDAO;
import model.maglietta.MagliettaOrdine;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarrelloModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient ArrayList<MagliettaOrdine> carrello;
    private final transient MagliettaDAO magliettaDAO;

    public CarrelloModel() {
        this(new MagliettaDAO());
    }

    public CarrelloModel(MagliettaDAO dao) {
        this.carrello = new ArrayList<>();
        this.magliettaDAO = dao;
    }

    public List<MagliettaOrdine> getCarrello() {
        return carrello;
    }

    public void setCarrello(List<MagliettaOrdine> carrello) {
        this.carrello = (ArrayList<MagliettaOrdine>) carrello;
    }

    public synchronized void aggiungi(int id, String taglia) {
        for (MagliettaOrdine m : carrello) {
            if (m.getMagliettaBean().getID() == id &&
                m.getTaglia().equals(taglia)) {
                m.incrementaQuantita();
                return;
            }
        }

        try {
            MagliettaBean magliettaBean = magliettaDAO.doRetrieveByKey(id);
            carrello.add(new MagliettaOrdine(magliettaBean, taglia));
        } catch (SQLException e) {
            throw new GenericError();
        }
    }

    public synchronized void setQuantita(int id, int quantita, String taglia) {
        for (MagliettaOrdine m : carrello) {
            if (m.getMagliettaBean().getID() == id && m.getTaglia().equals(taglia)) {
                if (m.getQuantita() <= 0 || quantita == 0)
                    carrello.remove(m);
                else
                    m.setQuantita(quantita);
                return;
            }
        }
    }

    public synchronized void rimuovi(int id, String taglia) {
        carrello.removeIf(m -> m.getMagliettaBean().getID() == id &&
                m.getTaglia().equals(taglia));
    }

}

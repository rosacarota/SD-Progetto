package model.acquisto;

public class AcquistoBean {
    private int IDAcquisto, IDOrdine, IDMaglietta, quantita;
    private String immagine, taglia;
    private float prezzoAq;
    private int ivaAq;

    public int getIDAcquisto() {
        return IDAcquisto;
    }

    public void setIDAcquisto(int IDAcquisto) {
        this.IDAcquisto = IDAcquisto;
    }

    public String getTaglia() {
        return taglia;
    }

    public void setTaglia(String taglia) {
        this.taglia = taglia;
    }

    public int getIDOrdine() {
        return IDOrdine;
    }

    public void setIDOrdine(int IDOrdine) {
        this.IDOrdine = IDOrdine;
    }

    public int getIDMaglietta() {
        return IDMaglietta;
    }

    public void setIDMaglietta(int IDMaglietta) {
        this.IDMaglietta = IDMaglietta;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public float getPrezzoAq() {
        return prezzoAq;
    }

    public void setPrezzoAq(float prezzoAq) {
        this.prezzoAq = prezzoAq;
    }

    public int getIvaAq() {
        return ivaAq;
    }

    public void setIvaAq(int ivaAq) {
        this.ivaAq = ivaAq;
    }

    @Override
    public String toString() {
        return "AcquistoBean{" +
                "IDOrdine=" + IDOrdine +
                ", IDMaglietta=" + IDMaglietta +
                ", quantita=" + quantita +
                ", immagine='" + immagine + '\'' +
                ", prezzoAq=" + prezzoAq +
                ", ivaAq=" + ivaAq +
                '}';
    }
}

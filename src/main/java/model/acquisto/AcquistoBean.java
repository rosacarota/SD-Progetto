package model.acquisto;

public class AcquistoBean {
    // NOTE: String fields are marked @nullable because this bean can be created/filled incrementally in Java
    // (defaults to null), while DB NOT NULL constraints are enforced at persistence/DAO level.

    /*@ public invariant IDAcquisto  >= 0;
      @ public invariant IDOrdine    >= 0;
      @ public invariant IDMaglietta >= 0;

      @ public invariant quantita >= 0;

      @ public invariant prezzoAq >= 0.0f;

      @ public invariant 0 <= ivaAq && ivaAq <= 100;

      @ public invariant immagine == null || (immagine.length() > 0 && immagine.length() <= 400);

      @ public invariant taglia == null ==> quantita == 0;
      @ public invariant quantita > 0 ==> taglia != null;

      @ public invariant
      @   taglia == null || (taglia.equals("XS") || taglia.equals("S") || taglia.equals("M")
      @       || taglia.equals("L") || taglia.equals("XL") || taglia.equals("XXL"));
      @*/

    private /*@ spec_public @*/ int IDAcquisto;
    private /*@ spec_public @*/ int IDOrdine;
    private /*@ spec_public @*/ int IDMaglietta;
    private /*@ spec_public @*/ int quantita;

    private /*@ spec_public @*/ float prezzoAq;
    private /*@ spec_public @*/ int ivaAq;

    private /*@ spec_public nullable @*/ String immagine;
    private /*@ spec_public nullable @*/ String taglia;

    /*@ public normal_behavior
      @ ensures IDAcquisto == 0 && IDOrdine == 0 && IDMaglietta == 0;
      @ ensures quantita == 0 && prezzoAq == 0.0f && ivaAq == 0;
      @ ensures immagine == null && taglia == null;
      @*/
    public AcquistoBean() {}

    /*@ public normal_behavior
      @ ensures \result == IDAcquisto;
      @ pure
      @*/
    public int getIDAcquisto() {
        return IDAcquisto;
    }

    /*@ public normal_behavior
      @ requires IDAcquisto >= 0;
      @ assignable this.IDAcquisto;
      @ ensures this.IDAcquisto == IDAcquisto;
      @*/
    public void setIDAcquisto(int IDAcquisto) {
        this.IDAcquisto = IDAcquisto;
    }

    /*@ public normal_behavior
      @ ensures \result == IDOrdine;
      @ pure
      @*/
    public int getIDOrdine() {
        return IDOrdine;
    }

    /*@ public normal_behavior
      @ requires IDOrdine >= 0;
      @ assignable this.IDOrdine;
      @ ensures this.IDOrdine == IDOrdine;
      @*/
    public void setIDOrdine(int IDOrdine) {
        this.IDOrdine = IDOrdine;
    }

    /*@ public normal_behavior
      @ ensures \result == IDMaglietta;
      @ pure
      @*/
    public int getIDMaglietta() {
        return IDMaglietta;
    }

    /*@ public normal_behavior
      @ requires IDMaglietta >= 0;
      @ assignable this.IDMaglietta;
      @ ensures this.IDMaglietta == IDMaglietta;
      @*/
    public void setIDMaglietta(int IDMaglietta) {
        this.IDMaglietta = IDMaglietta;
    }

    /*@ public normal_behavior
      @ ensures \result == quantita;
      @ pure
      @*/
    public int getQuantita() {
        return quantita;
    }

    /*@ public normal_behavior
      @ requires quantita >= 0;
      @ requires quantita == 0 || this.taglia != null;
      @ assignable this.quantita;
      @ ensures this.quantita == quantita;
      @*/
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    /*@ public normal_behavior
      @ ensures \result == immagine;
      @ pure
      @*/
    public /*@ nullable @*/ String getImmagine() {
        return immagine;
    }

    /*@ public normal_behavior
      @ requires immagine == null || (immagine.length() > 0 && immagine.length() <= 400);
      @ assignable this.immagine;
      @ ensures this.immagine == immagine;
      @*/
    public void setImmagine(/*@ nullable @*/ String immagine) {
        this.immagine = immagine;
    }

    /*@ public normal_behavior
      @ ensures \result == taglia;
      @ pure
      @*/
    public /*@ nullable @*/ String getTaglia() {
        return taglia;
    }

    /*@ public normal_behavior
      @ requires taglia == null ||
      @   (taglia.equals("XS") || taglia.equals("S") || taglia.equals("M")
      @    || taglia.equals("L") || taglia.equals("XL") || taglia.equals("XXL"));
      @ requires taglia != null || this.quantita == 0;
      @ assignable this.taglia;
      @ ensures this.taglia == taglia;
      @*/
    public void setTaglia(/*@ nullable @*/ String taglia) {
        this.taglia = taglia;
    }

    /*@ public normal_behavior
      @ ensures \result == prezzoAq;
      @ pure
      @*/
    public float getPrezzoAq() {
        return prezzoAq;
    }

    /*@ public normal_behavior
      @ requires prezzoAq >= 0.0f;
      @ assignable this.prezzoAq;
      @ ensures this.prezzoAq == prezzoAq;
      @*/
    public void setPrezzoAq(float prezzoAq) {
        this.prezzoAq = prezzoAq;
    }

    /*@ public normal_behavior
      @ ensures \result == ivaAq;
      @ pure
      @*/
    public int getIvaAq() {
        return ivaAq;
    }

    /*@ public normal_behavior
      @ requires 0 <= ivaAq && ivaAq <= 100;
      @ assignable this.ivaAq;
      @ ensures this.ivaAq == ivaAq;
      @*/
    public void setIvaAq(int ivaAq) {
        this.ivaAq = ivaAq;
    }

    // Skip ESC verification for toString(): string concatenations can generate heavy SMT queries.
    //@ skipesc
    @Override
    public String toString() {
        return "AcquistoBean{" +
                "IDOrdine=" + IDOrdine +
                ", IDMaglietta=" + IDMaglietta +
                ", quantita=" + quantita +
                ", immagine='" + immagine + '\'' +
                ", prezzoAq=" + prezzoAq +
                ", ivaAq=" + ivaAq +
                ", taglia='" + taglia + '\'' +
                '}';
    }
}

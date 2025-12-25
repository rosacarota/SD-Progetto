package model.acquisto;

public class AcquistoBean {

    // =========================================================
    // CLASS INVARIANTS
    // =========================================================
    //@ public invariant IDAcquisto  >= 0;
    //@ public invariant IDOrdine    >= 0;
    //@ public invariant IDMaglietta >= 0;
    //@ public invariant quantita    >= 0;
    //@ public invariant prezzoAq    >= 0.0f;
    //@ public invariant ivaAq       >= 0;

    // =========================================================
    // FIELDS
    // spec_public: allows referencing private fields in JML specs.
    // nullable: in JML, references are non-null by default, but Java reference fields
    // are initialized to null unless explicitly set.
    // =========================================================
    private /*@ spec_public @*/ int IDAcquisto, IDOrdine, IDMaglietta, quantita;
    private /*@ spec_public @*/ float prezzoAq;
    private /*@ spec_public @*/ int ivaAq;

    private /*@ spec_public @*/ /*@ nullable @*/ String immagine, taglia;

    // =========================================================
    // GETTERS (pure + assignable \nothing)
    // =========================================================

    /*@ public normal_behavior
      @ ensures \result == IDAcquisto;
      @ assignable \nothing;
      @ pure
      @*/
    public int getIDAcquisto() {
        return IDAcquisto;
    }

    /*@ public normal_behavior
      @ ensures \result == IDOrdine;
      @ assignable \nothing;
      @ pure
      @*/
    public int getIDOrdine() {
        return IDOrdine;
    }

    /*@ public normal_behavior
      @ ensures \result == IDMaglietta;
      @ assignable \nothing;
      @ pure
      @*/
    public int getIDMaglietta() {
        return IDMaglietta;
    }

    /*@ public normal_behavior
      @ ensures \result == quantita;
      @ assignable \nothing;
      @ pure
      @*/
    public int getQuantita() {
        return quantita;
    }

    /*@ public normal_behavior
      @ ensures \result == immagine;
      @ assignable \nothing;
      @ pure
      @*/
    public /*@ nullable @*/ String getImmagine() {
        return immagine;
    }

    /*@ public normal_behavior
      @ ensures \result == taglia;
      @ assignable \nothing;
      @ pure
      @*/
    public /*@ nullable @*/ String getTaglia() {
        return taglia;
    }

    /*@ public normal_behavior
      @ ensures \result == prezzoAq;
      @ assignable \nothing;
      @ pure
      @*/
    public float getPrezzoAq() {
        return prezzoAq;
    }

    /*@ public normal_behavior
      @ ensures \result == ivaAq;
      @ assignable \nothing;
      @ pure
      @*/
    public int getIvaAq() {
        return ivaAq;
    }

    // =========================================================
    // SETTERS (requires + assignable + ensures)
    // - requires: precondition on inputs
    // - assignable: which field(s) may be modified
    // - ensures: postcondition after the call
    // =========================================================

    /*@ public normal_behavior
      @ requires IDAcquisto >= 0;
      @ assignable this.IDAcquisto;
      @ ensures this.IDAcquisto == IDAcquisto;
      @*/
    public void setIDAcquisto(int IDAcquisto) {
        this.IDAcquisto = IDAcquisto;
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
      @ requires IDMaglietta >= 0;
      @ assignable this.IDMaglietta;
      @ ensures this.IDMaglietta == IDMaglietta;
      @*/
    public void setIDMaglietta(int IDMaglietta) {
        this.IDMaglietta = IDMaglietta;
    }

    /*@ public normal_behavior
      @ requires quantita >= 0;
      @ assignable this.quantita;
      @ ensures this.quantita == quantita;
      @*/
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    /*@ public normal_behavior
      @ requires immagine != null;
      @ assignable this.immagine;
      @ ensures this.immagine == immagine;
      @*/
    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    /*@ public normal_behavior
      @ requires taglia != null;
      @ assignable this.taglia;
      @ ensures this.taglia == taglia;
      @*/
    public void setTaglia(String taglia) {
        this.taglia = taglia;
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
      @ requires ivaAq >= 0;
      @ assignable this.ivaAq;
      @ ensures this.ivaAq == ivaAq;
      @*/
    public void setIvaAq(int ivaAq) {
        this.ivaAq = ivaAq;
    }

    // Skip ESC verification for toString(): string concatenations can generate heavy SMT queries
    // and slow down (or appear to hang) the verification process.
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
                '}';
    }
}

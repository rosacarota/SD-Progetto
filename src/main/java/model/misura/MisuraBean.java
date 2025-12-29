package model.misura;

public class MisuraBean {
    // NOTE: String fields are marked @nullable because this bean can be created/filled incrementally in Java
    // (defaults to null), while DB NOT NULL constraints are enforced at persistence/DAO level.

    /*@ public invariant IDMaglietta >= 0;
      @ public invariant quantita >= 0;
      @ public invariant quantita > 0 ==> taglia != null;
      @ public invariant
      @   taglia != null ==> (taglia.equals("XS") || taglia.equals("S") || taglia.equals("M")
      @     || taglia.equals("L") || taglia.equals("XL") || taglia.equals("XXL"));
    @*/
    private /*@ spec_public @*/ int IDMaglietta;
    private /*@ spec_public @*/ int quantita;
    private /*@ spec_public nullable @*/ String taglia;

    /*@ public normal_behavior
      @ ensures IDMaglietta == 0 && quantita == 0 && taglia == null;
      @*/
    public MisuraBean() {}

    /*@ public normal_behavior
      @ requires IDMaglietta >= 0;
      @ requires quantita >= 0;
      @ requires quantita > 0 ==> taglia != null;
      @ requires taglia != null ==> (taglia.equals("XS") || taglia.equals("S") || taglia.equals("M")
      @     || taglia.equals("L") || taglia.equals("XL") || taglia.equals("XXL"));
      @ ensures this.IDMaglietta == IDMaglietta;
      @ ensures this.quantita == quantita;
      @ ensures this.taglia == taglia;
      @*/
    public MisuraBean(int IDMaglietta, int quantita, /*@ nullable @*/ String taglia) {
        this.IDMaglietta = IDMaglietta;
        this.quantita = quantita;
        this.taglia = taglia;
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
      @ requires taglia == null ==> this.quantita == 0; // facoltativo ma coerente
      @ assignable this.taglia;
      @ ensures this.taglia == taglia;
      @*/
    public void setTaglia(/*@ nullable @*/ String taglia) {
        this.taglia = taglia;
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
      @ requires quantita > 0 ==> this.taglia != null;
      @ assignable this.quantita;
      @ ensures this.quantita == quantita;
      @*/
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    // Skip ESC verification for toString(): string concatenations can generate heavy SMT queries.
    //@ skipesc
    @Override
    public String toString() {
        return "MisuraBean{" +
                "IDMaglietta=" + IDMaglietta +
                ", quantita=" + quantita +
                ", taglia='" + taglia + '\'' +
                '}';
    }
}
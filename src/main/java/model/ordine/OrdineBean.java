package model.ordine;

import java.time.LocalDate;

public class OrdineBean {
    // NOTE: String fields are marked @nullable because this bean can be created/filled incrementally in Java
    // (defaults to null), while DB NOT NULL constraints are enforced at persistence/DAO level.

    /*@ public invariant ID >= 0;
      @ public invariant prezzoTotale >= 0.0f;

      @ public invariant dataConsegna != null ==> dataOrdine != null;

      @ public invariant username == null || (username.length() > 0 && username.length() <= 30);
      @ public invariant nomeConsegna == null || (nomeConsegna.length() > 0 && nomeConsegna.length() <= 30);
      @ public invariant cognomeConsegna == null || (cognomeConsegna.length() > 0 && cognomeConsegna.length() <= 30);
      @ public invariant citta == null || (citta.length() > 0 && citta.length() <= 30);
      @ public invariant via == null || (via.length() > 0 && via.length() <= 70);
      @ public invariant cap == null || cap.length() == 5;
      @*/
    private /*@ spec_public @*/ int ID;

    private /*@ spec_public nullable @*/ String username;
    private /*@ spec_public nullable @*/ String cap;
    private /*@ spec_public nullable @*/ String via;
    private /*@ spec_public nullable @*/ String citta;
    private /*@ spec_public nullable @*/ String nomeConsegna;
    private /*@ spec_public nullable @*/ String cognomeConsegna;

    private /*@ spec_public nullable @*/ LocalDate dataConsegna;
    private /*@ spec_public nullable @*/ LocalDate dataOrdine;

    private /*@ spec_public @*/ float prezzoTotale;

    /*@ public normal_behavior
      @ ensures \result == ID;
      @ pure
      @*/
    public int getID() {
        return ID;
    }

    /*@ public normal_behavior
      @ requires ID >= 0;
      @ assignable this.ID;
      @ ensures this.ID == ID;
      @*/
    public void setID(int ID) {
        this.ID = ID;
    }

    /*@ public normal_behavior
      @ ensures \result == username;
      @ pure
      @*/
    public /*@ nullable @*/ String getUsername() {
        return username;
    }

    /*@ public normal_behavior
      @ requires username == null || (username.length() > 0 && username.length() <= 30);
      @ assignable this.username;
      @ ensures this.username == username;
      @*/
    public void setUsername(/*@ nullable @*/ String username) {
        this.username = username;
    }

    /*@ public normal_behavior
      @ ensures \result == cap;
      @ pure
      @*/
    public /*@ nullable @*/ String getCap() {
        return cap;
    }

    /*@ public normal_behavior
      @ assignable this.cap;
      @ requires cap == null || cap.length() == 5;
      @ ensures this.cap == cap;
      @*/
    public void setCap(/*@ nullable @*/ String cap) {
        this.cap = cap;
    }

    /*@ public normal_behavior
      @ ensures \result == nomeConsegna;
      @ pure
      @*/
    public /*@ nullable @*/ String getNomeConsegna() {
        return nomeConsegna;
    }

    /*@ public normal_behavior
      @ assignable this.nomeConsegna;
      @ requires nomeConsegna == null || (nomeConsegna.length() > 0 && nomeConsegna.length() <= 30);
      @ ensures this.nomeConsegna == nomeConsegna;
      @*/
    public void setNomeConsegna(/*@ nullable @*/ String nomeConsegna) {
        this.nomeConsegna = nomeConsegna;
    }

    /*@ public normal_behavior
      @ ensures \result == cognomeConsegna;
      @ pure
      @*/
    public /*@ nullable @*/ String getCognomeConsegna() {
        return cognomeConsegna;
    }

    /*@ public normal_behavior
      @ assignable this.cognomeConsegna;
      @ requires cognomeConsegna == null || (cognomeConsegna.length() > 0 && cognomeConsegna.length() <= 30);
      @ ensures this.cognomeConsegna == cognomeConsegna;
      @*/
    public void setCognomeConsegna(/*@ nullable @*/ String cognomeConsegna) {
        this.cognomeConsegna = cognomeConsegna;
    }

    /*@ public normal_behavior
      @ ensures \result == via;
      @ pure
      @*/
    public /*@ nullable @*/ String getVia() {
        return via;
    }

    /*@ public normal_behavior
      @ assignable this.via;
      @ requires via == null || (via.length() > 0 && via.length() <= 70);
      @ ensures this.via == via;
      @*/
    public void setVia(/*@ nullable @*/ String via) {
        this.via = via;
    }

    /*@ public normal_behavior
      @ ensures \result == citta;
      @ pure
      @*/
    public /*@ nullable @*/ String getCitta() {
        return citta;
    }

    /*@ public normal_behavior
      @ assignable this.citta;
      @ requires citta == null || (citta.length() > 0 && citta.length() <= 30);
      @ ensures this.citta == citta;
      @*/
    public void setCitta(/*@ nullable @*/ String citta) {
        this.citta = citta;
    }

    /*@ public normal_behavior
      @ ensures \result == dataConsegna;
      @ pure
      @*/
    public /*@ nullable @*/ LocalDate getDataConsegna() {
        return dataConsegna;
    }

    /*@ public normal_behavior
      @requires dataConsegna == null || this.dataOrdine != null;
      @ assignable this.dataConsegna;
      @ ensures this.dataConsegna == dataConsegna;
      @*/
    public void setDataConsegna(/*@ nullable @*/ LocalDate dataConsegna) {
        this.dataConsegna = dataConsegna;
    }

    /*@ public normal_behavior
      @ ensures \result == dataOrdine;
      @ pure
      @*/
    public /*@ nullable @*/ LocalDate getDataOrdine() {
        return dataOrdine;
    }

    /*@ public normal_behavior
      @ requires dataOrdine == null ==> this.dataConsegna == null;
      @ assignable this.dataOrdine;
      @ ensures this.dataOrdine == dataOrdine;
      @*/
    public void setDataOrdine(/*@ nullable @*/ LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    /*@ public normal_behavior
      @ ensures \result == prezzoTotale;
      @ pure
      @*/
    public float getPrezzoTotale() {
        return prezzoTotale;
    }

    /*@ public normal_behavior
      @ requires prezzoTotale >= 0.0f;
      @ assignable this.prezzoTotale;
      @ ensures this.prezzoTotale == prezzoTotale;
      @*/
    public void setPrezzoTotale(float prezzoTotale) {
        this.prezzoTotale = prezzoTotale;
    }

    // Skip ESC verification for toString(): string concatenations can generate heavy SMT queries.
    //@ skipesc
    @Override
    public String toString() {
        return "OrdineBean{" +
                "username='" + username + '\'' +
                ", cap='" + cap + '\'' +
                ", via='" + via + '\'' +
                ", citta='" + citta + '\'' +
                ", dataConsegna=" + dataConsegna +
                ", dataOrdine=" + dataOrdine +
                ", prezzoTotale=" + prezzoTotale +
                '}';
    }
}

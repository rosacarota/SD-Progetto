package model.utente;

import java.io.Serializable;
import java.time.LocalDate;

public class UtenteBean implements Serializable {
    // NOTE: String fields are marked @nullable because this bean can be created/filled incrementally in Java
    // (defaults to null), while DB NOT NULL constraints are enforced at persistence/DAO level.
    private static final long serialVersionUID = 1L;

    /*@ public invariant username == null || (username.length() > 0 && username.length() <= 30);
      @ public invariant pwd == null || (pwd.length() > 0 && pwd.length() <= 100);
      @ public invariant nome == null || (nome.length() > 0 && nome.length() <= 30);
      @ public invariant cognome == null || (cognome.length() > 0 && cognome.length() <= 30);
      @ public invariant email == null || (email.length() > 0 && email.length() <= 40);
      @
      @ public invariant tipo == null || tipo.equals("admin") || tipo.equals("user");
      @
      @ public invariant cap == null || cap.length() == 5;
      @ public invariant via == null || (via.length() > 0 && via.length() <= 70);
      @ public invariant citta == null || (citta.length() > 0 && citta.length() <= 50);
      @*/

    private /*@ spec_public nullable @*/ String username;
    private /*@ spec_public nullable @*/ String pwd;
    private /*@ spec_public nullable @*/ String nome;
    private /*@ spec_public nullable @*/ String cognome;
    private /*@ spec_public nullable @*/ String email;

    private /*@ spec_public nullable @*/ String nomeCarta;
    private /*@ spec_public nullable @*/ String cognomeCarta;
    private /*@ spec_public nullable @*/ String numCarta;
    private /*@ spec_public nullable @*/ String CVV;

    private /*@ spec_public nullable @*/ String cap;
    private /*@ spec_public nullable @*/ String via;
    private /*@ spec_public nullable @*/ String citta;

    private /*@ spec_public nullable @*/ String tipo;

    private /*@ spec_public nullable @*/ LocalDate dataNascita;
    private /*@ spec_public nullable @*/ LocalDate dataScadenza;


    /*@ public normal_behavior
      @ ensures username == null && pwd == null && nome == null && cognome == null && email == null;
      @ ensures nomeCarta == null && cognomeCarta == null && numCarta == null && CVV == null;
      @ ensures cap == null && via == null && citta == null && tipo == null;
      @ ensures dataNascita == null && dataScadenza == null;
      @*/
    public UtenteBean() {}

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
      @ ensures \result == pwd;
      @ pure
      @*/
    public /*@ nullable @*/ String getPwd() {
        return pwd;
    }

    /*@ public normal_behavior
      @ requires pwd == null || (pwd.length() > 0 && pwd.length() <= 100);
      @ assignable this.pwd;
      @ ensures this.pwd == pwd;
      @*/
    public void setPwd(/*@ nullable @*/ String pwd) {
        this.pwd = pwd;
    }

    /*@ public normal_behavior
      @ ensures \result == nome;
      @ pure
      @*/
    public /*@ nullable @*/ String getNome() {
        return nome;
    }

    /*@ public normal_behavior
      @ requires nome == null || (nome.length() > 0 && nome.length() <= 30);
      @ assignable this.nome;
      @ ensures this.nome == nome;
      @*/
    public void setNome(/*@ nullable @*/ String nome) {
        this.nome = nome;
    }

    /*@ public normal_behavior
      @ ensures \result == cognome;
      @ pure
      @*/
    public /*@ nullable @*/ String getCognome() {
        return cognome;
    }

    /*@ public normal_behavior
      @ requires cognome == null || (cognome.length() > 0 && cognome.length() <= 30);
      @ assignable this.cognome;
      @ ensures this.cognome == cognome;
      @*/
    public void setCognome(/*@ nullable @*/ String cognome) {
        this.cognome = cognome;
    }

    /*@ public normal_behavior
      @ ensures \result == email;
      @ pure
      @*/
    public /*@ nullable @*/ String getEmail() {
        return email;
    }

    /*@ public normal_behavior
      @ requires email == null || (email.length() > 0 && email.length() <= 40);
      @ assignable this.email;
      @ ensures this.email == email;
      @*/
    public void setEmail(/*@ nullable @*/ String email) {
        this.email = email;
    }

    /*@ public normal_behavior
      @ ensures \result == nomeCarta;
      @ pure
      @*/
    public /*@ nullable @*/ String getNomeCarta() {
        return nomeCarta;
    }

    /*@ public normal_behavior
      @ assignable this.nomeCarta;
      @ ensures this.nomeCarta == nomeCarta;
      @*/
    public void setNomeCarta(/*@ nullable @*/ String nomeCarta) {
        this.nomeCarta = nomeCarta;
    }

    /*@ public normal_behavior
      @ ensures \result == cognomeCarta;
      @ pure
      @*/
    public /*@ nullable @*/ String getCognomeCarta() {
        return cognomeCarta;
    }

    /*@ public normal_behavior
      @ assignable this.cognomeCarta;
      @ ensures this.cognomeCarta == cognomeCarta;
      @*/
    public void setCognomeCarta(/*@ nullable @*/ String cognomeCarta) {
        this.cognomeCarta = cognomeCarta;
    }

    /*@ public normal_behavior
      @ ensures \result == numCarta;
      @ pure
      @*/
    public /*@ nullable @*/ String getNumCarta() {
        return numCarta;
    }

    /*@ public normal_behavior
      @ assignable this.numCarta;
      @ ensures this.numCarta == numCarta;
      @*/
    public void setNumCarta(/*@ nullable @*/ String numCarta) {
        this.numCarta = numCarta;
    }

    /*@ public normal_behavior
      @ ensures \result == CVV;
      @ pure
      @*/
    public /*@ nullable @*/ String getCVV() {
        return CVV;
    }

    /*@ public normal_behavior
      @ assignable this.CVV;
      @ ensures this.CVV == CVV;
      @*/
    public void setCVV(/*@ nullable @*/ String CVV) {
        this.CVV = CVV;
    }

    /*@ public normal_behavior
      @ ensures \result == cap;
      @ pure
      @*/
    public /*@ nullable @*/ String getCap() {
        return cap;
    }

    /*@ public normal_behavior
      @ requires cap == null || cap.length() == 5;
      @ assignable this.cap;
      @ ensures this.cap == cap;
      @*/
    public void setCap(/*@ nullable @*/ String cap) {
        this.cap = cap;
    }

    /*@ public normal_behavior
      @ ensures \result == via;
      @ pure
      @*/
    public /*@ nullable @*/ String getVia() {
        return via;
    }

    /*@ public normal_behavior
      @ requires via == null || (via.length() > 0 && via.length() <= 70);
      @ assignable this.via;
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
      @ requires citta == null || (citta.length() > 0 && citta.length() <= 50);
      @ assignable this.citta;
      @ ensures this.citta == citta;
      @*/
    public void setCitta(/*@ nullable @*/ String citta) {
        this.citta = citta;
    }

    /*@ public normal_behavior
      @ ensures \result == tipo;
      @ pure
      @*/
    public /*@ nullable @*/ String getTipo() {
        return tipo;
    }

    /*@ public normal_behavior
      @ requires tipo == null || tipo.equals("admin") || tipo.equals("user");
      @ assignable this.tipo;
      @ ensures this.tipo == tipo;
      @*/
    public void setTipo(/*@ nullable @*/ String tipo) {
        this.tipo = tipo;
    }

    /*@ public normal_behavior
      @ ensures \result == dataNascita;
      @ pure
      @*/
    public /*@ nullable @*/ LocalDate getDataNascita() {
        return dataNascita;
    }

    /*@ public normal_behavior
      @ assignable this.dataNascita;
      @ ensures this.dataNascita == dataNascita;
      @*/
    public void setDataNascita(/*@ nullable @*/ LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    /*@ public normal_behavior
      @ ensures \result == dataScadenza;
      @ pure
      @*/
    public /*@ nullable @*/ LocalDate getDataScadenza() {
        return dataScadenza;
    }

    /*@ public normal_behavior
      @ assignable this.dataScadenza;
      @ ensures this.dataScadenza == dataScadenza;
      @*/
    public void setDataScadenza(/*@ nullable @*/ LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    // Skip ESC verification for toString(): string concatenations can generate heavy SMT queries.
    //@ skipesc
    @Override
    public String toString() {
        return "UtenteBean{" +
                "username='" + username + '\'' +
                ", pwd='" + pwd + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", numCarta='" + numCarta + '\'' +
                ", cap='" + cap + '\'' +
                ", via='" + via + '\'' +
                ", citta='" + citta + '\'' +
                ", tipo='" + tipo + '\'' +
                ", dataNascita=" + dataNascita +
                ", dataScadenza=" + dataScadenza +
                '}';
    }
}

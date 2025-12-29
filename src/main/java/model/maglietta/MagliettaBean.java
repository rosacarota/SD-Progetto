package model.maglietta;

public class MagliettaBean {
    // NOTE: String fields are marked @nullable because this bean can be created/filled incrementally in Java
    // (defaults to null), while DB NOT NULL constraints are enforced at persistence/DAO level.

    /*@ public invariant ID >= 0;
      @ public invariant 0 <= IVA && IVA <= 100;
      @ public invariant prezzo >= 0.0f;

      @ public invariant nome == null || (nome.length() > 0 && nome.length() <= 50);
      @ public invariant colore == null || (colore.length() > 0 && colore.length() <= 30);
      @ public invariant tipo == null || (tipo.length() > 0 && tipo.length() <= 50);
      @ public invariant grafica == null || (grafica.length() > 0 && grafica.length() <= 400);
      @ public invariant descrizione == null || (descrizione.length() > 0 && descrizione.length() <= 150);
      @*/

    private /*@ spec_public @*/ int ID;
    private /*@ spec_public @*/ int IVA;

    private /*@ spec_public nullable @*/ String nome;
    private /*@ spec_public nullable @*/ String colore;
    private /*@ spec_public nullable @*/ String tipo;
    private /*@ spec_public nullable @*/ String grafica;
    private /*@ spec_public nullable @*/ String descrizione;

    private /*@ spec_public @*/ float prezzo;

    /*@ public normal_behavior
      @ ensures ID == 0 && IVA == 0 && prezzo == 0.0f;
      @ ensures nome == null && colore == null && tipo == null && grafica == null && descrizione == null;
      @*/
    public MagliettaBean() {}

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
      @ ensures \result == IVA;
      @ pure
      @*/
    public int getIVA() {
        return IVA;
    }

    /*@ public normal_behavior
      @ requires 0 <= IVA && IVA <= 100;
      @ assignable this.IVA;
      @ ensures this.IVA == IVA;
      @*/
    public void setIVA(int IVA) {
        this.IVA = IVA;
    }

    /*@ public normal_behavior
      @ ensures \result == nome;
      @ pure
      @*/
    public /*@ nullable @*/ String getNome() {
        return nome;
    }

    /*@ public normal_behavior
      @ requires nome == null || (nome.length() > 0 && nome.length() <= 50);
      @ assignable this.nome;
      @ ensures this.nome == nome;
      @*/
    public void setNome(/*@ nullable @*/ String nome) {
        this.nome = nome;
    }

    /*@ public normal_behavior
      @ ensures \result == colore;
      @ pure
      @*/
    public /*@ nullable @*/ String getColore() {
        return colore;
    }

    /*@ public normal_behavior
      @ requires colore == null || (colore.length() > 0 && colore.length() <= 30);
      @ assignable this.colore;
      @ ensures this.colore == colore;
      @*/
    public void setColore(/*@ nullable @*/ String colore) {
        this.colore = colore;
    }

    /*@ public normal_behavior
      @ ensures \result == tipo;
      @ pure
      @*/
    public /*@ nullable @*/ String getTipo() {
        return tipo;
    }

    /*@ public normal_behavior
      @ requires tipo == null || (tipo.length() > 0 && tipo.length() <= 50);
      @ assignable this.tipo;
      @ ensures this.tipo == tipo;
      @*/
    public void setTipo(/*@ nullable @*/ String tipo) {
        this.tipo = tipo;
    }

    /*@ public normal_behavior
      @ ensures \result == grafica;
      @ pure
      @*/
    public /*@ nullable @*/ String getGrafica() {
        return grafica;
    }

    /*@ public normal_behavior
      @ requires grafica == null || (grafica.length() > 0 && grafica.length() <= 400);
      @ assignable this.grafica;
      @ ensures this.grafica == grafica;
      @*/
    public void setGrafica(/*@ nullable @*/ String grafica) {
        this.grafica = grafica;
    }

    /*@ public normal_behavior
      @ ensures \result == descrizione;
      @ pure
      @*/
    public /*@ nullable @*/ String getDescrizione() {
        return descrizione;
    }

    /*@ public normal_behavior
      @ requires descrizione == null || (descrizione.length() > 0 && descrizione.length() <= 150);
      @ assignable this.descrizione;
      @ ensures this.descrizione == descrizione;
      @*/
    public void setDescrizione(/*@ nullable @*/ String descrizione) {
        this.descrizione = descrizione;
    }

    /*@ public normal_behavior
      @ ensures \result == prezzo;
      @ pure
      @*/
    public float getPrezzo() {
        return prezzo;
    }

    /*@ public normal_behavior
      @ requires prezzo >= 0.0f;
      @ assignable this.prezzo;
      @ ensures this.prezzo == prezzo;
      @*/
    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    // Skip ESC verification for toString(): string concatenations can generate heavy SMT queries.
    //@ skipesc
    @Override
    public String toString() {
        return "MagliettaBean{" +
                "ID=" + ID +
                ", IVA=" + IVA +
                ", nome='" + nome + '\'' +
                ", colore='" + colore + '\'' +
                ", tipo='" + tipo + '\'' +
                ", grafica='" + grafica + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", prezzo=" + prezzo +
                '}';
    }
}

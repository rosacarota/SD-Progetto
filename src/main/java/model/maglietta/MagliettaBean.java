package model.maglietta;

public class MagliettaBean {
    // NOTE: String fields are marked @nullable because this bean can be created/filled incrementally in Java
    // (defaults to null), while DB NOT NULL constraints are enforced at persistence/DAO level.
    private /*@ spec_public @*/ int ID;
    private /*@ spec_public @*/ int IVA;

    private /*@ spec_public nullable @*/ String nome;
    private /*@ spec_public nullable @*/ String colore;
    private /*@ spec_public nullable @*/ String tipo;
    private /*@ spec_public nullable @*/ String grafica;
    private /*@ spec_public nullable @*/ String descrizione;

    private /*@ spec_public @*/ float prezzo;

    /*@ public invariant ID >= 0;
      @ public invariant 0 <= IVA && IVA <= 100;
      @ public invariant prezzo >= 0.0f;
      @*/

    /*@ public normal_behavior
      @ ensures \result == ID;
      @ assignable \nothing;
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
      @ assignable \nothing;
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
      @ assignable \nothing;
      @ pure
      @*/
    public /*@ nullable @*/ String getNome() {
        return nome;
    }

    /*@ public normal_behavior
      @ assignable this.nome;
      @ ensures this.nome == nome;
      @*/
    public void setNome(/*@ nullable @*/ String nome) {
        this.nome = nome;
    }

    /*@ public normal_behavior
      @ ensures \result == colore;
      @ assignable \nothing;
      @ pure
      @*/
    public /*@ nullable @*/ String getColore() {
        return colore;
    }

    /*@ public normal_behavior
      @ assignable this.colore;
      @ ensures this.colore == colore;
      @*/
    public void setColore(/*@ nullable @*/ String colore) {
        this.colore = colore;
    }

    /*@ public normal_behavior
      @ ensures \result == tipo;
      @ assignable \nothing;
      @ pure
      @*/
    public /*@ nullable @*/ String getTipo() {
        return tipo;
    }

    /*@ public normal_behavior
      @ assignable this.tipo;
      @ ensures this.tipo == tipo;
      @*/
    public void setTipo(/*@ nullable @*/ String tipo) {
        this.tipo = tipo;
    }

    /*@ public normal_behavior
      @ ensures \result == grafica;
      @ assignable \nothing;
      @ pure
      @*/
    public /*@ nullable @*/ String getGrafica() {
        return grafica;
    }

    /*@ public normal_behavior
      @ assignable this.grafica;
      @ ensures this.grafica == grafica;
      @*/
    public void setGrafica(/*@ nullable @*/ String grafica) {
        this.grafica = grafica;
    }

    /*@ public normal_behavior
      @ ensures \result == prezzo;
      @ assignable \nothing;
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

    /*@ public normal_behavior
      @ ensures \result == descrizione;
      @ assignable \nothing;
      @ pure
      @*/
    public /*@ nullable @*/ String getDescrizione() {
        return descrizione;
    }

    /*@ public normal_behavior
      @ assignable this.descrizione;
      @ ensures this.descrizione == descrizione;
      @*/
    public void setDescrizione(/*@ nullable @*/ String descrizione) {
        this.descrizione = descrizione;
    }

    // Skip ESC verification for toString(): string concatenations can generate heavy SMT queries
    // and slow down (or appear to hang) the verification process.
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

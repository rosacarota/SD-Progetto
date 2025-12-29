package model.maglietta;

public class MagliettaOrdine {
    /*@ public invariant magliettaBean != null;
      @ public invariant quantita >= 1;
      @ public invariant
      @ taglia != null ==> (taglia.equals("XS") || taglia.equals("S") || taglia.equals("M")
      @     || taglia.equals("L") || taglia.equals("XL") || taglia.equals("XXL"));
      @*/
    private /*@ spec_public @*/ MagliettaBean magliettaBean;
    private /*@ spec_public @*/ int quantita;
    private /*@ spec_public @*/ String taglia;

    /*@ public normal_behavior
      @ requires magliettaBean != null;
      @ requires taglia != null;
      @ requires taglia.equals("XS") || taglia.equals("S") || taglia.equals("M")
      @     || taglia.equals("L") || taglia.equals("XL") || taglia.equals("XXL");
      @ ensures this.magliettaBean == magliettaBean;
      @ ensures this.taglia == taglia;
      @ ensures this.quantita == 1;
      @*/
    public MagliettaOrdine(MagliettaBean magliettaBean, String taglia) {
        this.magliettaBean = magliettaBean;
        this.taglia = taglia;
        quantita = 1;
    }

    /*@ public normal_behavior
      @ ensures \result == magliettaBean;
      @ pure
      @*/
    public /*@ non_null @*/ MagliettaBean getMagliettaBean() {
        return magliettaBean;
    }

    /*@ public normal_behavior
      @ requires magliettaBean != null;
      @ assignable this.magliettaBean;
      @ ensures this.magliettaBean == magliettaBean;
      @*/
    public void setMagliettaBean(/*@ non_null @*/ MagliettaBean magliettaBean) {
        this.magliettaBean = magliettaBean;
    }

    /*@ public normal_behavior
      @ ensures \result == taglia;
      @ pure
      @*/
    public /*@ non_null @*/ String getTaglia() {
        return taglia;
    }

    /*@ public normal_behavior
      @ requires taglia != null;
      @ requires taglia.equals("XS") || taglia.equals("S") || taglia.equals("M")
      @     || taglia.equals("L") || taglia.equals("XL") || taglia.equals("XXL");
      @ assignable this.taglia;
      @ ensures this.taglia == taglia;
      @*/
    public void setTaglia(/*@ non_null @*/ String taglia) {
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
      @ requires quantita >= 1;
      @ assignable this.quantita;
      @ ensures this.quantita == quantita;
      @*/
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    /*@ public normal_behavior
      @ requires this.quantita < Integer.MAX_VALUE;
      @ assignable this.quantita;
      @ ensures this.quantita == \old(this.quantita) + 1;
      @*/
    public void incrementaQuantita() {
        quantita++;
    }

    /*@ public normal_behavior
      @ requires this.quantita > 1;
      @ assignable this.quantita;
      @ ensures this.quantita == \old(this.quantita) - 1;
      @*/
    public void decremenetaQuantita() {
        quantita--;
    }

    /*@ public normal_behavior
      @ requires magliettaBean.getPrezzo() >= 0.0f;
      @ ensures \result == this.quantita * this.magliettaBean.getPrezzo();
      @ ensures \result >= 0.0f;
      @ pure
      @*/
    public float getPrezzoTotale() {
        return quantita * magliettaBean.getPrezzo();
    }
}

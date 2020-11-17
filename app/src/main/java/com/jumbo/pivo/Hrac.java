package com.jumbo.pivo;


import java.util.List;

public class Hrac extends Polozka {

    private String jmeno;
    private int vek;
    private boolean fanousek;
    private String datum;
    private int dniDoNarozenin;
    //1 = detailní zobrazení pro seznam hráčů s datem narození a věkem, 0 pro spojení Hráč + jméno, 2 pro Hráč + jméno + počet piv
    private boolean oznacenyHrac;
    private Pivo pocetPiv;
    private List<Zapas> seznamZapasu;

    /**konstruktor
     * @param jmeno jméno hráče
     * @param datum datum narození
     * @param fanousek boolean který v případě true potvrzuje, že je to fanoušek
     */
    public Hrac(String jmeno, String datum, boolean fanousek) {
        this.jmeno = jmeno;
        this.datum = datum;
        this.fanousek = fanousek;
        this.vek = Datum.urciVek(datum);
        this.dniDoNarozenin = Datum.setDniDoNarozenin(datum);
        this.pocetPiv = new Pivo();
    }

    public Hrac(String jmeno, String datum, boolean fanousek, ZobrazeniPolozky zobrazeniPolozky) {
        this.jmeno = jmeno;
        this.datum = datum;
        this.fanousek = fanousek;
        this.vek = Datum.urciVek(datum);
        this.dniDoNarozenin = Datum.setDniDoNarozenin(datum);
        this.zobrazeniPolozky = zobrazeniPolozky;
    }

    public Hrac(String jmeno, String datum, boolean fanousek, Pivo pocetPiv, ZobrazeniPolozky zobrazeniPolozky) {
        this.jmeno = jmeno;
        this.datum = datum;
        this.fanousek = fanousek;
        this.vek = Datum.urciVek(datum);
        this.dniDoNarozenin = Datum.setDniDoNarozenin(datum);
        this.pocetPiv = pocetPiv;
        this.zobrazeniPolozky = zobrazeniPolozky;
    }


    /**
     * bezparametrický konstruktor
     */
    public Hrac() {
    }

    //toString je potřeba pro print content classy

    @Override
    public String toString() {
        if (zobrazeniPolozky == ZobrazeniPolozky.Zakladni) {
            if (fanousek) {
                return "Fanoušek " + jmeno;
            } else {
                return "Hráč " + jmeno;
            }

        }
        else if (zobrazeniPolozky == ZobrazeniPolozky.Detailni) {
            if (fanousek) {
                return "Fanoušek " + jmeno + ", datum narození: " + Datum.zmenDatumDoFront(datum) + ", věk: " + vek;
            } else {
                return "Hráč " + jmeno + ", datum narození: " + Datum.zmenDatumDoFront(datum) + ", věk: " + vek;
            }
        }
        else if (zobrazeniPolozky == ZobrazeniPolozky.Pivni) {
            if (fanousek) {
                return "Fanoušek " + jmeno + ", " + pocetPiv;
            } else {
                return "Hráč " + jmeno + ", " + pocetPiv;
            }
        }
        else {
            if (fanousek) {
                return "Fanoušek " + jmeno;
            } else {
                return "Hráč " + jmeno;
            }
        }
    }

    //gettery a settery


    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public int getVek() {
        return vek;
    }

    public void setVek(int vek) {
        this.vek = vek;
    }

    public boolean isFanousek() {
        return fanousek;
    }

    public void setFanousek(boolean fanousek) {
        this.fanousek = fanousek;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }


    public int getDniDoNarozenin() {

        return dniDoNarozenin;
    }

    public void setDniDoNarozenin(int dniDoNarozenin) {
        this.dniDoNarozenin = dniDoNarozenin;
    }

    public boolean isOznacenyHrac() {
        return oznacenyHrac;
    }

    public void setOznacenyHrac(boolean oznacenyHrac) {
        this.oznacenyHrac = oznacenyHrac;
    }

    public Pivo getPocetPiv() {
        return pocetPiv;
    }

    public void setPocetPiv(Pivo pocetPiv) {
        this.pocetPiv = pocetPiv;
    }

    public ZobrazeniPolozky getZobrazeniPolozky() {
        return zobrazeniPolozky;
    }

    public void setZobrazeniPolozky(ZobrazeniPolozky zobrazeniPolozky) {
        this.zobrazeniPolozky = zobrazeniPolozky;
    }

    public List<Zapas> getSeznamZapasu() {
        return seznamZapasu;
    }

    public void setSeznamZapasu(List<Zapas> seznamZapasu) {
        this.seznamZapasu = seznamZapasu;
    }

    private void pridejPiva(Pivo pocetPiv) {
        this.pocetPiv.pridejVelkaPiva(pocetPiv.getPocetVelkych());
        this.pocetPiv.pridejMalaPiva(pocetPiv.getPocetMalych());

    }

    public void aktualizujZeZapasuPocetPiv(List<Zapas> seznamZapasu) {
        int seznamZapasuSize = seznamZapasu.size();
        getPocetPiv().vynulujPocetPiv();
        for (int i = 0; i < seznamZapasuSize; i++) {
            if (seznamZapasu.get(i).getSeznamHracu().contains(this)) {
                pridejPiva(seznamZapasu.get(i).getSeznamHracu().get(seznamZapasu.get(i).getSeznamHracu().indexOf(this)).getPocetPiv());
            }
        }
    }


}

package com.jumbo.pivo;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Zapas extends Polozka {

    private String souper;
    private String datum;
    private boolean domaciZapas;
    private int pocetPiv;
    private int pocetHracu;
    private List<Hrac> seznamHracu;
    private Sezona sezona;
    private int pocetVelkychPiv;
    private int pocetMalychPiv;
    private Hrac oznacenyHrac;

    //sezona 4 = ostatní, 1 = jaro 2020, 2 = podzim 2020, 3 = jaro 2021


    private static final String TAG = Zapas.class.toString();

    /**
     * Konstruktor s parametry
     * @param souper soupeř Trusu
     * @param datum datum zápasu
     * @param domaciZapas true pokud hraje Trus doma
     */
    public Zapas (String souper, String datum, boolean domaciZapas, List<Hrac> seznamHracu) {

        this.souper = souper;
        this.datum = datum;
        this.domaciZapas = domaciZapas;
        zaradSezonu(datum);
        this.seznamHracu = seznamHracu;
    }

    //konstruktor i s pivama
    public Zapas (String souper, String datum, boolean domaciZapas, int pocetVelkychPiv, int pocetMalychPiv) {

        this.souper = souper;
        this.datum = datum;
        this.domaciZapas = domaciZapas;
        zaradSezonu(datum);
        this.pocetVelkychPiv = pocetVelkychPiv;
        this.pocetMalychPiv = pocetMalychPiv;
        seznamHracu = new ArrayList<>();
    }

    //bezparametrický konstruktor
    public Zapas() {

    }

    /**metoda vrátí do jaké části sezony dle data zápas patří
     * @param datum datum v SQL formatu
     * @return
     */
   public void zaradSezonu (String datum) {
       Log.d(TAG, "zařazuji sezonu podle data " + datum);
        int rok = Datum.zjistiRok(datum);
        int mesic = Datum.zjistiMesic(datum) + 1;

        if (rok == 2020 && mesic > 7) {
            sezona = Sezona.Podzim2020;
        } else if (rok == 2021 && mesic <= 7) {
            sezona = Sezona.Jaro2021;
        } else if (rok == 2021 && mesic > 7) {
            sezona = Sezona.Podzim2021;
        } else if (rok == 2022 && mesic <= 7) {
            sezona = Sezona.Jaro2022;
        } else if (rok == 2022 && mesic > 7) {
            sezona = Sezona.Podzim2022;
        } else {
            sezona = Sezona.Ostatni;
        }
    }


    @Override
    public String toString() {

        if (zobrazeniPolozky == ZobrazeniPolozky.Pivni) {
            if (domaciZapas) {
                return "Liščí Trus - " + souper + ", hráno: " + Datum.zmenDatumDoFront(datum) + " počet velkých piv: " + getCelkovyPocetVelkychPiv() + ", malých piv:  " + getCelkovyPocetMalychPiv();
            } else {
                return souper + " - Liščí Trus, hráno: " + Datum.zmenDatumDoFront(datum) + " počet velkých piv: " + getCelkovyPocetVelkychPiv() + ", malých piv:  " + getCelkovyPocetMalychPiv();
            }

        }

        else if (zobrazeniPolozky == ZobrazeniPolozky.PivniProJednohoHrace) {
            if (domaciZapas) {
                return "Liščí Trus - " + souper + ", hráno: " + Datum.zmenDatumDoFront(datum) + " počet velkých piv: " + oznacenyHrac.getPocetPiv().getPocetVelkych() + ", malých piv:  " + oznacenyHrac.getPocetPiv().getPocetMalych();
            } else {
                return souper + " - Liščí Trus, hráno: " + Datum.zmenDatumDoFront(datum) + " počet velkých piv: " + oznacenyHrac.getPocetPiv().getPocetVelkych() + ", malých piv:  " + oznacenyHrac.getPocetPiv().getPocetMalych();
            }

        }
        else {

            if (domaciZapas) {
                return "Liščí Trus - " + souper + ", hráno: " + Datum.zmenDatumDoFront(datum);
            } else {
                return souper + " - Liščí Trus, hráno: " + Datum.zmenDatumDoFront(datum);
            }
        }

    }

    public String getSouper() {
        return souper;
    }

    public void setSouper(String souper) {
        this.souper = souper;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public boolean isDomaciZapas() {
        return domaciZapas;
    }

    public void setDomaciZapas(boolean domaciZapas) {
        this.domaciZapas = domaciZapas;
    }

    public int getPocetPiv() {
        return pocetPiv;
    }

    public void setPocetPiv(int pocetPiv) {
        this.pocetPiv = pocetPiv;
    }

    public int getPocetHracu() {
        return pocetHracu;
    }

    public void setPocetHracu(int pocetHracu) {
        this.pocetHracu = pocetHracu;
    }

    public Sezona getSezona() {
        return sezona;
    }

    public void setSezona(Sezona sezona) {
        this.sezona = sezona;
    }

    public int getPocetVelkychPiv() {
        return pocetVelkychPiv;
    }

    public void setPocetVelkychPiv(int pocetVelkychPiv) {
        this.pocetVelkychPiv = pocetVelkychPiv;
    }

    public int getPocetMalychPiv() {
        return pocetMalychPiv;
    }

    public void setPocetMalychPiv(int pocetMalychPiv) {
        this.pocetMalychPiv = pocetMalychPiv;
    }

    public List<Hrac> getSeznamHracu() {
        return seznamHracu;
    }

    public void setSeznamHracu(List<Hrac> seznamHracu) {
        this.seznamHracu = seznamHracu;
    }

    public int getCelkovyPocetVelkychPiv () {
       int pocetPiv = 0;
       for (int i = 0; i < seznamHracu.size(); i++) {
           try {
               pocetPiv += seznamHracu.get(i).getPocetPiv().getPocetVelkych();
           }
           catch (Exception e) {
               Log.e(TAG, "pri vyberu celkovych poctu chub nastala chyba:" + e.toString());
           }
       }
        Log.d(TAG, "Z celkového počtu " + seznamHracu.size() + " hráčů vracím " + pocetPiv + " velkých počet piv");
       return pocetPiv;
    }

    public int getCelkovyPocetMalychPiv () {
        int pocetPiv = 0;
        for (int i = 0; i < seznamHracu.size(); i++) {
            try {
                pocetPiv += seznamHracu.get(i).getPocetPiv().getPocetMalych();
            }
            catch (Exception e) {
                Log.e(TAG, "pri vyberu celkovych poctu chub nastala chyba:" + e.toString());
            }
        }
        Log.d(TAG, "Z celkového počtu " + seznamHracu.size() + " hráčů vracím " + pocetPiv + " malých počet piv");
        return pocetPiv;
    }

    public int getHracuvPocetMalychPiv (boolean fanousek) {
        int pocetPiv = 0;
        for (int i = 0; i < seznamHracu.size(); i++) {
            try {
                if (seznamHracu.get(i).isFanousek() == fanousek) {
                    pocetPiv += seznamHracu.get(i).getPocetPiv().getPocetMalych();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "pri vyberu celkovych poctu chub nastala chyba:" + e.toString());
            }
        }
        Log.d(TAG, "Z celkového počtu " + seznamHracu.size() + " hráčů vracím " + pocetPiv + " malých počet piv Účastníkem je fanoušek" + fanousek);
        return pocetPiv;
    }

    public int getHracuvPocetVelkychPiv (boolean fanousek) {
        int pocetPiv = 0;
        for (int i = 0; i < seznamHracu.size(); i++) {
            try {
                if (seznamHracu.get(i).isFanousek() == fanousek) {
                    pocetPiv += seznamHracu.get(i).getPocetPiv().getPocetVelkych();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "pri vyberu celkovych poctu chub nastala chyba:" + e.toString());
            }
        }
        Log.d(TAG, "Z celkového počtu " + seznamHracu.size() + " hráčů vracím " + pocetPiv + " malých počet piv. Účastníkem je fanoušek " + fanousek);
        return pocetPiv;
    }

    /**
     * @param hrac hrac musí mít v proměnných nějaká piva aby se daly zobrazit. To znamená parametr by měl být např. ze seznamu hráčů zápasu
     */
    public void nastavPivniZobrazeniProJednohoHrace(Hrac hrac) {
       setZobrazeniPolozky(ZobrazeniPolozky.PivniProJednohoHrace);
       this.oznacenyHrac = hrac;
    }


    public void pridejHrace(Hrac hrac) {
       seznamHracu.add(hrac);
    }
    public void odeberHrace(Hrac hrac) {
       seznamHracu.remove(hrac);
    }
}

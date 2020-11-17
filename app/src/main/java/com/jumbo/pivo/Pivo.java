package com.jumbo.pivo;

public class Pivo extends Polozka {

    private int pocetMalych;
    private int pocetVelkych;

    public Pivo() {
    }

    public Pivo(int pocetVelkych, int pocetMalych) {

        this.pocetMalych = pocetMalych;
        this.pocetVelkych = pocetVelkych;
    }

    @Override
    public String toString() {
        return pocetVelkych + " velkejch a " + pocetMalych + " malejch piv";

    }

    public int getPocetMalych() {
        return pocetMalych;
    }

    public void setPocetMalych(int pocetMalychPiv) {
        this.pocetMalych = pocetMalychPiv;
    }

    public int getPocetVelkych() {
        return pocetVelkych;
    }

    public void setPocetVelkych(int pocetVelkych) {
        this.pocetVelkych = pocetVelkych;
    }

    public void odeberMalyPivo() {
        this.pocetMalych--;
    }
    public void pridejMalyPivo() {
        this.pocetMalych ++;
    }
    public void odeberVelkyPivo() {
        this.pocetVelkych--;
    }
    public void pridejVelkyPivo() {
        this.pocetVelkych++;
    }
    public void pridejVelkaPiva(int pocetVelkych) {
        this.pocetVelkych+=pocetVelkych;
    }
    public void pridejMalaPiva(int pocetMalych) {
        this.pocetMalych+=pocetMalych;
    }
    public void vynulujPocetPiv() {
        pocetVelkych = 0;
        pocetMalych = 0;
    }

    //přepsání metody equals, tady budem porovnávat podle počtu piv
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Pivo pivo = (Pivo) o;
        return pocetMalych == pivo.pocetMalych &&
                pocetVelkych == pivo.pocetVelkych;
    }
}

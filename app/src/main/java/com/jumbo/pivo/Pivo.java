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
        return pocetMalych + " malejch a " + pocetVelkych + " velkejch piv";

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


}

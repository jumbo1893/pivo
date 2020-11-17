package com.jumbo.pivo;


public enum ZobrazeniPolozky {
    //detailní zobrazení položky
    Detailni("Detailní"),
    //zobrazení položky se základními údaji
    Zakladni("Základní"),
    //Zobrazení položky s celkovým počtem vypitých piv
    Pivni("Pivní"),
    //zobrazení položky s počtem vypitých piv pouze pro jeden element (pro jednoho hráče v zápase)
    PivniProJednohoHrace("Pivní pro hráče");

    private String text;

    ZobrazeniPolozky(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

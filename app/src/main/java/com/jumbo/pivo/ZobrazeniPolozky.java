package com.jumbo.pivo;


public enum ZobrazeniPolozky {
    Detailni("Detailní"),
    Zakladni("Základní"),
    Pivni("Pivní"),
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

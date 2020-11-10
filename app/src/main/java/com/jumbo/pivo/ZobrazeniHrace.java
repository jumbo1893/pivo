package com.jumbo.pivo;


public enum ZobrazeniHrace {
    Detailni("Detailní"),
    Zakladni("Základní"),
    Pivni("Pivní");

    private String text;

    ZobrazeniHrace(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

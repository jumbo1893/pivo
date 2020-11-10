package com.jumbo.pivo;

import android.util.Log;

public enum HracEnum {

    Vse("Zobrazit vše", "hráči a fanoušci"),
    Hrac("Hráč", "hráči"),
    Fanousek("Fanoušek", "fanoušci");


    private String text;
    private String mnozneCisloTextu;

    private static final String TAG = HracEnum.class.toString();


    HracEnum(String text, String mnozneCisloTextu) {
        this.text = text;
        this.mnozneCisloTextu = mnozneCisloTextu;


    }

    public static HracEnum zaradSezonuDleComba(int combobox) {
        switch (combobox) {
            case 0:
                Log.d(TAG, "zařazuji sezonu podle comboboxu s výsledkem " + Vse);
                return Vse;
            case 1:
                Log.d(TAG, "zařazuji sezonu podle comboboxu s výsledkem " + Hrac);
                return Hrac;
            case 2:
                Log.d(TAG, "zařazuji sezonu podle comboboxu s výsledkem " + Fanousek);
                return Fanousek;
        }
        Log.d(TAG, "Nebyla nalezena sezona dle comboboxu, vracím " + Vse);
        return Vse;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getMnozneCisloTextu() {
        return mnozneCisloTextu;
    }
}

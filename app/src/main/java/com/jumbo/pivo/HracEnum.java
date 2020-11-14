package com.jumbo.pivo;

import android.util.Log;

public enum HracEnum {

    Vse("Zobrazit vše", "hráči a fanoušci", false),
    Hrac("Hráč", "hráči", false),
    Fanousek("Fanoušek", "fanoušci", true);


    private String text;
    private String mnozneCisloTextu;
    private boolean fanousek;

    private static final String TAG = HracEnum.class.toString();


    HracEnum(String text, String mnozneCisloTextu, boolean fanousek) {
        this.text = text;
        this.mnozneCisloTextu = mnozneCisloTextu;
        this.fanousek = fanousek;

    }

    public static HracEnum zaradHraceDleComba(int combobox) {
        switch (combobox) {
            case 0:
                return Vse;
            case 1:
                return Hrac;
            case 2:
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

    public boolean isFanousek() {
        return fanousek;
    }
}

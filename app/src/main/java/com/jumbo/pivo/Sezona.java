package com.jumbo.pivo;

import android.util.Log;

public enum Sezona {
    Vse("Zobrazit vše"),
    Ostatni("Ostatní"),
    Podzim2020("Podzim 2020"),
    Jaro2021("Jaro 2021"),
    Podzim2021("Podzim 2021"),
    Jaro2022("Jaro 2022"),
    Podzim2022("Podzim 2022");


    private String text;
    private static final String TAG = Sezona.class.toString();


    Sezona(String text) {
        this.text = text;
    }

    public static Sezona zaradSezonuDleComba(int combobox) {
        switch (combobox) {
            case 0: //nic, vracím default ostatní
                break;
            case 1:
                Log.d(TAG, "zařazuji sezonu podle comboboxu s výsledkem " + Podzim2020);
                return Podzim2020;
            case 2:
                Log.d(TAG, "zařazuji sezonu podle comboboxu s výsledkem " + Jaro2021);
                return Jaro2021;
            case 3:
                Log.d(TAG, "zařazuji sezonu podle comboboxu s výsledkem " + Podzim2021);
                return Podzim2021;
            case 4:
                Log.d(TAG, "zařazuji sezonu podle comboboxu s výsledkem " + Jaro2022);
                return Jaro2022;
            case 5:
                Log.d(TAG, "zařazuji sezonu podle comboboxu s výsledkem " + Podzim2022);
                return Podzim2022;
            case 6:
                Log.d(TAG, "zařazuji sezonu podle comboboxu s výsledkem " + Ostatni);
                return Ostatni;
        }
        Log.d(TAG, "Nebyla nalezena sezona dle comboboxu, vracím " + Ostatni);
        return Ostatni;
    }


    @Override
    public String toString() {
        return text;
    }

}

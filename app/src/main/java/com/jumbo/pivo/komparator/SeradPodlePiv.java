package com.jumbo.pivo.komparator;

import android.util.Log;

import com.jumbo.pivo.Hrac;
import com.jumbo.pivo.Polozka;
import com.jumbo.pivo.Zapas;

import java.util.Comparator;

public class SeradPodlePiv implements Comparator<Polozka> {

    private static final String TAG = "SeradPodlePiv";

    public int compare (Polozka a, Polozka b) {
        Log.d(TAG, "Položky seřazený podle počtu vypitejch piv");
        if (a instanceof Hrac && b instanceof Hrac) {
            return (((Hrac) b).getPocetPiv().getPocetVelkych()*5 + ((Hrac) b).getPocetPiv().getPocetMalych()*3)
                    - (((Hrac) a).getPocetPiv().getPocetVelkych()*5 + ((Hrac) a).getPocetPiv().getPocetMalych()*3);
        }
        else if (a instanceof Zapas && b instanceof Zapas) {
            return (((Zapas) b).getCelkovyPocetVelkychPiv()*5 + ((Zapas) b).getCelkovyPocetMalychPiv()*3)
                    - (((Zapas) a).getCelkovyPocetVelkychPiv()*5 + ((Zapas) a).getCelkovyPocetMalychPiv()*3);
        }
        else {
            return 0;
        }
    }
}

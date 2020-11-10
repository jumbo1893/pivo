package com.jumbo.pivo.komparator;

import android.util.Log;

import com.jumbo.pivo.Hrac;

import java.util.Comparator;

public class SeradHracePrvni implements Comparator<Hrac> {
    private static final String TAG = "SeradHracePrvni";

    public int compare (Hrac a, Hrac b) {
        Log.d(TAG, "Hráči seřazeni podle hráči první, fans druzí");
        return Boolean.compare(a.isFanousek(), b.isFanousek());
    }
}

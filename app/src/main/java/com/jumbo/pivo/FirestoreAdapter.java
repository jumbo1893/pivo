package com.jumbo.pivo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreAdapter {

    private FirebaseFirestore db;

    private List<Hrac> seznamHracu;
    private List<Zapas> seznamZapasu;

    private static final String TAG = FirestoreAdapter.class.toString();

    public FirestoreAdapter() {
        db = FirebaseFirestore.getInstance();

    }

    public void pridatDoDatabaze(Polozka polozka) {
        String keyword = "";
        if (polozka instanceof Hrac) {
            keyword = "hrac";
        }
        else if (polozka instanceof Zapas) {
            keyword = "zapas";
        }
        else if (polozka instanceof Pivo) {
            keyword = "pivo";
        }
        else {
            Log.e(TAG, "Zadán neznámý objekt " + polozka + ". Do db se nic nepřidá");
            return;
        }
        try {
            db.collection(keyword).document(String.valueOf(polozka.getTimestamp())).set(polozka);
        } catch (Exception e) {
            Log.e(TAG, "Error při přidávání " + polozka + " do databáze " + keyword, e);
        }
    }

    public void smazZDatabaze(final Polozka polozka) {
        String keyword = "";
        if (polozka instanceof Hrac) {
            keyword = "hrac";
        }
        else if (polozka instanceof Zapas) {
            keyword = "zapas";
        }
        else if (polozka instanceof Pivo) {
            keyword = "pivo";
        }
        else {
            Log.e(TAG, "Zadán neznámý objekt " + polozka + ". Do db se nic nepřidá");
            return;
        }
        db.collection(keyword).document(String.valueOf(polozka.getTimestamp())).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  polozka + " úspěšně smazán z db ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error při přidávání " + polozka + " do databáze ", e);
                    }
                });
    }

    public List<Hrac> vratSeznamHracu(final ZobrazeniPolozky zobrazeniPolozky) {
        Log.d(TAG, "vracím seznam hráčů");
        db.collection("hrac")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            seznamHracu = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Hrac hrac = document.toObject(Hrac.class);
                                hrac.setZobrazeniPolozky(zobrazeniPolozky);
                                seznamHracu.add(hrac);
                            }
                            Log.d(TAG, "vracím seznam hráčů: " + seznamHracu);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return seznamHracu;
    }

    public List<Zapas> vratSeznamZapasu() {
        Log.d(TAG, "vracím seznam zápasů");
        db.collection("zapas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            seznamZapasu = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Zapas zapas = document.toObject(Zapas.class);
                                seznamZapasu.add(zapas);
                            }
                            Log.d(TAG, "vracím seznam zápasů: " + seznamZapasu);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return seznamZapasu;
    }
}

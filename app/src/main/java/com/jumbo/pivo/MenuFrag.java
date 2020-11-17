
package com.jumbo.pivo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.pivo.komparator.SeradHracePrvni;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class MenuFrag extends Fragment {

    private TextView tv_oslavenec, tv_random, tv_nadpisDialogu, tv_infoAplikace;
    private Button btn_info, btn_zavrit, btn_obnovit, btn_ok;
    private Spinner sp_vybratInfo;

    private ArrayAdapter infoArrayAdapter;

    private List<Zapas> seznamZapasu;
    private List<Hrac> seznamHracu;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceZapas;
    private CollectionReference collectionReferenceHrac;
    private FirestoreAdapter firestoreAdapter = new FirestoreAdapter();


    private static final String TAG = MenuFrag.class.toString();

    //klasický automatický načítání seznamu hráčů a zápasů z firestre db
    @Override
    public void onStart() {
        super.onStart();
        //kontrola jestli se něco změnilo na serveru ve firestore. Pokud ano vykoná se akce
        collectionReferenceZapas = db.collection("zapas");
        collectionReferenceZapas.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "Chyba " + error.toString());
                    return;
                }
                seznamZapasu = new ArrayList<>();
                seznamZapasu.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Zapas zapas = documentSnapshot.toObject(Zapas.class);
                    seznamZapasu.add(zapas);
                    Log.d(TAG, "Automaticky načten seznam zápasů po změně " + seznamZapasu);
                }
            }

        });
        collectionReferenceHrac = db.collection("hrac");
        collectionReferenceHrac.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "Chyba " + error.toString());
                    return;
                }
                seznamHracu = new ArrayList<>();
                seznamHracu.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Hrac hrac = documentSnapshot.toObject(Hrac.class);
                    hrac.setZobrazeniPolozky(ZobrazeniPolozky.Zakladni);
                    seznamHracu.add(hrac);
                    Log.d(TAG, "Automaticky načten seznam hráčů po změně " + seznamHracu);
                }
                seznamHracu.sort(new SeradHracePrvni());
                aktualizujNarozeniny();
                nastavNarozkyText();
                try {
                    nastavZajimavost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        tv_oslavenec = view.findViewById(R.id.tv_oslavenec);
        tv_random = view.findViewById(R.id.tv_random);
        btn_info = view.findViewById(R.id.btn_info);
        btn_obnovit = view.findViewById(R.id.btn_obnovit);
        btn_zavrit = view.findViewById(R.id.btn_zavrit);


        //nactiAktualniNarozky();
        //nastavZajimavost();

        btn_obnovit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nastavNarozkyText();
                try {
                    nastavZajimavost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_zavrit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Opravdu chcete zavřít aplikaci?")
                        .setCancelable(false)
                        .setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })
                        .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder infoAppDialog = new AlertDialog.Builder(getActivity());
                View infoAppView = getLayoutInflater().inflate(R.layout.dialog_info_aplikace, null);
                sp_vybratInfo = (Spinner) infoAppView.findViewById(R.id.sp_vybratInfo);
                tv_infoAplikace = (TextView) infoAppView.findViewById(R.id.tv_infoAplikace);
                btn_ok = (Button) infoAppView.findViewById(R.id.btn_ok);

                infoArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.info_app));
                infoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_vybratInfo.setAdapter(infoArrayAdapter);

                infoAppDialog.setView(infoAppView);
                final AlertDialog dialog = infoAppDialog.create();
                dialog.show();

                sp_vybratInfo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        switch (position) {
                            case 0:
                                tv_infoAplikace.setText(R.string.info_app1);
                                break;
                            case 1:
                                tv_infoAplikace.setText(R.string.info_app2);
                                break;
                            case 2:
                                tv_infoAplikace.setText(R.string.info_app3);
                                break;
                            case 3:
                                tv_infoAplikace.setText(R.string.info_app4);
                                break;
                            case 4:
                                tv_infoAplikace.setText(R.string.info_app5);
                                break;
                            case 5:
                                tv_infoAplikace.setText(R.string.info_app6);
                                break;
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return view;
    }
    //musíme aktualizovat to číslo dní do narozek v db
    private void aktualizujNarozeniny() {
        int seznamHracuSize = seznamHracu.size();
        for (int i = 0; i < seznamHracuSize; i++) {
            if (seznamHracu.get(i).vypocitejDniDoNarozenin()) {
                firestoreAdapter.pridatDoDatabaze(seznamHracu.get(i));
                Log.d(TAG, "Měním hráče " + seznamHracu.get(i) + " v db");
            }
        }
    }
    //Nastavuje text narozek
    private void nastavNarozkyText() {
        Hrac hrac = najdiOslavence();
        String text;
        if (hrac.getDniDoNarozenin() == 0) {
            if (hrac.isFanousek()) {
                text = "Dnes slaví narozeniny fanoušek" + hrac.getJmeno() + ", který slaví " + hrac.getVek() + " let. Už ten sud vyval a ať ti slouží splávek!";
            }
            else {
                text = "Dnes slaví narozeniny hráč" + hrac.getJmeno() + ", který slaví " + hrac.getVek() + " let. Už ten sud vyval a ať ti slouží splávek! Na Trus!!";
            }
        }
        else {
            if (hrac.isFanousek()) {
                text = "Příští rundu platí věrný fanoušek" + hrac.getJmeno() + ", který bude mít za " + hrac.getDniDoNarozenin() + " dní své " + hrac.getVek() + " narozeniny";
            }
            else {
                text = "Příští rundu platí " + hrac.getJmeno() + ", který bude mít za " + hrac.getDniDoNarozenin() + " dní své " + hrac.getVek() + " narozeniny";
            }
        }

        tv_oslavenec.setText(text);

    }

    //vrátí hráče co má v nejbližší době narozeniny
    private Hrac najdiOslavence() {
        Hrac hrac = null;
        int nejdrivejsiNarozky = 365;
        int seznamHracuSize = seznamHracu.size();
        for (int i = 0; i < seznamHracuSize; i++) {
            if (seznamHracu.get(i).getDniDoNarozenin() < nejdrivejsiNarozky) {
                hrac = seznamHracu.get(i);
                nejdrivejsiNarozky = hrac.getDniDoNarozenin();
            }
        }
        return hrac;
    }

    //nastavuje podle náhodnýho intu zajímavost
    private void nastavZajimavost() throws Exception {
        Random random = new Random();
        switch (random.nextInt(2)) {
            case 0:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratHraceSNejvetsimPoctemVelkychPiv() + "\n\n ");
                break;
            case 1:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratHraceSNejvetsimPoctemMalychPiv() + "\n\n ");
                break;
           /* case 2:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiMaxVelkychVZapase() + "\n\n ");
                break;
            case 3:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiMaxMalychVZapase() + "\n\n ");
                break;
            case 4:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiMaxVelkychZaSezonu() + "\n\n ");
                break;
            case 5:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiMaxMalýchZaSezonu() + "\n\n ");
                break;
            case 6:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.prepoctiPocetMalychNaVelke() + "\n\n ");
                break;
            case 7:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.vypocitejPrumernyPocetVypitychPiv() + "\n\n ");
                break;
            case 8:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiMinVelkychVZapase() + "\n\n ");
                break;
            case 9:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiNejvetsiUcastNaZapase() + "\n\n ");
                break;
            case 10:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiNejmensiUcastNaZapase() + "\n\n ");
                break;
            case 11:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiShoduNarozeninAZapasu() + "\n\n ");
                break;*/

        }
    }

    private String vratHraceSNejvetsimPoctemVelkychPiv() {
        Hrac hrac = null;
       /* int maximalniPocetPiv = 0;
        int seznamHracuSize = seznamHracu.size();
        for (int i = 0; i < seznamHracuSize; i++) {
            seznamHracu.get(i).aktualizujZeZapasuPocetPiv(seznamZapasu);
            if (seznamHracu.get(i).getPocetPiv().getPocetVelkych() > maximalniPocetPiv) {
                hrac = seznamHracu.get(i);
                maximalniPocetPiv = hrac.getPocetPiv().getPocetVelkych();
            }
        }*/
        if (hrac == null) {
            return "Nelze najít největšího pijana, protože si ještě nikdo nedal pivo???!!";
        }
        else {
            return "Nejvíce velkých piv za historii si dal " + hrac.getJmeno() + " který vypil " + hrac.getPocetPiv().getPocetVelkych() + " piv";
        }
    }

    private String vratHraceSNejvetsimPoctemMalychPiv() {
        Hrac hrac = null;
        int maximalniPocetPiv = 0;
        int seznamHracuSize = seznamHracu.size();
        /*for (int i = 0; i < seznamHracuSize; i++) {
            seznamHracu.get(i).aktualizujZeZapasuPocetPiv(seznamZapasu);
            if (seznamHracu.get(i).getPocetPiv().getPocetMalych() > maximalniPocetPiv) {
                hrac = seznamHracu.get(i);
                maximalniPocetPiv = hrac.getPocetPiv().getPocetMalych();
            }
        }*/
        if (hrac == null) {
            return "Ještě nikdo si nedal malý pivo a to je dobře";
        } else {
            return "Nejvíce malých piv za historii si dal " + hrac.getJmeno() + " který vypil " + hrac.getPocetPiv().getPocetVelkych() + " malejch piv";
        }
    }

    private String vratZapasSNejvetsimPoctemVelkychPiv() {
        Hrac hrac = null;
        int maximalniPocetPiv = 0;
        int seznamZapasuSize = seznamZapasu.size();
        for (int i = 0; i < seznamZapasuSize; i++) {
            seznamHracu.get(i).aktualizujZeZapasuPocetPiv(seznamZapasu);
            if (seznamHracu.get(i).getPocetPiv().getPocetVelkych() > maximalniPocetPiv) {
                hrac = seznamHracu.get(i);
                maximalniPocetPiv = hrac.getPocetPiv().getPocetVelkych();
            }
        }
        if (hrac == null) {
            return "Nelze najít největšího pijana, protože si ještě nikdo nedal pivo???!!";
        }
        else {
            return "Nejvíce velkých piv za historii si dal " + hrac.getJmeno() + " který vypil " + hrac.getPocetPiv().getPocetVelkych() + " piv";
        }
    }
}



package com.jumbo.pivo.gui;

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
import com.jumbo.pivo.Datum;
import com.jumbo.pivo.FirestoreAdapter;
import com.jumbo.pivo.Hrac;
import com.jumbo.pivo.R;
import com.jumbo.pivo.Sezona;
import com.jumbo.pivo.Zapas;
import com.jumbo.pivo.ZobrazeniPolozky;
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
        switch (random.nextInt(16)) {
            case 0:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratHraceSNejvetsimPoctemVelkychPiv() + "\n\n ");
                break;
            case 1:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratHraceSNejvetsimPoctemMalychPiv() + "\n\n ");
                break;
            case 2:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratZapasSNejvetsimPoctemVelkychPiv() + "\n\n ");
                break;
            case 3:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratZapasSNejvetsimPoctemMalychPiv() + "\n\n ");
                break;
            case 4:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratPocetVelkychTutoSezonu() + "\n\n ");
                break;
            case 5:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratPocetMalychTutoSezonu() + "\n\n ");
                break;
            case 6:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratPorovnaniSezon() + "\n\n ");
                break;
            case 7:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratPrumerPivNaHraceAFanouska()+ "\n\n ");
                break;
            case 8:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratPrumerPivNaHrace() + "\n\n ");
                break;
            case 9:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratPrumerPivNaFanouska() + "\n\n ");
                break;
            case 10:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratPrumerPivNaZapas() + "\n\n ");
                break;
            case 11:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratNejvyssiPrumerVZapase() + "\n\n ");
                break;
            case 12:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratNejnizsiPrumerVZapase() + "\n\n ");
                break;
            case 13:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratNejvetsiUcastVZapase() + "\n\n ");
                break;
            case 14:
                tv_random.setText("Náhodná zajímavost: \n\n" + vratNejnizsiUcastVZapase() + "\n\n ");
                break;
            case 15:
                tv_random.setText("Náhodná zajímavost: \n\n" + najdiShoduNarozeninAZapasu() + "\n\n ");
                break;

        }
    }

    /**
     * @return vrací String s hráčem co má zatím největší počet vypitých velkých piv. Pokud zatím nikdo nic nevypil, vrací větu s tímto významem
     */
    private String vratHraceSNejvetsimPoctemVelkychPiv() {
        Hrac hrac = null;
        int maximalniPocetPiv = 0;
        int seznamHracuSize = seznamHracu.size();
        for (int i = 0; i < seznamHracuSize; i++) {
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

    /**
     * @return vrací String s hráčem co má zatím největěěí počet vypitých malých piv. Pokud zatím nikdo nic nevypil, vrací větu s tímto významem
     */
    private String vratHraceSNejvetsimPoctemMalychPiv() {
        Hrac hrac = null;
        int maximalniPocetPiv = 0;
        int seznamHracuSize = seznamHracu.size();
        for (int i = 0; i < seznamHracuSize; i++) {
            if (seznamHracu.get(i).getPocetPiv().getPocetMalych() > maximalniPocetPiv) {
                hrac = seznamHracu.get(i);
                maximalniPocetPiv = hrac.getPocetPiv().getPocetMalych();
            }
        }
        if (hrac == null) {
            return "Ještě nikdo si nedal malý pivo a to je dobře";
        } else {
            return "Nejvíce malých piv za historii si dal " + hrac.getJmeno() + " který vypil " + hrac.getPocetPiv().getPocetVelkych() + " malejch piv";
        }
    }

    /**
     * @return vrací String se zápase kde je zatím největší počet vypitých velkých piv. Pokud zatím nikdo nic nevypil, vrací větu s tímto významem
     */
    private String vratZapasSNejvetsimPoctemVelkychPiv() {
        Zapas zapas = null;
        int maximalniPocetPiv = 0;
        int seznamZapasuSize = seznamZapasu.size();
        for (int i = 0; i < seznamZapasuSize; i++) {
            if (seznamZapasu.get(i).getCelkovyPocetVelkychPiv() > maximalniPocetPiv) {
                zapas = seznamZapasu.get(i);
                maximalniPocetPiv = zapas.getCelkovyPocetVelkychPiv();
            }
        }
        if (zapas == null) {
            return "Nelze najít zápas s největším počtem kousků, jelikož zatím nikdo žádný nevypil??";
        }
        else {
            return "Nejvíce velkých piv v historii padl v zápase se soupeřem " + zapas.getSouper() + " hraný " + Datum.zmenDatumDoFront(zapas.getDatum()) + " kdy se vypilo " + zapas.getCelkovyPocetVelkychPiv() + " piv";
        }
    }

    /**
     * @return vrací String se zápase kde je zatím největší počet vypitých malých piv. Pokud zatím nikdo nic nevypil, vrací větu s tímto významem
     */
    private String vratZapasSNejvetsimPoctemMalychPiv() {
        Zapas zapas = null;
        int maximalniPocetPiv = 0;
        int seznamZapasuSize = seznamZapasu.size();
        for (int i = 0; i < seznamZapasuSize; i++) {
            if (seznamZapasu.get(i).getCelkovyPocetMalychPiv() > maximalniPocetPiv) {
                zapas = seznamZapasu.get(i);
                maximalniPocetPiv = zapas.getCelkovyPocetMalychPiv();
            }
        }
        if (zapas == null) {
            return "Zatím se v žádném zápase nevypilo malé pivo. Díky!";
        }
        else {
            return "Nejvíce malých piv v historii padl v zápase se soupeřem " + zapas.getSouper() + " hraný " + Datum.zmenDatumDoFront(zapas.getDatum()) + " kdy se vypilo " + zapas.getCelkovyPocetMalychPiv() + "malejch";
        }
    }

    /**
     * @return vrací String s počtem vypitých velkých piv pro tuto sezonu. Pokud zatím nikdo nic nevypil, vrací větu s tímto významem
     */
    private String vratPocetVelkychTutoSezonu() {
        Zapas zapas = new Zapas("srovnani", Datum.zformatuj(Datum.zjistiDnesniDatum()), false, null);
        int maximalniPocetPiv = 0;
        int seznamZapasuSize = seznamZapasu.size();
        for (int i = 0; i < seznamZapasuSize; i++) {
            if (seznamZapasu.get(i).getCelkovyPocetVelkychPiv() > maximalniPocetPiv && seznamZapasu.get(i).getSezona() == zapas.getSezona()) {
                zapas = seznamZapasu.get(i);
                maximalniPocetPiv = zapas.getCelkovyPocetVelkychPiv();
            }
        }
        if (zapas == null) {
            return "Tuto sezonu se v žádném zápase nevypilo žádné pivo";
        }
        else {
            return "Nejvíce velkých piv v této sezoně " + zapas.getSezona() + " padlo v zápase se soupeřem " + zapas.getSouper() + " hraný " + Datum.zmenDatumDoFront(zapas.getDatum()) + " kdy se vypilo " + zapas.getCelkovyPocetVelkychPiv() + " piv";
        }
    }

    /**
     * @return vrací String s počtem vypitých malých piv pro tuto sezonu. Pokud zatím nikdo nic nevypil, vrací větu s tímto významem
     */
    private String vratPocetMalychTutoSezonu() {
        Zapas zapas = new Zapas("srovnani", Datum.zformatuj(Datum.zjistiDnesniDatum()), false, null);
        int maximalniPocetPiv = 0;
        int seznamZapasuSize = seznamZapasu.size();
        for (int i = 0; i < seznamZapasuSize; i++) {
            if (seznamZapasu.get(i).getCelkovyPocetMalychPiv() > maximalniPocetPiv && seznamZapasu.get(i).getSezona() == zapas.getSezona()) {
                zapas = seznamZapasu.get(i);
                maximalniPocetPiv = zapas.getCelkovyPocetMalychPiv();
            }
        }
        if (zapas == null) {
            return "Tuto sezonu se v žádném zápase nevypilo žádné malé pivo. Díky";
        }
        else {
            return "Nejvíce malých piv v této sezoně " + zapas.getSezona() + " padlo v zápase se soupeřem " + zapas.getSouper() + " hraný " + Datum.zmenDatumDoFront(zapas.getDatum()) + " kdy se vypilo " + zapas.getCelkovyPocetVelkychPiv() + "malých piv";
        }
    }

    /**
     * @return vrací String se sezonou kdy se vypilo nejvíc piv, plus odpovídající údaje. Pokud zatím nikdo nic nevypil, vrací větu s tímto významem
     */
    private String vratPorovnaniSezon() {
        Sezona sezona = null;
        int pocetZapasu = 0;
        int maximalniPocetPiv = 0;
        int seznamZapasuSize = seznamZapasu.size();
        for (int j = 1; j < Sezona.values().length-1; j++) {
            int sezonniPiva = 0;
            int pocetSezonnichZapasu = 0;
            for (int i = 0; i < seznamZapasuSize; i++) {
                if (seznamZapasu.get(i).getSezona() == Sezona.zaradSezonuDleComba(j)) {
                    sezonniPiva += seznamZapasu.get(i).getCelkovyPocetVelkychPiv();
                    pocetSezonnichZapasu++;
                }
            }
            if (sezonniPiva > maximalniPocetPiv) {
                maximalniPocetPiv = sezonniPiva;
                sezona = Sezona.zaradSezonuDleComba(j);
                pocetZapasu = pocetSezonnichZapasu;
            }
        }
        if (sezona == null) {
            return "Zatím se nevypilo žádné pivo";
        }
        else {
            return "Nejvíce velkých piv se vypilo v sezoně " + sezona + " kdy padlo celkem v " + pocetZapasu + " zápasech " + maximalniPocetPiv + " velkých piv";
        }
    }

    /**
     * @return Vrací průměrný počet vypitých piv na jednoho účastníka zápasu
     */
    private String vratPrumerPivNaHraceAFanouska() {
        float pocetPiv = 0;
        int seznamHracuSize = seznamHracu.size();
        for (int i = 0; i < seznamHracuSize; i++) {
            pocetPiv += seznamHracu.get(i).getPocetPiv().getPocetVelkych();
        }
        float prumer = pocetPiv/seznamHracuSize;
        return "Za celou historii průměrně každý hráč a fanoušek trusu vypil " + prumer + " velkých piv za zápas";
    }

    /**
     * @return Vrací průměrný počet vypitých piv na jednoho hráče
     */
    private String vratPrumerPivNaHrace() {
        float pocetPiv = 0;
        int seznamHracuSize = seznamHracu.size();
        for (int i = 0; i < seznamHracuSize; i++) {
            if (!seznamHracu.get(i).isFanousek()) {
                pocetPiv += seznamHracu.get(i).getPocetPiv().getPocetVelkych();
            }
        }
        float prumer = pocetPiv/seznamHracuSize;
        return "Za celou historii průměrně každý hráč trusu vypil " + prumer + " velkých piv za zápas";
    }

    /**
     * @return Vrací průměrný počet vypitých piv na jednoho fanouška
     */
    private String vratPrumerPivNaFanouska() {
        float pocetPiv = 0;
        int seznamHracuSize = seznamHracu.size();
        for (int i = 0; i < seznamHracuSize; i++) {
            if (seznamHracu.get(i).isFanousek()) {
                pocetPiv += seznamHracu.get(i).getPocetPiv().getPocetVelkych();
            }
        }
        float prumer = pocetPiv/seznamHracuSize;
        return "Za celou historii průměrně každý fanoušek trusu vypil " + prumer + " velkých piv za zápas";
    }

    /**
     * @return Vrací průměrný počet vypitých piv v jednom zápase
     */
    private String vratPrumerPivNaZapas() {
        float pocetPiv = 0;
        int seznamZapasuSize = seznamZapasu.size();
        for (int i = 0; i < seznamZapasuSize; i++) {
            pocetPiv += seznamZapasu.get(i).getCelkovyPocetVelkychPiv();
        }
        float prumer = pocetPiv/seznamZapasuSize;
        return "Průměrně se v zápase Trusu vypije " + prumer + " velkých piv";
    }

    /**
     * @return Vrací dosud nejvyšší průměrný počet vypitých piv v jednom zápase
     */
    private String vratNejvyssiPrumerVZapase() {
        Zapas zapas = null;
        int seznamZapasuSize = seznamZapasu.size();
        float prumer = 0;
        for (int i = 0; i < seznamZapasuSize; i++) {
            if (seznamZapasu.get(i).getCelkovyPocetVelkychPiv() / seznamZapasu.get(i).getSeznamHracu().size() > prumer) {
                prumer = seznamZapasu.get(i).getCelkovyPocetVelkychPiv() / (float) seznamZapasu.get(i).getSeznamHracu().size();
                zapas = seznamZapasu.get(i);
            }
        }

        return "Nejvyšší průměr počtu vypitých piv v zápase proběhl na zápase se soupeřem " + zapas.getSouper() + " hraném v sezoně " + zapas.getSezona() +
                " konkrétně " + Datum.zmenDatumDoFront(zapas.getDatum()) + ". Vypilo se " + zapas.getCelkovyPocetVelkychPiv() + " piv v " + zapas.getSeznamHracu().size() +
                " lidech, což dělá průměr " + prumer + " na hráče";
    }

    /**
     * @return Vrací dosud nejnižší průměrný počet vypitých piv v jednom zápase
     */
    private String vratNejnizsiPrumerVZapase() {
        Zapas zapas = null;
        int seznamZapasuSize = seznamZapasu.size();
        float prumer = 1000;
        for (int i = 0; i < seznamZapasuSize; i++) {
            if ((seznamZapasu.get(i).getCelkovyPocetVelkychPiv() / seznamZapasu.get(i).getSeznamHracu().size() < prumer) && (seznamZapasu.get(i).getCelkovyPocetVelkychPiv() != 0)) {
                prumer = seznamZapasu.get(i).getCelkovyPocetVelkychPiv() / (float) seznamZapasu.get(i).getSeznamHracu().size();
                zapas = seznamZapasu.get(i);
            }
        }

        return "Ostudný den Liščího Trusu, kdy byl pokořen rekord v nejnižším průměru počtu vypitých piv v zápase proběhl na zápase se soupeřem " + zapas.getSouper() + " hraném v sezoně " + zapas.getSezona() +
                " konkrétně " + Datum.zmenDatumDoFront(zapas.getDatum()) + ". Vypilo se " + zapas.getCelkovyPocetVelkychPiv() + " piv v " + zapas.getSeznamHracu().size() +
                " lidech, což dělá průměr " + prumer + " na hráče. Vzpomeňte si na to, až si budete objednávat další rundu!";
    }

    /**
     * @return Vrací dosud nejvyšší účast jednom zápase spolu s podrobnými údaji
     */
    private String vratNejvetsiUcastVZapase() {
        Zapas zapas = null;
        int seznamZapasuSize = seznamZapasu.size();
        int ucastVZapase = 0;
        for (int i = 0; i < seznamZapasuSize; i++) {
            if (seznamZapasu.get(i).getSeznamHracu().size() > ucastVZapase) {
                ucastVZapase = seznamZapasu.get(i).getSeznamHracu().size();
                zapas = seznamZapasu.get(i);
            }
        }
        int pocetFans = 0;
        for (int i = 0; i < zapas.getSeznamHracu().size(); i++) {
            if (zapas.getSeznamHracu().get(i).isFanousek()) {
                pocetFans++;
            }
        }
        String fanousek;
        if (pocetFans==1) {
            fanousek = "z toho 1 fanoušek";
        }
        else if (pocetFans > 1 && pocetFans < 5) {
            fanousek = "z toho " + pocetFans + " fanoušci";
        }
        else {
            fanousek = "z toho " + pocetFans + " fanoušků";
        }
        String hrac;
        if ((ucastVZapase-pocetFans)==1) {
            hrac = "1 hráč";
        }
        else if ((ucastVZapase-pocetFans) > 1 && (ucastVZapase-pocetFans) < 5) {
            hrac = pocetFans + " hráči";
        }
        else {
            hrac = pocetFans + " hráčů";
        }

        return "Největší účast na zápase Liščího Trusu proběhla " + Datum.zmenDatumDoFront(zapas.getDatum()) + " se soupeřem " + zapas.getSouper() +
                " kdy celkový počet účastníků byl " + ucastVZapase + " lidí " + fanousek + " a " + hrac +
                ". Celkově se vypilo " + zapas.getCelkovyPocetVelkychPiv() + " velkých piv";
    }

    /**
     * @return Vrací dosud nejnižší účast jednom zápase spolu s podrobnými údaji
     */
    private String vratNejnizsiUcastVZapase() {
        Zapas zapas = null;
        int seznamZapasuSize = seznamZapasu.size();
        int ucastVZapase = 1000;
        for (int i = 0; i < seznamZapasuSize; i++) {
            if (seznamZapasu.get(i).getSeznamHracu().size() < ucastVZapase) {
                ucastVZapase = seznamZapasu.get(i).getSeznamHracu().size();
                zapas = seznamZapasu.get(i);
            }
        }
        int pocetFans = 0;
        for (int i = 0; i < zapas.getSeznamHracu().size(); i++) {
            if (zapas.getSeznamHracu().get(i).isFanousek()) {
                pocetFans++;
            }
        }
        String fanousek;
        if (pocetFans==1) {
            fanousek = "z toho 1 fanoušek";
        }
        else if (pocetFans > 1 && pocetFans < 5) {
            fanousek = "z toho " + pocetFans + " fanoušci";
        }
        else {
            fanousek = "z toho " + pocetFans + " fanoušků";
        }
        String hrac;
        if ((ucastVZapase-pocetFans)==1) {
            hrac = "1 hráč";
        }
        else if ((ucastVZapase-pocetFans) > 1 && (ucastVZapase-pocetFans) < 5) {
            hrac = pocetFans + " hráči";
        }
        else {
            hrac = pocetFans + " hráčů";
        }
        return "Nejnižší účast na zápase Liščího Trusu proběhla " + Datum.zmenDatumDoFront(zapas.getDatum()) + " se soupeřem " + zapas.getSouper() +
                " kdy celkový počet účastníků byl " + ucastVZapase + " lidí " + fanousek + " a " + hrac +
                ". Celkově se vypilo " + zapas.getCelkovyPocetVelkychPiv() + " velkých piv";
    }

    /**
     * @return vrací seznam hráčů, kteří slavili (nebo budou slavit v případě budoucího data) narozeniny na zápase spolu s údajem o počtu vypitých piv. Pokud nikdo neslavil vrací se odpovídající text
     */
    private String najdiShoduNarozeninAZapasu() {
        List<String> seznamNarozenin = new ArrayList<>();
        seznamNarozenin.add("Jedním ze zápasů kdy se zapíjely narozeniny pijana Liščího Trusu byl: \n");
        int seznamZapasuSize = seznamZapasu.size();
        int seznamHracuSize = seznamHracu.size();
        Log.d(TAG, "seznam hráčů: " + seznamHracu);

        for (int i = 0; i < seznamZapasuSize; i++) {
            for (int j = 0; j < seznamHracuSize; j++) {
                if (Datum.setDniDoNarozenin(seznamZapasu.get(i).getDatum()) == (Datum.setDniDoNarozenin(seznamHracu.get(j).getDatum()))) {
                    Log.d(TAG, "zápas: " + seznamZapasu.get(i) + Datum.setDniDoNarozenin(seznamZapasu.get(i).getDatum()) + " hráč: " + seznamHracu.get(j) + (Datum.setDniDoNarozenin(seznamHracu.get(j).getDatum())));
                    seznamNarozenin.add(seznamZapasu.get(i) + " kdy se zapíjely narozeniny hráče " + seznamHracu.get(j).getJmeno() + " a vypilo se " + seznamZapasu.get(i).getCelkovyPocetVelkychPiv() + " piv\n");
                }
            }
        }
        if (seznamNarozenin.size() == 1) {
            seznamNarozenin.clear();
            seznamNarozenin.add("Zatím se nenašel zápas kdy by nějaký hráč zapíjel narozky. Už se všichni těšíme");
        }
        String odpoved = "";
        for (int i = 0; i < seznamNarozenin.size(); i++) {
            odpoved += seznamNarozenin.get(i);
        }
        return odpoved;
    }


}


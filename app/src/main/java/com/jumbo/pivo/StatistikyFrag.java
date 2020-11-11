
package com.jumbo.pivo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StatistikyFrag extends Fragment {

    private Button btn_hledej, btn_celkove, btn_zrusit, btn_celkoveStats;
    private Switch sw_hracZapas;
    private Spinner sp_vyberHraceSezony, sp_podrobnyVyber;
    private TextView tv_nadpis, tv_statsCelkem;
    private ListView lv_seznam, lv_seznamVyber, lv_statsCelkem;
    private EditText et_hledej;
    private boolean swZobrazeniZapasu;

    private ArrayAdapter spinnerArrayAdapter;
    private ArrayAdapter spinnerHraciDialogArrayAdapter;
    private ArrayAdapter spinnerZapasyDialogArrayAdapter;
    private ArrayAdapter listViewZapasArrayAdapter;
    private ArrayAdapter listViewHraciArrayAdapter;
    private ArrayAdapter listViewZapasyVyberArrayAdapter;
    private ArrayAdapter celkemStatsArrayAdapter;
    private ValidacePoli validace = new ValidacePoli();

    private int idHrace;
    private int idZapasu;
    private int poziceSpinneru = 0;

    private List<Zapas> seznamZapasu;
    private List<Hrac> seznamHracu;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceZapas;
    private CollectionReference collectionReferenceHrac;
    private FirestoreAdapter firestoreAdapter = new FirestoreAdapter();

    private static final String TAG = StatistikyFrag.class.toString();

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
                //zobrazListViewDleSpinneru(seznamZapasu);
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
                    hrac.setZobrazeniHrace(ZobrazeniHrace.Detailni);
                    seznamHracu.add(hrac);
                    Log.d(TAG, "Automaticky načten seznam hráčů po změně " + seznamHracu);
                }
                seznamHracu.sort(new SeradHracePrvni());
                //zobrazHraceNaListView(seznamHracu);
            }

        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //propojení tlačítek a kódu
        View view = inflater.inflate(R.layout.fragment_statistiky, container, false);
        btn_hledej = (Button) view.findViewById(R.id.btn_hledej);
        sw_hracZapas = (Switch) view.findViewById(R.id.sw_hracZapas);
        sp_vyberHraceSezony = (Spinner) view.findViewById(R.id.sp_vyberHraceSezony);
        btn_celkoveStats = (Button) view.findViewById(R.id.btn_celkoveStats);
        lv_seznam = (ListView) view.findViewById(R.id.lv_seznam);
        et_hledej = (EditText) view.findViewById(R.id.et_hledej);


        //defaultní zobrazení při prvním zobrazení stránky
        //zobrazObrazovkuSHraci();

        //switch pro nastavení zobrazení
        sw_hracZapas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {

                    //zobrazObrazovkuSHraci();
                    setSwZobrazeniZapasu(false);
                    sw_hracZapas.setText(R.string.zobrazeni_zapasu);
                    nastavZobrazeniSpinneru(false);

                }
                else {

                    setSwZobrazeniZapasu(true);
                    sw_hracZapas.setText(R.string.zobrazeni_hracu);
                    nastavZobrazeniSpinneru(true);
                    //zobrazObrazovkuSeZapasy();

                }
            }
        });

        btn_celkoveStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zobrazCelkovySeznamDialog();
            }
        });
        nastavZobrazeniSpinneru(swZobrazeniZapasu);

        return view;
    }


    private void nastavZobrazeniSpinneru(boolean zobrazeniZapasu) {
        if (zobrazeniZapasu) {
            spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sezony));
            sp_vyberHraceSezony.setAdapter(spinnerArrayAdapter);
        }
        else {
            spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hraci));
            sp_vyberHraceSezony.setAdapter(spinnerArrayAdapter);
        }
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //zobrazení list view podle polohy na spinneru
        sp_vyberHraceSezony.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                poziceSpinneru = position;
                Log.d(TAG, "pozice spinneru " + position + " nastavuji jako " + poziceSpinneru);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                poziceSpinneru = 0;
                Log.d(TAG, "pozice spinneru neni zadna, nastavuji jako " + poziceSpinneru);
            }
        });

    }


/**
     * zobrazí všechny zápasy z databáze na listview
     * @param dBadapter2
     */

   /* private void zobrazVsechnyHraceNaListView(DBadapter dBadapter2) {
        listViewHraciArrayAdapter = new ArrayAdapter<Hrac>(getActivity(), android.R.layout.simple_list_item_1, dBadapter2.vyberVsechnyHrace(0));
        lv_seznam.setAdapter(listViewHraciArrayAdapter);
    }


*//**zobrazí vybrané zápasy dle spinneru na listview
     * @param dBadapter2
     * @param pozice
     *//*

    private void zobrazHraceNaListView(DBadapter dBadapter2, int pozice) {
        listViewHraciArrayAdapter = new ArrayAdapter<Hrac>(getActivity(), android.R.layout.simple_list_item_1, dBadapter2.vyberHrace(pozice));
        lv_seznam.setAdapter(listViewHraciArrayAdapter);
    }



*//**
     * zobrazí listviewdle spinneru i s volbou zobrazit vše
     *//*

    private void zobrazListViewHracuDleSpinneru(int polohaSpinneruHraci) {
        if (polohaSpinneruHraci == 1) {
            zobrazVsechnyHraceNaListView(dBadapter);
        }
        else if (polohaSpinneruHraci == 0) {
            List<String> vyberHrace = new ArrayList<>();
            listViewHraciArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, vyberHrace);
            lv_seznam.setAdapter(listViewHraciArrayAdapter);
        }
        else {
            zobrazHraceNaListView(dBadapter, polohaSpinneruHraci);
        }

    }


*//**zobrazení vyfiltrovaných zápasů z db adaptéru, primárně určené pro výpis dialogového okna
     * @param dBadapter2
     * @param position pozice druhého spinneru
     *//*

    private void zobrazZapasyNaListViewProStatistiku(DBadapter dBadapter2, int position) {
        listViewZapasyVyberArrayAdapter = new ArrayAdapter<Zapas>(getActivity(), android.R.layout.simple_list_item_1, dBadapter2.zobrazZapasyProStatistiku(idHrace, position));
        lv_seznamVyber.setAdapter(listViewZapasyVyberArrayAdapter);
    }


*//**zobrazení vyfiltrovaných hráčů z db adaptéru, primárně určené pro výpis dialogového okna
     * @param dBadapter2
     * @param position pozice druhého spinneru
     *//*

    private void zobrazHraceNaListViewProStatistiku(DBadapter dBadapter2, int position) {
        listViewZapasyVyberArrayAdapter = new ArrayAdapter<Hrac>(getActivity(), android.R.layout.simple_list_item_1, dBadapter2.zobrazHraceProStatistiku(idHrace, position));
        lv_seznamVyber.setAdapter(listViewZapasyVyberArrayAdapter);
    }


*//**vypíše seznam všech zápasů
     * @param dBadapter2
     *//*

    private void zobrazZapasyNaListView(DBadapter dBadapter2) {
        listViewZapasArrayAdapter = new ArrayAdapter<Zapas>(getActivity(), android.R.layout.simple_list_item_1, dBadapter2.vyberVsechnyZapasy());
        lv_seznam.setAdapter(listViewZapasArrayAdapter);
    }


*//**podle pozice vypíše seznam zápasů dle sezony
     * @param dBadapter2
     * @param pozice
     *//*

    private void zobrazSezonyNaListView(DBadapter dBadapter2, int pozice) {
        listViewZapasArrayAdapter = new ArrayAdapter<Zapas>(getActivity(), android.R.layout.simple_list_item_1, dBadapter2.vyberSezonu(pozice));
        lv_seznam.setAdapter(listViewZapasArrayAdapter);
    }


*//**podle polohy spinneru zavolá buď metodu pro výpis všech zápasů či metodu pro výpis konkrétní sezony dle filteru
     * @param polohaSpinneruZapasy
     *//*

    private void zobrazListViewZapasuDleSpinneru(int polohaSpinneruZapasy) {
        polohaSpinneruZapasy--;
        if (polohaSpinneruZapasy == 0) {
            zobrazZapasyNaListView(dBadapter);
        }
        else if (polohaSpinneruZapasy == -1) {
            List<String> vyberZapas = new ArrayList<>();
            listViewHraciArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, vyberZapas);
            lv_seznam.setAdapter(listViewHraciArrayAdapter);
        }
        else {
            zobrazSezonyNaListView(dBadapter, polohaSpinneruZapasy);
        }

    }


*//**zobrazí na listview arraylist načtený z databáze po stisku hledat
     * @param dBadapter2
     * @param pozice
     *//*

    private void zobrazHledaneZapasy(DBadapter dBadapter2, int pozice, String hledaneSlovo) {
        listViewZapasArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dBadapter2.hledatZapasySezona(pozice, hledaneSlovo));
        lv_seznam.setAdapter(listViewZapasArrayAdapter);
    }


*//**zobrazí na listview arraylist načtený z databáze po stisku hledat
     * @param
     * @param
     *//*

    private void zobrazHledaneHrace(DBadapter dBadapter2, int pozice, String hledaneSlovo) {
        listViewHraciArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dBadapter2.hledatHrace(pozice, hledaneSlovo));
        lv_seznam.setAdapter(listViewHraciArrayAdapter);
    }



    public void setIdHrace(int idHrace) {
        this.idHrace = idHrace;
    }


    public void setIdZapasu(int idZapasu) {
        this.idZapasu = idZapasu;
    }

    private void zobrazObrazovkuSHraci() {

        sw_hracZapas.setText(R.string.zobrazeni_zapasu);

        //spárování spinneru a arraylistu pro zobrazení ze stringu
        spinnerHraciArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.statistiky_hraci));
        spinnerHraciArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_vyberHraceSezony.setAdapter(spinnerHraciArrayAdapter);

        //zobrazení list view podle polohy na spinneru
        sp_vyberHraceSezony.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                zobrazListViewHracuDleSpinneru(position);
                setHracPosition(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //tlačítko hledat
        btn_hledej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                et_hledej.setHint("zadej heslo");
                if (validace.zvalidujJmeno(et_hledej)) {
                    Toast.makeText(getActivity(), "Buď si zadal jméno delší než 100 znaků nebo si použil zakázané znaky. Povoleny jsou pouze alfanumerické znaky, podtržítko a pomlčka", Toast.LENGTH_LONG).show();
                } else {
                    zobrazHledaneHrace(dBadapter, hracPosition, et_hledej.getText().toString());
                }
            }
        });


        lv_seznam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Hrac oznacenyHrac = (Hrac) parent.getItemAtPosition(position);
                setIdHrace(oznacenyHrac.getId());

                //definování dialogového okna pro zobrazení statistik
                final AlertDialog.Builder hracDialog = new AlertDialog.Builder(getActivity());

                //definování tlačítek a layoutu v dialogu pro zobrazení statistik
                View hracView = getLayoutInflater().inflate(R.layout.dialog_vyber_statistiky, null);
                btn_celkove = (Button) hracView.findViewById(R.id.btn_celkove);
                btn_zrusit = (Button) hracView.findViewById(R.id.btn_zrusit);
                lv_seznamVyber = (ListView) hracView.findViewById(R.id.lv_seznamVyber);
                sp_podrobnyVyber = (Spinner) hracView.findViewById(R.id.sp_podrobnyVyber);
                tv_nadpis = (TextView) hracView.findViewById(R.id.tv_nadpis);

                tv_nadpis.setText("Statistiky pro " + oznacenyHrac.getJmeno());

                hracDialog.setView(hracView);
                final AlertDialog dialog = hracDialog.create();
                dialog.show();

                btn_zrusit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //spárování spinneru u prvního dialogu a arraylistu pro zobrazení ze stringu
                spinnerZapasyDialogArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sezony));
                spinnerZapasyDialogArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_podrobnyVyber.setAdapter(spinnerZapasyDialogArrayAdapter);

                //zobrazení druhého list view podle polohy na spinneru
                sp_podrobnyVyber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        zobrazZapasyNaListViewProStatistiku(dBadapter, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                btn_celkove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), oznacenyHrac.getJmeno() + " vypil celkově " + dBadapter.getCelkovyPocetMalychPiv() + " malých piv a " + dBadapter.getCelkovyPocetVelkychPiv() + " velkých piv v " + dBadapter.getCelkovyPocetZapasu() + " zápasech.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void zobrazObrazovkuSeZapasy() {

        sw_hracZapas.setText(R.string.zobrazeni_hracu);

        //spárování spinneru a arraylistu pro zobrazení ze stringu

        spinnerZapasyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.statistiky_sezony));
        spinnerZapasyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_vyberHraceSezony.setAdapter(spinnerZapasyArrayAdapter);

        //zobrazení list view podle polohy na spinneru
        sp_vyberHraceSezony.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                zobrazListViewZapasuDleSpinneru(position);
                if(!swCheck) {
                    setSezonaPosition(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //tlačítko hledat
        btn_hledej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_hledej.setHint("zadej heslo");
                if (validace.zvalidujJmeno(et_hledej)) {
                    Toast.makeText(getActivity(), "Buď si zadal jméno delší než 100 znaků nebo si použil zakázané znaky. Povoleny jsou pouze alfanumerické znaky, podtržítko a pomlčka", Toast.LENGTH_LONG).show();
                } else {
                    zobrazHledaneZapasy(dBadapter, sezonaPosition, et_hledej.getText().toString());
                }
            }
        });


        lv_seznam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Zapas oznacenyZapas = (Zapas) parent.getItemAtPosition(position);
                setIdHrace(oznacenyZapas.getId());

                //definování dialogového okna pro zobrazení statistik
                final AlertDialog.Builder zapasDialog = new AlertDialog.Builder(getActivity());

                //definování tlačítek a layoutu v dialogu pro zobrazení statistik
                View zapasView = getLayoutInflater().inflate(R.layout.dialog_vyber_statistiky, null);
                btn_celkove = (Button) zapasView.findViewById(R.id.btn_celkove);
                btn_zrusit = (Button) zapasView.findViewById(R.id.btn_zrusit);
                lv_seznamVyber = (ListView) zapasView.findViewById(R.id.lv_seznamVyber);
                sp_podrobnyVyber = (Spinner) zapasView.findViewById(R.id.sp_podrobnyVyber);
                tv_nadpis = (TextView) zapasView.findViewById(R.id.tv_nadpis);

                tv_nadpis.setText("Statistiky se soupeřem " + oznacenyZapas.getSouper());

                zapasDialog.setView(zapasView);
                final AlertDialog dialog = zapasDialog.create();
                dialog.show();

                btn_zrusit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //spárování spinneru u prvního dialogu a arraylistu pro zobrazení ze stringu
                spinnerHraciDialogArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.statistiky_dialog_hraci));
                spinnerHraciDialogArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_podrobnyVyber.setAdapter(spinnerHraciDialogArrayAdapter);

                //zobrazení druhého list view podle polohy na spinneru
                sp_podrobnyVyber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        zobrazHraceNaListViewProStatistiku(dBadapter, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                btn_celkove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Na zápase se soupeřem " + oznacenyZapas.getSouper() + " se celkově vypilo " + dBadapter.getCelkovyPocetMalychPiv() + " malých piv a " + dBadapter.getCelkovyPocetVelkychPiv() + " velkých piv v " + dBadapter.getCelkovyPocetZapasu() + ".  lidech.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }*/


    public void setSwZobrazeniZapasu(boolean swZobrazeniZapasu) {
        this.swZobrazeniZapasu = swZobrazeniZapasu;
    }

    /**
     * @param sezona Pokud není sezona vše, tak se pomocí ifu vytřídí volání pouze zápasů dle určené sezony
     * @param hracEnum enum podle kterého se určí zda volat metodu pro výpočet všech piv či metodu pro výpočet hráče/fanouška
     * @return počet vypitejch piv
     */
    private int spocitejSezonniVelkaPiva(Sezona sezona, HracEnum hracEnum) {
        Log.d(TAG, "Počítám velká piva pro sezonu " + sezona + " a pro účastníky " + hracEnum + "...");
        int pocetPiv = 0;
        int size = seznamZapasu.size();
        for (int i = 0; i < size; i++) {
            if (hracEnum == HracEnum.Vse) {
                if (seznamZapasu.get(i).getSezona() == sezona || sezona == Sezona.Vse) {
                    pocetPiv += seznamZapasu.get(i).getCelkovyPocetVelkychPiv();
                }
            }
            else {
                if (seznamZapasu.get(i).getSezona() == sezona || sezona == Sezona.Vse) {
                        pocetPiv += seznamZapasu.get(i).getHracuvPocetVelkychPiv(hracEnum == HracEnum.Fanousek);
                }
            }

        }
        Log.d(TAG, "vracím " + pocetPiv + " počet velkých piv za sezonu " + sezona);
        return pocetPiv;
    }

    private int spocitejSezonniMalaPiva(Sezona sezona, HracEnum hracEnum) {
        Log.d(TAG, "Počítám malá piva pro sezonu " + sezona + " a pro účastníky " + hracEnum + "...");
        int pocetPiv = 0;
        int size = seznamZapasu.size();
        for (int i = 0; i < size; i++) {
            if (hracEnum == HracEnum.Vse) {
                if (seznamZapasu.get(i).getSezona() == sezona || sezona == Sezona.Vse) {
                    pocetPiv += seznamZapasu.get(i).getCelkovyPocetMalychPiv();
                }
            }
            else {
                if (seznamZapasu.get(i).getSezona() == sezona || sezona == Sezona.Vse) {
                    pocetPiv += seznamZapasu.get(i).getHracuvPocetMalychPiv(hracEnum == HracEnum.Fanousek);
                }
            }
        }
        Log.d(TAG, "vracím " + pocetPiv + " počet malých piv za sezonu " + sezona);
        return pocetPiv;
    }



    //Spinner se řídí dle enumu HracEnum a Sezona. Podle polohy a for cyklu pak volá privátní metody pro výpočet poščtu piv a přidává je do stringu na zobrazení
    private List<String> vratListCelkovychVypitychPiv() {
        List<String> listCelkovychPiv = new ArrayList<>();
        //pokud je zobrazen ve switchi zápas
        if (swZobrazeniZapasu) {
            //pokud se žádná piva v sezoně nevypila tak nemá cenu projíždět cyklus. Akorát se do listu přidá upozornění že žádný piva se nevypila
            if (spocitejSezonniVelkaPiva(Sezona.zaradSezonuDleComba(poziceSpinneru), HracEnum.Vse) == 0 && spocitejSezonniMalaPiva(Sezona.zaradSezonuDleComba(poziceSpinneru), HracEnum.Vse) == 0) {
                listCelkovychPiv.add("V sezoně " + Sezona.zaradSezonuDleComba(poziceSpinneru) + " se zatím nevypila žádná piva");
            }
            else {
                //projíždí všechny hodnoty z enumu hráč/fanoušek
                for (int i = 0; i < HracEnum.values().length; i++) {
                    //první záznam je celkový
                    if (i == 0) {
                        listCelkovychPiv.add("Celkově " + HracEnum.zaradHraceDleComba(i).getMnozneCisloTextu() + " vypili " + spocitejSezonniVelkaPiva(Sezona.zaradSezonuDleComba(poziceSpinneru), HracEnum.Vse) + " velkých a "
                                + spocitejSezonniMalaPiva(Sezona.zaradSezonuDleComba(poziceSpinneru), HracEnum.Vse) + " malých piv za sezonu " + Sezona.zaradSezonuDleComba(poziceSpinneru));
                    }
                    //následuje podle enumu hráč/fanoušek
                    else {
                        //podmínka která se ptá jestli se vypilo aspoň jedno pivo
                        if (spocitejSezonniVelkaPiva(Sezona.zaradSezonuDleComba(poziceSpinneru), HracEnum.zaradHraceDleComba(i)) > 0 || (spocitejSezonniMalaPiva(Sezona.zaradSezonuDleComba(poziceSpinneru), HracEnum.zaradHraceDleComba(i)) > 0)) {

                            listCelkovychPiv.add("Celkově " + HracEnum.zaradHraceDleComba(i).getMnozneCisloTextu() + " vypili " + spocitejSezonniVelkaPiva(Sezona.zaradSezonuDleComba(poziceSpinneru), HracEnum.zaradHraceDleComba(i)) + " velkých a "
                                    + spocitejSezonniMalaPiva(Sezona.zaradSezonuDleComba(poziceSpinneru), HracEnum.zaradHraceDleComba(i)) + " malých piv za sezonu " + Sezona.zaradSezonuDleComba(poziceSpinneru));
                        }
                        //pokud hráč/fanoušek nic nevypil tak se vypíše že v této sezoně se nic nevypilo
                        else {
                            listCelkovychPiv.add("V sezoně " + Sezona.zaradSezonuDleComba(poziceSpinneru) + " " + HracEnum.zaradHraceDleComba(i).getMnozneCisloTextu() + " nevypili zatím nic");
                        }
                    }
                }
            }
        }
        //pokud je zobrazen ve switchi seznam hráčů
        else {
            //pokud se žádná piva v sezoně nevypila tak nemá cenu projíždět cyklus. Akorát se do listu přidá upozornění že žádný piva se nevypila
            if (spocitejSezonniVelkaPiva(Sezona.Vse, HracEnum.zaradHraceDleComba(poziceSpinneru)) == 0 && spocitejSezonniMalaPiva(Sezona.Vse, HracEnum.zaradHraceDleComba(poziceSpinneru)) == 0) {
                listCelkovychPiv.add("V sezoně " + Sezona.zaradSezonuDleComba(poziceSpinneru) + " se zatím nevypila žádná piva");
            }
            else {
                //projedou se všechny pozice spinneru pro sezony
                for (int i = 0; i < Sezona.values().length; i++) {
                    //pomocí statické metody z enumu HracEnum se dopočítává jestli je spinner na jaké je spinner pozici
                    //na pozici 0 se VŽDY dopočítávají celková piva za všechny sezony
                    if (i == 0) {
                        listCelkovychPiv.add("Celkově " + HracEnum.zaradHraceDleComba(poziceSpinneru).getMnozneCisloTextu() + "  vypili " + spocitejSezonniVelkaPiva(Sezona.Vse, HracEnum.zaradHraceDleComba(poziceSpinneru)) + " velkých a "
                                + spocitejSezonniMalaPiva(Sezona.Vse, HracEnum.zaradHraceDleComba(poziceSpinneru)) + " malých piv za všechny sezony");
                    }
                    //zde se dopočítavaj celková piva za danou sezonu dle for cyklu. Pokud je v dané sezoně počet piv 0 řádek se vynechává
                    else {
                        //podmínka která se ptá jestli se vypilo aspoň jedno pivo
                        if (spocitejSezonniVelkaPiva(Sezona.zaradSezonuDleComba(i), HracEnum.zaradHraceDleComba(poziceSpinneru)) > 0 || (spocitejSezonniMalaPiva(Sezona.zaradSezonuDleComba(i), HracEnum.zaradHraceDleComba(poziceSpinneru)) > 0)) {
                            listCelkovychPiv.add("Za sezonu " + Sezona.zaradSezonuDleComba(i) + " se vypilo " + spocitejSezonniVelkaPiva(Sezona.zaradSezonuDleComba(i), HracEnum.zaradHraceDleComba(poziceSpinneru))
                                    + " velkých a " + spocitejSezonniMalaPiva(Sezona.zaradSezonuDleComba(i), HracEnum.zaradHraceDleComba(poziceSpinneru)) + " malých piv.");
                        }
                    }
                }
            }
        }

        return listCelkovychPiv;
    }

    private void zobrazCelkovySeznamDialog() {
        final AlertDialog.Builder statsDialog = new AlertDialog.Builder(getActivity());

        //definování tlačítek a layoutu v dialogu pro zobrazení statistik
        View statsView = getLayoutInflater().inflate(R.layout.dialog_statistiky_celkove, null);
        lv_statsCelkem = (ListView) statsView.findViewById(R.id.lv_statsCelkem);
        tv_statsCelkem = (TextView) statsView.findViewById(R.id.tv_statsCelkem);
        celkemStatsArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, vratListCelkovychVypitychPiv());
        lv_statsCelkem.setAdapter(celkemStatsArrayAdapter);

        statsDialog.setView(statsView);
        final AlertDialog dialog = statsDialog.create();
        dialog.show();
    }

}


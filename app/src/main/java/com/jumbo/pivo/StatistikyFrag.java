
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.pivo.komparator.SeradHracePrvni;
import com.jumbo.pivo.komparator.SeradPodlePiv;

import java.util.ArrayList;
import java.util.List;

public class StatistikyFrag extends Fragment {

    private Button btn_hledej, btn_seradit_dialog_lv, btn_ok, btn_celkoveStats, btn_seradit_lv;
    private Switch sw_hracZapas;
    private Spinner sp_vyberHraceSezony, sp_podrobnyVyber;
    private TextView tv_nadpis, tv_statsCelkem;
    private ListView lv_seznam, lv_seznamVyber, lv_statsCelkem;
    private EditText et_hledej;
    //switch a globální proměnný podle kterejch se pozná jestli se má volat metoda s listview se řazenim nebo ne
    private boolean swZobrazeniZapasu, serazeniZakladnihoLV = false, serazeniDialogovehoLV = false;

    private ArrayAdapter spinnerArrayAdapter;
    private ArrayAdapter spinnerDialogArrayAdapter;
    private ArrayAdapter listViewArrayAdapter;
    private ArrayAdapter celkemStatsArrayAdapter;
    private ArrayAdapter detailListViewAdapter;

    private Polozka oznacenaPolozka;
    private int poziceSpinneru = 0;
    private int poziceSpinneruVDialogu = 0;
    //Poslední slovo zadané do hledej. Používá se seřazení již vyfiltrovaného seznamu
    private String hledaneSlovo;
    //zda se má řadit seznam komplet či filtrovaný
    private boolean bylNaposledFiltr;

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
                zobrazListView(serazeniZakladnihoLV);
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
                zobrazListView(serazeniZakladnihoLV);
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
        btn_hledej = view.findViewById(R.id.btn_hledej);
        sw_hracZapas = view.findViewById(R.id.sw_hracZapas);
        sp_vyberHraceSezony = view.findViewById(R.id.sp_vyberHraceSezony);
        btn_celkoveStats = view.findViewById(R.id.btn_celkoveStats);
        lv_seznam = view.findViewById(R.id.lv_seznam);
        et_hledej = view.findViewById(R.id.et_hledej);
        btn_seradit_lv = view.findViewById(R.id.btn_seradit_lv);


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
                    //měníme na false, aby to při změně obrazovky vždy bylo neseřažený
                    serazeniZakladnihoLV = false;
                    zobrazListView(serazeniZakladnihoLV);
                }
                else {
                    setSwZobrazeniZapasu(true);
                    sw_hracZapas.setText(R.string.zobrazeni_hracu);
                    nastavZobrazeniSpinneru(true);
                    //měníme na false, aby to při změně obrazovky vždy bylo neseřažený
                    serazeniZakladnihoLV = false;
                    zobrazListView(serazeniZakladnihoLV);
                }
            }
        });
        //tlačítko celkově
        btn_celkoveStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zobrazCelkovySeznamDialog();
            }
        });
        nastavZobrazeniSpinneru(swZobrazeniZapasu);
        nastavKlikNaListview();
        //tlačítko seřadit
        btn_seradit_lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serazeniZakladnihoLV) {
                    serazeniZakladnihoLV = false;
                }
                else {
                    serazeniZakladnihoLV = true;
                }
                //pokud byl naposled filtr voláme zobrazení listview s naposled hledaným slovem
                if (bylNaposledFiltr) {
                    zobrazFiltrovaneListView(serazeniZakladnihoLV, hledaneSlovo);
                }
                else {
                    zobrazListView(serazeniZakladnihoLV);
                }
            }
        });
        //tlačítko hledej. Poslední hledané slovo se ukládá do paměti
        btn_hledej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hledaneSlovo = et_hledej.getText().toString();
                zobrazFiltrovaneListView(serazeniZakladnihoLV, hledaneSlovo);
            }
        });

        return view;
    }

    //podle toho jestli je swichtnuto na zobrazen zápasů nebo hráčů to nastavuje spinner se sezonama nebo hráčema/fanouškama
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
                zobrazListView(serazeniZakladnihoLV);
                Log.d(TAG, "pozice spinneru " + position + " nastavuji jako " + poziceSpinneru);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                poziceSpinneru = 0;
                zobrazListView(serazeniZakladnihoLV);
                Log.d(TAG, "pozice spinneru neni zadna, nastavuji jako " + poziceSpinneru);
            }
        });

    }



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
                    //pomocí statické metody z enumu HracEnum se dopočítává na jaké je spinner pozici
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
    //metoda která zajistí zobrazení propojení backendu s frontendem/ xml s kódem
    private void zobrazCelkovySeznamDialog() {
        final AlertDialog.Builder statsDialog = new AlertDialog.Builder(getActivity());

        //definování tlačítek a layoutu v dialogu pro zobrazení statistik
        View statsView = getLayoutInflater().inflate(R.layout.dialog_statistiky_celkove, null);
        lv_statsCelkem = statsView.findViewById(R.id.lv_statsCelkem);
        tv_statsCelkem = statsView.findViewById(R.id.tv_statsCelkem);
        if (swZobrazeniZapasu) {
            tv_statsCelkem.setText(R.string.tv_statistiky_celkove_ucastnici);
        }
        else {
            tv_statsCelkem.setText(R.string.tv_statistiky_celkove_sezony);
        }
        celkemStatsArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, vratListCelkovychVypitychPiv());
        lv_statsCelkem.setAdapter(celkemStatsArrayAdapter);

        statsDialog.setView(statsView);
        final AlertDialog dialog = statsDialog.create();
        dialog.show();
    }

    //metoda co propojuje listview z xml s kodem pomocí vlastní metody
    //zároveň jde zde nastaveno co se má v listview zobrazovat - zbytečné to rozdělovat na 2 metody
    //pokud se má list seřadit, volá se s parametrem true
    private void zobrazListView(boolean seradit) {
        Log.d(TAG, "Zobrazuji základní listview...");
        List<Polozka> seznamProListView = new ArrayList<>();
        try {
            int seznamZapasuSize = seznamZapasu.size();

        //pokud je zobrazen ve switchi zápas
        if (swZobrazeniZapasu) {
            //hodíme stranou velikost zápasů aby se to ve for cyklu nepočítalo pořád dokola
            Sezona sezona = Sezona.zaradSezonuDleComba(poziceSpinneru);
            //projíždíme for cyklem všechny zápasy
            for (int i = 0; i < seznamZapasuSize; i++) {
                //pokud je spinner na pozici všechny zápasy či na pozici sezony které odpovídá zápas v databázi tak to přidáme do listview
                if (sezona == Sezona.Vse || sezona == seznamZapasu.get(i).getSezona()) {
                    seznamZapasu.get(i).setZobrazeniPolozky(ZobrazeniPolozky.Pivni);
                    seznamProListView.add(seznamZapasu.get(i));
                }
            }
        }
        else {
            int seznamHracuSize = seznamHracu.size();
            HracEnum hracEnum = HracEnum.zaradHraceDleComba(poziceSpinneru);
            //projíždíme seznam hráčů
            for (int i = 0; i < seznamHracuSize; i++) {
                //vyfiltrujem dle podmínek
                if (hracEnum == HracEnum.Vse || hracEnum.isFanousek() == seznamHracu.get(i).isFanousek()) {
                    //voláme metodu pro zjištění celkového počtu piv
                    seznamHracu.get(i).setZobrazeniPolozky(ZobrazeniPolozky.Pivni);
                    seznamProListView.add(seznamHracu.get(i));
                }
            }
        }
        //řadíme pokud musíme
        if (seradit) {
            seznamProListView.sort(new SeradPodlePiv());
        }
        listViewArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, seznamProListView);
        lv_seznam.setAdapter(listViewArrayAdapter);
        bylNaposledFiltr = false;
        }
        catch (Exception e) {
        }
    }

    private void zobrazFiltrovaneListView(boolean seradit, String filtr) {
        Log.d(TAG, "Zobrazuji základní listview...");
        List<Polozka> seznamProListView = new ArrayList<>();
        try {
            int seznamZapasuSize = seznamZapasu.size();

            //pokud je zobrazen ve switchi zápas
            if (swZobrazeniZapasu) {
                //hodíme stranou velikost zápasů aby se to ve for cyklu nepočítalo pořád dokola
                Sezona sezona = Sezona.zaradSezonuDleComba(poziceSpinneru);
                //projíždíme for cyklem všechny zápasy
                for (int i = 0; i < seznamZapasuSize; i++) {
                    //pokud je spinner na pozici všechny zápasy či na pozici sezony které odpovídá zápas v databázi tak to přidáme do listview
                    if ((sezona == Sezona.Vse || sezona == seznamZapasu.get(i).getSezona()) && seznamZapasu.get(i).toString().toLowerCase().contains(filtr.toLowerCase().trim())) {
                        seznamZapasu.get(i).setZobrazeniPolozky(ZobrazeniPolozky.Pivni);
                        seznamProListView.add(seznamZapasu.get(i));
                    }
                }
            }
            else {
                int seznamHracuSize = seznamHracu.size();
                HracEnum hracEnum = HracEnum.zaradHraceDleComba(poziceSpinneru);
                //projíždíme seznam hráčů
                for (int i = 0; i < seznamHracuSize; i++) {
                    //vyfiltrujem dle podmínek
                    if ((hracEnum == HracEnum.Vse || hracEnum.isFanousek() == seznamHracu.get(i).isFanousek()) && seznamHracu.get(i).toString().toLowerCase().contains(filtr.toLowerCase().trim())) {
                        //voláme metodu pro zjištění celkového počtu piv
                        seznamHracu.get(i).setZobrazeniPolozky(ZobrazeniPolozky.Pivni);
                        seznamProListView.add(seznamHracu.get(i));
                    }
                }
            }
            if (seradit) {
                seznamProListView.sort(new SeradPodlePiv());
            }
            listViewArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, seznamProListView);
            lv_seznam.setAdapter(listViewArrayAdapter);
            bylNaposledFiltr = true;
        }
        catch (Exception e) {
        }
    }
    // nastavuje dialog pro kliknutí na položku v hlavním listview
    private void nastavKlikNaListview() {
        lv_seznam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //měníme na false, aby primárně nebyly položky seřazené
                serazeniDialogovehoLV = false;

                oznacenaPolozka = (Polozka) parent.getItemAtPosition(position);

                Log.d(TAG, "zobrazuji dialog pro " + oznacenaPolozka);
                //definování dialogového okna pro zobrazení statistik
                AlertDialog.Builder lvDialog = new AlertDialog.Builder(getActivity());

                //definování tlačítek a layoutu v dialogu pro zobrazení statistik
                View lvView = getLayoutInflater().inflate(R.layout.dialog_vyber_statistiky, null);
                btn_seradit_dialog_lv = lvView.findViewById(R.id.btn_seradit);
                btn_ok = lvView.findViewById(R.id.btn_ok);
                lv_seznamVyber =  lvView.findViewById(R.id.lv_seznamVyber);
                sp_podrobnyVyber = lvView.findViewById(R.id.sp_podrobnyVyber);
                tv_nadpis = lvView.findViewById(R.id.tv_nadpis);

                //nastavení nadpisu dialogu. Rozdílné podle polohy switche
                if (oznacenaPolozka instanceof Zapas) {
                    tv_nadpis.setText("Statistiky se soupeřem " + ((Zapas) oznacenaPolozka).getSouper());
                }
                else if (oznacenaPolozka instanceof Hrac){
                    tv_nadpis.setText("Statistiky pro hráče " + ((Hrac) oznacenaPolozka).getJmeno());
                }
                lvDialog.setView(lvView);
                final AlertDialog dialog = lvDialog.create();
                dialog.show();

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //spárování spinneru u dialogu a arraylistu pro zobrazení ze stringu. Je rozdílný podle polohy switche
                if (!swZobrazeniZapasu) {
                    spinnerDialogArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sezony));
                }
                else {
                    spinnerDialogArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hraci));

                }
                spinnerDialogArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_podrobnyVyber.setAdapter(spinnerDialogArrayAdapter);

                //zobrazení druhého list view podle polohy na spinneru
                sp_podrobnyVyber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        zobrazListViewDialogu(position, serazeniDialogovehoLV);
                        poziceSpinneruVDialogu = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                //seřazení dialogového listView
                btn_seradit_dialog_lv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (serazeniDialogovehoLV) {
                            serazeniDialogovehoLV = false;
                        }
                        else {
                            serazeniDialogovehoLV = true;
                        }
                        zobrazListViewDialogu(poziceSpinneruVDialogu, serazeniDialogovehoLV);
                    }
                });


            }
        });
    }

    private void zobrazListViewDialogu (int pozice, boolean seradit) {
        List<String> listPolozekNaListView = new ArrayList<>();
        List<Polozka> listPolozekProZpracovani = new ArrayList<>();

        int celkemMalych = 0;
        int celkemVelkych = 0;
        //pokud je zobrazen ve switchi zápas
        if (swZobrazeniZapasu) {
            //hodíme stranou velikost zápasů aby se to ve for cyklu nepočítalo pořád dokola
            List<Hrac> seznamHracuDialog = ((Zapas) oznacenaPolozka).getSeznamHracu();
            int seznamHracuSize = seznamHracuDialog.size();
            HracEnum hracEnum = HracEnum.zaradHraceDleComba(pozice);
            //projíždíme for cyklem všechny zápasy
            for (int i = 0; i < seznamHracuSize; i++) {
                //pokud je spinner na pozici všechny zápasy či na pozici sezony které odpovídá zápas v databázi tak to přidáme do listview
                if (hracEnum == HracEnum.Vse || hracEnum.isFanousek() == seznamHracuDialog.get(i).isFanousek()) {
                    //napočítáváme počet piv do celkem koše
                    celkemVelkych += seznamHracuDialog.get(i).getPocetPiv().getPocetVelkych();
                    celkemMalych += seznamHracuDialog.get(i).getPocetPiv().getPocetMalych();
                    seznamHracuDialog.get(i).setZobrazeniPolozky(ZobrazeniPolozky.Pivni);
                    listPolozekProZpracovani.add(seznamHracuDialog.get(i));
                }
            }

            //pokud je počet piv větší než nula přidáme položku celkem jako první do listu na zobrazení v listview
            if (celkemVelkych > 0 || celkemMalych > 0) {
                listPolozekNaListView.add(0,"");
                listPolozekNaListView.add(0, "Celkem se v zápase vypilo " + celkemVelkych + " velkých a " + celkemMalych + " malých piv");
                //pokud se zavolá metoda s parametrem seřazení, tak se nejdřív seznam zápasů seřadí podle počtu vypitejch pivek
                if (seradit) {
                    listPolozekProZpracovani.sort(new SeradPodlePiv());
                }
            }
            //pokud není žádné pivo vypité tak se vrátí vyhubování
            else {
                listPolozekNaListView.add(0,"Zobrazuji seznam hanby:");
                listPolozekNaListView.add(0,"V tomto zápase se žádné pivo nevypilo!!??");
                //pokud chce uživatel seřadit seznam bez piv, tak se o to ani nemá smysl pokoušet, pouze dostane odpověď, že je na hlavu
                if (seradit) {
                    Toast.makeText(getActivity(), "Co chceš řadit? Nikdo žádný pivo nevypil vole!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {

            Hrac oznacenyHrac = ((Hrac) oznacenaPolozka);
            int seznamZapasuSize = seznamZapasu.size();
            Sezona sezona = Sezona.zaradSezonuDleComba(pozice);
            //projedeme loopem seznam zápasů
            for (int i = 0; i < seznamZapasuSize; i++) {
                //zápas zpracujeme pouze pokud je spinner nastaven na zobrazit vše či na vybranou sezonu
                Log.d(TAG, "projíždím zápas ze seznamu zápasů " + seznamZapasu.get(i) + " který má seznam hráčů " + seznamZapasu.get(i).getSeznamHracu());
                if (sezona == Sezona.Vse || sezona == seznamZapasu.get(i).getSezona()) {
                    int seznamHracuSize = seznamZapasu.get(i).getSeznamHracu().size();
                    //zde projíždíme loopem v konkrétním zápase seznam hráčů
                    for (int j = 0; j < seznamHracuSize; j++) {
                        Log.d(TAG, "projíždím zápas ze seznamu zápasů " + seznamZapasu.get(i) + " a hráče ze seznamu hráčů " + seznamZapasu.get(i).getSeznamHracu().get(j));
                        //porovnáváme podle přepsané metody equals jestli souhlasí hráč s označeneným hráčem. Pokud je nalezen, breaknem to, víc nepotřebujem
                        if (oznacenyHrac.equals(seznamZapasu.get(i).getSeznamHracu().get(j))) {
                            //voláme metodu která určí zobrazování zápasu jenom s pivama tohoto hráče
                            seznamZapasu.get(i).nastavPivniZobrazeniProJednohoHrace(seznamZapasu.get(i).getSeznamHracu().get(j));
                            listPolozekProZpracovani.add(seznamZapasu.get(i));
                            celkemVelkych += seznamZapasu.get(i).getSeznamHracu().get(j).getPocetPiv().getPocetVelkych();
                            celkemMalych += seznamZapasu.get(i).getSeznamHracu().get(j).getPocetPiv().getPocetMalych();
                            break;
                        }
                    }
                }
            }
            //pokud je počet piv větší než nula přidáme položku celkem jako první do listu na zobrazení v listview
            if (celkemVelkych > 0 || celkemMalych > 0) {
                listPolozekNaListView.add(0,"");
                listPolozekNaListView.add(0, "Celkem hráč vypil " + celkemVelkych + " velkých a " + celkemMalych + " malých piv");
                //pokud se zavolá metoda s parametrem seřazení, tak se seznam hráčů seřadí podle počtu vypitejch pivek
                if (seradit) {
                    listPolozekProZpracovani.sort(new SeradPodlePiv());
                }
            }
            //pokud není žádné pivo vypité tak se vrátí vyhubování
            else {
                listPolozekNaListView.add(0,"");
                listPolozekNaListView.add(0,"Tento hráč zatím žádné pivo nevypil!!??");
                //pokud chce uživatel seřadit seznam bez piv, tak se o to ani nemá smysl pokoušet, pouze dostane odpověď, že je na hlavu
                if (seradit) {
                    Toast.makeText(getActivity(), "Co chceš řadit? Nikdo žádný pivo nevypil vole!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        //Na položky se zavolá ve for loopu metoda to string a přidá se do listu stringů. Je to tu jenom kvůli tomu řazení, jinak by to moh bejt rovnou list stringů
        for (Polozka polozka : listPolozekProZpracovani) {
            listPolozekNaListView.add(polozka.toString());
        }
        detailListViewAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listPolozekNaListView);
        lv_seznamVyber.setAdapter(detailListViewAdapter);
    }

}


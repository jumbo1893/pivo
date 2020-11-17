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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;


public class PridatPivoFrag extends Fragment{

    private ListView lv_seznamZapasu, lv_vyberHrace;
    private Spinner sp_sezony;
    private EditText et_pocetPiv, et_pocetPivMaly;
    private ImageView im_plus, im_minus, im_plusMaly, im_minusMaly;
    private Button btn_ok, btn_zrusit;
    private TextView tv_jmenoHrace;

    private ArrayAdapter zapasArrayAdapter;
    private ArrayAdapter sezonyArrayAdapter;
    private ArrayAdapter vyberHraceArrayAdapter;
    private ValidacePoli validace = new ValidacePoli();
    private Zapas oznacenyZapas;
    private Hrac oznacenyHrac;

    private int zobrazenaSezona;
    private int idHrace;
    private int vyrovnaniMalychPiv;
    private int vyrovnaniVelkychPiv;

    private List<Zapas> seznamZapasu;
    private List<Hrac> seznamHracu;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private FirestoreAdapter firestoreAdapter = new FirestoreAdapter();

    private static final String TAG = PridatPivoFrag.class.toString();

    @Override
    public void onStart() {
        super.onStart();
        //kontrola jestli se něco změnilo na serveru ve firestore. Pokud ano vykoná se akce
        collectionReference = db.collection("zapas");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                }
                Log.d(TAG, "Automaticky načten seznam zápasů po změně " + seznamZapasu);
                zobrazListViewDleSpinneru(seznamZapasu);
                if (oznacenyHrac != null) {
                    Log.d(TAG, "Označený hráč " + oznacenyHrac + " není null, volám metodu na aktualizování počtu piv");
                    aktualizujPocetPivUHrace(oznacenyHrac);
                }
                else {
                    Log.d(TAG, "Označený hráč je null, metoda na aktualizaci nebude volána");
                }
            }

        });

        collectionReference = db.collection("hrac");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    seznamHracu.add(hrac);

                }
                Log.d(TAG, "Automaticky načten seznam hráčů po změně " + seznamHracu);
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
        View view = inflater.inflate(R.layout.fragment_pridat_pivo, container, false);
        lv_seznamZapasu = view.findViewById(R.id.lv_seznamZapasu);
        sp_sezony = view.findViewById(R.id.sp_sezony);

        sezonyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sezony));
        sezonyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_sezony.setAdapter(sezonyArrayAdapter);




        //Spinner pro výběr sezony
        sp_sezony.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d(TAG, "spinner sezony posunut na pozici " + position);
                setZobrazenaSezona(position);
                zobrazListViewDleSpinneru(seznamZapasu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //
            }

        });

        //nastavení listview na úvodní obrazovce, zobrazení dialogu atd
        lv_seznamZapasu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //vytvoření instance Zápasu, který nám řekne id zápasu pro spárování s pivem a následné uložení do lokální proměnné
                oznacenyZapas = (Zapas) parent.getItemAtPosition(position);
                Log.d(TAG, "kliknuto na zápas " + oznacenyZapas);

                final AlertDialog.Builder vyberHraceDialog = new AlertDialog.Builder(getActivity());
                final View vyberHraceView = getLayoutInflater().inflate(R.layout.dialog_pivo_vyber_hrace, null);
                lv_vyberHrace = (ListView) vyberHraceView.findViewById(R.id.lv_vyberHrace);
                vyberHraceArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, oznacenyZapas.getSeznamHracu());
                lv_vyberHrace.setAdapter(vyberHraceArrayAdapter);
                vyberHraceDialog.setView(vyberHraceView);
                final AlertDialog vyberHrace = vyberHraceDialog.create();
                vyberHrace.show();

                //nastavení dialogu po kliknutí na jméno hráče, pro výběr piva
                lv_vyberHrace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //vytvoření instance Hracmodelu, který nám řekne id hráče pro spárování s pivem a následné uložení do lokální proměnné
                        oznacenyHrac = (Hrac) parent.getItemAtPosition(position);

                        Log.d(TAG, "kliknuto na hráče " + oznacenyHrac);


                        final AlertDialog.Builder pocetPivDialog = new AlertDialog.Builder(getActivity());
                        View pocetPivView = getLayoutInflater().inflate(R.layout.dialog_pridani_piva, null);
                        btn_ok = (Button) pocetPivView.findViewById(R.id.btn_zavrit);
                        btn_zrusit = (Button) pocetPivView.findViewById(R.id.btn_ok);
                        im_plus = (ImageView) pocetPivView.findViewById(R.id.im_plus);
                        im_minus = (ImageView) pocetPivView.findViewById(R.id.im_minus);
                        et_pocetPiv = (EditText) pocetPivView.findViewById(R.id.et_pocetPiv);
                        im_plusMaly = (ImageView) pocetPivView.findViewById(R.id.im_plusMaly);
                        im_minusMaly = (ImageView) pocetPivView.findViewById(R.id.im_minusMaly);
                        et_pocetPivMaly = (EditText) pocetPivView.findViewById(R.id.et_pocetPivMaly);

                        tv_jmenoHrace = (TextView) pocetPivView.findViewById(R.id.tv_jmenoHrace);

                        pocetPivDialog.setView(pocetPivView);
                        pocetPivDialog.setTitle("Upravit počet vypitých piv");
                        final AlertDialog pocetPiv = pocetPivDialog.create();

                        tv_jmenoHrace.setText(oznacenyHrac.getJmeno());

                        //hledáme zda již existuje záznam v databázi. Pro zobrazení edittextu
                            et_pocetPiv.setText(String.valueOf(oznacenyHrac.getPocetPiv().getPocetVelkych()));
                            et_pocetPivMaly.setText(String.valueOf(oznacenyHrac.getPocetPiv().getPocetMalych()));
                            vyrovnaniMalychPiv = oznacenyHrac.getPocetPiv().getPocetMalych();
                            vyrovnaniVelkychPiv = oznacenyHrac.getPocetPiv().getPocetVelkych();
                            Log.d(TAG, oznacenyHrac + " má záznam v pivech u zápasu " + oznacenyZapas + ". nastavuji počet piv: " + oznacenyHrac.getPocetPiv());

                        //tlačítko plus
                        im_plus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                oznacenyHrac.getPocetPiv().pridejVelkyPivo();
                                et_pocetPiv.setText(String.valueOf(oznacenyHrac.getPocetPiv().getPocetVelkych()));
                            }
                        });

                        //tlačítko minus
                        im_minus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                oznacenyHrac.getPocetPiv().odeberVelkyPivo();
                                if (oznacenyHrac.getPocetPiv().getPocetVelkych() < 0) {
                                    oznacenyHrac.getPocetPiv().setPocetVelkych(0);
                                }
                                et_pocetPiv.setText(String.valueOf(oznacenyHrac.getPocetPiv().getPocetVelkych()));
                            }
                        });

                        //tlačítko plus
                        im_plusMaly.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                oznacenyHrac.getPocetPiv().pridejMalyPivo();
                                et_pocetPivMaly.setText(String.valueOf(oznacenyHrac.getPocetPiv().getPocetMalych()));
                            }
                        });

                        //tlačítko minus
                        im_minusMaly.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                oznacenyHrac.getPocetPiv().odeberMalyPivo();
                                if (oznacenyHrac.getPocetPiv().getPocetMalych() < 0) {
                                    oznacenyHrac.getPocetPiv().setPocetMalych(0);
                                }
                                et_pocetPivMaly.setText(String.valueOf(oznacenyHrac.getPocetPiv().getPocetMalych()));
                            }
                        });

                        //tlačítko zrušit
                        btn_zrusit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                oznacenyHrac.getPocetPiv().setPocetVelkych(vyrovnaniVelkychPiv);
                                oznacenyHrac.getPocetPiv().setPocetMalych(vyrovnaniMalychPiv);
                                pocetPiv.dismiss();
                                Log.d(TAG, "Kliknuto na tlačítko zrušit. Vracím hodnoty piva na původní. Malá piva: " + vyrovnaniMalychPiv + " velká piva: "+ vyrovnaniVelkychPiv);
                            }
                        });

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "Kliknuto na OK");
                                if (zvalidujPocetPiv(et_pocetPiv) || zvalidujPocetPiv(et_pocetPivMaly)) {
                                    Log.d(TAG, "Potvrzeni počtu piv neprošlo validací");
                                }
                                else
                                {

                                    try {
                                        firestoreAdapter.pridatDoDatabaze(oznacenyZapas);
                                        Toast.makeText(getActivity(), oznacenyHrac.getJmeno() + " v zápase " + oznacenyZapas + " vypil " + oznacenyHrac.getPocetPiv(), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, oznacenyHrac.getJmeno() + " v zápase " + oznacenyZapas + " vypil " + oznacenyHrac.getPocetPiv());
                                        pocetPiv.dismiss();

                                    } catch (Exception e) {
                                        Toast.makeText(getActivity(), "Něco se posralo při zadávání, napiš to pořádně", Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "Chyba při ukládání počtu vypitejch piv");
                                    }


                                }
                            }
                        });
                        pocetPiv.show();

                    }
                });
            }
        });

        return view;
    }

    private boolean zvalidujPocetPiv (EditText et_pocetPiv) {
        Log.d(TAG, "Validuje se počet piv " + et_pocetPiv.getText().toString());
        if (validace.zvalidujPrazdnePole(et_pocetPiv)) {
            Toast.makeText(getActivity(), "Není vyplněn počet piv!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Pole " + et_pocetPiv.getText().toString() + " jméno zápasu je prázdné!");
            return true;
        }
        else if (validace.zvalidujPocetPiv(et_pocetPiv)) {
            Toast.makeText(getActivity(), "Zadal jsi počet piv ve špatném formátu! Povoleny jsou jenom číslice a maximální počet je 99 piv", Toast.LENGTH_LONG).show();
            Log.d(TAG, et_pocetPiv.getText().toString() + " je ve špatném formátu");
            return true;
        }
        else {
            Log.d(TAG, "Validace počtu piv proběhla v pořádku");
            return false;
        }
    }


    /**
     * zobrazí listviewdle spinneru i s volbou zobrazit vše
     */
    private void zobrazListViewDleSpinneru(List<Zapas> seznamZapasu) {
        if (zobrazenaSezona <= 0) {
            zapasArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, seznamZapasu);
            Log.d(TAG, "zobrazuji kompletní seznam zápasů " + seznamZapasu);
        }
        else {
            List<Zapas> tridenySeznam = new ArrayList<>();
            int size = seznamZapasu.size();
            for (int i=0; i < size; i++) { //prohledává se seznam, který byl poslán jako parametr
                //načte se sezona zápasu a porovnává se sezonou, která by měla být podle spinneru (proměnná zobrazenaSezona)
                if (seznamZapasu.get(i).getSezona() == Sezona.zaradSezonuDleComba(zobrazenaSezona)) {
                    tridenySeznam.add(seznamZapasu.get(i)); //pokud je nalezena shoda, přidá se z třídenýho seznamu zápas
                }
            }
            Log.d(TAG, "zobrazuji tříděný seznam zápasů podle pozice " + zobrazenaSezona + " u spinneru. :" + tridenySeznam);
            zapasArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, tridenySeznam);
        }
        lv_seznamZapasu.setAdapter(zapasArrayAdapter);

    }

    private void setZobrazenaSezona(int zobrazenaSezona) {
        this.zobrazenaSezona = zobrazenaSezona-1;
    }

    private void aktualizujPocetPivUHrace(Hrac hrac) {
        int seznamHracuSize = seznamHracu.size();
        for (int i = 0; i < seznamHracuSize; i++) {
            if (hrac.equals(seznamHracu.get(i))) {
                if (seznamHracu.get(i).aktualizujZeZapasuPocetPiv(seznamZapasu)) {
                    Log.d(TAG, "Měním hráče " + hrac + " v db");
                    firestoreAdapter.pridatDoDatabaze(seznamHracu.get(i));
                    return;
                }
            }
        }
    }

}

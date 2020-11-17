package com.jumbo.pivo.gui.piva;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

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
import com.jumbo.pivo.ValidacePoli;
import com.jumbo.pivo.Zapas;
import com.jumbo.pivo.ZobrazeniPolozky;
import com.jumbo.pivo.komparator.SeradHracePrvni;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PridatZapasFrag extends Fragment {

    //Tlačítka v xml layoutu
    private Button btn_kalendarZapas, btn_zobrazVse, btn_pridat, btn_kalendarUpravaZapasu, btn_upravZapas, btn_smazZapas, btn_ne, btn_ano, btn_vyberHrace, btn_vybratHrace, btn_zrusit, btn_upravitVybraneHrace;
    private EditText et_datumZapasu, et_jmenoSoupere, et_upravJmenoSoupere, et_upravDatumZapasu;
    private Switch sw_domaci, sw_upravaDomaciZapas;
    private ListView lv_seznamZapasu, lv_vyberHrace;
    private Spinner sp_sezony, sp_sezonyDialog;

    private DatePickerDialog datePickerDialog;
    private Calendar c;
    private ArrayAdapter zapasArrayAdapter;
    private ArrayAdapter sezonyArrayAdapter;
    private ArrayAdapter vyberHraceArrayAdapter;
    private ValidacePoli validace = new ValidacePoli();
    private Zapas oznacenyZapas;
    //nutné pomocné proměnné pro seznam hráčů
    private List<Hrac> seznamHracu = new ArrayList<>();
    private List<Hrac> oznaceniHraciList;
    private List<Hrac> oznaceniHraciProNovyZapas = new ArrayList<>();
    private List<Hrac> oznaceniHraciProUpravuZapasu = new ArrayList<>();

    private List<Zapas> seznamZapasu = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private FirestoreAdapter firestoreAdapter = new FirestoreAdapter();

    //private int poziceComboboxuVDialogu = 0;
    private int zobrazenaSezona;

    private static final String TAG = PridatZapasFrag.class.toString();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

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
                seznamZapasu.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Zapas zapas = documentSnapshot.toObject(Zapas.class);
                    seznamZapasu.add(zapas);
                    Log.d(TAG, "Automaticky načten seznam zápasů po změně " + seznamZapasu);
                }
                zobrazListViewDleSpinneru(seznamZapasu);

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
                seznamHracu.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Hrac hrac = documentSnapshot.toObject(Hrac.class);
                    hrac.setZobrazeniPolozky(ZobrazeniPolozky.Zakladni);
                    seznamHracu.add(hrac);
                    Log.d(TAG, "Automaticky načten seznam hráčů po změně " + seznamHracu);
                }
                seznamHracu.sort(new SeradHracePrvni());

            }

        });

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //propojení tlačítek a kódu
        View view = inflater.inflate(R.layout.fragment_pridat_zapas, container, false);
        btn_kalendarZapas = (Button) view.findViewById(R.id.btn_kalendarUpravaZapasu);
        btn_pridat = (Button) view.findViewById(R.id.btn_pridat);
        et_datumZapasu = (EditText) view.findViewById(R.id.et_datumZapasu);
        et_jmenoSoupere = (EditText) view.findViewById(R.id.et_jmenoSoupere);
        sw_domaci = (Switch) view.findViewById(R.id.sw_domaci);
        sp_sezony = (Spinner) view.findViewById(R.id.sp_sezony);
        lv_seznamZapasu = (ListView) view.findViewById(R.id.lv_seznamZapasu);
        btn_vyberHrace = (Button) view.findViewById(R.id.btn_vyberHrace);

        sezonyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sezony));
        sezonyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_sezony.setAdapter(sezonyArrayAdapter);

        //spinner sezon
        sp_sezony.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d(TAG, "spinner sezony posunut na pozici " + position);
                setZobrazenaSezona(position);
                zobrazListViewDleSpinneru(seznamZapasu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //button listener pro kalendář
        btn_kalendarZapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Kliknuto na zobrazit kalendář ");
                zobrazKalendar(et_datumZapasu);
            }
        });

        //button listener pro přidání zápasu
        btn_pridat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Kliknuto na přidat zápas");
                pridatZapas();
            }
        });
        //nastavení základního listView
        lv_seznamZapasu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                oznacenyZapas = (Zapas) parent.getItemAtPosition(position);
                Log.d(TAG, "Kliknuto na zápas " + oznacenyZapas);

                //definování dialogového okna pro úpravu zápasu a smazání zápasu
                final AlertDialog.Builder zapasDialog = new AlertDialog.Builder(getActivity());

                View zapasView = getLayoutInflater().inflate(R.layout.dialog_uprava_zapasu, null);

                et_upravJmenoSoupere = (EditText) zapasView.findViewById(R.id.et_upravJmenoSoupere);
                et_upravDatumZapasu = (EditText) zapasView.findViewById(R.id.et_upravDatumZapasu);
                sw_upravaDomaciZapas = (Switch) zapasView.findViewById(R.id.sw_upravaDomaciZapas);
                btn_upravZapas = (Button) zapasView.findViewById(R.id.btn_upravZapas);
                btn_smazZapas = (Button) zapasView.findViewById(R.id.btn_smazZapas);
                btn_kalendarUpravaZapasu = (Button) zapasView.findViewById(R.id.btn_kalendarUpravaZapasu);
                btn_upravitVybraneHrace = zapasView.findViewById(R.id.btn_upravitVybraneHrace);
                sp_sezonyDialog = zapasView.findViewById(R.id.sp_sezonyDialog);

                et_upravDatumZapasu.setText(Datum.zmenDatumDoFront(oznacenyZapas.getDatum()));
                et_upravJmenoSoupere.setText(oznacenyZapas.getSouper());
                sw_upravaDomaciZapas.setChecked(oznacenyZapas.isDomaciZapas());
                sezonyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sezony_dialog));
                sezonyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_sezonyDialog.setAdapter(sezonyArrayAdapter);

                //vytvoření dialogového okna
                zapasDialog.setView(zapasView);
                final AlertDialog dialog = zapasDialog.create();
                //nacte seznam hracu pro pripad, ze by se seznam hracu neupravoval
                oznaceniHraciProUpravuZapasu = oznacenyZapas.getSeznamHracu();

                //button listener pro combobox se sezonama
                sp_sezonyDialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        //pokud je spinner na pozici 0, to znamená textově neměnit pozici, tak by se nemělo nic stát.
                        // Pokud je na jiné pozici, tak s tím někdo aktivně hýbal a ta sezona už se nastaví
                        Log.d(TAG, "spinner sezony v dialogu posunut na pozici " + position);
                        if (position != 0) {
                            oznacenyZapas.setSezona(Sezona.zaradSezonuDleComba(position));
                        }
                        else {
                            oznacenyZapas.zaradSezonu(et_upravDatumZapasu.getText().toString());
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        //nemělo by nikdy nastat
                    }
                });
                //kliknuti na upravu seznamu hracu v zápase
                btn_upravitVybraneHrace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nastavVybraniHraceDialogUprava();
                    }
                });
                //button listener pro kalendář
                btn_kalendarUpravaZapasu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zobrazKalendar(et_upravDatumZapasu);
                        Log.d(TAG, "Kliknuto na zobrazit kalendář v dialogu ");
                    }
                });

                //tlačítko pro úpravu zápasu
                btn_upravZapas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upravZapas(dialog);
                        Log.d(TAG, "Kliknuto na upravit zápas v dialogu ");
                    }
                });

                //tlačítko pro smazání zápasu
                btn_smazZapas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Kliknuto na smazat zápas " + oznacenyZapas);

                        //definování okna a tlačítek pro potvrzovací dialog
                        final AlertDialog.Builder smazDialog = new AlertDialog.Builder(getActivity());
                        View smazView = getLayoutInflater().inflate(R.layout.dialog_potvrzeni, null);
                        btn_ne = (Button) smazView.findViewById(R.id.btn_ne);
                        btn_ano = (Button) smazView.findViewById(R.id.btn_ano);

                        smazDialog.setView(smazView);
                        smazDialog.setTitle("Smazat zápas");

                        final AlertDialog smazatDialog = smazDialog.create();

                        btn_ne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                smazatDialog.dismiss();
                                Log.d(TAG, "Smazání zápasu vzato zpět");
                            }
                        });

                        btn_ano.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                firestoreAdapter.smazZDatabaze(oznacenyZapas);
                                Toast.makeText(getActivity(), "Smazán zápas " + oznacenyZapas.toString(), Toast.LENGTH_SHORT).show();
                                smazatDialog.dismiss();
                                dialog.dismiss();
                                Log.d(TAG, "Potvrzeno smazání zápasu");
                            }
                        });

                        //zobrazení dialogu pro potvrzení
                        smazatDialog.show();
                    }
                });

                //zobrazení dialogu pro úpravu hráče
                dialog.show();

            }
        });

        //button pro zobrazení list view se seznamem hráčů který hrajou zápas
        btn_vyberHrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nastavVybraniHraceDialog();
            }
        });

        return view;
    }
    //podle vybrané pozice spinneru se zobrazuje zápas. Hodnota spinneru se bere z globální proměnný
    private void zobrazListViewDleSpinneru(List<Zapas> seznamZapasu) {
        if (zobrazenaSezona == 0) {
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
    //nastavení kalendáře
    private void zobrazKalendar (final EditText editText) {

        c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                editText.setText(dayOfMonth + "." + month + "." + year);

            }
        }, year, month, day);
        datePickerDialog.show();
        Log.d(TAG, "zobrazuji kalendář");
    }

    private void setZobrazenaSezona(int zobrazenaSezona) {
        this.zobrazenaSezona = zobrazenaSezona;
    }
    //metoda pro validaci
    private boolean zvalidujJmenoZapasu (EditText et_jmeno) {
        Log.d(TAG, "Validuje se jméno zápasu " + et_jmeno.getText().toString());
        if (validace.zvalidujPrazdnePole(et_jmeno)) {
            Toast.makeText(getActivity(), "Zapomněl jsi vyplnit jméno soupeře!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Pole " + et_jmeno.getText().toString() + " jméno zápasu je prázdné!");
            return true;
        }
        else if (validace.zvalidujJmeno(et_jmeno)) {
            Toast.makeText(getActivity(), "Buď si zadal jméno delší než 100 znaků nebo si použil zakázané znaky. Povoleny jsou pouze alfanumerické znaky, podtržítko a pomlčka", Toast.LENGTH_LONG).show();
            Log.d(TAG, et_jmeno.getText().toString() + " obsahuje zakázané znaky");
            return true;
        }
        else {
            Log.d(TAG, "Validace jména zápasu proběhla v pořádku");
            return false;
        }
    }
    //metoda pro validaci
    private boolean zvalidujDatumZapasu (EditText et_datum) {
        Log.d(TAG, "Validuje se datum zápasu " + et_datum.getText().toString());
        if (validace.zvalidujPrazdnePole(et_datum)) {
            Toast.makeText(getActivity(), "Zapomněl jsi vyplnit datum zápasu!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Pole datum je prázdné!");
            return true;
        }
        else if (validace.zvalidujDatum(et_datum)) {
            Toast.makeText(getActivity(), "Datum zápasu je ve špatném formátu! Použij dd.MM.yyyy", Toast.LENGTH_LONG).show();
            Log.d(TAG, et_datum.getText().toString() + " obsahuje zakázané znaky");
            return true;
        }
        else {
            Log.d(TAG, "Validace datumu zápasu proběhla v pořádku");
            return false;
        }
    }
    //metoda pro přidání zápasu
    private void pridatZapas() {
        Log.d(TAG, "Probíhá přidání zápasu");
        if (zvalidujJmenoZapasu(et_jmenoSoupere) || zvalidujDatumZapasu(et_datumZapasu)) {
            Log.d(TAG, "Přidání zápasu neprošlo validací");
        }
        else if (oznaceniHraciProNovyZapas.size() == 0) {
            Log.d(TAG, "Nový zápas neprošel validací, seznam hráčů ke prázdný");
            Toast.makeText(getActivity(), "zadej aspoň jednoho hráče co si šel čutnout", Toast.LENGTH_LONG).show();
        }
        else {
            //pokud validace prošla bez chyb
            Zapas zapas;
            try {
                //nulujeme hráčům počet piv. Hráči se berou z DB, kde již mají vepsaný celkový počet piv a do jednotlivejch zápasů by měli vstupovat s nulou
                for (int i = 0; i < oznaceniHraciProNovyZapas.size(); i++) {
                    oznaceniHraciProNovyZapas.get(i).getPocetPiv().vynulujPocetPiv();
                }
                zapas = new Zapas(et_jmenoSoupere.getText().toString(), Datum.zmenDatumDoSQL(et_datumZapasu.getText().toString()), sw_domaci.isChecked(), oznaceniHraciProNovyZapas);
                Toast.makeText(getActivity(), zapas.toString(), Toast.LENGTH_LONG).show();
                //zde přidáváme do DB a může se to posrat
                firestoreAdapter.pridatDoDatabaze(zapas);
                Log.d(TAG, "Pridavan zapas " + zapas + oznaceniHraciProNovyZapas );
                //mažeme seznam hráčů aby se dal použít i příště
                oznaceniHraciProNovyZapas.clear();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Něco se posralo při zadávání, napiš to pořádně", Toast.LENGTH_LONG).show();
            }

        }
    }
    //metoda pro úpravu zápasu
    private void upravZapas (AlertDialog dialog) {
        Log.d(TAG, "Probíhá úprava zápasu");
        if (zvalidujJmenoZapasu(et_upravJmenoSoupere) || zvalidujDatumZapasu(et_upravDatumZapasu)) {
            Log.d(TAG, "Úprava zápasu neprošlo validací");
        }
        else {
            try {
                oznacenyZapas.setSouper(et_upravJmenoSoupere.getText().toString());
                oznacenyZapas.setDatum(Datum.zmenDatumDoSQL(et_upravDatumZapasu.getText().toString()));
                oznacenyZapas.setDomaciZapas(sw_upravaDomaciZapas.isChecked());
                oznacenyZapas.setSeznamHracu(oznaceniHraciProUpravuZapasu);
                //upravujeme v DB
                firestoreAdapter.pridatDoDatabaze(oznacenyZapas);
                dialog.dismiss();
                Toast.makeText(getActivity(), "Upraven zápas " + oznacenyZapas.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Upraven zápas " + oznacenyZapas.toString() + " se seznamem hracu " + oznaceniHraciProUpravuZapasu);

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Něco se posralo při zadávání, napiš to pořádně", Toast.LENGTH_LONG).show();
            }
        }
    }
    //nastavení dialogu pro NOVÝ zápas
    private void nastavVybraniHraceDialog() {
        Log.d(TAG, "Kliknuto na zobrazení list view se seznamem hráčů který hrajou zápas " + seznamHracu);
        //duplikujem seznam pro porovnání
        oznaceniHraciList = oznaceniHraciProNovyZapas;
        final AlertDialog.Builder vyberHraceDialog = new AlertDialog.Builder(getActivity());

        View vyberHraceView = getLayoutInflater().inflate(R.layout.dialog_vyber_hrace, null);
        lv_vyberHrace = vyberHraceView.findViewById(R.id.lv_vyberHrace);
        btn_vybratHrace = vyberHraceView.findViewById(R.id.btn_vybratHrace);
        btn_zrusit = vyberHraceView.findViewById(R.id.btn_ok);
        lv_vyberHrace.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        vyberHraceArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, seznamHracu);
        lv_vyberHrace.setAdapter(vyberHraceArrayAdapter);
        //pokud je nějaký hráč zaškrtnut tak ho tak rovnou zobrazíme. Použijeme v případě že uživatel již nějaké hráče zaškrtne, pak změní třeba datum zápasu a ještě se k tomu vrátí
        for (int i = 0; i < seznamHracu.size(); i++) {
            if (oznaceniHraciList.contains(seznamHracu.get(i))) {
                lv_vyberHrace.setItemChecked(i, true);
            }
            else {
                lv_vyberHrace.setItemChecked(i, false);
            }
        }
        vyberHraceDialog.setView(vyberHraceView);
        final AlertDialog vyberHrace = vyberHraceDialog.create();
        vyberHrace.show();
        //pokud klikneme na zaškrtutého tak ho odškrtnem a naopak
        lv_vyberHrace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hrac vybranyHrac = (Hrac) parent.getItemAtPosition(position);
                if (oznaceniHraciList.contains(vybranyHrac)) {
                    oznaceniHraciList.remove(vybranyHrac);
                    Log.d(TAG, "Hráč " + vybranyHrac + " je odškrtnut");
                } else {
                    oznaceniHraciList.add(vybranyHrac);
                    Log.d(TAG, "Hráč " + vybranyHrac + " je zaškrtnut");
                }
            }
        });
        //tlačítkem potvrzujeme hráče. Ještě se neukládají do DB, to až stiskem nového zápasu!!
        btn_vybratHrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oznaceniHraciProNovyZapas = oznaceniHraciList;
                Log.d(TAG, "přidávám hráče " + oznaceniHraciProNovyZapas);
                vyberHrace.dismiss();
            }
        });
        //rušíme výběr. Zaškrtutí hráči nebudou příště zobrazovaný
        btn_zrusit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "žádný hráč nepřidán");
                vyberHrace.dismiss();
            }
        });
    }
    //metoda pro vybrání hráčů v úpravě zápasu
    private void nastavVybraniHraceDialogUprava() {
        boolean obohaceniHrace = false;
        Log.d(TAG, "Kliknuto na zobrazení list view se seznamem hráčů který sou k dispozici " + seznamHracu);
        //berem seznam hráčů rovnou ze zápasu
        oznaceniHraciList = oznacenyZapas.getSeznamHracu();
        final AlertDialog.Builder vyberHraceDialog = new AlertDialog.Builder(getActivity());

        View vyberHraceView = getLayoutInflater().inflate(R.layout.dialog_vyber_hrace, null);
        lv_vyberHrace = vyberHraceView.findViewById(R.id.lv_vyberHrace);
        btn_vybratHrace = vyberHraceView.findViewById(R.id.btn_vybratHrace);
        btn_zrusit = vyberHraceView.findViewById(R.id.btn_ok);
        lv_vyberHrace.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //probiha kontrola, zda seznam hracu u daneho zapasu nema jiz smazaneho hrace
        List<Hrac> novySeznam = new ArrayList<>();
        for (int j = 0; j < oznaceniHraciList.size(); j++) {
            if (!seznamHracu.contains(oznaceniHraciList.get(j))) { //pokud ma, tak se nacte seznam hracu o tohoto hrace obohati s hlaskou
                Log.d(TAG, "Hráč " + oznaceniHraciList.get(j) + " byl drive smazany. Zobrazuji obohaceny seznam o tohoto hrace");
                Toast.makeText(getActivity(), "Hráč " + oznaceniHraciList.get(j) + " byl dříve smazaný, ale je součástí tohoto zápasu. Zobrazuji ho.", Toast.LENGTH_LONG).show();
                novySeznam.addAll(seznamHracu);
                novySeznam.add(oznaceniHraciList.get(j));
                vyberHraceArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, novySeznam);
                //proměnná ci značí že na seznamu je hráč co tam nemá co dělat a musíme použít obohacený seznam
                obohaceniHrace = true;

            }
        }
        lv_vyberHrace.setAdapter(vyberHraceArrayAdapter);
        if (obohaceniHrace) { // používáme obohacený seznam
            Log.d(TAG, "Začínám zaškrtávat hráče na obohaceném seznamu...");
            for (int i = 0; i < novySeznam.size(); i++) {
                for (int j = 0; j < oznaceniHraciList.size(); j++) {
                    //hráči co jsou již na seznamu dostanou zaškrtnutí fajkou true
                    if (oznaceniHraciList.get(j).equals(novySeznam.get(i))) {
                        lv_vyberHrace.setItemChecked(i, true);
                        break;
                        //co nejsou dostanou vynulovaný počet piv
                    } else {
                        novySeznam.get(i).getPocetPiv().vynulujPocetPiv();
                    }
                }
            }
        }
        else { //pokud se nenasel zadny hrac navic oproti klasickemu seznamu, tak nacteme jednoduse seznam hracu
            Log.d(TAG, "seznam hráčů z db odpovídá seznamu hráčů pro tento mač. Začínám zaškrtávat hráče...");
            //používáme nový seznam i zde. Je to důležité, na novém seznamu se nuluje počet piv u nezaškrtnutých hráčů
            novySeznam.clear();
            novySeznam.addAll(seznamHracu);
            vyberHraceArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, novySeznam);
            //projíždíme
            for (int i = 0; i < novySeznam.size(); i++) {
                for (int j = 0; j < oznaceniHraciList.size(); j++) {

                    //hráči co jsou již na seznamu dostanou zaškrtnutí fajkou true
                    if (oznaceniHraciList.get(j).equals(novySeznam.get(i))) {
                        lv_vyberHrace.setItemChecked(i, true);
                        break;
                        //co nejsou dostanou vynulovaný počet piv
                    } else {
                        novySeznam.get(i).getPocetPiv().vynulujPocetPiv();
                    }
                }
            }

        }
        vyberHraceDialog.setView(vyberHraceView);
        final AlertDialog vyberHrace = vyberHraceDialog.create();
        vyberHrace.show();
        lv_vyberHrace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hrac vybranyHrac = (Hrac) parent.getItemAtPosition(position);
                //nastavujeme pro zaškrtnutí
                boolean shodaHrace = false;
                for (int j = 0; j < oznaceniHraciList.size(); j++) {
                    if (oznaceniHraciList.get(j).equals(vybranyHrac)) {
                        //odstranuje se jiz tady, protoze podle instance se neda poznat kdo je kdo
                        oznaceniHraciList.remove(j);
                        shodaHrace = true;
                        break;
                    }

                }
                if (shodaHrace) {
                    Log.d(TAG, "Hráč " + vybranyHrac + " je odškrtnut");
                } else {
                    oznaceniHraciList.add(vybranyHrac);
                    Log.d(TAG, "Hráč " + vybranyHrac + " je zaškrtnut");
                }
            }
        });
        //tlačítkem potvrzujeme v úpravě. Ještě se neukládají do DB, to až stiskem nového zápasu!!
        btn_vybratHrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oznaceniHraciProUpravuZapasu = oznaceniHraciList;
                vyberHrace.dismiss();
            }
        });
        //rušíme výběr. Zaškrtutí hráči nebudou příště zobrazovaný
        btn_zrusit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "žádný hráč nepřidán");
                vyberHrace.dismiss();
            }
        });
    }

}

package com.jumbo.pivo;

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
import com.jumbo.pivo.komparator.SeradHracePrvni;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class PridatHraceFrag extends Fragment {

    //Tlačítka v xml layoutu
    private Button btn_pridat, btn_kalendar, btn_kalendarUpravaHrace, btn_upravHrace, btn_smazHrace, btn_ne, btn_ano;
    private EditText et_jmenoHrace, et_upravDatumNarozenin, et_datumNarozeniHrace, et_upravJmenoHrace;
    private Switch sw_fanousek, sw_upravaFanousek;
    private ListView lv_seznamHracu;
    private Calendar c;
    private DatePickerDialog datePickerDialog;
    private Hrac oznacenyHrac;

    private ArrayAdapter hracArrayAdapter;
    private ValidacePoli validace = new ValidacePoli();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private List<Hrac> seznamHracu = new ArrayList<>();
    private FirestoreAdapter firestoreAdapter = new FirestoreAdapter();

    private static final String TAG = PridatHraceFrag.class.toString();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        //kontrola jestli se něco změnilo na serveru ve firestore. Pokud ano vykoná se akce
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
                    hrac.setZobrazeniPolozky(ZobrazeniPolozky.Detailni);
                    seznamHracu.add(hrac);
                    Log.d(TAG, "Automaticky načten seznam hráčů po změně " + seznamHracu);
                }
                seznamHracu.sort(new SeradHracePrvni());
                zobrazHraceNaListView(seznamHracu);

            }

        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pridat_hrace, container, false);
        btn_pridat = (Button) view.findViewById(R.id.btn_pridat);
        btn_kalendar = (Button) view.findViewById(R.id.btn_kalendar);
        et_jmenoHrace = (EditText) view.findViewById(R.id.et_jmenoHrace);
        //et_datumNarozeniHrace = (EditText) view.findViewById(R.id.et_datumNarozeniHrace);
        sw_fanousek = (Switch) view.findViewById(R.id.sw_fanousek);
        lv_seznamHracu = (ListView) view.findViewById(R.id.lv_seznamHracu);
        et_datumNarozeniHrace = (EditText) view.findViewById(R.id.et_datumNarozeniHrace);



        //instance db adaptéru pro načtení contextu z této stránky

        //zobrazHraceNaListView();

        //button listeners

        //button listener pro kalendář
        btn_kalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Kliknuto na button kalendáře");
                zobrazKalendar(et_datumNarozeniHrace);
            }
        });

        //button listener pro přidání hráče
        btn_pridat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Kliknuto na button přidání hráče");
                pridatHrace();
            }
        });

        //metoda pro kliknutí na jednotlivé položky v listview - jednotlivé hráče
        lv_seznamHracu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                oznacenyHrac = (Hrac) parent.getItemAtPosition(position);
                Log.d(TAG, "Kliknuto na hráče " + oznacenyHrac + " v listview");

                //definování dialogového okna pro úpravu hráče a smazání hráče
                final AlertDialog.Builder hracDialog = new AlertDialog.Builder(getActivity());

                //definování tlačítek a layoutu v dialogu pro úpravu a smazání hráče
                View hracView = getLayoutInflater().inflate(R.layout.dialog_uprava_hrace, null);
                et_upravJmenoHrace = (EditText) hracView.findViewById(R.id.et_upravJmenoHrace);
                et_upravDatumNarozenin = (EditText) hracView.findViewById(R.id.et_upravDatumNarozenin);
                sw_upravaFanousek = (Switch) hracView.findViewById(R.id.sw_upravaFanousek);
                btn_upravHrace = (Button) hracView.findViewById(R.id.btn_upravHrace);
                btn_smazHrace = (Button) hracView.findViewById(R.id.btn_smazHrace);
                btn_kalendarUpravaHrace = (Button) hracView.findViewById(R.id.btn_kalendarUpravaHrace);

                et_upravDatumNarozenin.setText(Datum.zmenDatumDoFront(oznacenyHrac.getDatum()));
                et_upravJmenoHrace.setText(oznacenyHrac.getJmeno());
                sw_upravaFanousek.setChecked(oznacenyHrac.isFanousek());

                //vytvoření dialogového okna
                hracDialog.setView(hracView);
                final AlertDialog dialog = hracDialog.create();

                //button listener pro kalendář
                btn_kalendarUpravaHrace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Kliknuto na button kalendáře v uprav hráče dialogu");

                        zobrazKalendar(et_upravDatumNarozenin);
                    }
                });


                //tlačítko pro úpravu hráče
                btn_upravHrace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Kliknuto na button upravit hráče");
                        upravitHrace(dialog);
                    }
                });

                //tlačítko pro smazání hráče
                btn_smazHrace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Kliknuto na smazat hráče");

                        //definování okna a tlačítek pro potvrzovací dialog
                        final AlertDialog.Builder smazDialog = new AlertDialog.Builder(getActivity());
                        View smazView = getLayoutInflater().inflate(R.layout.dialog_potvrzeni, null);
                        btn_ne = (Button) smazView.findViewById(R.id.btn_ne);
                        btn_ano = (Button) smazView.findViewById(R.id.btn_ano);

                        smazDialog.setView(smazView);
                        smazDialog.setTitle("Smazat hráče");

                        final AlertDialog smazatDialog = smazDialog.create();

                        btn_ne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "Vybrána možnost NE");
                                smazatDialog.dismiss();
                            }
                        });

                        btn_ano.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "Vybrána možnost ANO. Mazán hráč " + oznacenyHrac);

                                firestoreAdapter.smazZDatabaze(oznacenyHrac);
                                //zobrazHraceNaListView();
                                Toast.makeText(getActivity(), "Smazán hráč " + oznacenyHrac.toString(), Toast.LENGTH_SHORT).show();
                                smazatDialog.dismiss();
                                dialog.dismiss();
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

        return view;
    }

    /**
     * @param
     * //zobrazí list hráčů
     */
    private void zobrazHraceNaListView(List<Hrac> seznamHracu) {
        Log.d(TAG, "Zobrazují se hráči na listview");
        hracArrayAdapter = new ArrayAdapter<Hrac>(getActivity(), android.R.layout.simple_list_item_1, seznamHracu);
        lv_seznamHracu.setAdapter(hracArrayAdapter);
    }

    private void zobrazKalendar (final EditText editText) {
        Log.d(TAG, "Zobrazuje se kalendář");
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

    }

    private boolean zvalidujJmenoHrace (EditText et_jmeno) {
        Log.d(TAG, "Validuje se jméno hráče " + et_jmeno.getText().toString());
        if (validace.zvalidujPrazdnePole(et_jmeno)) {
            Toast.makeText(getActivity(), "Zapomněl jsi vyplnit jméno hráče!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Pole jméno je prázdné!");
            return true;
        }
        else if (validace.zvalidujJmeno(et_jmeno)) {
            Toast.makeText(getActivity(), "Buď si zadal jméno delší než 100 znaků nebo si použil zakázané znaky. Povoleny jsou pouze alfanumerické znaky, podtržítko a pomlčka", Toast.LENGTH_LONG).show();
            Log.d(TAG, et_jmeno.getText().toString() + " obsahuje zakázané znaky");
            return true;
        }

        else {
            Log.d(TAG, "Validace jména hráče proběhla v pořádku");
            return false;
        }

    }

    private boolean zvalidujDatumNarozeniHrace (EditText et_datum) {
        Log.d(TAG, "Validuje se datum narození hráče " + et_datum.getText().toString());
        if (validace.zvalidujPrazdnePole(et_datum)) {
            Toast.makeText(getActivity(), "Zapomněl jsi vyplnit datum narození hráče hráče!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Pole datum je prázdné!");
            return true;
        }
        else if (validace.zvalidujDatum(et_datum)) {
            Toast.makeText(getActivity(), "Datum zápasu je ve špatném formátu! Použij dd.MM.yyyy", Toast.LENGTH_LONG).show();
            Log.d(TAG, et_datum.getText().toString() + " obsahuje zakázané znaky");
            return true;
        }

        else {
            Log.d(TAG, "Validace datumu narození hráče proběhla v pořádku");
            return false;
        }

    }

    private void pridatHrace() {
        Log.d(TAG, "Probíhá přidání hráče");
        if (zvalidujJmenoHrace(et_jmenoHrace) || zvalidujDatumNarozeniHrace(et_datumNarozeniHrace)) {

            //Toast.makeText(getActivity(), "Přidání hráče neprošlo validací", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Přidání hráče neprošlo validací");
        }
        else {
            Hrac hrac;
            try {
                //Propojení tlačítek s proměnnými v této třídě.
                hrac = new Hrac(et_jmenoHrace.getText().toString(), Datum.zmenDatumDoSQL(et_datumNarozeniHrace.getText().toString()), sw_fanousek.isChecked());
                Log.d(TAG, "Probíhá přidání hráče " + hrac + " do databáze");
                firestoreAdapter.pridatDoDatabaze(hrac);
                Toast.makeText(getActivity(), " Přidán " + hrac.toString(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Něco se posralo při zadávání, napiš to pořádně", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error při přidávání hráče: ", e);
            }

        }
    }

    private void upravitHrace(AlertDialog dialog) {
        Log.d(TAG, "Probíhá úprava hráče");
        if (zvalidujJmenoHrace(et_upravJmenoHrace) || zvalidujDatumNarozeniHrace(et_upravDatumNarozenin)) {
            Log.d(TAG, "Úprava hráče neprošla validací");
        }
         else {
            try {
                oznacenyHrac.setJmeno(et_upravJmenoHrace.getText().toString());
                oznacenyHrac.setDatum(Datum.zmenDatumDoSQL(et_upravDatumNarozenin.getText().toString()));
                oznacenyHrac.setFanousek(sw_upravaFanousek.isChecked());
                oznacenyHrac.setVek(Datum.urciVek(oznacenyHrac.getDatum()));
                oznacenyHrac.setDniDoNarozenin(Datum.setDniDoNarozenin(oznacenyHrac.getDatum()));
                Log.d(TAG, "Probíhá úprava hráče " + oznacenyHrac + " a následná změna v databázi");
                firestoreAdapter.pridatDoDatabaze(oznacenyHrac);
                dialog.dismiss();
                //zobrazHraceNaListView();
                Toast.makeText(getActivity(), "Upraven hráč " + oznacenyHrac.toString(), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Něco se posralo při zadávání, napiš to pořádně", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error při úpravě hráče: ", e);
            }
        }

    }

}

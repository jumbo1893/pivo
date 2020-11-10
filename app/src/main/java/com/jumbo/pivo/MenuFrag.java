/*
package com.jumbo.pivo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Random;



public class MenuFrag extends Fragment {

    private TextView tv_oslavenec, tv_random, tv_nadpisDialogu, tv_infoAplikace;
    private Button btn_info, btn_zavrit, btn_obnovit, btn_ok;
    private Spinner sp_vybratInfo;

    private ArrayAdapter infoArrayAdapter;

    DBadapter dBadapter;



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
        tv_oslavenec = (TextView) view.findViewById(R.id.tv_oslavenec);
        tv_random = (TextView) view.findViewById(R.id.tv_random);
        btn_info = (Button) view.findViewById(R.id.btn_info);
        btn_obnovit = (Button) view.findViewById(R.id.btn_obnovit);
        btn_zavrit = (Button) view.findViewById(R.id.btn_zavrit);

        dBadapter = new DBadapter(getActivity());


        dBadapter.nactiAktualniNarozeniny();
        tv_oslavenec.setText(dBadapter.najdiOslavence());
        nastavZajimavost();
        btn_obnovit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dBadapter.nactiAktualniNarozeniny();
                tv_oslavenec.setText(dBadapter.najdiOslavence());
                dBadapter.najdiMaxVelkychPivNaHrace();
                nastavZajimavost();
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
    private void nastavZajimavost() {
        Random random = new Random();
        switch (random.nextInt(12)) {
            case 0:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiMaxVelkychPivNaHrace() + "\n\n ");
                break;
            case 1:
                tv_random.setText("Náhodná zajímavost: \n\n" + dBadapter.najdiMaxMalychPivNaHrace() + "\n\n ");
                break;
            case 2:
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
                break;

        }
    }
}
*/

/*
package com.jumbo.pivo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBadapter extends SQLiteOpenHelper {

    public static final String HRACI_TABLE = "HRACI";
    public static final String COLUMN_JMENO_HRACE = "JMENO_HRACE";
    public static final String COLUMN_VEK_HRACE = "VEK_HRACE";
    public static final String COLUMN_FANOUSEK = "FANOUSEK";
    public static final String COLUMN_DATUM_NAROZENI = "DATUM_NAROZENI";
    public static final String COLUMN_DNI_DO_NAROZEK = "DNI_DO_NAROZEK";

    public static final String COLUMN_ID = "ID";

    public static final String ZAPASY_TABLE = "ZAPASY";
    public static final String COLUMN_JMENO_SOUPERE = "JMENO_SOUPERE";
    public static final String COLUMN_DATUM_ZAPASU = "DATUM_ZAPASU";
    public static final String COLUMN_DOMACI_ZAPAS = "DOMACI_ZAPAS";
    public static final String COLUMN_POCET_PIV = "POCET_PIV";
    public static final String COLUMN_SEZONA = "SEZONA";

    public static final String PIVA_TABLE = "PIVA";
    public static final String COLUMN_POCET_VELKYCH_PIV = "POCET_VELKYCH_PIV";
    public static final String COLUMN_POCET_MALYCH_PIV = "POCET_MALYCH_PIV";
    public static final String COLUMN_ID_ZAPASU = "ID_ZAPASU";
    public static final String COLUMN_ID_HRACE = "ID_HRACE";

    private SQLiteDatabase db;


    private int celkovyPocetVelkychPiv = 0;
    private int celkovyPocetMalychPiv = 0;
    private int pocetMalychPiv = 0;
    private int pocetVelkychPiv = 0;
    private int celkovyPocetZapasu = 0;
    private String jmenoHrace = "";
    private String jmenoSoupere = "";
    private String datumZapasu = "";

    */
/**
     * @param context
     * povinný konstruktor
     *//*

    public DBadapter(@Nullable Context context) {
        super(context, "pivo.db", null, 1);
    }

    */
/**
     * @param db
     * voláno když se přistupuje do db (inputy či čtení). Obsahuje kód pro vytvoření db
     *//*

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableHraci = "CREATE TABLE " + HRACI_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_JMENO_HRACE + " TEXT, " + COLUMN_VEK_HRACE + " INT, " + COLUMN_DATUM_NAROZENI + " TEXT, " + COLUMN_FANOUSEK + " BOOL, " + COLUMN_DNI_DO_NAROZEK + " INT)";

        db.execSQL(createTableHraci);

        String createTableZapasy = "CREATE TABLE " + ZAPASY_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_JMENO_SOUPERE + " TEXT, " + COLUMN_POCET_PIV + " INT, " + COLUMN_DATUM_ZAPASU + " TEXT, " + COLUMN_DOMACI_ZAPAS + " BOOL, " + COLUMN_SEZONA + " TEXT)";

        db.execSQL(createTableZapasy);

        String createTablePiva = "CREATE TABLE " + PIVA_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_POCET_VELKYCH_PIV + " INT, " + COLUMN_POCET_MALYCH_PIV + " INT, " + COLUMN_ID_ZAPASU + " INT, " + COLUMN_ID_HRACE + " INT)";

        db.execSQL(createTablePiva);

    }
    */
/**
     * @param db
     * @param oldVersion
     * @param newVersion
     * voláno když se změní verze db. Zabraňuje předchozím uživatelům appky  rozbití když se změní design databáze.
     *//*

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //METODY PRO TABULKU HRACI
    */
/**
     * metoda pro přidání hráčů do databáze
     * @param hrac obsahuje jméno hráče, věk hráče a boolean fanousek
     * @return true v případě úspěšného vložení
     *//*

    public boolean pridejHrace (Hrac hrac) {

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_JMENO_HRACE, hrac.getJmeno());
        cv.put(COLUMN_DATUM_NAROZENI, hrac.getDatum());
        cv.put(COLUMN_FANOUSEK, hrac.isFanousek());
        cv.put(COLUMN_VEK_HRACE, hrac.getVek());
        cv.put(COLUMN_DNI_DO_NAROZEK, hrac.getDniDoNarozenin());

        long insert = db.insert(HRACI_TABLE, null, cv);
        if (insert == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    */
/**
     * Najde záznam v databázi. V případě nalezení záznam smaže a vrátí true. Pokud nenajde tak vrátí false
     * @param hrac
     * @return true pro úspěšné smazání, false pro neúspěšné smazání
     *//*

    public boolean smazHrace (Hrac hrac) {

        db = this.getWritableDatabase();
        String queryStringHrac = "DELETE FROM " + HRACI_TABLE + " WHERE " + COLUMN_ID + " = " + hrac.getId();

        Cursor cursorHrac = db.rawQuery(queryStringHrac, null);

        String queryStringPivo = "DELETE FROM " + PIVA_TABLE + " WHERE " + COLUMN_ID_HRACE + " = " + hrac.getId();

        Cursor cursorPivo = db.rawQuery(queryStringPivo, null);

        if (cursorPivo.moveToFirst()) {
            do {

            }
            while (cursorPivo.moveToNext());

        }
        else {

        }

        if (cursorHrac.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }




    }

    public boolean zmenHrace (Hrac hrac) {
        ContentValues cv = new ContentValues();
        db = this.getWritableDatabase();
        cv.put(COLUMN_JMENO_HRACE, hrac.getJmeno());
        cv.put(COLUMN_VEK_HRACE, hrac.getVek());
        cv.put(COLUMN_FANOUSEK, hrac.isFanousek());
        cv.put(COLUMN_DATUM_NAROZENI, hrac.getDatum());
        cv.put(COLUMN_DNI_DO_NAROZEK, hrac.getDniDoNarozenin());

        return db.update(HRACI_TABLE, cv, COLUMN_ID + "=?", new String[]{String.valueOf(hrac.getId())}) > 0;
    }

    */
/**
     * načtení dat z databáze pro list hráčů
     * @return returnList
     *//*

    public List<Hrac> vyberVsechnyHrace(int zobrazeniProVyber) {

        List<Hrac> returnList = new ArrayList<>();

        //kód pro select všech dat z databáze
        String queryString = "SELECT * FROM " + HRACI_TABLE;

        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursor = db.rawQuery(queryString, null);


        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursor.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {
                int hracID = cursor.getInt(0); //hledá v prvním sloupci (0)
                String jmenoHrace = cursor.getString(1);
                int vek = cursor.getInt(2);
                String datumNarozeni = cursor.getString(3);
                boolean fanousek = cursor.getInt(4) == 1 ? true: false;

                Hrac novyHrac = new Hrac(hracID, jmenoHrace, datumNarozeni, fanousek);
                //novyHrac.setZobrazeniProVyber(zobrazeniProVyber);
                if (!fanousek) {
                    returnList.add(0, novyHrac);
                }
                else {
                    returnList.add(novyHrac);
                }

            } while (cursor.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }


        //zavření cursoru a db, aby šlo do budoucna použít!
        cursor.close();
        db.close();
        return returnList;
    }

    public List<Hrac> vyberVsechnyHraceProVyber(int zobrazeniProVyber) {

        List<Hrac> returnList = new ArrayList<>();

        //kód pro select všech dat z databáze
        String queryString = "SELECT * FROM " + HRACI_TABLE;

        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursor = db.rawQuery(queryString, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursor.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {
                int hracID = cursor.getInt(0); //hledá v prvním sloupci (0)
                String jmenoHrace = cursor.getString(1);
                int vek = cursor.getInt(2);
                String datumNarozeni = cursor.getString(3);
                boolean fanousek = cursor.getInt(4) == 1 ? true: false;

                Hrac novyHrac = new Hrac(hracID, jmenoHrace, datumNarozeni, fanousek);
                //novyHrac.setZobrazeniProVyber(zobrazeniProVyber);
                if (!fanousek) {
                    returnList.add(0, novyHrac);
                }
                else {
                    returnList.add(novyHrac);
                }

            } while (cursor.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }

        //zavření cursoru a db, aby šlo do budoucna použít!
        cursor.close();
        db.close();
        return returnList;
    }

    */
/**metoda pro zobrazení seznamu hráčů z db včetně množství vypitých piv pro fragment pivo. Čerpá data z tabulky PIVA a HRACI
     * @param idZapasu
     * @return
     *//*

    public List<Hrac> vyberVsechnyHraceProVyberPiv(int idZapasu) {

        List<Hrac> returnList = new ArrayList<>();

        //kód pro select všech dat z databáze
        String queryStringHraci = "SELECT * FROM " + HRACI_TABLE;

        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursor = db.rawQuery(queryStringHraci, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursor.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {
                int hracID = cursor.getInt(0); //hledá v prvním sloupci (0)
                String jmenoHrace = cursor.getString(1);
                String datumNarozeni = cursor.getString(3);
                boolean fanousek = cursor.getInt(4) == 1 ? true: false;

                String queryStringPiva = "SELECT * FROM " + PIVA_TABLE +
                                            " WHERE " + COLUMN_ID_HRACE + " = " + hracID + " AND " + COLUMN_ID_ZAPASU + " = " + idZapasu;
                Cursor piva = db.rawQuery(queryStringPiva, null);

                piva.moveToFirst();
                int pocetVelkychPiv = 0;
                int pocetMalychPiv = 0;
                try {
                    pocetVelkychPiv = piva.getInt(1);
                    pocetMalychPiv = piva.getInt(2);
                }
                catch (Exception e) {

                }

                Hrac novyHrac = new Hrac(hracID, jmenoHrace, datumNarozeni, fanousek, pocetVelkychPiv, pocetMalychPiv, ZobrazeniHrace.Pivni);


                if (!fanousek) {
                    returnList.add(0, novyHrac);
                }
                else {
                    returnList.add(novyHrac);
                }

            } while (cursor.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }

        //zavření cursoru a db, aby šlo do budoucna použít!
        cursor.close();
        db.close();
        return returnList;
    }

    public String najdiOslavence() {

        String nejblizsiOslavenec = "";

        //kód pro select jména, věku a dni do narozek pro nejbližšího z databáze
        String queryString = "SELECT min(" + COLUMN_DNI_DO_NAROZEK + "), " + COLUMN_VEK_HRACE + ", " + COLUMN_JMENO_HRACE + " FROM " + HRACI_TABLE;

        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                int dniDoNarozek = cursor.getInt(0); //hledá v prvním sloupci (0)
                int vek = cursor.getInt(1) +1;
                String jmeno = cursor.getString(2);

                if (jmeno == null) {
                    nejblizsiOslavenec = "Zatím nebyl přidaný žádný hráč pro vypočítání příštích narozenin. Naprav to, ať víme kdo příště platí rundu!";
                }
                else if (dniDoNarozek == 1) {
                    nejblizsiOslavenec = "Příští rundu platí " + jmeno + ", který bude mít zítra své " + vek + " narozeniny! Dej chladit sud!";
                }
                else if (dniDoNarozek == 0) {
                    nejblizsiOslavenec = "Dnes slaví narozeniny " + jmeno + ", který má své " + vek + " narozeniny! Už ten sud vyval a ať ti slouží splávek!";
                }
                else {
                    nejblizsiOslavenec = "Příští rundu platí " + jmeno + ", který bude mít za " + dniDoNarozek + " dní své " + vek + " narozeniny!";

                }

            } while (cursor.moveToNext());

        }
        else {
            //nic
        }

        //zavření cursoru a db, aby šlo do budoucna použít!
        cursor.close();
        db.close();
        return nejblizsiOslavenec;
    }

    //Přečte každý řádek z db hraci, vytvoří instance pro jednotlivé hráče a zavolá skrz ně metodu aktualizujDnyDoNarozek
    public void nactiAktualniNarozeniny() {

        String queryString = "SELECT * FROM " + HRACI_TABLE;

        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                int hracID = cursor.getInt(0); //hledá v prvním sloupci (0)
                String jmenoHrace = cursor.getString(1);
                int vek = cursor.getInt(2);
                String datumNarozeni = cursor.getString(3);
                boolean fanousek = cursor.getInt(4) == 1 ? true: false;

                Hrac novyHrac = new Hrac(hracID, jmenoHrace, datumNarozeni, fanousek);
                novyHrac.setZobrazeniHrace(ZobrazeniHrace.Pivni);
                aktualizujDnyDoNarozek(novyHrac);

            } while (cursor.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }

                //zavření cursoru a db, aby šlo do budoucna použít!
        cursor.close();
        db.close();

    }

    private boolean aktualizujDnyDoNarozek (Hrac hrac) {
        ContentValues cv = new ContentValues();
        db = this.getWritableDatabase();
        hrac.setDniDoNarozenin(Datum.setDniDoNarozenin(hrac.getDatum()));
        cv.put(COLUMN_DNI_DO_NAROZEK, hrac.getDniDoNarozenin());

        return db.update(HRACI_TABLE, cv, COLUMN_ID + "=?", new String[]{String.valueOf(hrac.getId())}) > 0;
    }


    public List<Hrac> vyberHrace(int pozice) {

        List<Hrac> returnList = new ArrayList<>();
        int fanda;
        if (pozice == 2) {
            fanda = 0;
        }
        else {
            fanda = 1;
        }

        //kód pro select všech dat z databáze
        String queryString = "SELECT * FROM " + HRACI_TABLE + " WHERE " + COLUMN_FANOUSEK + " = " + fanda;

        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursor = db.rawQuery(queryString, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursor.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {
                int hracID = cursor.getInt(0); //hledá v prvním sloupci (0)
                String jmenoHrace = cursor.getString(1);
                String datumNarozeni = cursor.getString(3);
                boolean fanousek = cursor.getInt(4) == 1 ? true: false;

                Hrac novyHrac = new Hrac(hracID, jmenoHrace, datumNarozeni, fanousek, ZobrazeniHrace.Detailni);

                returnList.add(novyHrac);


            } while (cursor.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }

        cursor.close();
        db.close();
        return returnList;
    }

    //METODY PRO TABULKU ZAPASY
    public boolean pridejZapas (Zapas zapas) {

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_JMENO_SOUPERE, zapas.getSouper());
        cv.put(COLUMN_DATUM_ZAPASU, zapas.getDatum());
        cv.put(COLUMN_DOMACI_ZAPAS, zapas.isDomaciZapas());
        cv.put(COLUMN_POCET_PIV, zapas.getPocetPiv());
        cv.put(COLUMN_SEZONA, zapas.getSezona());

        long insert = db.insert(ZAPASY_TABLE, null, cv);
        if (insert == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean smazZapas (Zapas zapas) {

        db = this.getWritableDatabase();
        String queryStringZapas = "DELETE FROM " + ZAPASY_TABLE + " WHERE " + COLUMN_ID + " = " + zapas.getId();

        Cursor cursorZapas = db.rawQuery(queryStringZapas, null);

        String queryStringPivo = "DELETE FROM " + PIVA_TABLE + " WHERE " + COLUMN_ID_ZAPASU + " = " + zapas.getId();

        Cursor cursorPivo = db.rawQuery(queryStringPivo, null);


        if (cursorPivo.moveToFirst()) {
            do {

            }
            while (cursorPivo.moveToNext());

        }
        else {

        }

        if (cursorZapas.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }


    }

    public boolean zmenZapas (Zapas zapas) {
        ContentValues cv = new ContentValues();
        db = this.getWritableDatabase();
        cv.put(COLUMN_JMENO_SOUPERE, zapas.getSouper());
        cv.put(COLUMN_DATUM_ZAPASU, zapas.getDatum());
        cv.put(COLUMN_DOMACI_ZAPAS, zapas.isDomaciZapas());
        cv.put(COLUMN_SEZONA, zapas.getSezona());
        return db.update(ZAPASY_TABLE, cv, COLUMN_ID + "=?", new String[]{String.valueOf(zapas.getId())}) > 0;
    }

    */
/**
     * načtení dat z databáze pro list hráčů
     * @return returnList
     *//*

    public List<Zapas> vyberVsechnyZapasy() {

        List<Zapas> returnList = new ArrayList<>();

        //kód pro select všech dat z databáze
        String queryString = "SELECT * FROM " + ZAPASY_TABLE + " ORDER BY " + COLUMN_DATUM_ZAPASU + " DESC";

        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursor = db.rawQuery(queryString, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursor.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {

                int zapasID = cursor.getInt(0); //hledá v prvním sloupci (0)
                String jmenoSoupere = cursor.getString(1);
                String datumZapasu = cursor.getString(3);
                boolean domaciZapas = cursor.getInt(4) == 1 ? true: false;

                Zapas novyZapas = new Zapas(zapasID, jmenoSoupere, datumZapasu, domaciZapas);

                returnList.add(novyZapas);


            } while (cursor.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<Zapas> vyberSezonu(int pozice) {

        List<Zapas> returnList = new ArrayList<>();

        //kód pro select všech dat z databáze
        String queryString = "SELECT * FROM " + ZAPASY_TABLE + " WHERE " + COLUMN_SEZONA + " = " + pozice + " ORDER BY " + COLUMN_DATUM_ZAPASU + " DESC";

        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursor = db.rawQuery(queryString, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursor.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {
                int zapasID = cursor.getInt(0); //hledá v prvním sloupci (0)
                String jmenoSoupere = cursor.getString(1);
                String datumZapasu = cursor.getString(3);
                boolean domaciZapas = cursor.getInt(4) == 1 ? true: false;

                Zapas novyZapas = new Zapas(zapasID, jmenoSoupere, datumZapasu, domaciZapas);

                returnList.add(novyZapas);


            } while (cursor.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }

        cursor.close();
        db.close();
        return returnList;
    }


    //PŘÍKAZY PRO TABULKU PIVA
    public boolean pridejPivo (Pivo pivo) {

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_POCET_VELKYCH_PIV, pivo.getPocetVelkychPiv());
        cv.put(COLUMN_POCET_MALYCH_PIV, pivo.getPocetMalychPiv());
        cv.put(COLUMN_ID_ZAPASU, pivo.getIdZapasu());
        cv.put(COLUMN_ID_HRACE, pivo.getIdHrace());

        long insert = db.insert(PIVA_TABLE, null, cv);
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    */
/**vrací počet řádků v table pivo pomocí filtru na id zápasu a id hráče.
     * Měl by sloužit k tomu, že pokud je výsledek 0, musí se založit nový záznam, pokud 1 řádek se bude editovat
     * @param  idZapasu,  idHrace
     * @return
     *//*

    public int najdiShoduVPivech(int idZapasu, int idHrace) {

        int pocetRadek = 0;

        //kód pro select id zápasu a id hráče aby se netvořil zbytečně nový řádek
        String queryString = "SELECT COUNT(*) FROM " + PIVA_TABLE + " WHERE " + COLUMN_ID_ZAPASU + " = " + idZapasu + " AND " + COLUMN_ID_HRACE + " = " + idHrace;

        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToFirst();
        pocetRadek = cursor.getInt(0);


        //zavření cursoru a db, aby šlo do budoucna použít!
        cursor.close();
        db.close();
        return pocetRadek;
    }

    */
/** metoda pro zjištění zda již existuje záznam se stejným ID v databázi pivo table. vyhledává pomocí id zápasu a hráče. V případě nalezení záznamu vrací instanci
     * @param idZapasu
     * @param idHrace
     * @return instance piva s nalezeným záznamem
     *//*

    public Pivo najdiExistujiciPivniZaznam(int idZapasu, int idHrace) {


        //kód pro select všech dat z databáze
        String queryString = "SELECT * FROM " + PIVA_TABLE + " WHERE " + COLUMN_ID_ZAPASU + " = " + idZapasu + " AND " + COLUMN_ID_HRACE + " = " + idHrace;

        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursor = db.rawQuery(queryString, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        cursor.moveToFirst();
                int idPivo = cursor.getInt(0); //hledá v prvním sloupci (0)
                int pocetVelkychPiv = cursor.getInt(1);
                int pocetMalychPiv = cursor.getInt(2);
                int idZapas = cursor.getInt(3);
                int idHrac = cursor.getInt(4);

                Pivo zaznamPivo = new Pivo (idPivo, pocetVelkychPiv, pocetMalychPiv, idZapas, idHrac);


        cursor.close();
        db.close();
        return zaznamPivo;
    }

    public boolean zmenPivo (Pivo pivo) {
        ContentValues cv = new ContentValues();
        db = this.getWritableDatabase();
        cv.put(COLUMN_POCET_VELKYCH_PIV, pivo.getPocetVelkychPiv());
        cv.put(COLUMN_POCET_MALYCH_PIV, pivo.getPocetMalychPiv());
        return db.update(PIVA_TABLE, cv, COLUMN_ID + "=?", new String[]{String.valueOf(pivo.getId())}) > 0;
    }

    //PRO STATISTIKY

    public List<Zapas> zobrazZapasyProStatistiku(int idHrace, int sezona) {

        List<Zapas> returnList = new ArrayList<>();

        celkovyPocetVelkychPiv = 0;
        celkovyPocetMalychPiv = 0;
        celkovyPocetZapasu = 0;

        //kód pro select hráčů podle id ze záznamu pivní databáze
        String queryStringPiva = "SELECT * FROM " + PIVA_TABLE + " WHERE " + COLUMN_ID_HRACE + " = " + idHrace;

        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursorPiva = db.rawQuery(queryStringPiva, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursorPiva.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {
                int zapasId = cursorPiva.getInt(3);
                int pocetVelkychPiv = cursorPiva.getInt(1);
                int pocetMalychPiv = cursorPiva.getInt(2);

                String queryStringZapasy;
                if (sezona > 0) {
                    queryStringZapasy = "SELECT * FROM " + ZAPASY_TABLE + " WHERE " + COLUMN_ID + " = " + zapasId + " AND " + COLUMN_SEZONA + " = " + sezona;
                }

                else {
                    queryStringZapasy = "SELECT * FROM " + ZAPASY_TABLE + " WHERE " + COLUMN_ID + " = " + zapasId;

                }


                Cursor cursorZapasy = db.rawQuery(queryStringZapasy, null);

                cursorZapasy.moveToFirst();

                try {
                    int zapasID = cursorZapasy.getInt(0); //hledá v prvním sloupci (0)
                    String jmenoSoupere = cursorZapasy.getString(1);
                    String datumZapasu = cursorZapasy.getString(3);
                    boolean domaciZapas = cursorZapasy.getInt(4) == 1 ? true : false;

                    celkovyPocetZapasu++;
                    celkovyPocetMalychPiv += pocetMalychPiv;
                    celkovyPocetVelkychPiv += pocetVelkychPiv;

                    Zapas novyZapas = new Zapas(zapasID, jmenoSoupere, datumZapasu, domaciZapas, pocetVelkychPiv, pocetMalychPiv, 1);


                    returnList.add(novyZapas);

                }
                catch (Exception e) {

                }

                cursorZapasy.close();



            } while (cursorPiva.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }
        
        

        cursorPiva.close();


        db.close();
        return returnList;
    }

    public List<Hrac> zobrazHraceProStatistiku(int idZapasu, int fandousekInput) {

        fandousekInput--;

        List<Hrac> returnList = new ArrayList<>();

        celkovyPocetVelkychPiv = 0;
        celkovyPocetMalychPiv = 0;
        celkovyPocetZapasu = 0;

        //kód pro select hráčů podle id ze záznamu pivní databáze
        String queryStringPiva = "SELECT * FROM " + PIVA_TABLE + " WHERE " + COLUMN_ID_ZAPASU + " = " + idZapasu;

        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursorPiva = db.rawQuery(queryStringPiva, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursorPiva.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {
                int hracId = cursorPiva.getInt(4);
                int pocetVelkychPiv = cursorPiva.getInt(1);
                int pocetMalychPiv = cursorPiva.getInt(2);

                String queryStringHraci;
                if (fandousekInput > -1) {
                    queryStringHraci = "SELECT * FROM " + HRACI_TABLE + " WHERE " + COLUMN_ID + " = " + hracId + " AND " + COLUMN_FANOUSEK + " = " + fandousekInput;
                }

                else {
                    queryStringHraci = "SELECT * FROM " + HRACI_TABLE + " WHERE " + COLUMN_ID + " = " + hracId;

                }


                Cursor cursorHraci = db.rawQuery(queryStringHraci, null);

                cursorHraci.moveToFirst();


                try {
                    int hracID = cursorHraci.getInt(0); //hledá v prvním sloupci (0)
                    String jmenoHrace = cursorHraci.getString(1);
                    String datumNarozeni = cursorHraci.getString(3);
                    boolean fanousek = cursorHraci.getInt(4) == 1 ? true: false;

                    celkovyPocetZapasu++;
                    celkovyPocetMalychPiv += pocetMalychPiv;
                    celkovyPocetVelkychPiv += pocetVelkychPiv;

                    Hrac novyHrac = new Hrac(hracID, jmenoHrace, datumNarozeni, fanousek, pocetVelkychPiv, pocetMalychPiv, ZobrazeniHrace.Pivni);


                    if (!fanousek) {
                        returnList.add(0, novyHrac);
                    }
                    else {
                        returnList.add(novyHrac);
                    }

                }
                catch (Exception e) {

                }
                //Zapas novyZapas = new Zapas(zapasID, jmenoSoupere, datumZapasu, domaciZapas);

                //returnList.add(novyZapas);
                cursorHraci.close();



            } while (cursorPiva.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }



        cursorPiva.close();


        db.close();
        return returnList;
    }

    public List<Zapas> hledatZapasySezona(int pozice, String hledaneSlovo) {
        pozice--;

        List<Zapas> returnList = new ArrayList<>();
        String queryString;

        if (pozice == 0) {
            //kód pro select všech dat z databáze
            queryString = "SELECT * FROM " + ZAPASY_TABLE + " WHERE " + COLUMN_JMENO_SOUPERE + " LIKE '%" + hledaneSlovo + "%' ORDER BY " + COLUMN_DATUM_ZAPASU + " DESC";
        }
        else {
            queryString = "SELECT * FROM " + ZAPASY_TABLE + " WHERE " + COLUMN_SEZONA + " = " + pozice + " AND " + COLUMN_JMENO_SOUPERE + " LIKE '%" + hledaneSlovo + "%' ORDER BY " + COLUMN_DATUM_ZAPASU + " DESC";

        }
        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursor = db.rawQuery(queryString, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursor.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {
                int zapasID = cursor.getInt(0); //hledá v prvním sloupci (0)
                String jmenoSoupere = cursor.getString(1);
                String datumZapasu = cursor.getString(3);
                boolean domaciZapas = cursor.getInt(4) == 1 ? true: false;

                Zapas novyZapas = new Zapas(zapasID, jmenoSoupere, datumZapasu, domaciZapas);

                returnList.add(novyZapas);


            } while (cursor.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<Hrac> hledatHrace(int pozice, String hledaneSlovo) {
        pozice -= 2;

        List<Hrac> returnList = new ArrayList<>();
        String queryString;

        if (pozice < 0) {
            //kód pro select všech dat z databáze
            queryString = "SELECT * FROM " + HRACI_TABLE + " WHERE " + COLUMN_JMENO_HRACE + " LIKE '%" + hledaneSlovo + "%'";
        }
        else {
            queryString = "SELECT * FROM " + HRACI_TABLE+ " WHERE " + COLUMN_FANOUSEK + " = " + pozice + " AND " + COLUMN_JMENO_HRACE + " LIKE '%" + hledaneSlovo + "%'";

        }
        db = this.getReadableDatabase();

        //Cursor je result set od SQLite db. Komplexní array ze všech položek
        Cursor cursor = db.rawQuery(queryString, null);

        //cursor vrací true pokud byly vybrány nějaké položky
        if (cursor.moveToFirst()) {
            //Projde cursor (set resultů) a vytvoří objekty hráče. Vloží je do returnlist.
            do {
                int hracID = cursor.getInt(0); //hledá v prvním sloupci (0)
                String jmenoHrace = cursor.getString(1);
                String datumNarozeni = cursor.getString(3);
                boolean fanousek = cursor.getInt(4) == 1 ? true: false;

                Hrac novyHrac = new Hrac(hracID, jmenoHrace, datumNarozeni, fanousek, ZobrazeniHrace.Detailni);
                if (!fanousek) {
                    returnList.add(0, novyHrac);
                }
                else {
                    returnList.add(novyHrac);
                }


            } while (cursor.moveToNext());

        }
        else {
            //chyba, na list se nic nepřidá
        }

        cursor.close();
        db.close();
        return returnList;
    }

    */
/**Vrátí list stringů s celkovými statistika za sezony a fanoušky rozděleně do list view
     * @return
     *//*

    public List<String> zobrazCelkovyPocetVypitychPiv() {

        celkovyPocetZapasu = 0;
        celkovyPocetVelkychPiv = 0;
        celkovyPocetMalychPiv = 0;
        String returnString = "";
        List<String> returnList = new ArrayList<>();

        for (int l = 1; l < 5; l++) {
            for (int p = 0; p < 2; p++) {
                pocetVelkychPiv = 0;
                pocetMalychPiv = 0;
                returnString = "";

                //kód pro select podle id ze záznamu  databáze
                String queryString = "SELECT " + COLUMN_ID_HRACE + ", " + COLUMN_ID_ZAPASU + ", " + COLUMN_POCET_VELKYCH_PIV + ", " + COLUMN_POCET_MALYCH_PIV + ", " + COLUMN_SEZONA + ", " + COLUMN_FANOUSEK +
                        " FROM " + PIVA_TABLE + " INNER JOIN " + HRACI_TABLE + " ON " + HRACI_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_HRACE +
                        " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU + " WHERE " + COLUMN_SEZONA + " = " + l + " AND " + COLUMN_FANOUSEK + " = " + p;

                db = this.getReadableDatabase();

                Cursor cursor = db.rawQuery(queryString, null);

                if (cursor.moveToFirst()) {
                    do {
                        celkovyPocetVelkychPiv += cursor.getInt(2);
                        celkovyPocetMalychPiv += cursor.getInt(3);
                        pocetVelkychPiv += cursor.getInt(2);
                        pocetMalychPiv += cursor.getInt(3);


                    } while (cursor.moveToNext());

                    if (l == 1) {
                        returnString += "Za jaro 2020 ";
                    }
                    else if (l == 2) {
                        returnString += "Za podzim 2020 ";
                    }
                    else if (l == 3) {
                        returnString += "Za jaro 2021 ";
                    }
                    else {
                        returnString += "Za ostatní zápasy ";
                    }
                    if (p == 0) {
                        returnString += "hráči vypili " + pocetMalychPiv + " malých a " + pocetVelkychPiv + " velkých piv";
                    }
                    else {
                        returnString += "fanoušci vypili " + pocetMalychPiv + " malých a " + pocetVelkychPiv + " velkých piv";
                    }
                    returnList.add(returnString);

                } else {
                    //nic
                }
            }
        }
        returnString = "Celkově se vypilo " + celkovyPocetMalychPiv + " malých a " + celkovyPocetVelkychPiv + " velkých piv";
        returnList.add(returnString);

        return returnList;
    }

    //PŘÍKAZY PRO HLAVNÍ MENU

    public String najdiMaxVelkychPivNaHrace() {

        String returnString = "Rekord v počtu vypitých velkých piv v jednom zápase padl: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT " + COLUMN_ID_HRACE + ", " + COLUMN_ID_ZAPASU + ", " + COLUMN_POCET_VELKYCH_PIV + ", " + COLUMN_JMENO_HRACE + ", " + COLUMN_JMENO_SOUPERE + ", " + COLUMN_DATUM_ZAPASU +
                " FROM " + PIVA_TABLE + " INNER JOIN " + HRACI_TABLE + " ON " + HRACI_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_HRACE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " WHERE " + COLUMN_POCET_VELKYCH_PIV + " = (SELECT MAX(" + COLUMN_POCET_VELKYCH_PIV + ") FROM " + PIVA_TABLE + ")";

        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                pocetVelkychPiv = cursor.getInt(2);
                jmenoHrace = cursor.getString(3);
                jmenoSoupere = cursor.getString(4);
                datumZapasu = cursor.getString(5);
                returnString += Datum.zmenDatumDoFront(datumZapasu) + " v zápase proti " + jmenoSoupere + ", kdy " + jmenoHrace + " vypil " + pocetVelkychPiv + " velkých piv";

                if (!cursor.isLast()) {
                    returnString += ", \n";
                }
            } while (cursor.moveToNext());
        }
        else {
            returnString = "Zde má být vypsán rekordman v počtu vypitých piv za zápas, ale ještě si nikdo žádný nedal?!";

        }

        return returnString;
    }

    public String najdiMaxMalychPivNaHrace() {

        String returnString = "Rekord v počtu vypitých malých piv v jednom zápase padl: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT " + COLUMN_ID_HRACE + ", " + COLUMN_ID_ZAPASU + ", " + COLUMN_POCET_MALYCH_PIV + ", " + COLUMN_JMENO_HRACE + ", " + COLUMN_JMENO_SOUPERE + ", " + COLUMN_DATUM_ZAPASU +
                " FROM " + PIVA_TABLE + " INNER JOIN " + HRACI_TABLE + " ON " + HRACI_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_HRACE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " WHERE " + COLUMN_POCET_MALYCH_PIV + " = (SELECT MAX(" + COLUMN_POCET_MALYCH_PIV + ") FROM " + PIVA_TABLE + ")";

        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                pocetMalychPiv = cursor.getInt(2);
                jmenoHrace = cursor.getString(3);
                jmenoSoupere = cursor.getString(4);
                datumZapasu = cursor.getString(5);
                returnString += Datum.zmenDatumDoFront(datumZapasu) + " v zápase proti " + jmenoSoupere + ", kdy " + jmenoHrace + " vypil " + pocetMalychPiv + " malých piv";

                if (!cursor.isLast()) {
                    returnString += ", \n";
                }
            } while (cursor.moveToNext());
        }
        else {
            returnString = "Žádný hráč si nedal malé pivo aby byl spočítán rekord, dejte si aspoň 1 a budete navždy zvěčněni!";

        }
        if (pocetMalychPiv == 0) {
            returnString = " Žádný hráč si nedal malé pivo aby byl spočítán rekord, dejte si aspoň 1 a budete navždy zvěčněni!";
        }

        return returnString;
    }

    public String najdiMaxVelkychVZapase() {

        String returnString = "Nejvíce velkých piv v jednom zápase se vypilo: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT " + COLUMN_ID_ZAPASU + ", " +  COLUMN_JMENO_SOUPERE + ", " + COLUMN_DATUM_ZAPASU + ", " +
                " SUM (" + COLUMN_POCET_VELKYCH_PIV + ")" +
                " FROM " + PIVA_TABLE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " GROUP BY " + COLUMN_ID_ZAPASU +
                " ORDER BY " + " SUM (" + COLUMN_POCET_VELKYCH_PIV + ")" + " DESC" +
                " LIMIT 1";


        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                pocetVelkychPiv = cursor.getInt(3);
                jmenoSoupere = cursor.getString(1);
                datumZapasu = cursor.getString(2);
                returnString += Datum.zmenDatumDoFront(datumZapasu) + " v zápase proti " + jmenoSoupere + ", kdy padlo přesně " + pocetVelkychPiv + " kousků";

                if (!cursor.isLast()) {
                    returnString += ", \n";
                }
            } while (cursor.moveToNext());
        }
        else {
            returnString = " Pro získání rekordu v počtu vypitých velkých piv v zápase chybí data!";

        }

        return returnString;
    }

    public String najdiMaxMalychVZapase() {

        String returnString = "Nejvíce malých piv v jednom zápase se vypilo: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT " + COLUMN_ID_ZAPASU + ", " +  COLUMN_JMENO_SOUPERE + ", " + COLUMN_DATUM_ZAPASU + ", " +
                " SUM (" + COLUMN_POCET_MALYCH_PIV + ")" +
                " FROM " + PIVA_TABLE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " GROUP BY " + COLUMN_ID_ZAPASU +
                " ORDER BY " + " SUM (" + COLUMN_POCET_MALYCH_PIV + ")" + " DESC" +
                " LIMIT 1";


        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                pocetMalychPiv = cursor.getInt(3);
                jmenoSoupere = cursor.getString(1);
                datumZapasu = cursor.getString(2);
                returnString += Datum.zmenDatumDoFront(datumZapasu) + " v zápase proti " + jmenoSoupere + ", kdy padlo přesně " + pocetMalychPiv + " mini kousků";

                if (!cursor.isLast()) {
                    returnString += ", \n";
                }
            } while (cursor.moveToNext());
        }
        else {
            returnString = " Pro získání rekordu v počtu vypitých malých piv v zápase chybí data! Nejspíš se žádná malá piva nepila a to je dobře";

        }

        return returnString;
    }

    public String najdiMaxVelkychZaSezonu() {

        String returnString = "Nejvíce velkých piv v sezoně se vypilo: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT " + COLUMN_ID_ZAPASU + ", " +  COLUMN_SEZONA + ", " +
                " SUM (" + COLUMN_POCET_VELKYCH_PIV + ")" +
                " FROM " + PIVA_TABLE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " GROUP BY " + COLUMN_SEZONA +
                " ORDER BY " + " SUM (" + COLUMN_POCET_VELKYCH_PIV + ")" + " DESC" +
                " LIMIT 1";


        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                pocetVelkychPiv = cursor.getInt(2);
                int sezona = cursor.getInt(1);
                switch (sezona) {
                    case 1:
                        returnString += "Na jaře 2020 kdy padlo přesně " + pocetVelkychPiv + " piv";
                        break;
                    case 2:
                        returnString += "Na podzim 2020 kdy padlo přesně " + pocetVelkychPiv + " piv";
                        break;
                    case 3:
                        returnString += "Na jaře 2021 kdy padlo přesně " + pocetVelkychPiv + " piv";
                        break;
                    case 4:
                        returnString += "V ostatních termínově neurčených zápasech kdy padlo přesně " + pocetVelkychPiv + " piv";
                        break;
                }
                if (!cursor.isLast()) {
                    returnString += ", \n";
                }
            } while (cursor.moveToNext());
        }
        else {
            returnString = " Pro získání rekordu počtu velkých piv za sezony chybí data! To si nikdo nedal pivo?";

        }

        return returnString;
    }

    public String najdiMaxMalýchZaSezonu() {

        String returnString = "Nejvíce malých piv v sezoně se vypilo: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT " + COLUMN_ID_ZAPASU + ", " +  COLUMN_SEZONA + ", " +
                " SUM (" + COLUMN_POCET_MALYCH_PIV + ")" +
                " FROM " + PIVA_TABLE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " GROUP BY " + COLUMN_SEZONA +
                " ORDER BY " + " SUM (" + COLUMN_POCET_MALYCH_PIV + ")" + " DESC" +
                " LIMIT 1";


        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                pocetMalychPiv = cursor.getInt(2);
                int sezona = cursor.getInt(1);
                switch (sezona) {
                    case 1:
                        returnString += "Na jaře 2020 kdy padlo přesně " + pocetMalychPiv + " piv";
                        break;
                    case 2:
                        returnString += "Na podzim 2020 kdy padlo přesně " + pocetMalychPiv + " piv";
                        break;
                    case 3:
                        returnString += "Na jaře 2021 kdy padlo přesně " + pocetMalychPiv + " piv";
                        break;
                    case 4:
                        returnString += "V ostatních termínově neurčených zápasech kdy padlo přesně " + pocetMalychPiv + " piv";
                        break;
                }
                if (!cursor.isLast()) {
                    returnString += ", \n";
                }
            } while (cursor.moveToNext());
        }
        else {
            returnString = " Pro získání rekordu počtu malých piv za sezony chybí data! Nejspíš se žádná malá piva nepila a to je dobře";
        }

        return returnString;
    }

    public String vypocitejPrumernyPocetVypitychPiv() {

        String returnString = "Celkem za všechny zápasy se vypilo v průměru: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT AVG (" + COLUMN_POCET_VELKYCH_PIV + ")" +
                " FROM " + PIVA_TABLE +
                " WHERE " + COLUMN_POCET_VELKYCH_PIV + " > 0";



        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                double pocetVelkejch = cursor.getDouble(0);

                returnString += pocetVelkejch + " velkých piv";

            } while (cursor.moveToNext());
        }
        else {
            returnString = " Chybí data pro výpočet průměrného počtu piv, to si nikdo nedal pivo?";
        }

        return returnString;
    }

    public String prepoctiPocetMalychNaVelke() {

        String returnString = "Celkem se za historii vypilo: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT SUM (" + COLUMN_POCET_MALYCH_PIV + ")" +
                " FROM " + PIVA_TABLE;



        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                pocetMalychPiv = cursor.getInt(0);

                double maleNaVelke = pocetMalychPiv * 0.6;

                returnString += pocetMalychPiv + " malých piv, což je ekvivalent " + maleNaVelke + " velkých piv";

            } while (cursor.moveToNext());
        }
        else {
            returnString = " Pro získání rekordu počtu malých piv za sezony chybí data! Nejspíš se žádná malá piva nepila a to je dobře";
        }

        return returnString;
    }

    public String najdiMinVelkychVZapase() {

        String returnString = "Ostudný rekord v nejmenším počtu vypitých piv v historii padl: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT " + COLUMN_ID_ZAPASU + ", " +  COLUMN_JMENO_SOUPERE + ", " + COLUMN_DATUM_ZAPASU + ", " +
                " SUM (" + COLUMN_POCET_VELKYCH_PIV + ")" +
                " FROM " + PIVA_TABLE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " GROUP BY " + COLUMN_ID_ZAPASU +
                " ORDER BY " + " SUM (" + COLUMN_POCET_VELKYCH_PIV + ")" + " ASC" +
                " LIMIT 1";


        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                pocetVelkychPiv = cursor.getInt(3);
                jmenoSoupere = cursor.getString(1);
                datumZapasu = cursor.getString(2);
                returnString += Datum.zmenDatumDoFront(datumZapasu) + " v zápase proti " + jmenoSoupere + ", kdy padlo přesně " + pocetVelkychPiv + " kousků";

                if (!cursor.isLast()) {
                    returnString += ", \n";
                }
            } while (cursor.moveToNext());
        }
        else {
            returnString = " Pro získání rekordu v počtu vypitých velkých piv v zápase chybí data!";

        }

        return returnString;
    }

    public String najdiNejvetsiUcastNaZapase() {

        String returnString = "Nejvíce pijanů se účastnilo zápasu: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT " + COLUMN_JMENO_SOUPERE + ", " +  COLUMN_DATUM_ZAPASU + ", " +
                " COUNT (" + COLUMN_ID_HRACE + ")," +
                " SUM (" + COLUMN_POCET_VELKYCH_PIV + "), " + " SUM (" + COLUMN_POCET_MALYCH_PIV + ")" +
                " FROM " + PIVA_TABLE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " GROUP BY " + COLUMN_ID_ZAPASU +
                " ORDER BY " + " COUNT (" + COLUMN_ID_HRACE+ ")" + " DESC" +
                " LIMIT 1";


        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                jmenoSoupere = cursor.getString(0);
                datumZapasu = cursor.getString(1);
                int pocetHracu = cursor.getInt(2);
                pocetVelkychPiv = cursor.getInt(3);
                pocetMalychPiv = cursor.getInt(4);

                returnString += Datum.zmenDatumDoFront(datumZapasu) + " se soupeřem " + jmenoSoupere + " v počtu " + pocetHracu + ", kteří vypili " + pocetVelkychPiv + " velkých a " + pocetMalychPiv + " malých piv.";
            } while (cursor.moveToNext());
        }
        else {
            returnString = " Pro zobrazení nejvyšší účasti pijanů chybí data";

        }

        return returnString;
    }

    public String najdiNejmensiUcastNaZapase() {

        String returnString = "Nejméně pijanů se účastnilo zápasu: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT " + COLUMN_JMENO_SOUPERE + ", " +  COLUMN_DATUM_ZAPASU + ", " +
                " COUNT (" + COLUMN_ID_HRACE + ")," +
                " SUM (" + COLUMN_POCET_VELKYCH_PIV + "), " + " SUM (" + COLUMN_POCET_MALYCH_PIV + ")" +
                " FROM " + PIVA_TABLE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " GROUP BY " + COLUMN_ID_ZAPASU +
                " ORDER BY " + " COUNT (" + COLUMN_ID_HRACE+ ")" + " ASC" +
                " LIMIT 1";


        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                jmenoSoupere = cursor.getString(0);
                datumZapasu = cursor.getString(1);
                int pocetHracu = cursor.getInt(2);
                pocetVelkychPiv = cursor.getInt(3);
                pocetMalychPiv = cursor.getInt(4);

                returnString += Datum.zmenDatumDoFront(datumZapasu) + " se soupeřem " + jmenoSoupere + " v počtu " + pocetHracu + ", kteří vypili " + pocetVelkychPiv + " velkých a " + pocetMalychPiv + " malých piv.";
            } while (cursor.moveToNext());
        }
        else {
            returnString = " Pro zobrazení nejnižší účasti pijanů chybí data";

        }

        return returnString;
    }

    public String najdiShoduNarozeninAZapasu() {

        String returnString = "Jedním ze zápasů kdy se zapíjely narozeniny pijana Liščího Trusu byl: \n";

        //kód pro select podle id ze záznamu  databáze
        String queryString = "SELECT "+ COLUMN_JMENO_SOUPERE + ", "  + COLUMN_JMENO_HRACE + ", "  + COLUMN_DATUM_NAROZENI + ", " +  COLUMN_DATUM_ZAPASU + ", " + COLUMN_POCET_VELKYCH_PIV +  ", " + COLUMN_POCET_MALYCH_PIV + ", " + COLUMN_FANOUSEK +
                " FROM " + PIVA_TABLE +
                " INNER JOIN " + ZAPASY_TABLE + " ON " + ZAPASY_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_ZAPASU +
                " INNER JOIN " + HRACI_TABLE + " ON " + HRACI_TABLE + "." + COLUMN_ID + " = " + PIVA_TABLE + "." + COLUMN_ID_HRACE;


        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                jmenoSoupere = cursor.getString(0);
                jmenoHrace = cursor.getString(1);
                String datumNarozeni = cursor.getString(2);
                datumZapasu = cursor.getString(3);
                pocetVelkychPiv = cursor.getInt(4);
                pocetMalychPiv = cursor.getInt(5);
                boolean fanousek = cursor.getInt(6) == 1 ? true : false;

                if (Datum.setDniDoNarozenin(datumNarozeni) == Datum.setDniDoNarozenin(datumZapasu)) {
                    if (fanousek) {
                        returnString += "Zápas konaný " + Datum.zmenDatumDoFront(datumZapasu) + " se soupeřem " + jmenoSoupere + " kdy oslavenec, fanoušek Liščího Trusu, " + jmenoHrace + ", vypil " + pocetVelkychPiv + " velkých a " + pocetMalychPiv + " malých piv.";
                    }
                    else {
                        returnString += "Zápas konaný " + Datum.zmenDatumDoFront(datumZapasu) + " se soupeřem " + jmenoSoupere + " kdy oslavenec, hráč Liščího Trusu, " + jmenoHrace + ", vypil " + pocetVelkychPiv + " velkých a " + pocetMalychPiv + " malých piv.";

                    }
                }

            } while (cursor.moveToNext());
        }
        else {
            returnString = " Chybí data pro nalezení počtu piv oslavence";

        }
        if (returnString.equals("Jedním ze zápasů kdy se zapíjely narozeniny pijana Liščího Trusu byl: \n")) {
            returnString = " Zatím žádný z hráčů neslavil narozeniny přímo na zápase Liščího Trusu";
        }

        return returnString;
    }

    public int getCelkovyPocetVelkychPiv() {
        return celkovyPocetVelkychPiv;
    }

    public int getCelkovyPocetMalychPiv() {
        return celkovyPocetMalychPiv;
    }

    public int getCelkovyPocetZapasu() {
        return celkovyPocetZapasu;
    }
}
*/

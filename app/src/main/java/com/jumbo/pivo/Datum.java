package com.jumbo.pivo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class Datum {

    private static DateFormat formatDataSQL = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat formatDataFront = new SimpleDateFormat("dd.MM.yyyy");

    public static void nastavPulnoc(Calendar datum)
    {
        datum.set(Calendar.HOUR_OF_DAY, 0);
        datum.set(Calendar.MINUTE, 0);
        datum.set(Calendar.SECOND, 0);
        datum.set(Calendar.MILLISECOND, 0);
    }

    public static String zformatuj(Calendar datum)
    {
        String datumText = formatDataSQL.format(datum.getTime());
        return datumText;
    }

    public static Calendar naparsuj(String datumText)
    {
        Calendar datum = Calendar.getInstance();
        try {
            datum.setTime(formatDataSQL.parse(datumText));
        } catch (ParseException e) {
            //
        }
        return datum;
    }

    public static Calendar naparsujDoSQL (String datumText)
    {
        Calendar datum = Calendar.getInstance();
        try {
            datum.setTime(formatDataFront.parse(datumText));
        } catch (ParseException e) {
            //
        }
        return datum;
    }

    /**
     * metoda určující věk hráče
     * @param date datum narození ve Stringu
     * @return vek hrace
     */
    public static int urciVek (String date){
        int rok = zjistiRok(date);
        int mesic = zjistiMesic(date) + 1;
        int datum = zjistiDen(date);
        LocalDate narozeniny = LocalDate.of(rok, mesic, datum);
        LocalDate ted = LocalDate.now();
        Period rozdil = Period.between(narozeniny, ted);
        return rozdil.getYears();
    }

    /**
     * Metoda co spočítá počet dní do narozenin k dnešnímu dni
     * @param date datum narození ve Stringu
     * @return rozdil mezi dneškem a dnem narození
     */
    public static int setDniDoNarozenin (String date){
        int rok;
        int mesic = zjistiMesic(date) + 1;
        int datum = zjistiDen(date);

        LocalDate ted = LocalDate.now();
        if (mesic > ted.getMonthValue()) {
            rok = ted.getYear();
        }
        else if (mesic < ted.getMonthValue()) {
            rok = ted.getYear()+1;

        }
        else {
            if (datum < ted.getDayOfMonth()) {
                rok = ted.getYear()+1;
            }
            else {
                rok = ted.getYear();
            }

        }
        LocalDate narozeniny = LocalDate.of(rok, mesic, datum);
        int rozdil = (int) ChronoUnit.DAYS.between(ted, narozeniny);
        return rozdil;
    }

    public static int zjistiDen (String date) {
        int den;
        Calendar c = naparsuj(date);
        den = c.get(Calendar.DATE);
        return den;
    }

    public static int zjistiMesic (String date) {
        int mesic;
        Calendar c = naparsuj(date);
        mesic = c.get(Calendar.MONTH);
        return mesic;
    }

    public static int zjistiRok (String date) {
        int rok;
        Calendar c = naparsuj(date);
        rok = c.get(Calendar.YEAR);
        return rok;
    }
    public static Calendar zjistiDnesniDatum() {
        LocalDate ted = LocalDate.now();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        //assuming start of day
        calendar.set(ted.getYear(), ted.getMonthValue()-1, ted.getDayOfMonth());
        return calendar;
    }

    public static String zmenDatumDoSQL (String datum) {

        String datumSQL = formatDataSQL.format(naparsujDoSQL(datum).getTime());

        return datumSQL;
    }

    public static String zmenDatumDoFront (String datumSQL) {

        String datum = formatDataFront.format(naparsuj(datumSQL).getTime());

        return datum;
    }



}

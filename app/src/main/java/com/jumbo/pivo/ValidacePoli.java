package com.jumbo.pivo;

import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.*;

public class ValidacePoli {

    //konstruktor
    public ValidacePoli() {

    }

    public boolean zvalidujPrazdnePole (EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean zvalidujDatum (EditText datum) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        try {
            Date javaDate = dateFormat.parse(datum.getText().toString());
        }
        catch (ParseException e) {
            return true;
        }
        return false;
    }

    public boolean zvalidujJmeno (EditText jmeno) {
        String regex = "^[a-zA-Z0-9_ áčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ-]{0,100}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(jmeno.getText().toString());
        if (m.matches()) {
            return false;
        }
        else {
            return true;
        }
    }
    public boolean zvalidujPocetPiv (EditText piva) {
        String regex = "^[0-9]{0,2}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(piva.getText().toString());
        if (m.matches()) {
            return false;
        }
        else {
            return true;
        }
    }
}

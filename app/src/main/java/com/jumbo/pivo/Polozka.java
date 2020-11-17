package com.jumbo.pivo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public abstract class Polozka {

    //plní funkci unikátního ID
    private long timestamp;
    //enum podle kterýho se určuje to zobrazení položky v gui. V podstatě se to využívá u metody toString
    protected ZobrazeniPolozky zobrazeniPolozky;

    public Polozka() {
        this.timestamp = System.currentTimeMillis();
        this.zobrazeniPolozky = ZobrazeniPolozky.Zakladni;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ZobrazeniPolozky getZobrazeniPolozky() {
        return zobrazeniPolozky;
    }

    public void setZobrazeniPolozky(ZobrazeniPolozky zobrazeniPolozky) {
        this.zobrazeniPolozky = zobrazeniPolozky;
    }

    //přepsaná metoda equals aby se dalo porovnávat pomocí timestampů
/*    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Polozka) {
            return (this.getTimestamp() == ((Polozka) obj).getTimestamp());
        }
        else {
            return this == obj;
        }
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polozka polozka = (Polozka) o;
        return timestamp == polozka.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

}

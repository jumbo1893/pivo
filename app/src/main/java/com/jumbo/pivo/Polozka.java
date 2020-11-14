package com.jumbo.pivo;

import androidx.annotation.Nullable;

public abstract class Polozka {

    private long timestamp;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Polozka) {
            return (this.getTimestamp() == ((Polozka) obj).getTimestamp());
        }
        else {
            return this == obj;
        }
    }

}

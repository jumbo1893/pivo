package com.jumbo.pivo;

import androidx.annotation.Nullable;

public abstract class Polozka {

    private long timestamp;

    public Polozka() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

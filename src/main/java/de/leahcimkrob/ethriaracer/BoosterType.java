package de.leahcimkrob.ethriaracer;

public enum BoosterType {
    SPEED,
    JUMP,
    SLOW;

    public BoosterType next() {
        BoosterType[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public BoosterType prev() {
        BoosterType[] values = values();
        return values[(this.ordinal() - 1 + values.length) % values.length];
    }
}
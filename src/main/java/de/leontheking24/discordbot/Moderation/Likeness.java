package de.leontheking24.discordbot.Moderation;

public class Likeness {

    private final boolean isLikeness;
    private final int deviation;

    public Likeness(boolean isLikeness, int deviation) {
        this.isLikeness = isLikeness;
        this.deviation = deviation;
    }

    public boolean isLikeness() {
        return isLikeness;
    }

    public int getDeviation() {
        return deviation;
    }
}

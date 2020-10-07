package de.leontheking24.discordbot.Moderation;

public enum ELeetSpeak {

    A('4'),
    B('8'),
    E('3'),
    I('!'),
    L('1'),
    S('5'),
    T('7'),
    O('0');

    private final char toReplace;

    ELeetSpeak(char toReplace) {
        this.toReplace = toReplace;
    }

    public char getToReplace() {
        return toReplace;
    }

    public static ELeetSpeak getCharByLeet(char toReplace) {
        ELeetSpeak returnValue = null;
        for(ELeetSpeak leet : ELeetSpeak.values()) {
            if(leet.getToReplace() == toReplace) {
                returnValue = leet;
            }
        }
        return returnValue;
    }

}

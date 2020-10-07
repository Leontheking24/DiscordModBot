package de.leontheking24.discordbot.Poll.Entitys;

public enum PollEmojis {
    Num0(0, ":zero:", "U+0030 U+FE0F U+20E3", "U+30U+fe0fU+20e3"),
    Num1(1, ":one:", "U+0031 U+FE0F U+20E3", "U+31U+fe0fU+20e3"),
    Num2(2, ":two:", "U+0032 U+FE0F U+20E3", "U+32U+fe0fU+20e3"),
    Num3(3, ":three:", "U+0033 U+FE0F U+20E3", "U+33U+fe0fU+20e3"),
    Num4(4, ":four:", "U+0034 U+FE0F U+20E3", "U+34U+fe0fU+20e3"),
    Num5(5, ":five:", "U+0035 U+FE0F U+20E3", "U+35U+fe0fU+20e3"),
    Num6(6, ":six:", "U+0036 U+FE0F U+20E3", "U+36U+fe0fU+20e3"),
    Num7(7, ":seven:", "U+0037 U+FE0F U+20E3", "U+37U+fe0fU+20e3"),
    Num8(8, ":eight:", "U+0038 U+FE0F U+20E3", "U+38U+fe0fU+20e3"),
    Num9(9, ":nine:", "U+0039 U+FE0F U+20E3", "U+39U+fe0fU+20e3"),
    NumError(-1, "Error", "Error", "Error");

    private int digit;
    private String emoji;
    private String unicode;
    private String unicodeDB;

    PollEmojis(int digit, String emoji, String unicode, String unicodeDB) {
        this.digit = digit;
        this.emoji = emoji;
        this.unicode = unicode;
        this.unicodeDB = unicodeDB;
    }

    public int getDigit() {
        return digit;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getUnicode() {
        return unicode;
    }

    public String getUnicodeDB() {
        return unicodeDB;
    }

    public static PollEmojis getByDigit(int digit) {
        for(PollEmojis emoji : PollEmojis.values()) {
            if(emoji.getDigit() == digit) {
                return emoji;
            }
        }
        return PollEmojis.NumError;
    }
}

package de.leontheking24.discordbot.Poll.Entitys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PollDateSearch {

    private Date date;
    private String replaceArgs;

    public PollDateSearch(String date, String replaceArgs) {
        try {
            this.date = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.replaceArgs = replaceArgs;
    }

    public Date getDate() {
        return date;
    }

    public String getReplaceArgs() {
        return replaceArgs;
    }

}

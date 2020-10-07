package de.leontheking24.discordbot.LanguageManager;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    public LanguageManager() {}

    public String getMessage(String key, String languageKey) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Languages/language", Locale.forLanguageTag(languageKey));
        return resourceBundle.getString(key);
    }

}

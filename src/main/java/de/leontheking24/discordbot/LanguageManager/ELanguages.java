package de.leontheking24.discordbot.LanguageManager;

public enum ELanguages {

    de("Deutsch"),
    en("English");

    private String name;

    ELanguages(String name) {
        this.name = name;
    }

    public boolean contains(String value) {
        for(ELanguages languages : ELanguages.values()) {
            if(languages.equals(value.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

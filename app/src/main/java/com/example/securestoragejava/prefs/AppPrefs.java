package com.example.securestoragejava.prefs; // Declares the preferences package requested by the lab.

import android.content.Context; // Provides app context access to private preferences.
import android.content.SharedPreferences; // Provides key-value storage for non-sensitive data.

public final class AppPrefs { // Utility class for clear, non-sensitive SharedPreferences.
    private static final String PREFS_NAME = "app_prefs"; // Names the clear preferences file for UI-only data.
    private static final String KEY_NAME = "pref_name"; // Stores a non-sensitive username preference.
    private static final String KEY_LANG = "pref_lang"; // Stores a non-sensitive language preference.
    private static final String KEY_THEME = "pref_theme"; // Stores a non-sensitive theme preference.

    private AppPrefs() { // Prevents creating instances of this static helper class.
    } // Ends the private constructor.

    private static SharedPreferences prefs(Context context) { // Centralizes access to the preferences file.
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // MODE_PRIVATE keeps this clear prefs file private to the app.
    } // Ends the preferences accessor.

    public static void save(Context context, String name, String lang, String theme, boolean sync) { // Saves non-sensitive UI settings.
        SharedPreferences.Editor editor = prefs(context).edit(); // Opens an editor for the app_prefs file.
        editor.putString(KEY_NAME, name); // Saves only a non-sensitive username value.
        editor.putString(KEY_LANG, lang); // Saves only a non-sensitive language code.
        editor.putString(KEY_THEME, theme); // Saves only a non-sensitive theme choice.
        if (sync) { // Selects commit when the caller needs a synchronous result.
            editor.commit(); // commit writes synchronously and returns success, but may block the UI thread.
        } else { // Selects apply for normal UI saves.
            editor.apply(); // apply writes asynchronously and is preferred for responsive UI updates.
        } // Ends the apply-versus-commit choice.
    } // Ends save.

    public static Triple load(Context context) { // Loads non-sensitive UI settings.
        SharedPreferences sharedPreferences = prefs(context); // Reads from the app-private preferences file.
        String name = sharedPreferences.getString(KEY_NAME, ""); // Defaults the name to empty when no value exists.
        String lang = sharedPreferences.getString(KEY_LANG, "fr"); // Defaults the language to French as required.
        String theme = sharedPreferences.getString(KEY_THEME, "system"); // Defaults the theme to system as required.
        return new Triple(name, lang, theme); // Returns the three clear preferences together.
    } // Ends load.

    public static void clear(Context context) { // Clears all non-sensitive clear preferences.
        prefs(context).edit().clear().apply(); // apply clears asynchronously because no secret-dependent result is exposed.
    } // Ends clear.

    public static final class Triple { // Simple return object for the three non-sensitive preference values.
        public final String name; // Holds the loaded username.
        public final String lang; // Holds the loaded language code.
        public final String theme; // Holds the loaded theme choice.

        public Triple(String name, String lang, String theme) { // Creates an immutable preference tuple.
            this.name = name; // Assigns the username value.
            this.lang = lang; // Assigns the language value.
            this.theme = theme; // Assigns the theme value.
        } // Ends Triple constructor.
    } // Ends Triple.
} // Ends AppPrefs.

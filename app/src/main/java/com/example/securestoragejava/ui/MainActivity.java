package com.example.securestoragejava.ui; // Places the activity in the requested UI package.

import android.os.Bundle; // Provides the saved instance state type for activity startup.
import android.util.Log; // Provides Logcat logging for non-sensitive diagnostics only.
import android.view.View; // Provides the click listener type.
import android.widget.ArrayAdapter; // Binds the offline language list to the Spinner.
import android.widget.Button; // Represents each one-screen action button.
import android.widget.EditText; // Represents username and token input fields.
import android.widget.Spinner; // Represents the fixed language selector.
import android.widget.Switch; // Represents the dark theme preference toggle.
import android.widget.TextView; // Represents the non-sensitive result output.

import androidx.appcompat.app.AppCompatActivity; // Provides the AppCompat activity base class for Empty Views Activity.

import com.example.securestoragejava.R; // Imports generated resource identifiers for this namespace.
import com.example.securestoragejava.cache.CacheStore; // Writes and purges temporary cache files.
import com.example.securestoragejava.external.ExternalAppFilesStore; // Handles app-specific external exports.
import com.example.securestoragejava.files.InternalTextStore; // Handles private internal UTF-8 text files.
import com.example.securestoragejava.files.StudentsJsonStore; // Handles private students.json serialization.
import com.example.securestoragejava.model.Student; // Represents sample student records.
import com.example.securestoragejava.prefs.AppPrefs; // Handles clear non-sensitive preferences.
import com.example.securestoragejava.prefs.SecurePrefs; // Handles encrypted token preferences.

import org.json.JSONException; // Handles JSON write failures cleanly.

import java.io.FileNotFoundException; // Handles absent note files without leaking data.
import java.io.IOException; // Handles storage failures cleanly.
import java.security.GeneralSecurityException; // Handles encryption setup failures cleanly.
import java.util.ArrayList; // Creates sample student lists.
import java.util.List; // Uses list interfaces for student data.

public class MainActivity extends AppCompatActivity { // Single-screen activity for the complete secure storage lab.
    private static final String TAG = "SecureStorageLab"; // Log tag used only for non-sensitive diagnostics.
    private static final String NOTE_FILE = "note.txt"; // Internal private note file name.
    private static final String EXPORT_FILE = "export.txt"; // App-specific external export file name.
    private static final String CACHE_FILE = "last_ui.txt"; // Temporary cache file name for non-sensitive UI state.

    private EditText etName; // Holds the username field reference.
    private Spinner spLang; // Holds the language spinner reference.
    private Switch swDark; // Holds the dark theme switch reference.
    private EditText etToken; // Holds the token input reference, which is never echoed in clear text.
    private TextView tvResult; // Holds the result text reference for safe output.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Initializes the single activity screen.
        super.onCreate(savedInstanceState); // Lets AppCompat restore normal activity state.
        setContentView(R.layout.activity_main); // Loads the one-screen XML layout.
        bindViews(); // Connects Java fields to XML views.
        configureLanguageSpinner(); // Adds the offline language choices fr, en, and ar.
        configureButtons(); // Attaches click handlers for each lab action.
    } // Ends onCreate.

    private void bindViews() { // Finds every UI control by its resource id.
        etName = findViewById(R.id.etName); // Binds the username input.
        spLang = findViewById(R.id.spLang); // Binds the language spinner.
        swDark = findViewById(R.id.swDark); // Binds the dark theme switch.
        etToken = findViewById(R.id.etToken); // Binds the masked token input.
        tvResult = findViewById(R.id.tvResult); // Binds the non-sensitive result label.
    } // Ends bindViews.

    private void configureLanguageSpinner() { // Configures fixed offline language options.
        String[] languages = new String[]{"fr", "en", "ar"}; // Uses the required language codes without network access.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages); // Creates a built-in simple spinner adapter.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Uses a built-in dropdown row layout.
        spLang.setAdapter(adapter); // Attaches the adapter to the spinner.
    } // Ends configureLanguageSpinner.

    private void configureButtons() { // Wires all one-screen button actions.
        Button btnSavePrefs = findViewById(R.id.btnSavePrefs); // Binds the save preferences button locally.
        Button btnLoadPrefs = findViewById(R.id.btnLoadPrefs); // Binds the load preferences button locally.
        Button btnSaveJson = findViewById(R.id.btnSaveJson); // Binds the save JSON button locally.
        Button btnLoadJson = findViewById(R.id.btnLoadJson); // Binds the load JSON button locally.
        Button btnExternalExport = findViewById(R.id.btnExternalExport); // Binds the external export button locally.
        Button btnExternalRead = findViewById(R.id.btnExternalRead); // Binds the external read button locally.
        Button btnClear = findViewById(R.id.btnClear); // Binds the clear all button locally.

        btnSavePrefs.setOnClickListener(new View.OnClickListener() { // Handles saving clear prefs plus encrypted token.
            @Override
            public void onClick(View v) { // Runs when the user taps Save Prefs.
                savePreferences(); // Performs the save operation.
            } // Ends click callback.
        }); // Ends save prefs listener.

        btnLoadPrefs.setOnClickListener(new View.OnClickListener() { // Handles loading clear prefs plus token length.
            @Override
            public void onClick(View v) { // Runs when the user taps Load Prefs.
                loadPreferences(); // Performs the load operation.
            } // Ends click callback.
        }); // Ends load prefs listener.

        btnSaveJson.setOnClickListener(new View.OnClickListener() { // Handles JSON and internal note creation.
            @Override
            public void onClick(View v) { // Runs when the user taps Save JSON.
                saveJson(); // Performs the JSON save operation.
            } // Ends click callback.
        }); // Ends save JSON listener.

        btnLoadJson.setOnClickListener(new View.OnClickListener() { // Handles JSON and internal note loading.
            @Override
            public void onClick(View v) { // Runs when the user taps Load JSON.
                loadJson(); // Performs the JSON load operation.
            } // Ends click callback.
        }); // Ends load JSON listener.

        btnExternalExport.setOnClickListener(new View.OnClickListener() { // Handles app-specific external export.
            @Override
            public void onClick(View v) { // Runs when the user taps External Export.
                exportExternal(); // Performs the external write operation.
            } // Ends click callback.
        }); // Ends external export listener.

        btnExternalRead.setOnClickListener(new View.OnClickListener() { // Handles app-specific external read.
            @Override
            public void onClick(View v) { // Runs when the user taps External Read.
                readExternal(); // Performs the external read operation.
            } // Ends click callback.
        }); // Ends external read listener.

        btnClear.setOnClickListener(new View.OnClickListener() { // Handles complete cleanup.
            @Override
            public void onClick(View v) { // Runs when the user taps Clear All.
                clearAll(); // Performs cleanup without logging secrets.
            } // Ends click callback.
        }); // Ends clear listener.
    } // Ends configureButtons.

    private void savePreferences() { // Saves non-sensitive preferences and encrypted token data.
        String name = etName.getText().toString(); // Reads the non-sensitive username.
        String lang = spLang.getSelectedItem().toString(); // Reads the selected non-sensitive language.
        String theme = swDark.isChecked() ? "dark" : "light"; // Converts the switch state to a non-sensitive theme value.
        String token = etToken.getText().toString(); // Reads the token only to decide whether to encrypt and store it.
        boolean tokenStored = false; // Tracks whether a non-empty token was encrypted.
        try { // Handles storage and crypto errors cleanly.
            AppPrefs.save(this, name, lang, theme, false); // Saves clear non-sensitive values with apply for responsive UI.
            if (token.trim().length() > 0) { // Avoids String.isBlank for API 24 compatibility.
                SecurePrefs.saveToken(this, token); // Saves the token through EncryptedSharedPreferences.
                tokenStored = true; // Records that the token was stored encrypted.
            } // Ends token presence check.
            String cacheContent = "name=" + name + "\nlang=" + lang + "\ntheme=" + theme + "\ntokenLength=" + token.length(); // Stores only token length in temporary cache.
            CacheStore.write(this, CACHE_FILE, cacheContent); // Writes non-sensitive UI state to cache, which Android may delete.
            String message = "Saved prefs: name=" + name + ", lang=" + lang + ", theme=" + theme + ", tokenStoredEncrypted=" + tokenStored; // Builds a safe UI message.
            tvResult.setText(message); // Displays only non-sensitive values and encrypted-token status.
            Log.i(TAG, "Saved prefs name=" + name + ", lang=" + lang + ", theme=" + theme + ", tokenLength=" + token.length()); // Logs only non-sensitive values and token length.
        } catch (GeneralSecurityException e) { // Handles encrypted preferences crypto failures.
            showSafeError("Secure preferences are unavailable."); // Avoids exposing token data in UI.
            Log.e(TAG, "Secure preference save failed without secret data."); // Logs only a safe failure summary.
        } catch (IOException e) { // Handles preference/cache storage failures.
            showSafeError("Storage save failed."); // Avoids exposing file or token content.
            Log.e(TAG, "Preference or cache save failed without secret data."); // Logs only a safe failure summary.
        } // Ends save error handling.
    } // Ends savePreferences.

    private void loadPreferences() { // Loads clear prefs and reports encrypted token length only.
        try { // Handles storage and crypto errors cleanly.
            AppPrefs.Triple triple = AppPrefs.load(this); // Loads non-sensitive preference values.
            String token = SecurePrefs.loadToken(this); // Loads token internally but never displays or logs it.
            int tokenLength = token == null ? 0 : token.length(); // Converts the secret to a safe length value.
            etName.setText(triple.name); // Restores the username field.
            selectLanguage(triple.lang); // Restores the language spinner selection.
            swDark.setChecked("dark".equals(triple.theme)); // Restores the dark switch from the theme value.
            etToken.setText(""); // Leaves the token box empty so the secret is not displayed in clear text.
            String message = "Loaded prefs: name=" + triple.name + ", lang=" + triple.lang + ", theme=" + triple.theme + ", tokenLength=" + tokenLength; // Builds a safe result.
            tvResult.setText(message); // Displays token length instead of token value.
            Log.i(TAG, "Loaded prefs name=" + triple.name + ", lang=" + triple.lang + ", theme=" + triple.theme + ", tokenLength=" + tokenLength); // Logs no secret value.
        } catch (GeneralSecurityException e) { // Handles encrypted preferences crypto failures.
            showSafeError("Secure preferences are unavailable."); // Avoids exposing debug details.
            Log.e(TAG, "Secure preference load failed without secret data."); // Logs only a safe failure summary.
        } catch (IOException e) { // Handles encrypted preference storage failures.
            showSafeError("Storage load failed."); // Avoids exposing file content.
            Log.e(TAG, "Preference load failed without secret data."); // Logs only a safe failure summary.
        } // Ends load error handling.
    } // Ends loadPreferences.

    private void selectLanguage(String lang) { // Selects a spinner item by language code.
        for (int i = 0; i < spLang.getCount(); i++) { // Checks each offline spinner item.
            if (spLang.getItemAtPosition(i).toString().equals(lang)) { // Finds the matching language code.
                spLang.setSelection(i); // Updates the spinner selection.
                return; // Stops after the matching language is selected.
            } // Ends match check.
        } // Ends spinner loop.
        spLang.setSelection(0); // Defaults to fr when a saved value is missing or unexpected.
    } // Ends selectLanguage.

    private void saveJson() { // Saves sample students and a private note file.
        ArrayList<Student> students = new ArrayList<>(); // Creates the sample list.
        students.add(new Student(1, "Amina", 20)); // Adds the first required sample student.
        students.add(new Student(2, "Omar", 21)); // Adds the second required sample student.
        students.add(new Student(3, "Sara", 19)); // Adds the third required sample student.
        try { // Handles JSON and file errors cleanly.
            StudentsJsonStore.save(this, students); // Serializes sample students to private students.json.
            InternalTextStore.writeUtf8(this, NOTE_FILE, "Internal note: non-sensitive lab data only."); // Writes a UTF-8 note in private internal storage.
            tvResult.setText("Saved students.json with " + students.size() + " students and note.txt."); // Displays a safe save result.
            Log.i(TAG, "Saved JSON studentsCount=" + students.size()); // Logs only non-sensitive count data.
        } catch (IOException e) { // Handles internal file write errors.
            showSafeError("JSON storage failed."); // Reports safely without file content.
            Log.e(TAG, "JSON file save failed without sensitive data."); // Logs safe diagnostic text only.
        } catch (JSONException e) { // Handles JSON serialization errors.
            showSafeError("JSON creation failed."); // Reports safely without data dumps.
            Log.e(TAG, "JSON serialization failed without sensitive data."); // Logs safe diagnostic text only.
        } // Ends save JSON error handling.
    } // Ends saveJson.

    private void loadJson() { // Loads students and note data from private internal storage.
        List<Student> students = StudentsJsonStore.load(this); // Returns an empty list if the JSON is absent or corrupted.
        String note = ""; // Defaults note text to empty.
        try { // Handles absent note files separately.
            note = InternalTextStore.readUtf8(this, NOTE_FILE); // Reads the private UTF-8 note file.
        } catch (FileNotFoundException e) { // Handles first-run or cleared state.
            note = "note.txt is absent."; // Reports absent note without treating it as a secret leak.
        } catch (IOException e) { // Handles unreadable note files.
            note = "note.txt could not be read."; // Reports a safe generic read failure.
        } // Ends note read handling.
        StringBuilder builder = new StringBuilder(); // Builds the result text.
        builder.append("Student count: ").append(students.size()).append("\n"); // Shows the parsed student count.
        for (Student student : students) { // Adds each parsed student to the display.
            builder.append(student.id).append(" - ").append(student.name).append(" - ").append(student.age).append("\n"); // Displays non-sensitive sample student data.
        } // Ends student display loop.
        builder.append("Note: ").append(note); // Displays the non-sensitive note content.
        tvResult.setText(builder.toString()); // Updates the result area with JSON details.
        Log.i(TAG, "Loaded JSON studentsCount=" + students.size()); // Logs only non-sensitive count data.
    } // Ends loadJson.

    private void exportExternal() { // Writes a non-sensitive app-specific external file.
        String name = etName.getText().toString(); // Reads non-sensitive username for export context.
        String lang = spLang.getSelectedItem().toString(); // Reads non-sensitive language for export context.
        String content = "SecureStorageLabJava export\nname=" + name + "\nlang=" + lang + "\ncontainsSecrets=false"; // Builds export content without token data.
        String path = ExternalAppFilesStore.write(this, EXPORT_FILE, content); // Writes to /Android/data/package/files without public storage APIs.
        if (path == null) { // Handles unavailable app-specific external storage.
            showSafeError("External app-specific export failed."); // Reports safe failure.
            Log.e(TAG, "External export failed without sensitive data."); // Logs safe diagnostic text only.
        } else { // Handles successful export.
            tvResult.setText("Exported non-sensitive data to:\n" + path); // Shows the absolute path for inspection.
            Log.i(TAG, "External export path=" + path); // Logs only the app-specific path, not secrets.
        } // Ends export result handling.
    } // Ends exportExternal.

    private void readExternal() { // Reads the non-sensitive app-specific external export.
        String content = ExternalAppFilesStore.read(this, EXPORT_FILE); // Reads content from app-specific external files.
        if (content == null) { // Handles absent or unreadable export files.
            tvResult.setText("No external app-specific export found."); // Reports safe empty state.
            Log.i(TAG, "External export read returned no content."); // Logs only safe status.
        } else { // Handles successful external read.
            tvResult.setText("External content:\n" + content); // Displays only the non-sensitive export content.
            Log.i(TAG, "External export read length=" + content.length()); // Logs only content length.
        } // Ends read result handling.
    } // Ends readExternal.

    private void clearAll() { // Clears all lab storage areas and resets the UI.
        try { // Handles secure clear failures cleanly.
            AppPrefs.clear(this); // Clears clear non-sensitive SharedPreferences.
            SecurePrefs.clear(this); // Clears encrypted token preferences without exposing the token.
            StudentsJsonStore.delete(this); // Deletes private students.json.
            InternalTextStore.delete(this, NOTE_FILE); // Deletes private note.txt.
            ExternalAppFilesStore.delete(this, EXPORT_FILE); // Deletes the app-specific external export.
            int purged = CacheStore.purge(this); // Manually purges temporary cache files.
            etName.setText(""); // Resets username input.
            selectLanguage("fr"); // Resets language to the default.
            swDark.setChecked(false); // Resets the theme switch to light.
            etToken.setText(""); // Clears the token input without showing stored secret data.
            tvResult.setText("Cleanup complete. Cache files purged: " + purged); // Displays safe cleanup status.
            Log.i(TAG, "Cleanup complete cachePurged=" + purged); // Logs cleanup without sensitive data.
        } catch (GeneralSecurityException e) { // Handles encrypted preferences crypto failures during clear.
            showSafeError("Secure cleanup failed."); // Reports safely without secret details.
            Log.e(TAG, "Secure cleanup failed without secret data."); // Logs safe diagnostic text only.
        } catch (IOException e) { // Handles encrypted preference storage failures during clear.
            showSafeError("Cleanup storage failed."); // Reports safely without file content.
            Log.e(TAG, "Cleanup storage failed without secret data."); // Logs safe diagnostic text only.
        } // Ends cleanup error handling.
    } // Ends clearAll.

    private void showSafeError(String message) { // Displays a generic error that never includes secrets.
        tvResult.setText(message); // Shows only safe human-readable status.
    } // Ends showSafeError.
} // Ends MainActivity.

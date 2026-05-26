package com.example.securestoragejava.prefs; // Declares the secure preferences package requested by the lab.

import android.content.Context; // Provides app context for encrypted preferences.
import android.content.SharedPreferences; // Provides the common preferences API backed by encryption here.

import androidx.security.crypto.EncryptedSharedPreferences; // Stores encrypted keys and values.
import androidx.security.crypto.MasterKey; // Creates or loads the Android Keystore-backed master key.

import java.io.IOException; // Represents storage and crypto initialization failures.
import java.security.GeneralSecurityException; // Represents cryptographic setup failures.

public final class SecurePrefs { // Utility class for token storage.
    private static final String PREFS_NAME = "secure_prefs"; // Names the encrypted preferences file.
    private static final String KEY_TOKEN = "secure_api_token"; // Names the encrypted token entry.

    private SecurePrefs() { // Prevents creating instances of this static helper class.
    } // Ends the private constructor.

    private static SharedPreferences prefs(Context context) throws GeneralSecurityException, IOException { // Builds encrypted preferences safely.
        MasterKey masterKey = new MasterKey.Builder(context) // Starts a MasterKey builder bound to this app context.
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM) // Uses AES256_GCM for the Android Keystore-backed master key.
                .build(); // Creates or retrieves the key material managed by Android Keystore.
        return EncryptedSharedPreferences.create( // Creates a SharedPreferences implementation that encrypts content.
                context, // Uses the app context so storage is scoped to this app.
                PREFS_NAME, // Stores encrypted data in secure_prefs.
                masterKey, // Uses the Keystore-backed MasterKey to protect preference encryption keys.
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // Encrypts preference keys deterministically with AES256_SIV.
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // Encrypts preference values with AES256_GCM.
        ); // Ends encrypted preferences creation.
    } // Ends encrypted preferences accessor.

    public static void saveToken(Context context, String token) throws GeneralSecurityException, IOException { // Saves a token without logging it.
        prefs(context).edit().putString(KEY_TOKEN, token).apply(); // Stores the token encrypted because tokens must never be kept in clear text.
    } // Ends saveToken.

    public static String loadToken(Context context) throws GeneralSecurityException, IOException { // Loads the token for internal use only.
        return prefs(context).getString(KEY_TOKEN, ""); // Returns the secret to code, not to logs or clear UI output.
    } // Ends loadToken.

    public static void clear(Context context) throws GeneralSecurityException, IOException { // Removes encrypted token data.
        prefs(context).edit().clear().apply(); // Clears encrypted values without exposing secret contents.
    } // Ends clear.
} // Ends SecurePrefs.

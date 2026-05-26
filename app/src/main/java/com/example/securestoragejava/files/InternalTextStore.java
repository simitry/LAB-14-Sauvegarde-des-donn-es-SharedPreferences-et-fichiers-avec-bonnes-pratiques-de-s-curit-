package com.example.securestoragejava.files; // Declares the internal file storage package requested by the lab.

import android.content.Context; // Provides internal file APIs scoped to the app.

import java.io.ByteArrayOutputStream; // Collects bytes without using API 26 readAllBytes.
import java.io.File; // Represents an internal file path for deletion.
import java.io.FileInputStream; // Reads bytes from an app-private internal file.
import java.io.FileOutputStream; // Writes bytes to an app-private internal file.
import java.io.IOException; // Reports file errors without exposing content.
import java.nio.charset.StandardCharsets; // Provides UTF-8 constants available on API 24.

public final class InternalTextStore { // Utility class for app-private text files.
    private InternalTextStore() { // Prevents creating instances of this static helper class.
    } // Ends the private constructor.

    public static void writeUtf8(Context context, String fileName, String content) throws IOException { // Writes text using UTF-8.
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8); // Converts Java text to UTF-8 bytes consistently.
        FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE); // MODE_PRIVATE keeps internal storage private to this app.
        try { // Ensures the stream closes even if writing fails.
            outputStream.write(bytes); // Writes the UTF-8 bytes to internal storage.
        } finally { // Always runs cleanup.
            outputStream.close(); // Closes the file handle.
        } // Ends try/finally.
    } // Ends writeUtf8.

    public static String readUtf8(Context context, String fileName) throws IOException { // Reads text using UTF-8.
        FileInputStream inputStream = context.openFileInput(fileName); // Opens the app-private internal file.
        try { // Ensures the stream closes even if reading fails.
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(); // Buffers bytes for API 24 compatibility.
            byte[] chunk = new byte[4096]; // Uses a small reusable read buffer.
            int read; // Tracks the number of bytes read from each loop.
            while ((read = inputStream.read(chunk)) != -1) { // Reads until the end of the file.
                buffer.write(chunk, 0, read); // Appends the bytes just read.
            } // Ends read loop.
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8); // Decodes bytes as UTF-8 text.
        } finally { // Always runs cleanup.
            inputStream.close(); // Closes the file handle.
        } // Ends try/finally.
    } // Ends readUtf8.

    public static boolean delete(Context context, String fileName) { // Deletes one app-private internal file.
        File file = new File(context.getFilesDir(), fileName); // Builds a path inside the private files directory.
        return !file.exists() || file.delete(); // Treats an absent file as already deleted.
    } // Ends delete.
} // Ends InternalTextStore.

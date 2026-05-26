package com.example.securestoragejava.external; // Declares the external storage package requested by the lab.

import android.content.Context; // Provides access to app-specific external files.

import java.io.ByteArrayOutputStream; // Collects bytes without using API 26 readAllBytes.
import java.io.File; // Represents external app-specific files.
import java.io.FileInputStream; // Reads bytes from app-specific external storage.
import java.io.FileOutputStream; // Writes bytes to app-specific external storage.
import java.io.IOException; // Handles external storage failures cleanly.
import java.nio.charset.StandardCharsets; // Provides UTF-8 encoding for API 24 compatible text operations.

public final class ExternalAppFilesStore { // Utility class for app-specific external files.
    private ExternalAppFilesStore() { // Prevents creating instances of this static helper class.
    } // Ends the private constructor.

    private static File file(Context context, String fileName) { // Builds a file inside app-specific external storage.
        File dir = context.getExternalFilesDir(null); // Uses app-specific external storage, not public shared storage.
        if (dir == null) { // Handles unavailable external storage.
            return null; // Returns null so callers can report a safe failure.
        } // Ends availability check.
        return new File(dir, fileName); // Creates the target path under /Android/data/package/files.
    } // Ends file helper.

    public static String write(Context context, String fileName, String content) { // Writes non-sensitive export content.
        File target = file(context, fileName); // Resolves the app-specific external file path.
        if (target == null) { // Checks whether external app storage exists.
            return null; // Reports failure without leaking data.
        } // Ends null guard.
        try { // Handles IO failures without crashing.
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8); // Encodes export text as UTF-8.
            FileOutputStream outputStream = new FileOutputStream(target); // Opens the external app file for writing.
            try { // Ensures cleanup even if writing fails.
                outputStream.write(bytes); // Writes only non-sensitive content to external app-specific storage.
            } finally { // Always runs cleanup.
                outputStream.close(); // Closes the file handle.
            } // Ends inner try/finally.
            return target.getAbsolutePath(); // Returns the path so the user can inspect it in Device File Explorer.
        } catch (IOException e) { // Handles storage errors safely.
            return null; // Reports failure without exposing file content.
        } // Ends error handling.
    } // Ends write.

    public static String read(Context context, String fileName) { // Reads non-sensitive external app-specific content.
        File target = file(context, fileName); // Resolves the app-specific external file path.
        if (target == null || !target.exists()) { // Handles unavailable storage or absent file.
            return null; // Reports no content safely.
        } // Ends existence check.
        try { // Handles IO failures without crashing.
            FileInputStream inputStream = new FileInputStream(target); // Opens the file for reading.
            try { // Ensures cleanup even if reading fails.
                ByteArrayOutputStream buffer = new ByteArrayOutputStream(); // Buffers bytes for API 24 compatibility.
                byte[] chunk = new byte[4096]; // Uses a reusable byte buffer.
                int read; // Tracks bytes read per iteration.
                while ((read = inputStream.read(chunk)) != -1) { // Reads until the end of the export file.
                    buffer.write(chunk, 0, read); // Appends each read chunk.
                } // Ends read loop.
                return new String(buffer.toByteArray(), StandardCharsets.UTF_8); // Decodes file content as UTF-8.
            } finally { // Always runs cleanup.
                inputStream.close(); // Closes the file handle.
            } // Ends inner try/finally.
        } catch (IOException e) { // Handles read failures safely.
            return null; // Reports failure without exposing partial data.
        } // Ends error handling.
    } // Ends read.

    public static boolean delete(Context context, String fileName) { // Deletes one app-specific external file.
        File target = file(context, fileName); // Resolves the external app-specific file path.
        return target == null || !target.exists() || target.delete(); // Treats unavailable or absent files as already deleted.
    } // Ends delete.
} // Ends ExternalAppFilesStore.

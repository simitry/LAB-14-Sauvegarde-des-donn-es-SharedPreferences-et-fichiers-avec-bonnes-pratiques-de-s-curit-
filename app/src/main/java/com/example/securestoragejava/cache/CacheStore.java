package com.example.securestoragejava.cache; // Declares the cache package requested by the lab.

import android.content.Context; // Provides access to the app cache directory.

import java.io.ByteArrayOutputStream; // Collects bytes without using API 26 readAllBytes.
import java.io.File; // Represents cache files.
import java.io.FileInputStream; // Reads cache file bytes.
import java.io.FileOutputStream; // Writes cache file bytes.
import java.io.IOException; // Reports cache IO failures.
import java.nio.charset.StandardCharsets; // Provides UTF-8 for API 24 compatible text operations.

public final class CacheStore { // Utility class for temporary cache data.
    private CacheStore() { // Prevents creating instances of this static helper class.
    } // Ends the private constructor.

    public static void write(Context context, String fileName, String content) throws IOException { // Writes temporary cache text.
        File file = new File(context.getCacheDir(), fileName); // cacheDir is temporary and can be deleted by Android.
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8); // Encodes cache text as UTF-8.
        FileOutputStream outputStream = new FileOutputStream(file); // Opens the cache file for writing.
        try { // Ensures cleanup even when writing fails.
            outputStream.write(bytes); // Writes UTF-8 bytes to the cache file.
        } finally { // Always runs cleanup.
            outputStream.close(); // Closes the cache file handle.
        } // Ends try/finally.
    } // Ends write.

    public static String read(Context context, String fileName) throws IOException { // Reads temporary cache text.
        File file = new File(context.getCacheDir(), fileName); // Reads from app cache, which is not durable storage.
        FileInputStream inputStream = new FileInputStream(file); // Opens the cache file for reading.
        try { // Ensures cleanup even when reading fails.
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(); // Buffers bytes for API 24 compatibility.
            byte[] chunk = new byte[4096]; // Uses a reusable byte buffer.
            int read; // Tracks bytes read per iteration.
            while ((read = inputStream.read(chunk)) != -1) { // Reads until end of cache file.
                buffer.write(chunk, 0, read); // Appends each chunk to memory.
            } // Ends read loop.
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8); // Decodes the cache file as UTF-8 text.
        } finally { // Always runs cleanup.
            inputStream.close(); // Closes the cache file handle.
        } // Ends try/finally.
    } // Ends read.

    public static int purge(Context context) { // Deletes cache files manually.
        File[] files = context.getCacheDir().listFiles(); // Lists current cache files, which may already be empty.
        int deleted = 0; // Counts successfully removed cache entries.
        if (files == null) { // Handles rare cases where the cache directory cannot be listed.
            return 0; // Reports no deletions when nothing can be listed.
        } // Ends null guard.
        for (File file : files) { // Visits each cache entry.
            if (file.isFile() && file.delete()) { // Deletes regular cache files and skips directories.
                deleted++; // Counts the deleted file.
            } // Ends delete check.
        } // Ends purge loop.
        return deleted; // Returns the number of cache files deleted manually.
    } // Ends purge.
} // Ends CacheStore.

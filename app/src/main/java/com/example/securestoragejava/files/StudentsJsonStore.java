package com.example.securestoragejava.files; // Declares the JSON storage package requested by the lab.

import android.content.Context; // Provides access to app-private internal storage.

import com.example.securestoragejava.model.Student; // Uses the lab's Student model.

import org.json.JSONArray; // Serializes and parses student arrays.
import org.json.JSONException; // Handles malformed JSON without crashing the lab.
import org.json.JSONObject; // Serializes and parses individual student objects.

import java.io.FileNotFoundException; // Detects absent files and returns an empty list.
import java.io.IOException; // Handles storage errors cleanly.
import java.util.ArrayList; // Provides a mutable list result.
import java.util.List; // Provides the public list type.

public final class StudentsJsonStore { // Utility class for students.json.
    private static final String FILE_NAME = "students.json"; // Uses the required internal JSON file name.

    private StudentsJsonStore() { // Prevents creating instances of this static helper class.
    } // Ends the private constructor.

    public static void save(Context context, List<Student> students) throws IOException, JSONException { // Saves students to JSON.
        JSONArray array = new JSONArray(); // Creates the JSON array container.
        for (Student student : students) { // Visits each student to serialize it.
            JSONObject object = new JSONObject(); // Creates one JSON object per student.
            object.put("id", student.id); // Writes the student id field.
            object.put("name", student.name); // Writes the student name field.
            object.put("age", student.age); // Writes the student age field.
            array.put(object); // Adds the serialized student to the JSON array.
        } // Ends serialization loop.
        InternalTextStore.writeUtf8(context, FILE_NAME, array.toString(2)); // Writes formatted JSON through the shared UTF-8 helper.
    } // Ends save.

    public static List<Student> load(Context context) { // Loads students from JSON.
        ArrayList<Student> students = new ArrayList<>(); // Starts with an empty list for absent or corrupted files.
        try { // Parses carefully so bad JSON does not crash the lab.
            String json = InternalTextStore.readUtf8(context, FILE_NAME); // Reads UTF-8 JSON from internal storage.
            JSONArray array = new JSONArray(json); // Parses the JSON array text.
            for (int i = 0; i < array.length(); i++) { // Iterates over each JSON element.
                JSONObject object = array.getJSONObject(i); // Reads the current student object.
                int id = object.optInt("id", 0); // Parses id with a safe fallback.
                String name = object.optString("name", ""); // Parses name with a safe fallback.
                int age = object.optInt("age", 0); // Parses age with a safe fallback.
                students.add(new Student(id, name, age)); // Adds the parsed student to the result.
            } // Ends parse loop.
        } catch (FileNotFoundException e) { // Handles the first-run case where students.json does not exist.
            return new ArrayList<>(); // Returns empty because absent files are normal before saving.
        } catch (IOException e) { // Handles read failures without exposing file content.
            return new ArrayList<>(); // Returns empty because unreadable data should not crash the UI.
        } catch (JSONException e) { // Handles corrupted JSON safely.
            return new ArrayList<>(); // Returns empty because corrupted data is treated as recoverable.
        } // Ends load error handling.
        return students; // Returns parsed students when the file is valid.
    } // Ends load.

    public static boolean delete(Context context) { // Deletes students.json.
        return InternalTextStore.delete(context, FILE_NAME); // Reuses the internal storage helper to avoid duplicate IO code.
    } // Ends delete.
} // Ends StudentsJsonStore.

package com.example.securestoragejava.model; // Declares the model package requested by the lab.

public class Student { // Represents one student row saved to and loaded from JSON.
    public final int id; // Stores the stable numeric identifier for the student.
    public final String name; // Stores the student's display name for non-sensitive lab data.
    public final int age; // Stores the student's age for the JSON sample.

    public Student(int id, String name, int age) { // Builds an immutable Student instance from caller-provided values.
        this.id = id; // Copies the identifier into the public final field.
        this.name = name; // Copies the name into the public final field.
        this.age = age; // Copies the age into the public final field.
    } // Ends the Student constructor.
} // Ends the Student model class.

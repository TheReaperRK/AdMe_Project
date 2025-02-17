package cat.copernic.project3_group4.core.models;


import android.util.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cat.copernic.project3_group4.core.utils.enums.Roles;

public class User {

    /**
     * Unique identifier for the user. It is the primary key and auto-generated.
     */

    private Long id;

    /**
     * Name of the user. Cannot be null.
     */
    private String name;

    /**
     * Email of the user. Cannot be null.
     */
    private String email;

    /**
     * Phone number of the user. Cannot be null.
     */
    private String phoneNumber;

    /**
     * Binary content of the category image. Stored as a large object (LOB).
     */

    private String image;


    /**
     * Password of the user. Cannot be null.
     */
    private String word;

    /**
     * Status of the user. Indicates whether the user is active or not.
     */
    private boolean status;


    private Roles role;

    /**
     * List of advertisements associated with this user.
     * This is a one-to-many relationship with Ad entities.
     */
    private List<Ad> ads = new ArrayList<>();


    public User(String name, String email, String phoneNumber, String word, boolean status, Roles role) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.word = word;
        this.status = status;
        this.role = role;
    }

    public User(String name, String email, String phoneNumber, String word, boolean status, Roles role, List<Ad> ads) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.word = word;
        this.status = status;
        this.role = role;
        this.ads = ads;
    }

    public User(String name, String email, String phoneNumber, String image, String word, boolean status, Roles role, List<Ad> ads) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.word = word;
        this.status = status;
        this.role = role;
        this.ads = ads;
    }

    public User(Long id, String name, String email,  String phoneNumber, boolean status, Roles role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.role = role;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return status == user.status &&
                Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(word, user.word) &&
                role == user.role;  // Excluye ads de la comparación

    }

    public byte[] getImageBytes() {
        return image != null ? Base64.decode(image, Base64.DEFAULT) : null;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Roles getRole() {
        return role;
    }



}



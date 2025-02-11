package cat.copernic.project3_group4.core.models;

import java.util.ArrayList;
import java.util.List;

public class Category {

    /**
     * Unique identifier for the category. It is the primary key and auto-generated.
     */
    private Long id;

    /**
     * Name of the category. Must be unique and cannot be null.
     */
    private String name;

    /**
     * Description of the category. Must be unique and cannot be null.
     */
    private String description;

    /**
     * Binary content of the category image. Stored as a large object (LOB).
     */
    private byte[] image;

    /**
     * Indicates whether the category is a proposal. Must be unique and cannot be null.
     */
    private boolean proposal;

    /**
     * List of advertisements associated with this category.
     * This is a one-to-many relationship with Ad entities.
     */
    private List<Ad> ads = new ArrayList<>();
}

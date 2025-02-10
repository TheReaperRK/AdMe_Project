package cat.copernic.project3_group4.core.models;

import java.time.LocalDate;

public class Ad {

    /**
     * Unique identifier for the Ad. It is the primary key and auto-generated.
     */
    private Long id;

    /**
     * Title of the advertisement. Must be unique and cannot be null.
     */
    private String title;

    /**
     * Description of the advertisement. Must be unique and cannot be null.
     */
    private String description;

    /**
     * Binary content of the advertisement image. Stored as a large object (LOB).
     */
    private byte[] data;

    /**
     * Price of the advertisement. Must be unique and cannot be null.
     */
    private double price;

    /**
     * Date when the advertisement was created. Must be unique and cannot be null.
     */
    private LocalDate creationDate;

    /**
     * The author of the advertisement. It is a many-to-one relationship with User.
     */

    private User author;

    /**
     * The category to which the advertisement belongs. It is a many-to-one relationship with Category.
     */
    private Category category;
}

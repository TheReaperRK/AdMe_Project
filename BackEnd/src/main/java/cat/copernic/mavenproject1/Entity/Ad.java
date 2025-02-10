package cat.copernic.mavenproject1.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an advertisement (Ad) in the AdMe application.
 * This entity contains details about an advertisement, including title,
 * description, image data, price, creation date, author, and category.
 */
@Entity
@Table(name = "ads")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ad {
    
    /**
     * Unique identifier for the Ad. It is the primary key and auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Title of the advertisement. Must be unique and cannot be null.
     */
    @Column(nullable = false)
    private String title;
    
    /**
     * Description of the advertisement. Must be unique and cannot be null.
     */
    @Column(nullable = false)
    private String description;
    
    /**
     * Binary content of the advertisement image. Stored as a large object (LOB).
     */
    @Lob
    @Column(nullable = false)
    private byte[] data;
    
    /**
     * Price of the advertisement. Must be unique and cannot be null.
     */
    @Column(nullable = false)
    private double price;
    
    /**
     * Date when the advertisement was created. Must be unique and cannot be null.
     */
    @Column(nullable = false)
    private LocalDate creationDate;
    
    /**
     * The author of the advertisement. It is a many-to-one relationship with User.
     */
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    /**
     * The category to which the advertisement belongs. It is a many-to-one relationship with Category.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
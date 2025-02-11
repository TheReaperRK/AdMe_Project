package cat.copernic.mavenproject1.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a category in the AdMe application.
 * Each category has a unique name, description, an image, and can be marked as a proposal.
 * It also maintains a list of advertisements (ads) that belong to this category.
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /**
     * Unique identifier for the category. It is the primary key and auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the category. Must be unique and cannot be null.
     */
    @Column(nullable = false)
    private String name;
    
    /**
     * Description of the category. Must be unique and cannot be null.
     */
    @Column(nullable = false)
    private String description;
    
    /**
     * Binary content of the category image. Stored as a large object (LOB).
     */
    @Lob
    @Column(nullable = false)
    private byte[] image; 

    /**
     * Indicates whether the category is a proposal. Must be unique and cannot be null.
     */
    @Column(nullable = false)
    private boolean proposal;

    /**
     * List of advertisements associated with this category.
     * This is a one-to-many relationship with Ad entities.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Ad> ads = new ArrayList<>();
    


}

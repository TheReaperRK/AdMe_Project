package cat.copernic.mavenproject1.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;

/**
 * Represents an advertisement (Ad) in the AdMe application. This entity
 * contains details about an advertisement, including title, description, image
 * data, price, creation date, author, and category.
 */
@Entity
@Table(name = "ads")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
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
     * Binary content of the advertisement image. Stored as a large object
     * (LOB).
     */
    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] data;

    /**
     * Price of the advertisement. Must be unique and cannot be null.
     */
    @Column(nullable = false)
    private double price;

    /**
     * Date when the advertisement was created. Must be unique and cannot be
     * null.
     */
    @Column(nullable = false)
    private LocalDate creationDate;

    /**
     * The author of the advertisement. It is a many-to-one relationship with
     * User.
     */
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    //Constructors
    public Ad() {
    }

    public Ad(Long id, String title, String description, byte[] data, double price, LocalDate creationDate, User author, Category category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.data = data;
        this.price = price;
        this.creationDate = creationDate;
        this.author = author;
        this.category = category;
    }

    public Ad(String title, String description, byte[] data, double price, LocalDate creationDate, User author, Category category) {
        this.title = title;
        this.description = description;
        this.data = data;
        this.price = price;
        this.creationDate = creationDate;
        this.author = author;
        this.category = category;
    }
    //Geters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}

package cat.copernic.project3_group4.core.models;

import java.time.LocalDate;
import java.util.Base64;

public class Ad {
    private Long id;
    private String title;
    private String description;
    private String data; // Base64 encoded string
    private double price;
    private String creationDate; // Convertido a String
    private User author;
    private Category category;

    public Ad() {}

    public Ad(String title, String description, String data, double price, String creationDate, User author, Category category) {
        this.title = title;
        this.description = description;
        this.data = data;
        this.price = price;
        this.creationDate = creationDate;
        this.author = author;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCreationDate() { return creationDate; } // Ahora es String
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
package cat.copernic.project3_group4.core.models;

import android.util.Base64;

public class Category {

    /**
     * Unique identifier for the category.
     */
    private Long id;

    /**
     * Name of the category.
     */
    private String name;

    /**
     * Description of the category.
     */
    private String description;

    /**
     * Image URL or Base64 string.
     */
    private String image;

    /**
     * Indicates whether the category is a proposal.
     */
    private boolean proposal;

    // Constructor vacío requerido por Retrofit
    public Category() {
    }

    // Constructor completo
    public Category(String name, String description, String image, boolean proposal) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.proposal = proposal;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isProposal() {
        return proposal;
    }

    public void setProposal(boolean proposal) {
        this.proposal = proposal;
    }

    public byte[] getImageBytes() {
        return image != null ? Base64.decode(image, Base64.DEFAULT) : null;
    }
}

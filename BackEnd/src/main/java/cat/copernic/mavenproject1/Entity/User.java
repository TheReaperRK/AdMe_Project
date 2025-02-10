package cat.copernic.mavenproject1.Entity;

import cat.copernic.mavenproject1.enums.Roles;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user in the AdMe application.
 * Each user has a unique name, email, password, status, and role.
 * Users can also have multiple advertisements (ads) associated with them.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Unique identifier for the user. It is the primary key and auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the user. Cannot be null.
     */
    @Column(nullable = false)
    private String name;
    
    /**
     * Email of the user. Cannot be null.
     */
    @Column(nullable = false)
    private String email;
    
     /**
     * Phone number of the user. Cannot be null.
     */
    @Column(nullable = false)
    private String phoneNumber;
    
    /**
     * Password of the user. Cannot be null.
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * Status of the user. Indicates whether the user is active or not.
     */
    @Column(nullable = false)
    private boolean status;
    
    /**
     * Role of the user. Defines the access level within the application.
     * Enumeration type: {@link Role}.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;

    /**
     * List of advertisements associated with this user.
     * This is a one-to-many relationship with Ad entities.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ad> ads = new ArrayList<>();
}

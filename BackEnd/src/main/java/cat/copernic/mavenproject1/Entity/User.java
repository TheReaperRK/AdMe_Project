    package cat.copernic.mavenproject1.Entity;

    import cat.copernic.mavenproject1.enums.Roles;
import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import java.util.ArrayList;
    import java.util.List;
import java.util.Objects;
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
        @Column(nullable = false, unique = true)
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
        private String word;

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
        @JsonManagedReference
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
    }



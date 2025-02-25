package cat.copernic.mavenproject1.repository;

import cat.copernic.mavenproject1.Entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    Category findByName(String name);
    
    List<Category> findByProposalTrue();
}

package cat.copernic.mavenproject1.repository;

import cat.copernic.mavenproject1.Entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Ad
 */
@Repository
public interface AdRepo extends JpaRepository<Ad, Long> {

}

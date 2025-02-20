package cat.copernic.mavenproject1.logic;

import cat.copernic.mavenproject1.Entity.Ad;
import cat.copernic.mavenproject1.Entity.Category;
import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.repository.AdRepo;
import cat.copernic.mavenproject1.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;
import java.util.ArrayList;
import java.util.List;
import static org.hibernate.internal.CoreLogging.logger;
import static org.hibernate.internal.HEMLogging.logger;

/**
 * Lógica de negocio para la entidad Ad
 */
@Service
public class AdLogic {

    @Autowired
    private AdRepo adRepo;
    
    @Autowired
    private CategoryRepo categoryRepo;

    @Transactional
    public Ad getAdById(Long id) {
        Ad ad = adRepo.findById(id).orElse(null);
        if (ad != null) {
            Hibernate.initialize(ad.getCategory());
            Hibernate.initialize(ad.getAuthor());
            Hibernate.initialize(ad.getAuthor().getRole());
            Hibernate.initialize(ad.getAuthor().getAds()); // Inicializa ads

            // 💡 Evitar problema creando un nuevo objeto con una lista vacía de ads
            User authorClean = new User(
                ad.getAuthor().getName(),
                ad.getAuthor().getEmail(),
                ad.getAuthor().getPhoneNumber(),
                ad.getAuthor().getWord(),
                ad.getAuthor().isStatus(),
                ad.getAuthor().getRole()
            );

            // 💡 Asegurar que ads esté inicializado en authorClean
            authorClean.setAds(new ArrayList<>()); 

            Ad adClean = new Ad(
                ad.getId(),
                ad.getTitle(),
                ad.getDescription(),
                ad.getData(),
                ad.getPrice(),
                ad.getCreationDate(),
                authorClean,
                ad.getCategory()
            );

            return adClean;
        }
        return null;
    }


        public List<Ad> findAllAds() {
            return adRepo.findAll();
        }

        public boolean existsById(Long id) {
            return adRepo.existsById(id);
        }

        public void deleteAdById(Long id) {
            
            System.out.println("Se esta borrando ad by id " + id);
            adRepo.deleteById(id);
        }

        public Long saveAd(Ad ad) {

            Ad savedAd = adRepo.save(ad);
            return savedAd.getId();
        }
    public Long updateAd(Ad ad) {
        try {
            Ad oldAd = getAdById(ad.getId());
            if (oldAd == null) {
                throw new IllegalArgumentException("El anuncio con ID " + ad.getId() + " no existe.");
            }

            // Actualizar solo los campos necesarios
            oldAd.setAuthor(ad.getAuthor());
            oldAd.setCategory(ad.getCategory());
            oldAd.setCreationDate(ad.getCreationDate());
            oldAd.setData(ad.getData());
            oldAd.setDescription(ad.getDescription());
            oldAd.setPrice(ad.getPrice());
            oldAd.setTitle(ad.getTitle());

            adRepo.save(oldAd);
            return oldAd.getId();
        } catch (IllegalArgumentException e) {
            throw e; // Propaga el error si el anuncio no existe
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el anuncio con ID " + ad.getId(), e);
        }
    }



        public List<Ad> findAdsByCategory(Long categoryId) {
            return adRepo.findByCategory_Id(categoryId); // 🔥 Método corregido
        }

        public List<Ad> findAdsByPriceRange(double minPrice, double maxPrice) {
            return adRepo.findByPriceBetween(minPrice, maxPrice);
        }

        // ✅ Nuevo método para filtrar por categoría y precio
        public List<Ad> findAdsFiltered(Long categoryId, double minPrice, double maxPrice) {
            return adRepo.findByCategory_IdAndPriceBetween(categoryId, minPrice, maxPrice);
        }

        // ✅ Nuevo método para obtener todas las categorías
        public List<Category> getAllCategories() {
            return categoryRepo.findAll();
        }
        public List<Ad> findAdsByUser(Long userId) {
        return adRepo.findByAuthor_Id(userId);
    }

}
package cat.copernic.mavenproject1.logic;

import cat.copernic.mavenproject1.Entity.Ad;
import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.repository.AdRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lógica de negocio para la entidad Ad
 */
@Service
public class AdLogic {

    @Autowired
    private AdRepo adRepo;

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

        // 💡 Asegurar que `ads` esté inicializado en `authorClean`
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
        adRepo.deleteById(id);
    }

    public Long saveAd(Ad ad) {
        Ad savedAd = adRepo.save(ad);
        return savedAd.getId();
    }
}

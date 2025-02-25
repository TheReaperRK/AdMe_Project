package cat.copernic.mavenproject1.apiControllers;

import cat.copernic.mavenproject1.Entity.Ad;
import cat.copernic.mavenproject1.Entity.Category;
import cat.copernic.mavenproject1.logic.AdLogic;
import cat.copernic.mavenproject1.repository.AdRepo;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la entidad Ad
 */
@RestController
@RequestMapping("/rest/ads")
public class AdApiController {

    private static final Logger logger = LoggerFactory.getLogger(AdApiController.class);

    @Autowired
    private AdLogic adLogic;

    @Autowired
    private AdRepo adRepo;

    @PostConstruct
    private void init() {
        logger.info("✅ AdApiController initialized");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Ad>> findAll() {
        try {
            List<Ad> ads = adLogic.findAllAds();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-store");
            return new ResponseEntity<>(ads, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("❌ Error retrieving ads", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/byId/{adId}")
    public ResponseEntity<Ad> getById(@PathVariable Long adId) {
        try {
            Optional<Ad> ad = adRepo.findById(adId);
            return ad.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error("❌ Error retrieving ad with ID: {}", adId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createAd(@RequestBody Ad ad) {
        try {
            if (ad == null) {
                logger.error("❌ Ad received is null");
                return ResponseEntity.badRequest().build();
            }

            Long adId = adLogic.saveAd(ad);
            logger.info("✅ Ad created with ID: {}", adId);
            return new ResponseEntity<>(adId, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("❌ Error creating ad", e);
            return ResponseEntity.internalServerError().build();
        }
    }
@DeleteMapping("/delete/{adId}")
public ResponseEntity<Void> deleteAd(@PathVariable String adId) {
    try {
        Long adIdLong = Long.parseLong(adId);

        if (!adRepo.existsById(adIdLong)) {
            logger.warn("⚠️ Ad with ID {} not found", adIdLong);
            return ResponseEntity.notFound().build();
        }

        adLogic.deleteAdById(adIdLong); // ✅ Ahora pasamos solo el ID

        if (adRepo.existsById(adIdLong)) {
            logger.error("❌ Error: Ad with ID {} still exists after deletion", adIdLong);
            return ResponseEntity.internalServerError().build();
        }

        logger.info("✅ Ad with ID {} successfully deleted", adIdLong);
        return ResponseEntity.noContent().build();

    } catch (NumberFormatException e) {
        logger.error("⚠️ Invalid ad ID format: {}", adId, e);
        return ResponseEntity.badRequest().build();
    } catch (Exception e) {
        logger.error("❌ Unexpected error deleting ad with ID {}: {}", adId, e);
        return ResponseEntity.internalServerError().build();
    }
}




    @PutMapping("/update")
    public ResponseEntity<Void> updateAd(@RequestBody Ad ad) {
        try {
            if (ad == null || ad.getId() == null) {
                return ResponseEntity.badRequest().build();
            }

            Optional<Ad> existingAdOpt = adRepo.findById(ad.getId());
            if (existingAdOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ad existingAd = existingAdOpt.get();

            // Mantener el mismo autor si no se especifica uno en la solicitud
            if (ad.getAuthor() == null || ad.getAuthor().getId() == null) {
                ad.setAuthor(existingAd.getAuthor());
            }

            adLogic.updateAd(ad);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("❌ Error updating ad", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/byCategory/{categoryId}")
    public ResponseEntity<List<Ad>> getAdsByCategory(@PathVariable Long categoryId) {
        try {
            List<Ad> ads = adLogic.findAdsByCategory(categoryId);
            return new ResponseEntity<>(ads, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("❌ Error retrieving ads by category", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/priceRange")
    public ResponseEntity<List<Ad>> getAdsByPriceRange(@RequestParam double minPrice, @RequestParam double maxPrice) {
        try {
            List<Ad> ads = adLogic.findAdsByPriceRange(minPrice, maxPrice);
            return new ResponseEntity<>(ads, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("❌ Error retrieving ads by price range", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<Ad>> getAdsFiltered(
            @RequestParam Long categoryId,
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        try {
            List<Ad> ads = adLogic.findAdsFiltered(categoryId, minPrice, maxPrice);
            return new ResponseEntity<>(ads, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("❌ Error retrieving filtered ads", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        try {
            List<Category> categories = adLogic.getAllCategories();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("❌ Error retrieving categories", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<List<Ad>> getAdsByUser(@PathVariable Long userId) {
        try {
            List<Ad> ads = adLogic.findAdsByUser(userId);
            return new ResponseEntity<>(ads, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("❌ Error retrieving ads for user ID: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

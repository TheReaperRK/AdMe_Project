package cat.copernic.mavenproject1.apiControllers;

import cat.copernic.mavenproject1.Entity.Ad;
import cat.copernic.mavenproject1.logic.AdLogic;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.Entity.Category;
import java.util.Optional;
/**
 * Controlador REST para la entidad Ad
 */
@RestController
@RequestMapping("/rest/ads")
public class AdApiController {
    
    Logger logger = LoggerFactory.getLogger(AdApiController.class);

    @Autowired
    private AdLogic adLogic;

    @PostConstruct
    private void init() {
        logger.info("AdApiController initialized");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Ad>> findAll() {
        List<Ad> ads;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store");

        try {
            ads = adLogic.findAllAds();
            return new ResponseEntity<>(ads, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving ads", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{adId}")
    public ResponseEntity<Ad> getById(@PathVariable Long adId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store");

        try {
            Ad ad = adLogic.getAdById(adId);
            if (ad == null) {
                return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(ad, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving ad with ID: {}", adId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createAd(@RequestBody Ad ad) {
        try {

            logger.info("Intentando crear un anuncio: {}", ad);

            if (ad == null) {
                logger.error("El objeto Ad recibido es nulo");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Long adId = adLogic.saveAd(ad);

            logger.info("Anuncio creado con ID: {}", adId);
            return new ResponseEntity<>(adId, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creando el anuncio: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{adId}")
    public ResponseEntity<Void> deleteAd(@PathVariable String adId) {
        logger.info("Recibiendo petición de eliminación para adId: {}", adId);
        try {
            Long adIdLong = null;
            try {
                adIdLong = Long.parseLong(adId);
            } catch (NumberFormatException e) {
                logger.error("Formato de ID inválido: {}", adId, e);
                return ResponseEntity.badRequest().build();
            }

            if (adLogic.existsById(adIdLong)) {
                logger.info("Anuncio {} existe. Procediendo a borrarlo.", adIdLong);
                adLogic.deleteAdById(adIdLong);
                logger.info("Eliminación invocada para anuncio {}", adIdLong);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("No se encontró el anuncio con ID: {}", adIdLong);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error eliminando anuncio con ID: {}", adId, e);
            return ResponseEntity.internalServerError().build();
        }
    }



    @PutMapping("/update")
    public ResponseEntity<Void> updateAd(@RequestBody Ad ad) {
        try {
            if (ad == null || ad.getId() == null) {
                return ResponseEntity.badRequest().build();
            }

            Optional<Ad> existingAdOpt = Optional.ofNullable(adLogic.getAdById(ad.getId()));

            if (existingAdOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Obtener el anuncio existente
            Ad existingAd = existingAdOpt.get();

            // Mantener el mismo autor si no se especifica uno en la solicitud
            if (ad.getAuthor() == null || ad.getAuthor().getId() == null) {
                ad.setAuthor(existingAd.getAuthor());
            }

            // Realizar la actualización
            adLogic.updateAd(ad);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Error updating ad", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/byCategory/{categoryId}")
        public ResponseEntity<List<Ad>> getAdsByCategory(@PathVariable Long categoryId) {
            List<Ad> ads = adLogic.findAdsByCategory(categoryId);
            return new ResponseEntity<>(ads, HttpStatus.OK);
        }

        @GetMapping("/priceRange")
        public ResponseEntity<List<Ad>> getAdsByPriceRange(
                @RequestParam double minPrice,
                @RequestParam double maxPrice) {
            try {
                List<Ad> ads = adLogic.findAdsByPriceRange(minPrice, maxPrice);
                return new ResponseEntity<>(ads, HttpStatus.OK);
            } catch (Exception e) {
                logger.error("Error retrieving ads by price range", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // ✅ Nuevo método para filtrar anuncios por categoría y precio
        @GetMapping("/filtered")
        public ResponseEntity<List<Ad>> getAdsFiltered(
                @RequestParam Long categoryId,
                @RequestParam double minPrice,
                @RequestParam double maxPrice) {
            try {
                List<Ad> ads = adLogic.findAdsFiltered(categoryId, minPrice, maxPrice);
                return new ResponseEntity<>(ads, HttpStatus.OK);
            } catch (Exception e) {
                logger.error("Error retrieving filtered ads", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // ✅ Nuevo método para obtener todas las categorías
        @GetMapping("/categories")
        public ResponseEntity<List<Category>> getCategories() {
            try {
                List<Category> categories = adLogic.getAllCategories();
                return new ResponseEntity<>(categories, HttpStatus.OK);
            } catch (Exception e) {
                logger.error("Error retrieving categories", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        @GetMapping("/byUser/{userId}")
    public ResponseEntity<List<Ad>> getAdsByUser(@PathVariable Long userId) {
        try {
            List<Ad> ads = adLogic.findAdsByUser(userId);
            return new ResponseEntity<>(ads, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving ads for user ID: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}



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
    public ResponseEntity<Void> deleteAd(@PathVariable Long adId) {
        try {
            if (adLogic.existsById(adId)) {
                adLogic.deleteAdById(adId);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting ad with ID: {}", adId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateAd(@RequestBody Ad ad) {
        try {
            if (adLogic.existsById(ad.getId())) {
                adLogic.saveAd(ad);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error updating ad", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

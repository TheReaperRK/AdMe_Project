package project3group4.test;

import cat.copernic.mavenproject1.AdMeApplication;
import cat.copernic.mavenproject1.Entity.Ad;
import cat.copernic.mavenproject1.Entity.Category;
import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.enums.Roles;
import cat.copernic.mavenproject1.repository.AdRepo;
import cat.copernic.mavenproject1.repository.CategoryRepo;
import cat.copernic.mavenproject1.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.MediaType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AdMeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestAdTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private AdRepo adRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @BeforeEach
    public void setup() {
       
        User user = userRepo.findByEmail("carlos@gmail.com");
        if (user == null) {
            user = new User("Carlos", "carlos@gmail.com", "123456789", "password", true, Roles.ADMIN, new ArrayList<>());
            user = userRepo.saveAndFlush(user);
        }

        Category category = categoryRepo.findByName("ElectronicsTest");
        if (category == null) {
            category = new Category("ElectronicsTest", "Description for Electronics", new byte[0], false, new ArrayList<>());
            category = categoryRepo.saveAndFlush(category);
        }

        if (adRepo.findByCategory_Id(category.getId()).size() != 3) {
            
            adRepo.deleteAll(adRepo.findByCategory_Id(category.getId()));
            List<Ad> ads = List.of(
                    new Ad("Ad1", "Description1", new byte[]{}, 100.0, LocalDate.now(), user, category),
                    new Ad("Ad2", "Description2", new byte[]{}, 200.0, LocalDate.now(), user, category),
                    new Ad("Ad3", "Description3", new byte[]{}, 300.0, LocalDate.now(), user, category)
            );
            
            adRepo.saveAll(ads);
        }
            
        
        
    }

    @Test
    public void testGetAllAdsOk() {
        String url = "http://localhost:" + port + "/rest/ads/all";
        int numAds = adRepo.findAll().size();
        ResponseEntity<List<Ad>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Ad>>() {
        });

        List<Ad> ads = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(numAds, ads.size());
    }

    @Test
    public void testGetAdByIdOk() {
        Ad ad = adRepo.findAll().get(0);
        String url = "http://localhost:" + port + "/rest/ads/byId/" + ad.getId();

        ResponseEntity<Ad> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Ad.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ad.getTitle(), response.getBody().getTitle());
    }

    @Test
    public void testDeleteAdByIdOk() {
        Ad ad = adRepo.findAll().get(0);
        String url = "http://localhost:" + port + "/rest/ads/delete/" + ad.getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteAdByIdNotFound() {
        String url = "http://localhost:" + port + "/rest/ads/delete/" + Long.MAX_VALUE;

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateAdOk() {
        // Verificar si ya hay un usuario en la base de datos
        
        User user = userRepo.findByEmail("carlos@gmail.com");
        if (user == null) {
            user = new User("Carlos", "carlos@gmail.com", "123456789", "password", true, Roles.ADMIN, new ArrayList<>());
            user = userRepo.saveAndFlush(user);
        }
        
        
        
            
        

        // Verificar si ya hay una categoría en la base de datos
        List<Category> categories = categoryRepo.findAll();
        Category category = categories.get(0);
        
             
        

        // Crear un anuncio con datos válidos
        Ad ad = new Ad("New Ad", "New ad description.", new byte[]{1, 2, 3}, 200.0, LocalDate.now(), user, category);

        String url = "http://localhost:" + port + "/rest/ads/create";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Long> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(ad, headers),
                Long.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testUpdateAdOk() {

        List<Ad> ads = adRepo.findAll();
        assertFalse(ads.isEmpty(), "No hay anuncios en la base de datos");
        
        Ad ad = ads.get(0);
        //creem directament un producte a la BBDD
        User user = ad.getAuthor();
        
        //creem un nou producte amb el mateix id, pero amb les dades modificades
        Ad ad2 = new Ad("Modificado Ad", "descripcio modificada.", new byte[]{1, 2, 3}, 200.0, LocalDate.now(), user, ad.getCategory());
        ad2.setId(ad.getId());
        
        assertNotNull(ad2.getAuthor(), "El autor del anuncio es null");
        assertNotNull(ad2.getCategory(), "La categoría del anuncio es null");

        

        String url = "http://localhost:" + port + "/rest/ads/update";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(ad2, headers),
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateAdNotFound() {
        Ad ad = new Ad(999L, "Non-existent Ad", "Description", new byte[]{}, 100.0, LocalDate.now(), null, null);

        String url = "http://localhost:" + port + "/rest/ads/update";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(ad, headers),
                Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

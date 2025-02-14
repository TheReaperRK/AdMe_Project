package project3group4.test;

import cat.copernic.mavenproject1.AdMeApplication;
import cat.copernic.mavenproject1.Entity.Ad;
import cat.copernic.mavenproject1.Entity.Category;
import cat.copernic.mavenproject1.repository.CategoryRepo;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AdMeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestCategoryTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private CategoryRepo categoryRepo;

    @BeforeEach
    public void setup() {
        categoryRepo.deleteAll();   

        List<Category> categories = List.of(
                new Category(1L, "Electronics", "Devices and gadgets", new byte[]{1, 2, 3}, false, List.of()),
                new Category(2L, "Furniture", "Home and office furniture", new byte[]{4, 5, 6}, true, List.of()),
                new Category(3L, "Vehicles", "Cars and motorbikes", new byte[]{7, 8, 9}, false, List.of())
        );

        categoryRepo.saveAll(categories);
    }

    @Test
    public void testGetAllCategoriesOk() {
        String url = "http://localhost:" + port + "/rest/categories/all";
        ResponseEntity<List<Category>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Category>>() {
        }
        );
        List<Category> receivedList = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, receivedList.size());
    }

    @Test
    public void testDeleteCategoryByIdOk() {
        Category c = new Category(null, "Sports", "Outdoor and indoor sports", new byte[]{10, 11, 12}, true, List.of());
        categoryRepo.save(c);
        String url = "http://localhost:" + port + "/rest/categories/delete/" + c.getId();
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteByIdErrorNotExist() {
        String url = "http://localhost:" + port + "/rest/categories/delete/" + Long.MAX_VALUE;
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteErrorBadId() {
        String url = "http://localhost:" + port + "/rest/categories/delete/abcd";
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetByIdOk() {
        Category c = new Category(null, "Books", "Various genres of books", new byte[]{13, 14, 15}, false, List.of());
        categoryRepo.save(c);
        categoryRepo.flush();
        String url = "http://localhost:" + port + "/rest/categories/byId/" + c.getId();
        ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.GET, null, Category.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetByIdNotFound() {
        String url = "http://localhost:" + port + "/rest/categories/byId/" + Long.MAX_VALUE;
        ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.GET, null, Category.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetByIdErrorBadId() {
        String url = "http://localhost:" + port + "/rest/categories/byId/abcd";
        ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.GET, null, Category.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // 🔹 Cambia de NOT_FOUND a BAD_REQUEST
    }

    @Test
    public void testCreateOk() {
        Category c = new Category(null, "Toys", "Children’s toys and games", new byte[]{16, 17, 18}, false, List.of());
        String url = "http://localhost:" + port + "/rest/categories/create";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Long> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(c, headers), Long.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testCreateErrorMalformedObject() {
        String url = "http://localhost:" + port + "/rest/categories/create";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(null, headers), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateOk() {
        // 1️⃣ Guardar una categoría y obtener su ID
        Category c = new Category(null, "Music", "Musical instruments", new byte[]{19, 20, 21}, true, List.of());
        categoryRepo.save(c);
        categoryRepo.flush(); // 🔹 Asegurar que el ID se genere
        System.out.println("ID asignado a la categoría guardada: " + c.getId());

        // 2️⃣ Crear una categoría actualizada con el mismo ID
        Category updatedCategory = new Category(c.getId(), "Music & Audio", "All music-related items", new byte[]{22, 23, 24}, false, List.of());

        // 3️⃣ Enviar la solicitud de actualización
        String url = "http://localhost:" + port + "/rest/categories/update";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(updatedCategory, headers), Void.class);

        // 4️⃣ Verificar la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    public void testUpdateIdNotExist() {
        Category updatedCategory = new Category(Long.MAX_VALUE, "Music & Audio", "All music-related items", new byte[]{22, 23, 24}, false, List.of());
        updatedCategory.setId(Long.MAX_VALUE);
        String url = "http://localhost:" + port + "/rest/categories/update";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(updatedCategory, headers), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

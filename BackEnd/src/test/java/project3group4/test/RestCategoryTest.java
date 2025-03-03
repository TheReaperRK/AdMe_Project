package project3group4.test;

import cat.copernic.mavenproject1.AdMeApplication;
import cat.copernic.mavenproject1.Entity.Ad;
import cat.copernic.mavenproject1.Entity.Category;
import cat.copernic.mavenproject1.repository.CategoryRepo;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
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
import org.springframework.http.MediaType;
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
        
        if(categoryRepo.findByName("ElectronicsTest") ==null){
            categoryRepo.saveAndFlush(new Category(1L, "ElectronicsTest", "Devices and gadgets", new byte[]{1, 2, 3}, false, List.of()));
        }
        if(categoryRepo.findByName("FurnitureTest") ==null){
            categoryRepo.saveAndFlush(new Category(1L, "FurnitureTest", "Devices and gadgets", new byte[]{1, 2, 3}, false, List.of()));
        }
        if(categoryRepo.findByName("VehiclesTest") ==null){
            categoryRepo.saveAndFlush(new Category(1L, "VehiclesTest", "Devices and gadgets", new byte[]{1, 2, 3}, false, List.of()));
        }
        
        // Propostes
        if(categoryRepo.findByName("PropuestaTest1") ==null){
            categoryRepo.saveAndFlush(new Category("PropuestaTest1", "Descripcion PropuestaTest1", new byte[]{22, 23, 24}, true, List.of()));
        }
        if(categoryRepo.findByName("PropuestaTest2") ==null){
            categoryRepo.saveAndFlush(new Category("PropuestaTest2", "Descripcion PropuestaTest2", new byte[]{12, 22, 34}, true, List.of()));
        }
        if(categoryRepo.findByName("PropuestaTest3") ==null){
            categoryRepo.saveAndFlush(new Category("PropuestaTest3", "Descripcion PropuestaTest3", new byte[]{32, 12, 31}, true, List.of()));
        }

     
        
    }
    @AfterAll
    public void endingSetup() {
        if(categoryRepo.findByName("ElectronicsTest") != null){
            categoryRepo.deleteById(categoryRepo.findByName("ElectronicsTest").getId());
        }
        if(categoryRepo.findByName("FurnitureTest") !=null){
            categoryRepo.deleteById(categoryRepo.findByName("FurnitureTest").getId());
        }
        if(categoryRepo.findByName("VehiclesTest") !=null){
           categoryRepo.deleteById(categoryRepo.findByName("VehiclesTest").getId());
        }
        
        // Propostes
        if(categoryRepo.findByName("PropuestaTest1") !=null){
            categoryRepo.deleteById(categoryRepo.findByName("PropuestaTest1").getId());
        }
        if(categoryRepo.findByName("PropuestaTest2") !=null){
            categoryRepo.deleteById(categoryRepo.findByName("PropuestaTest2").getId());
        }
        if(categoryRepo.findByName("PropuestaTest3") !=null){
            categoryRepo.deleteById(categoryRepo.findByName("PropuestaTest3").getId());
        }
      }
    

    @Test
    public void testGetAllCategoriesOk() {
        int totalCategories = categoryRepo.findAll().size();
        String url = "http://localhost:" + port + "/rest/categories/all";
        ResponseEntity<List<Category>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Category>>() {
        }
        );
        List<Category> receivedList = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(totalCategories, receivedList.size());
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
        Category c = categoryRepo.findAll().getLast();
        
        
        String url = "http://localhost:" + port + "/rest/categories/byId/" + c.getId();
        ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.GET, null, Category.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(c.getName(), response.getBody().getName());
        assertEquals(c.getId(), response.getBody().getId());
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
        Category c = new Category(null, "test", "Children’s toys and games", new byte[]{16, 17, 18}, false, List.of());
        String url = "http://localhost:" + port + "/rest/categories/create";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        ResponseEntity<Long> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(c, headers), Long.class);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        categoryRepo.delete(c);
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
        Category c = categoryRepo.findAll().get(0);
        System.out.println("ID asignado a la categoría guardada: " + c.getId());

        // 2️⃣ Crear una categoría actualizada con el mismo ID
        Category updatedCategory = new Category(c.getId(), "Modified category", "All music-related items", new byte[]{22, 23, 24}, false, List.of());

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
    
    @Test
    public void testGetProposalsOkOrNoContent() {
        int totalPropostes = categoryRepo.findByProposalTrue().size();
        String url = "http://localhost:" + port + "/rest/categories/proposals";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<List<Category>> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Category>>() {
        }
        );
        
        List<Category> recivedPropostes = response.getBody();
               
        assertTrue(
            ((response.getStatusCode() == HttpStatus.OK)&& !recivedPropostes.isEmpty()) || ((response.getStatusCode() == HttpStatus.NO_CONTENT) && recivedPropostes.isEmpty())
        );
        
        assertEquals(recivedPropostes, totalPropostes);
        
    }
    
    
}

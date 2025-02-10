/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project3group4.test;

import cat.copernic.mavenproject1.AdMeApplication;
import cat.copernic.mavenproject1.Entity.Ad;
import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.logic.UserLogic;
import cat.copernic.mavenproject1.repository.UserRepo;
import java.util.List;
import cat.copernic.mavenproject1.enums.Roles;
import java.util.ArrayList;
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

/**
 *
 * @author carlo
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AdMeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestUsersTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private UserLogic userLogic;
    
    @Autowired
    private UserRepo userRepo;
    
    @BeforeEach
    public void setup() {
        
        userRepo.deleteAll();
        
        List<Ad> ads = new ArrayList<>(); // Lista vacía de anuncios

        List<User> users = List.of(
            new User(1L, "carlos", "carlosmendoza2003@gmail.com", "653035737", 
                     "adygyudgaufaiof", true, Roles.ADMIN, ads),
                
            new User(2L, "pepe", "pepe@gmail.com", "64826429749", 
                 "adygyudgaufaiof", false, Roles.USER, ads),
            
            new User(3L, "JOSE", "joselito@gmail.com", "580825285", 
                     "adygyudgaufaiof", true, Roles.ADMIN, ads)
        );

       
       userRepo.saveAll(users);
   
    }
    
    @Test
    public void testGetAllUsersOk() {
        
        String url = "http://localhost:" + port + "/rest/users/all";
        
        // Realizamos la petición al endpoint real
        ResponseEntity<List<User>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<User>>() {}
        );

        List<User> receivedList = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(receivedList.size(), 3);
        
    }
    
    
    @Test
    public void testDeleteUserByIdOk() {
                
        User u = new User("carlos", "carlosmendoza2003@gmail.com", "653035737", 
                     "adygyudgaufaiof", true, Roles.ADMIN);
        
        userRepo.save(u);
        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/users/delete/" + u.getId();

        // Realizamos la petición al endpoint real
        ResponseEntity<Void> response = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
    }
    
    @Test
    public void testDeleteByIdErrorNotExist() {
        
        
        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/users/delete/"+ Long.MAX_VALUE;

        // Realizamos la petición al endpoint real
        ResponseEntity<Void> response = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
    }
    
    @Test
    public void testDeleteErrorBadId() {
        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/users/delete/abcd";

        // Realizamos la petición al endpoint real
        ResponseEntity<Void> response = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
    }
    
    
    @Test
    public void testGetByIdOk() {
                
        User u = new User("carlos", "carlosmendoza2003@gmail.com", "653035737", 
                     "adygyudgaufaiof", true, Roles.ADMIN);
        
        userRepo.save(u);
        userRepo.flush();  // Asegura que la entidad se guarda inmediatamente

        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/users/byId/" + u.getId();

        // Realizamos la petición al endpoint real
        ResponseEntity<User> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            User.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
    }
    
    @Test
    public void testGetByIdNotFound() {
        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/users/byId/" + Long.MAX_VALUE;

        // Realizamos la petición al endpoint real
        ResponseEntity<User> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            User.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
    }
    
    @Test
    public void testGetByIdErrorBadId() {
        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/products/byId/abcd";

        // Realizamos la petición al endpoint real
        ResponseEntity<User> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            User.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
    }
    
    @Test
    public void testCreateOk() {
        
        User p = new User("carlos", "carlosmendoza2003@gmail.com", "653035737", 
                     "adygyudgaufaiof", true, Roles.ADMIN);
        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/users/create";
        
        //generem les capçaleres de la petició
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Realizamos la petición al endpoint real
        ResponseEntity<Long> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(p, headers),
            Long.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        Long id = response.getBody();
        
        User p2 = userRepo.findById(id).orElse(null);
        
        assertEquals(p.getName(), p2.getName());
        assertEquals(p.getEmail(), p2.getEmail());
        assertEquals(p.getPhoneNumber(), p2.getPhoneNumber());
        assertEquals(p.getPassword(), p2.getPassword());
        assertEquals(p.isStatus(), p2.isStatus());
        assertEquals(p.getRole(), p2.getRole());
        
    }
    
    
    @Test
    public void testCreateErrorMalformedObject() {
        
                
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/users/create";
        
        //generem les capçaleres de la petició
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Realizamos la petición al endpoint real
        ResponseEntity<Void> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            new HttpEntity<>(null, headers),
            Void.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
    }
    
    
    @Test
    public void testUpdateOk() {
        
        List<Ad> ads = new ArrayList<>(); // Lista vacía de anuncios
        //creem directament un producte a la BBDD
        User p = new User("carlos", "carlosmendoza2003@gmail.com", "653035737", 
                     "adygyudgaufaiof", true, Roles.ADMIN, ads);
        userRepo.save(p);
       
        List<Ad> ads2 = new ArrayList<>(); // Lista vacía de anuncios
        //creem un nou producte amb el mateix id, pero amb les dades modificades
        User p2 = new User("carlos2", "carlosmendoza20032@gmail.com", "653035738", 
                     "adygyudgaufaiof2", false, Roles.USER, ads2);
        p2.setId(p.getId());
        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/users/update";

        //generem les capçaleres de la petició
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Realizamos la petición al endpoint real
        ResponseEntity<Void> response = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            new HttpEntity<>(p2, headers),
            Void.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        User resultat = userRepo.findById(p.getId()).orElse(null);
        
        assertTrue(p2.equals(resultat));
    }
    
    
    @Test
    public void testUpdateIdNotExist() {
        
        User p = new User("carlos", "carlosmendoza2003@gmail.com", "653035737", 
                     "adygyudgaufaiof", true, Roles.ADMIN);
        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/products/update";

        //generem les capçaleres de la petició
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Realizamos la petición al endpoint real
        ResponseEntity<Void> response = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            new HttpEntity<>(p, headers),
            Void.class
        );

        // Verificamos la respuesta
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    

}


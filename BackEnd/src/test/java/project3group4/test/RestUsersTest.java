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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

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
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private UserLogic userLogic;
    
    @Autowired
    private UserRepo userRepo;
    
    @BeforeEach
    public void setup() {
        
       
        
        List<Ad> ads = new ArrayList<>(); // Lista vacía de anuncios
        if(userRepo.findByEmail("carlosmendoza2003@gmail.com") == null){
            userRepo.saveAndFlush(new User("carlos", "carlosmendoza2003@gmail.com", "653035737",  
                     passwordEncoder.encode("123456"), true, Roles.ADMIN, ads));
        }else{
            userRepo.delete(userRepo.findByEmail("carlosmendoza2003@gmail.com"));
            userRepo.saveAndFlush(new User("carlos", "carlosmendoza2003@gmail.com", "653035737",  
                     passwordEncoder.encode("123456"), true, Roles.ADMIN, ads));
        }
        if(userRepo.findByEmail("pepe@gmail.com") == null){
             userRepo.saveAndFlush(new User("pepe", "pepe@gmail.com", "64826429749", 
                 passwordEncoder.encode("123456"), false, Roles.USER, ads));
        }
        if(userRepo.findByEmail( "joselito@gmail.com") != null){
             userRepo.saveAndFlush(new User("JOSE", "joselito@gmail.com", "580825285", 
                     passwordEncoder.encode("123456"), true, Roles.ADMIN, ads));
        }
        

       
       
   
    }
    
    @Test
    public void testGetAllUsersOk() {
        int totalUsers = userRepo.findAll().size();
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
        assertEquals(totalUsers, receivedList.size());
        
        userRepo.saveAndFlush(new User("admin", "admin@gmail.com", "580825285", 
                     passwordEncoder.encode("123"), true, Roles.ADMIN, new ArrayList<Ad>()));
    }
    
    
    @Test
    public void testDeleteUserByIdOk() {
                
        
        
        User u = userRepo.findByEmail("carlosmendoza2003@gmail.com");
        
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
                
        User u = new User("ahton", "terminarot@gmail.com", "653035737", 
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
        userRepo.delete(u);
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
        
        User p = new User("deiV", "davidalama@gmail.com", "6567123737", 
                     "adawlfdgaufaiof", true, Roles.ADMIN);
        
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
        assertEquals(p.isStatus(), p2.isStatus());
        assertEquals(p.getRole(), p2.getRole());
        
        userRepo.delete(p);
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
    public void testUpdateOk() throws IOException {
        
        
        Path projectPath = Paths.get("").toAbsolutePath();
        Path imagePath = projectPath.resolve("src/main/java/cat/copernic/mavenproject1/tux.jpg");
        byte[] imageBytes = Files.readAllBytes(imagePath);

        // Convertir a MultipartFile simulado
        MultipartFile imageFile = new MockMultipartFile("imagen.jpg", imageBytes);
        
        List<Ad> ads = new ArrayList<>(); 
       
        
        List<User> users = userRepo.findAll();
        assertFalse(users.isEmpty(), "No hay usuarios en la base de datos");
        
        User p = users.get(0);
       
        User p2 = new User("carlos2", "carlosmendoza2003@gmail.com", "653035738", "123", false, Roles.ADMIN, ads);
                     
        
        //userLogic.createUser(p2, imageFile);

        p.setAds(p2.getAds());
        p.setEmail(p2.getEmail());
        p.setName(p2.getName());
        p.setPhoneNumber(p2.getPhoneNumber());
        p.setRole(p2.getRole());
        p.setWord(p2.getWord());
        
        p.setImage(imageFile.getBytes());
        
         // URL completa con puerto dinámico
        String url = "http://localhost:" + port + "/rest/users/update";

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
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        User res = userRepo.findById(p.getId()).orElse(null);
        
        assertEquals(p.getId(),res.getId());
       
        assertEquals(p.getName(),res.getName());
        assertEquals(p.getPhoneNumber(),res.getPhoneNumber());
        assertEquals(p.getRole(),res.getRole());
        assertEquals(p.getAds(), res.getAds());
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


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.mavenproject1.apiControllers;

import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.enums.Roles;
import cat.copernic.mavenproject1.logic.EmailLogic;
import cat.copernic.mavenproject1.logic.UserLogic;
import cat.copernic.mavenproject1.repository.UserRepo;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author carlo
 */

@RestController
@RequestMapping("/rest/users")
public class UserApiController {
    
    Logger logger = LoggerFactory.getLogger(UserApiController.class);
    
    @Autowired
    private EmailLogic emailLogic;
    
    @Autowired
    private UserLogic userLogic;
    
    @Autowired
    private UserRepo userRepo;
    
    @PostConstruct
    private void init()
    {
       
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<User>> findAll() throws IOException{
        
        //los datos a devolver (payload)
        List<User> llista;
        
        //la cabecera del transporte
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store"); //no usar caché
        
        try {
            
            llista = userLogic.findAllUsers();
            
            return new ResponseEntity<>(llista, headers, HttpStatus.OK);
            
        } catch (Exception e) {
           
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
       
    }
    
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId){
        
        ResponseEntity<Void> response;
        
        try {
            if (userLogic.existsById(userId))
            {
                userLogic.deleteUserById(userId);
                response = ResponseEntity.noContent().build();
            }
            else
                response = ResponseEntity.notFound().build();
            
            
        } catch (Exception e) {
            
            response = ResponseEntity.internalServerError().build();
        }
        
        return response;
    }
    
    /***
     * TODO: refactor de la part de verificar si existeix producte.
     * @param user
     * @return 
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<User> update(@PathVariable Long userId, @RequestBody User updatedUser) {
        ResponseEntity<User> response;

        
        logger.info("User ID from URL: " + userId);
        logger.info("Updated User Object: " + updatedUser);

        try {
            Optional<User> existingUserOptional = userRepo.findById(userId);

            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();

                // Actualizar solo los campos necesarios
                existingUser.setName(updatedUser.getName());
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                existingUser.setImage(updatedUser.getImage());
                existingUser.setStatus(updatedUser.isStatus());
                existingUser.setRole(updatedUser.getRole());

                userRepo.save(existingUser);
                return new ResponseEntity<>(existingUser, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            logger.error("ERROR UPDATE: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("/expireWord/{userId}")
    public ResponseEntity<User> expireWord(@PathVariable Long userId) {
        ResponseEntity<User> response;

        
        logger.info("User ID from URL: " + userId);

        try {
            Optional<User> existingUserOptional = userRepo.findById(userId);

            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();

                existingUser.setWord("1");//valor que nunca coincidira con el hash, por lo cual sera imposible iniciar sesion, obligatorio reestablecer contraseña

                userRepo.save(existingUser);
                emailLogic.sendExpirationMessage(existingUser.getEmail()); 
                
                return new ResponseEntity<>(existingUser, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            logger.error("ERROR EXPIRE: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    
    @PutMapping("/activate/{userId}")
    public ResponseEntity<Void> activate(@PathVariable Long userId){
                
        ResponseEntity<Void> response;
        
        try {
            if (userLogic.existsById(userId))
            {
                userLogic.activateUserById(userId);
                response = ResponseEntity.noContent().build();
            }
            else
                response = ResponseEntity.notFound().build();

        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();
        }
        return response;
    }
    
    @PutMapping("/desactivate/{userId}")
    public ResponseEntity<Void> desactivate(@PathVariable Long userId){
                
                ResponseEntity<Void> response;

        try {
            if (userLogic.existsById(userId))
            {
                userLogic.desactivateUserById(userId);
                response = ResponseEntity.noContent().build();
            }
            else
                response = ResponseEntity.notFound().build();

        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();
        }
            return response;

    }
    
    
    
    @GetMapping("/byId/{userId}")
    public ResponseEntity<User> byId(@PathVariable Long userId){
        
        //los datos a devolver (payload)
        User p;
        
        //el transporte HTTP
        ResponseEntity<User> response;
        
        //la cabecera del transporte
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store"); //no usar caché
        
        logger.info("Buscando usuario con ID: {}", userId);
        p = userLogic.getUserById(userId);
        logger.info("Usuario encontrado: {}", p);
        try {
            
            p = userLogic.getUserById(userId);
            logger.info("Usuario encontrado: {}", p);       
            
            if (p == null)
            {
                response = new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
            }
            else
            {
                response = new ResponseEntity<>(p, headers, HttpStatus.OK);
            }
            
        } catch (Exception e) {
           
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return response;
    }
    
    @PostMapping("/create")
    public ResponseEntity<Long> createUser(@RequestBody User user) throws IOException {
        //ResponseEntity<Long> response;
        
        
        // Ruta de la imagen en el sistema de archivos
        Path projectPath = Paths.get("").toAbsolutePath();

        // Construir la ruta dinámica a la imagen
        Path imagePath = projectPath.resolve("src/main/java/cat/copernic/mavenproject1/tux.jpg");
        //Path imagePath = Paths.get("C:\\Users\\carlo\\Documents\\projects\\proyect3_group4\\BackEnd\\src\\main\\java\\cat\\copernic\\mavenproject1\\tux.jpg");
        byte[] imageBytes = Files.readAllBytes(imagePath);

        // Convertir a MultipartFile simulado
        MultipartFile imageFile = new MockMultipartFile("imagen.jpg", imageBytes);
        
        try {
            
            if (user == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            else
            {
                
                if (userLogic.userIsUnique(user)){
                    Long userId = userLogic.createUser(user, imageFile);
                    return new ResponseEntity<>(userId, HttpStatus.CREATED);
                }else{
                    return new ResponseEntity<>(HttpStatus.IM_USED);
                }
                
            }
    
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
         
        
    }
    
    @GetMapping("/user/{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG) // O el tipo de imagen que corresponda
            .body(user.getImage());
    }
}

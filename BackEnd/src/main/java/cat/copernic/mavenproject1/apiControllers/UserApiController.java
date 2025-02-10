/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.mavenproject1.apiControllers;

import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.logic.UserLogic;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author carlo
 */

@RestController
@RequestMapping("/rest/users")
public class UserApiController {
    
    Logger logger = LoggerFactory.getLogger(UserApiController.class);
    
   
    
    @Autowired
    private UserLogic userLogic;
    
    @PostConstruct
    private void init()
    {
       
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<User>> findAll(){
        
        //los datos a devolver (payload)
        List<User> llista;
        
        //el transporte HTTP
        ResponseEntity<List<User>> response;
        
        //la cabecera del transporte
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store"); //no usar caché
        
        try {
            
            llista = userLogic.findAllUsers();
            
            response = new ResponseEntity<>(llista, headers, HttpStatus.OK);
            
        } catch (Exception e) {
           
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return response;
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
     * @param user
     * @return 
     */
    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody User user){
        
        ResponseEntity<Void> response;
        
        try {
            if (userLogic.existsById(user.getId()))
            {
                userLogic.saveUser(user);
                
                response = ResponseEntity.ok().build();
            }
            else
                response = ResponseEntity.notFound().build();


        } catch (Exception e) {

            response = ResponseEntity.internalServerError().build();
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
    public ResponseEntity<Long> createUser(@RequestBody User user) {
       ResponseEntity response;
        Long userId;
        
        try {
            
            if (user == null)
                response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            else
            {
                userId = userLogic.saveUser(user);
                response = new ResponseEntity<>(userId,HttpStatus.CREATED);
            }
    
        } catch (Exception e) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return response;  
        
    }
}

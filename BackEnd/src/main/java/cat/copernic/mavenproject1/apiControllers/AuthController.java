/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.mavenproject1.apiControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cat.copernic.mavenproject1.logic.UserLogic;
import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.enums.Roles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author carlo
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserLogic userLogic;
    
        
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        
        
        User user = userLogic.authenticateUser(email, password);


        if (user != null) {
            return ResponseEntity.ok(user);  // Enviar los datos del usuario si es correcto
        } else {
            
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String name, @RequestParam String email,
            @RequestParam String phone, @RequestParam String password,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        
        User createdUser = new User (name, email, phone, password, false, Roles.USER);
        
        if (userLogic.userIsUnique(createdUser)) {
            userLogic.tryCreation(createdUser, imageFile);
            return ResponseEntity.ok(createdUser);
        }
        
        return ResponseEntity.status(401).body("No s'ha pogut crear");
    }
}


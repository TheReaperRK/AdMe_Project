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

/**
 *
 * @author carlo
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserLogic userLogic;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    User user1 = new User("carlos2", "carlosmendoza20032@gmail.com", "653035738", 
                     "adygyudgaufaiof2", false, Roles.USER);
    
        
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        
        userLogic.tryCreation(user1);
        
        System.out.println("🔍 Email recibido: " + email);
        System.out.println("🔍 Password recibido: " + password);
        
        User user = userLogic.authenticateUser(email, password);


        if (user != null) {
            return ResponseEntity.ok(user);  // Enviar los datos del usuario si es correcto
        } else {
            System.out.println("🔍 Email recibido: " + email);
            System.out.println("🔍 Password recibido: " + password);
            System.out.println("pasword recibido haseado: " + passwordEncoder.encode(password));
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }
}


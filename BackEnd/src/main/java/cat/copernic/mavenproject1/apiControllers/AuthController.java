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
import cat.copernic.mavenproject1.repository.UserRepo;
import java.util.UUID; // Asegúrate de importar esta línea
import java.util.Collections;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
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
    

    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
        
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
    
    @PostMapping("/recover")
    public ResponseEntity<?> recoverPassword(@RequestParam String email) {
        User user = userLogic.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "El correo no está registrado"));
        }


        // Generar un token único (por ejemplo, UUID)
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepo.save(user);

        

        return ResponseEntity.ok(Collections.singletonMap("message", "Se ha enviado un correo con instrucciones para recuperar la contraseña"));
    }
    
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String token, @RequestParam String word) {
        User user = userLogic.findByEmail(email);
        System.out.println(user);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "El correo no está registrado"));
        } else if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "El correo no consta con un token de recuperacion"));
        }

        if (!(user.getResetToken().contentEquals(token))) {
            System.out.println(user.getResetToken() + ", " + token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Token invalido"));
        }
            user.setWord(passwordEncoder.encode(word));
                userLogic.saveUser(user);
                
            user.setResetToken(null); // per tal que no es pugui reutilitzar
            return ResponseEntity.ok(Collections.singletonMap("message", "se ha restablecido la contraseña"));
    }
    
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.mavenproject1.logic;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cat.copernic.mavenproject1.Entity.User;
import cat.copernic.mavenproject1.repository.UserRepo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author carlo
 */
@Service
public class UserLogic {
    
    @Autowired
    UserRepo userRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User getUser(Long login)
    {
        User ret = userRepo.findById(login).orElse(null);
        
        return ret;
    }
    
    public List<User> findAllUsers(){
        
        List<User> ret = new ArrayList<>();
        
        ret = userRepo.findAll();
        
        return ret;
        
    }
    
    /***
     * Verifica si un determinat usuari existeix a la BBDD
     * @param id
     * @return 
     */
    public boolean existsById(Long id)
    {
        User p = userRepo.findById(id).orElse(null);
        
        return (p != null);
    }
    
    public void deleteUserById(Long id){
        
        //TODO: validacions de negoci
        
        userRepo.deleteById(id);
        
    }
    
    public boolean userIsUnique (User user){
        
        return userRepo.findByEmail(user.getEmail()) == null;

    }
    
    public void tryCreation (User user, MultipartFile imageFile) {
        if (userIsUnique(user)){
            createUser(user, imageFile);
        }
    }
    
    public Long createUser(User user, MultipartFile imageFile) {
        
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                user.setImage(convertImageToBlob(imageFile));
            }
            
            user.setWord(passwordEncoder.encode(user.getWord()));
            User savedUser = userRepo.save(user);
            return savedUser.getId();
        } catch (Exception e) {
            return null;
        }

    }
    
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public User authenticateUser(String email, String rawPassword) {
        
        
    User user = userRepo.findByEmail(email);
    
    if (user == null) {
        System.out.println("Usuario no encontrado");
        return null;
    }
    
        System.out.println("Email: " + user.getEmail() + ", Hash almacenado: " + user.getWord());
        System.out.println("Contraseña ingresada: " + rawPassword);

        if (passwordEncoder.matches(rawPassword, user.getWord())) {
            System.out.println("✅ Autenticación exitosa");
            return user;
        }

        System.out.println("❌ Autenticación fallida");
        return null;
}

    
    public Long saveUser(User user){
        
        //TODO: validacions de negoci
        
        User ret = userRepo.save(user);
        
        return ret.getId();
        
    }
    
    public User getUserById(Long id){
        
        //TODO: validacions de negoci
        
        return userRepo.findById(id).orElse(null);
        
    }
    
    public byte[] convertImageToBlob(MultipartFile file) throws IOException {
        return file.getBytes();
    }
    
}

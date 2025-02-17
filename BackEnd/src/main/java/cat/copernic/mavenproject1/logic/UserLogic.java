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
import java.util.Optional;
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
    
    public <Optional>User findByEmail (String email){
        User ret = userRepo.findByEmail(email);
        
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
    
    public void activateUserById(Long id) {
    // Buscar el usuario por ID
    Optional<User> optionalUser = userRepo.findById(id);
    
    // Verificar si el usuario existe
    if (optionalUser.isPresent()) {
        User user = optionalUser.get(); // Obtener el usuario de Optional
        user.setStatus(true); // Modificar el estado
        userRepo.save(user); // Guardar los cambios en la base de datos
    } else {
        throw new RuntimeException("Usuario no encontrado con ID: " + id);
    }
}

    
    public void desactivateUserById(Long id){
        
        // Buscar el usuario por ID
        Optional<User> optionalUser = userRepo.findById(id);

        // Verificar si el usuario existe
        if (optionalUser.isPresent()) {
            User user = optionalUser.get(); // Obtener el usuario de Optional
            user.setStatus(false); // Modificar el estado
            userRepo.save(user); // Guardar los cambios en la base de datos
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }

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
    public Long updateUser(User user) {
        
        User oldUser = userRepo.findById(user.getId()).orElse(null);
        try{
            oldUser.setAds(user.getAds());
            oldUser.setId(user.getId());
            oldUser.setImage(user.getImage());
            oldUser.setName(user.getName());
            oldUser.setPhoneNumber(user.getPhoneNumber());
            oldUser.setRole(user.getRole());
            oldUser.setStatus(user.isStatus());
            oldUser.setWord(passwordEncoder.encode(user.getWord()));
        
        userRepo.saveAndFlush(oldUser);
        return oldUser.getId();
        }catch(Exception e){
            return null;
        }
    }
    
    public User getUserById(Long id){
        
        //TODO: validacions de negoci
        
        return userRepo.findById(id).orElse(null);
        
    }
    
    public byte[] convertImageToBlob(MultipartFile file) throws IOException {
        return file.getBytes();
    }
    
}

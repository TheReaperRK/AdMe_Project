package cat.copernic.mavenproject1.apiControllers;

import cat.copernic.mavenproject1.Entity.Ad;
import cat.copernic.mavenproject1.Entity.Category;
import cat.copernic.mavenproject1.logic.CategoryLogic;
import java.nio.file.Files;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/rest/categories")
public class CategoryApiController {

    @Autowired
    private CategoryLogic categoryLogic;

    @GetMapping("/all")
    public ResponseEntity<List<Category>> findAll() {
        List<Category> categories = categoryLogic.findAllCategories();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store");
        return new ResponseEntity<>(categories, headers, HttpStatus.OK);
    }

    @GetMapping("/byId/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
        Category category = categoryLogic.getCategoryById(categoryId);
        if (category != null) {
            return new ResponseEntity<>(category, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createCategory(@RequestParam String name, @RequestParam String description, @RequestParam boolean proposal, @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        
        byte[] img = null;
        Category category = new Category(name, description, img, proposal, new ArrayList<>());
        
        Long categoryId = categoryLogic.saveCategory(category, imageFile);
        return new ResponseEntity<>(categoryId, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long categoryId) {
        if (categoryLogic.existsById(categoryId)) {
            categoryLogic.deleteCategoryById(categoryId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateCategory(@RequestBody Category category) {
        if (category.getId() == null || !categoryLogic.existsById(category.getId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        categoryLogic.updateCategory(category);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

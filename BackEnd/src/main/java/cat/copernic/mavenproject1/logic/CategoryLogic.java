package cat.copernic.mavenproject1.logic;

import cat.copernic.mavenproject1.Entity.Category;
import cat.copernic.mavenproject1.repository.CategoryRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryLogic {
    
    @Autowired
    private CategoryRepo categoryRepo;
    
    public List<Category> findAllCategories() {
        return categoryRepo.findAll();
    }
    
    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id).orElse(null);
    }
    
    public boolean existsById(Long id) {
        return categoryRepo.existsById(id);
    }
    
    public void deleteCategoryById(Long id) {
        categoryRepo.deleteById(id);
    }
    
    public Long saveCategory(Category category) {
        return categoryRepo.save(category).getId();
    }
}

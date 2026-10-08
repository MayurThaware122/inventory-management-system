package com.example.inventory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String showProducts(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model) {

        keyword = keyword.trim();

        if (keyword.isEmpty()) {
            model.addAttribute("products", productRepository.findAll());
        } else {
            model.addAttribute(
                    "products",
                    productRepository.findByNameContainingIgnoreCase(keyword)
            );
        }

        model.addAttribute("product", new Product());
        model.addAttribute("keyword", keyword);

        return "products";
    }

    @PostMapping("/products")
    public String addProduct(@ModelAttribute("product") Product product) {
        product.setId(null);
        productRepository.save(product);

        return "redirect:/";
    }
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable("id") Long id) {
        productRepository.deleteById(id);

        return "redirect:/";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Product not found"));

        model.addAttribute("product", product);

        return "edit-product";
    }

    @PostMapping("/products/{id}/update")
    public String updateProduct(
            @PathVariable("id") Long id,
            @ModelAttribute("product") Product updatedProduct) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Product not found"));

        product.setName(updatedProduct.getName());
        product.setCategory(updatedProduct.getCategory());
        product.setPrice(updatedProduct.getPrice());
        product.setQuantity(updatedProduct.getQuantity());

        productRepository.save(product);

        return "redirect:/";
    }
}
package com.example.diplom.service;


import com.example.diplom.dto.request.CreateProductDtoRequest;
import com.example.diplom.dto.response.MainDtoResponse;
import com.example.diplom.dto.response.ProductDtoResponse;
import com.example.diplom.dto.response.ProductInfoMainDtoResponse;
import com.example.diplom.models.Client;
import com.example.diplom.models.Image;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.ImageRepository;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {
    private static final String UPLOAD_DIR = "uploads/";

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ImageRepository imageRepository;
    private final ClientRepository clientRepository;

    public Product createProduct(CreateProductDtoRequest request, Principal principal) throws IOException {
        Product product = new Product();
        product.setQuantity(request.getQuantity());
        product.setTitle(request.getTitle());
        product.setSellingPrice(request.getSellingPrice());
        product.setPrice(request.getPrice());
        product.setSupplier((Supplier) getUserByPrincipal(principal));

        List<Image> images = new ArrayList<>();

        for (int i = 0; i < request.getImages().size(); i++) {
            MultipartFile file = request.getImages().get(i);
            Image image = saveImageToFileSystem(file);
            image.setProduct(product);
            if (i == 0) {
                image.setPreviewImage(true);
            }
            images.add(image);
        }

        productRepository.save(product);
        imageRepository.saveAll(images);

        Long previewImageId = images.stream()
                .filter(Image::isPreviewImage)
                .findFirst()
                .map(Image::getId)
                .orElse(null);

        product.setPreviewImageId(previewImageId);
        return productRepository.save(product);
    }

    public List<MainDtoResponse> getAllProduct(){
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new MainDtoResponse(
                        product.getId(),
                        product.getTitle(),
                        product.getPrice()
                ))
                .toList();
    }

    public ProductInfoMainDtoResponse productInfo(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Товар не найден"));

        ProductInfoMainDtoResponse response = new ProductInfoMainDtoResponse(
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                product.getQuantity(),
                product.getSupplier().getLogin()
        );

        return response;
    }

    public ResponseEntity<String> deleteProduct(Long id, Principal principal){
        Product product = productRepository.findById(id).orElseThrow(()->new RuntimeException("Продукта нет"));
        Supplier currentUser = (Supplier)getUserByPrincipal(principal);
        if(!product.getSupplier().getId().equals(currentUser.getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете удалить объявление другого поставщика");
        }
        productRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Объявление успешно удалено");
    }

    public Object getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        Optional<Supplier> supplier = supplierRepository.findByLogin(principal.getName());
        if(supplier.isPresent()){
            return supplierRepository.findByLogin(principal.getName()).orElse(new Supplier());
        }
        return clientRepository.findByLogin(principal.getName()).orElse(new Client());
    }

    private Image saveImageToFileSystem(MultipartFile file) throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIR)); // Создаем папку, если ее нет

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setUrl("/images/" + fileName); // Сохраняем URL

        return image;
    }
}

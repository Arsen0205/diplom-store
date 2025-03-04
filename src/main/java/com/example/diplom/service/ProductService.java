package com.example.diplom.service;


import com.example.diplom.dto.request.CreateProductDtoRequest;
import com.example.diplom.dto.response.Response;
import com.example.diplom.models.Client;
import com.example.diplom.models.Image;
import com.example.diplom.models.Product;
import com.example.diplom.models.Supplier;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.ImageRepository;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

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
        product.setSupplier((Supplier)getUserByPrincipal(principal));

        List<Image> images = new ArrayList<>();

        for (int i = 0; i < request.getImages().size(); i++) {
            MultipartFile file = request.getImages().get(i);
            Image image = toImageEntity(file);
            image.setProduct(product);
            if(i==0){
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

    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }
}

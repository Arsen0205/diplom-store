package com.example.diplom.service;

import com.example.diplom.dto.response.*;
import com.example.diplom.models.*;
import com.example.diplom.repository.OrderItemRepository;
import com.example.diplom.repository.OrderRepository;
import com.example.diplom.repository.ProductRepository;
import com.example.diplom.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    public SuppliersInfoDtoResponse supplierInfo(Principal principal){
        String login = principal.getName();
        Supplier current = supplierRepository.findByLogin(login).orElseThrow(()->new UsernameNotFoundException("Поставщика с таким логином не существует"));

        List<Product> products = productRepository.findAllBySupplier(current);

        List<ProductDtoResponse> productDtoResponses = products.stream()
                .map(p->{
                    String url = p.getImages().stream()
                            .filter(Image::isPreviewImage)
                            .map(Image::getUrl)
                            .findFirst()
                            .orElse("/images/placeholder.png");
                    return new ProductDtoResponse(
                            p.getId(),
                            p.getTitle(),
                            p.getQuantity(),
                            p.getPrice(),
                            p.getSellingPrice(),
                            url
                    );
                })
                .toList();

        return new SuppliersInfoDtoResponse(
                current.getId(),
                current.getLogin(),
                current.getFio(),
                current.getEmail(),
                current.getPhoneNumber(),
                current.getLoginTelegram(),
                current.getChatId(),
                current.isActive(),
                current.getRole(),
                productDtoResponses
        );
    }

    @Transactional
    public List<ProductDtoResponse> getProductSupplier(Principal principal) {
        Supplier supplier = getUserByPrincipal(principal);

        List<Product> products = productRepository.findBySupplier(supplier);

        return products.stream()
                .map(product -> {
                    // 1) Находим картинку-превью или первую, иначе placeholder
                    String url = product.getImages().stream()
                            .filter(Image::isPreviewImage)
                            .map(Image::getUrl)
                            .findFirst()
                            .orElseGet(() -> product.getImages().stream()
                                    .map(Image::getUrl)
                                    .findFirst()
                                    .orElse("/images/placeholder.png")
                            );

                    // 2) Строим DTO продукта, передавая url
                    return new ProductDtoResponse(
                            product.getId(),
                            product.getTitle(),
                            product.getQuantity(),
                            product.getPrice(),
                            product.getSellingPrice(),
                            url
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<OrderSupplierDtoResponse> getSupplierOrders(Principal principal) {
        Supplier supplier = getUserByPrincipal(principal);

        List<Order> orders = orderRepository.findBySupplier(supplier);

        return orders.stream()
                .map(order -> new OrderSupplierDtoResponse(
                        order.getId(),
                        order.getAddress(),
                        order.getCity(),
                        order.getProfit(),
                        order.getStatus(),
                        order.getTotalCost(),
                        order.getTotalPrice(),
                        order.getClient().getLogin(),
                        order.getDateTime()
                ))
                .toList();
    }

    @Transactional
    public List<OrderItemSupplierDtoResponse> orderInfo(Long id, Principal principal) {
        Supplier supplier = getUserByPrincipal(principal);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Такого заказа не существует"));

        if (!supplier.getId().equals(order.getSupplier().getId())) {
            throw new RuntimeException("Доступ запрещен: заказ не принадлежит данному поставщику");
        }

        List<OrderItem> items = orderItemRepository.findByOrder(order);

        return items.stream()
                .map(orderItem -> {
                    // Находим imageUrl из product → images
                    String url = orderItem.getProduct().getImages().stream()
                            .filter(Image::isPreviewImage)
                            .map(Image::getUrl)
                            .findFirst()
                            .orElseGet(() -> orderItem.getProduct().getImages().stream()
                                    .map(Image::getUrl)
                                    .findFirst()
                                    .orElse("/images/placeholder.png")
                            );

                    return new OrderItemSupplierDtoResponse(
                            orderItem.getTitle(),
                            orderItem.getQuantity(),
                            orderItem.getSellingPrice(),
                            orderItem.getProductSku(),
                            orderItem.getTotalPrice(),
                            orderItem.getTotalCost(),
                            orderItem.getCostPrice(),
                            orderItem.getTotalCost().subtract(orderItem.getTotalPrice()),
                            url  // передаем вычисленный URL
                    );
                })
                .toList();
    }


    public Supplier getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        return supplierRepository.findByLogin(principal.getName()).orElse(new Supplier());
    }
}

package com.example.diplom.service;

import com.example.diplom.dto.request.AddCartDtoRequest;
import com.example.diplom.dto.request.DeleteCartItemDtoRequest;
import com.example.diplom.models.*;
import com.example.diplom.repository.CartItemRepository;
import com.example.diplom.repository.CartRepository;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ClientRepository clientRepository;
    private final CartItemRepository cartItemRepository;

    public void addCart(AddCartDtoRequest request, Principal principal){
        Cart cart = cartRepository.findByClientId(getUserByPrincipal(principal).getId()).orElseGet(()->{
            Cart newCart = new Cart();
            Client client = clientRepository.findById(getUserByPrincipal(principal).getId())
                    .orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));
            newCart.setClient(client);
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(request.getProductId()).orElseThrow(()-> new IllegalArgumentException("Продукт не найден"));

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElseGet(()->{
                    CartItem newItem = new CartItem();
                    newItem.setProduct(product);
                    newItem.setCart(cart);
                    cart.getItems().add(newItem);
                    return newItem;
                });
        if (product.getQuantity() >= request.getQuantity()) {
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }


        cartRepository.save(cart);
        updateCartTotalPrice(cart);
    }

    public void updateCartTotalPrice(Cart cart){
        BigDecimal totalPrice = cart.getItems().stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setPrice(totalPrice);
        cartRepository.save(cart);
    }

    public void calculatePrice(Long id){
        Optional<CartItem> itemOptional = cartItemRepository.findById(id);
        CartItem cartItem =itemOptional.get();
        Product product = cartItem.getProduct();
        cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cartItemRepository.save(cartItem);
    }

    public void cartRemove(Long id, Principal principal){
        Optional<CartItem> itemOptional = cartItemRepository.findById(id);
        Cart cart = cartRepository.findByClientId(getUserByPrincipal(principal).getId()).orElseThrow();

        if(itemOptional.isPresent()){
            CartItem cartItem = itemOptional.get();
            if(cartItem.getCart().getClient().getLogin().equals(principal.getName())){
                cartItemRepository.delete(cartItem);
                updateCartTotalPrice(cart);
            }
        }
    }

    public void cartRemoveQuantity(DeleteCartItemDtoRequest request, Principal principal){
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(request.getId());
        Client client = clientRepository.findById(getUserByPrincipal(principal).getId()).orElseThrow(()->new IllegalArgumentException("Пользователя не существует"));
        Cart cart = cartRepository.findByClientId(client.getId()).orElseThrow();

        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();

            if (cartItem.getCart().getClient().getLogin().equals(principal.getName())) {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - request.getQuantity());
                    cartItemRepository.save(cartItem);
                    calculatePrice(request.getId());
                    updateCartTotalPrice(cart);
                } else {
                    cartItemRepository.delete(cartItem);
                    updateCartTotalPrice(cart);
                }
            }
        }
    }

    public Client getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        return clientRepository.findByLogin(principal.getName()).orElse(new Client());
    }
}

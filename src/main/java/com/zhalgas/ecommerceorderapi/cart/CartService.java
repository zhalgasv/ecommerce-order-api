package com.zhalgas.ecommerceorderapi.cart;

import com.zhalgas.ecommerceorderapi.cart.dto.CartResponse;
import com.zhalgas.ecommerceorderapi.cart.mapper.CartMapper;
import com.zhalgas.ecommerceorderapi.exception.BadRequestException;
import com.zhalgas.ecommerceorderapi.exception.ResourceNotFoundException;
import com.zhalgas.ecommerceorderapi.product.Product;
import com.zhalgas.ecommerceorderapi.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartMapper cartMapper;

    private final ProductRepository productRepository;

    private final CartItemRepository cartItemRepository;

    public CartService(
            CartRepository cartRepository,
            CartMapper cartMapper,
            ProductRepository productRepository,
            CartItemRepository cartItemRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional(readOnly = true)
    public Cart findByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with id: " + userId));
    }

    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = findByUserId(userId);
        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponse addProductToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = findByUserId(userId);
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id: " + productId)
        );

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        CartItem cartItem;
        int newQuantity;

        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            newQuantity = cartItem.getQuantity() + quantity;
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cart.addItem(cartItem);
            newQuantity = quantity;
        }

        if (newQuantity > product.getStockQuantity()) {
            throw new BadRequestException("Not enough stock for product with id: " + product.getId());
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponse removeProductFromCart(Long userId, Long productId) {
        Cart cart = findByUserId(userId);
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + productId + " not found in cart"));
        cart.removeItem(cartItem);
        return cartMapper.toCartResponse(cart);
    }
}

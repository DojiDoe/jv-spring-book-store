package mate.academy.bookstore.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookstore.dto.cartitem.UpdateShoppingCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.model.ShoppingCart;

public class ShoppingCartTestUtil {
    private ShoppingCartTestUtil() {
    }

    public static ShoppingCart createShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(UserTestUtil.createUser());
        shoppingCart.setId(shoppingCart.getUser().getId());
        shoppingCart.setCartItems(new HashSet<>(Set.of(CartItemTestUtil
                .createCartItem(shoppingCart))));
        return shoppingCart;
    }

    public static ShoppingCartDto createShoppingCartDto(ShoppingCart shoppingCart) {
        return new ShoppingCartDto(shoppingCart.getId(),
                shoppingCart.getUser().getId(),
                shoppingCart.getCartItems().stream()
                        .map(CartItemTestUtil::createCartItemDto)
                        .collect(Collectors.toSet()));
    }

    public static UpdateShoppingCartItemRequestDto createUpdateShoppingCartItemRequestDto() {
        return new UpdateShoppingCartItemRequestDto(2);
    }

    public static ShoppingCartDto createUpdatedShoppingCartDto(
            ShoppingCart shoppingCart,
            UpdateShoppingCartItemRequestDto requestDto) {
        return new ShoppingCartDto(shoppingCart.getId(),
                shoppingCart.getUser().getId(),
                shoppingCart.getCartItems().stream()
                        .map(cartItem
                                -> CartItemTestUtil.createUpdatedCartItemDto(cartItem, requestDto))
                        .collect(Collectors.toSet()));
    }
}

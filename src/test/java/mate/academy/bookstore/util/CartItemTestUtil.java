package mate.academy.bookstore.util;

import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.cartitem.UpdateShoppingCartItemRequestDto;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;

public class CartItemTestUtil {
    private CartItemTestUtil() {

    }

    public static CartItem createCartItem(ShoppingCart shoppingCart) {
        return new CartItem()
                .setId(1L)
                .setShoppingCart(shoppingCart)
                .setBook(BookTestUtil.createBook())
                .setQuantity(1);
    }

    public static CartItemDto createCartItemDto(CartItem cartItem) {
        return new CartItemDto(cartItem.getId(),
                cartItem.getBook().getId(),
                cartItem.getBook().getTitle(),
                cartItem.getQuantity());
    }

    public static CreateCartItemRequestDto createValidCreateCartItemRequestDto() {
        return new CreateCartItemRequestDto(1L, 1);
    }

    public static CreateCartItemRequestDto createInValidCreateCartItemRequestDto() {
        return new CreateCartItemRequestDto(100L, 1);
    }

    public static CartItemDto createUpdatedCartItemDto(
            CartItem cartItem,
            UpdateShoppingCartItemRequestDto requestDto) {
        return new CartItemDto(cartItem.getId(),
                cartItem.getBook().getId(),
                cartItem.getBook().getTitle(),
                requestDto.quantity());
    }
}

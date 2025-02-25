package mate.academy.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.cartitem.UpdateShoppingCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.CartItemRepository;
import mate.academy.bookstore.repository.ShoppingCartRepository;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.util.CartItemTestUtil;
import mate.academy.bookstore.util.ShoppingCartTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private BookRepository bookRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Test
    @DisplayName("Verify whether valid ShoppingCartDto is returned when calling "
            + "addCartItem() method with valid CreateCartItemRequestDto userId")
    public void addCartItem_ValidCreateCartItemRequestDtoAndUserId_ShouldReturnShoppingCartDto() {
        // Given
        Long userId = 1L;
        CreateCartItemRequestDto requestDto = CartItemTestUtil
                .createValidCreateCartItemRequestDto();
        ShoppingCart shoppingCart = ShoppingCartTestUtil.createShoppingCart();
        CartItem cartItem = shoppingCart.getCartItems().iterator().next();
        ShoppingCartDto expected = ShoppingCartTestUtil
                .createShoppingCartDto(shoppingCart);
        when(bookRepository.findById(requestDto.bookId())).thenReturn(Optional
                .ofNullable(cartItem.getBook()));
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(shoppingCart);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        // When
        ShoppingCartDto actual = shoppingCartService.addCartItem(requestDto, userId);
        // Then
        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findById(1L);
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
        verifyNoMoreInteractions(bookRepository, shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling "
            + "addCartItem() is called with invalid CreateCartItemRequestDto")
    public void addCartItem_InvalidRequestDto_ShouldThrowEntityNotFoundException() {
        // Given
        Long userId = 1L;
        CreateCartItemRequestDto requestDto = CartItemTestUtil
                .createInValidCreateCartItemRequestDto();
        when(bookRepository.findById(requestDto.bookId())).thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.addCartItem(requestDto, userId)
        );
        // Then
        String expected = String.format("Book with id: %d is not found", requestDto.bookId());
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(requestDto.bookId());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify whether valid ShoppingCartDto is returned when calling "
            + "update() method with valid UpdateShoppingCartItemRequestDto")
    public void updateShoppingCart_ValidData_ShouldReturnShoppingCartDto() {
        // Given
        UpdateShoppingCartItemRequestDto requestDto = ShoppingCartTestUtil
                .createUpdateShoppingCartItemRequestDto();
        ShoppingCart shoppingCart = ShoppingCartTestUtil.createShoppingCart();
        CartItem cartItem = CartItemTestUtil.createCartItem(shoppingCart);
        ShoppingCartDto expected = ShoppingCartTestUtil
                .createUpdatedShoppingCartDto(shoppingCart, requestDto);
        when(shoppingCartRepository.findByUserId(shoppingCart.getUser().getId()))
                .thenReturn(shoppingCart);
        when(cartItemRepository
                .findByIdAndShoppingCartId(cartItem.getId(), shoppingCart.getId()))
                .thenReturn(cartItem);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        Long cartItemId = cartItem.getId();
        Long userId = shoppingCart.getUser().getId();
        // When
        ShoppingCartDto actual = shoppingCartService
                .updateShoppingCart(cartItemId, requestDto, userId);
        // Then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1))
                .findByUserId(userId);
        verify(cartItemRepository, times(1))
                .findByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        verify(shoppingCartRepository, times(1))
                .save(shoppingCart);
        verify(shoppingCartMapper, times(1))
                .toDto(shoppingCart);
        verifyNoMoreInteractions(bookRepository, shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Verify whether valid ShoppingCartDto is returned when calling "
            + "delete() method with valid user id and cartItem id")
    public void deleteCartItem_ValidId_ShouldReturnShoppingCartDto() {
        // Given
        ShoppingCart shoppingCart = ShoppingCartTestUtil.createShoppingCart();
        CartItem cartItem = shoppingCart.getCartItems().iterator().next();
        shoppingCart.setCartItems(new HashSet<>());
        ShoppingCartDto expected = ShoppingCartTestUtil.createShoppingCartDto(shoppingCart);
        when(shoppingCartRepository.findByUserId(shoppingCart.getUser().getId()))
                .thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(), shoppingCart.getId()))
                .thenReturn(cartItem);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        Long cartItemId = cartItem.getId();
        Long userId = shoppingCart.getUser().getId();
        // When
        ShoppingCartDto actual = shoppingCartService.deleteCartItem(cartItemId, userId);
        // Then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
        verify(cartItemRepository, times(1))
                .findByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(bookRepository, shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Verify whether valid ShoppingCartDto is returned when calling "
            + "getShoppingCartByUserId() method with valid user id")
    public void getShoppingCartByUserId_ValidId_ShouldReturnShoppingCartDto() {
        ShoppingCart shoppingCart = ShoppingCartTestUtil.createShoppingCart();
        // Given
        ShoppingCartDto expected = ShoppingCartTestUtil.createShoppingCartDto(shoppingCart);
        when(shoppingCartRepository.findByUserId(shoppingCart.getUser().getId()))
                .thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        Long userId = shoppingCart.getUser().getId();
        // When
        ShoppingCartDto actual = shoppingCartService.getShoppingCartByUserId(userId);
        // Then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }
}

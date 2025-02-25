package mate.academy.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.cartitem.UpdateShoppingCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.CartItemRepository;
import mate.academy.bookstore.util.CartItemTestUtil;
import mate.academy.bookstore.util.ShoppingCartTestUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;

    private static final String INSERT_ROLES_SCRIPT =
            "database/roles/insert-roles.sql";
    private static final String INSERT_USER_SCRIPT =
            "database/users/insert-user.sql";
    private static final String INSERT_CATEGORIES_CLASSPATH =
            "classpath:database/categories/insert-categories.sql";
    private static final String INSERT_BOOK_CLASSPATH =
            "classpath:database/books/insert-books.sql";
    private static final String INSERT_SHOPPING_CART_SCRIPT =
            "database/shoppingcarts/insert-shopping-cart.sql";
    private static final String INSERT_CART_ITEMS_CLASSPATH =
            "classpath:database/cartitems/insert-cart-items.sql";
    private static final String CLEAN_UP_SCRIPT =
            "database/cleanup/clean-up-data.sql";

    private static final String DELETE_BOOKS_AND_CART_ITEMS_SCRIPT =
            "database/cleanup/delete-books-and-cart-items.sql";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_ROLES_SCRIPT));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_USER_SCRIPT));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_SHOPPING_CART_SCRIPT));
        }
    }

    @Test
    @Sql(scripts = {
            INSERT_CATEGORIES_CLASSPATH,
            INSERT_BOOK_CLASSPATH,
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithUserDetails(value = "dojidoe@gmail.com")
    @DisplayName("beebadoobee")
    void addBookToShoppingCart_ValidRequestDto_ShouldReturnShoppingCartDto() throws Exception {
        //Given
        CreateCartItemRequestDto requestDto = CartItemTestUtil
                .createValidCreateCartItemRequestDto();
        ShoppingCart shoppingCart = ShoppingCartTestUtil.createShoppingCart();
        ShoppingCartDto expected = ShoppingCartTestUtil.createShoppingCartDto(shoppingCart);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertEquals(expected.cartItems().size(), actual.cartItems().size());
        assertTrue(reflectionEquals(expected, actual, "cartItems"));
        assertTrue(reflectionEquals(expected.cartItems().iterator().next(),
                actual.cartItems().iterator().next(), "id"));
    }

    @Test
    @Sql(scripts = {
            INSERT_CATEGORIES_CLASSPATH,
            INSERT_BOOK_CLASSPATH,
            INSERT_CART_ITEMS_CLASSPATH
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithUserDetails(value = "dojidoe@gmail.com")
    @DisplayName("beebadoobee2")
    void getShoppingCart_ValidData_ShouldReturnShoppingCartDto() throws Exception {
        // Given
        ShoppingCartDto expected = ShoppingCartTestUtil
                .createShoppingCartDto(ShoppingCartTestUtil.createShoppingCart());
        // When
        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertEquals(expected.cartItems().size(), actual.cartItems().size());
        assertTrue(reflectionEquals(expected, actual, "cartItems"));
        assertTrue(reflectionEquals(expected.cartItems().iterator().next(),
                actual.cartItems().iterator().next(), "id"));
    }

    @Test
    @Sql(scripts = {
            INSERT_CATEGORIES_CLASSPATH,
            INSERT_BOOK_CLASSPATH,
            INSERT_CART_ITEMS_CLASSPATH
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithUserDetails(value = "dojidoe@gmail.com")
    @DisplayName("beebadoobee3")
    void updateShoppingCart_ValidRequestDto_ShouldReturnShoppingCartDto() throws Exception {
        // Given
        Long cartItemIdToUpdate = 1L;
        UpdateShoppingCartItemRequestDto requestDto = ShoppingCartTestUtil
                .createUpdateShoppingCartItemRequestDto();
        ShoppingCartDto expected = ShoppingCartTestUtil.createUpdatedShoppingCartDto(
                ShoppingCartTestUtil.createShoppingCart(),
                requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(put("/cart/items/{cartItemId}",
                        cartItemIdToUpdate)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertEquals(expected.cartItems().size(), actual.cartItems().size());
        assertTrue(reflectionEquals(expected, actual, "cartItems"));
        assertTrue(reflectionEquals(expected.cartItems().iterator().next(),
                actual.cartItems().iterator().next(), "id"));
    }

    @Test
    @Sql(scripts = {
            INSERT_CATEGORIES_CLASSPATH,
            INSERT_BOOK_CLASSPATH,
            INSERT_CART_ITEMS_CLASSPATH
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithUserDetails(value = "dojidoe@gmail.com")
    @DisplayName("beebadoobee4")
    void deleteCartItem_ValidId_ShouldReturnShoppingCartDto() throws Exception {
        // Given
        Long cartItemIdToDelete = 1L;
        // When
        mockMvc.perform(delete("/cart/items/{cartItemId}",
                        cartItemIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        // Then
        Optional<CartItem> result = cartItemRepository.findById(cartItemIdToDelete);
        assertEquals(Optional.empty(), result);
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        tearDownBooksAndCartItems(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void tearDownBooksAndCartItems(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(DELETE_BOOKS_AND_CART_ITEMS_SCRIPT)
            );
        }
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(CLEAN_UP_SCRIPT)
            );
        }
    }
}

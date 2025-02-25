package mate.academy.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.util.ShoppingCartTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    private static final String INSERT_ROLES_CLASSPATH =
            "classpath:database/roles/insert-roles.sql";
    private static final String INSERT_USER_CLASSPATH =
            "classpath:database/users/insert-user.sql";
    private static final String INSERT_CATEGORIES_CLASSPATH =
            "classpath:database/categories/insert-categories.sql";
    private static final String INSERT_BOOK_CLASSPATH =
            "classpath:database/books/insert-books.sql";
    private static final String INSERT_SHOPPING_CART_CLASSPATH =
            "classpath:database/shoppingcarts/insert-shopping-cart.sql";
    private static final String INSERT_CART_ITEMS_CLASSPATH =
            "classpath:database/cartitems/insert-cart-items.sql";
    private static final String CLEAN_UP_CLASSPATH =
            "classpath:database/cleanup/clean-up-data.sql";

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("find shopping cart by user id")
    @Sql(scripts = {
            INSERT_ROLES_CLASSPATH,
            INSERT_USER_CLASSPATH,
            INSERT_CATEGORIES_CLASSPATH,
            INSERT_BOOK_CLASSPATH,
            INSERT_SHOPPING_CART_CLASSPATH,
            INSERT_CART_ITEMS_CLASSPATH
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            CLEAN_UP_CLASSPATH
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByUserId_ValidUserId_ShouldReturnShoppingCart() {
        Long userId = 2L;
        ShoppingCart actual = shoppingCartRepository.findByUserId(userId);
        ShoppingCart expected = ShoppingCartTestUtil.createShoppingCart();
        assertEquals(actual.getCartItems().size(), expected.getCartItems().size());
        assertEquals(actual.getUser().getId(), userId);
    }
}

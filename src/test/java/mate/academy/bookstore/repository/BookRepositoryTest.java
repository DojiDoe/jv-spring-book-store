package mate.academy.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import java.util.List;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.util.BookTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    private static final String INSERT_CATEGORIES_CLASSPATH =
            "classpath:database/categories/insert-categories.sql";
    private static final String INSERT_BOOKS_CLASSPATH =
            "classpath:database/books/insert-books.sql";
    private static final String CLEAN_UP_CLASSPATH =
            "classpath:database/cleanup/clean-up-data.sql";
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("find all books")
    @Sql(scripts = {
            INSERT_CATEGORIES_CLASSPATH,
            INSERT_BOOKS_CLASSPATH
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            CLEAN_UP_CLASSPATH
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAll_ValidPageable_ShouldReturnListOfBook() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> actual = bookRepository.findAll(pageable).stream().toList();
        assertEquals(3, actual.size());
        assertEquals("1984", actual.get(0).getTitle());
        assertEquals("978-0-596-52069-4", actual.get(1).getIsbn());
    }

    @Test
    @DisplayName("find all books by category id")
    @Sql(scripts = {
            INSERT_CATEGORIES_CLASSPATH,
            INSERT_BOOKS_CLASSPATH
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            CLEAN_UP_CLASSPATH
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoryId_ValidId_ShouldReturnListOfBook() {
        Long validCategoryId = 1L;
        List<Book> actual = bookRepository.findAllByCategoryId(validCategoryId);
        Book expected = BookTestUtil.createBook();
        assertEquals(3, actual.size());
        assertTrue(reflectionEquals(expected, actual.get(0), "categories"));
        assertEquals("Animal Farm", actual.get(1).getTitle());
        assertEquals("Ilarion Pavliuk", actual.get(2).getAuthor());
    }

}

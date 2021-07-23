package br.com.renatoschlogel.libraryapi.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.renatoschlogel.libraryapi.exception.BusinessException;
import br.com.renatoschlogel.libraryapi.model.entity.Book;
import br.com.renatoschlogel.libraryapi.model.repository.BookRepository;
import br.com.renatoschlogel.libraryapi.service.BookService;
import br.com.renatoschlogel.libraryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService bookService;
	
	@MockBean
	BookRepository bookRepository;
	
	@BeforeEach
	public void setUp() {
		this.bookService = new BookServiceImpl(bookRepository);
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	void saveBookTest() throws Exception {
		
		Book book = createValidBook();
		Book bookReturn = Book.builder().id(1L).title("Titulo").author("Autor").isbn("123").build();
		Mockito.when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(false);
		
		Mockito.when(bookRepository.save(book)).thenReturn(bookReturn);
		
		Book savedBook = bookService.incluir(book);
		
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getTitle()).isEqualTo(bookReturn.getTitle());
		assertThat(savedBook.getAuthor()).isEqualTo(bookReturn.getAuthor());
		assertThat(savedBook.getIsbn()).isEqualTo(bookReturn.getIsbn());
	}

	@Test
	@DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
	void shouldNotSaveABookWithDuplicateIsbn() throws Exception {
		Book book = createValidBook();
		
		Mockito.when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);
		
		Throwable exception = Assertions.catchThrowable(() -> bookService.incluir(book) );
		
		assertThat(exception).isInstanceOf(BusinessException.class)
		                     .hasMessage("Isbn já utilizado por outro livro!");
		Mockito.verify(bookRepository, Mockito.never()).save(book);
	}
	
	@Test
	@DisplayName("Deve obter um livro por id")
	void getByIdTest() throws Exception {
		Long id = 1l;
		
		Book book = createValidBook();
		book.setId(id);
		
		Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));
		
		Optional<Book> optBook = bookService.getById(id);
	
		assertThat(optBook.isPresent()).isTrue();
		Book bookAvail = optBook.get();
		assertThat(bookAvail.getId()).isEqualTo(id);
		assertThat(bookAvail.getTitle()).isEqualTo(book.getTitle());
		assertThat(bookAvail.getAuthor()).isEqualTo(book.getAuthor());
		assertThat(bookAvail.getIsbn()).isEqualTo(book.getIsbn());
		
	}
	
	@Test
	@DisplayName("Deve retornar vazio quando nao encontrar o livro na base")
	void bookNotFoundByIdTest() throws Exception {
		Long id = 1l;
		
		Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Book> optBook = bookService.getById(id);
	
		assertThat(optBook.isPresent()).isFalse();
	}
	
	@Test
	@DisplayName("Deve atualizar um livro")
	void updateBookTest() throws Exception {
		Book book = createValidBook();
		long id = 1L;
		String title = "Titulo Atualizado";
		book.setId(id);	
		book.setTitle(title);
		
		Mockito.when(bookRepository.save(book)).thenReturn(book);
		
		book = bookService.update(book);
		
		assertThat(book.getId()).isEqualTo(id);
		assertThat(book.getTitle()).isEqualTo(title);
		
	}
	
	@Test
	@DisplayName("Deve lancar uma exceção ao tentar atualizar pois o livro não esta persistido")
	void updateNotPersitBookTest() throws Exception {
		Book bookNullId = createValidBook();
		assertThrows(IllegalArgumentException.class, () ->bookService.update(bookNullId) );
		
		Mockito.verify(bookRepository, Mockito.never()).save(bookNullId);
	}
	
	@Test
	@DisplayName("Deve deletar um  livro")
	void deleteBookTest() throws Exception {
		Book book = createValidBook();
		long id = 1L;
		book.setId(id);	
			
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.delete(book));
		
		Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
	}
	
	@Test
	@DisplayName("Deve lancar uma exceção ao tentar deletar pois o livro não esta persistido")
	void deleteNotPersitBookTest() throws Exception {
		Book bookNullId = new Book();
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(bookNullId) );
		
		Mockito.verify(bookRepository, Mockito.never()).delete(bookNullId);
	}


	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Deve filtrar livros pelas propriedades ")
	void filterBooksByProperties () throws Exception {
		Book book = createValidBook();
		
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<Book> list = Arrays.asList(book);
		Page<Book> page = new PageImpl<Book>(list, pageRequest , 1);
		
		when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
			   .thenReturn(page);
		
		Page<Book> booksPage = bookService.find(book, pageRequest);
		
		assertThat(booksPage.getTotalElements()).isEqualTo(1);
		assertThat(booksPage.getContent()).isEqualTo(list);
		assertThat(booksPage.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(booksPage.getPageable().getPageSize()).isEqualTo(10);
	}
	
	@Test
	@DisplayName("Deve obter o livro pelo isbn")
	void shoudBeBookByIsbn() throws Exception {
		String isbn = "1234";
		Book book = Book.builder().id(1l).isbn(isbn).build();
		when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
		
		Optional<Book> optBook = bookService.getBookByIsbn(isbn);
		
		assertThat(optBook.isPresent()).isTrue();
		assertThat(optBook.get().getId()).isEqualTo(1l);
		assertThat(optBook.get().getIsbn()).isEqualTo(isbn);
		
		verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
	}
	
	private Book createValidBook() {
		return Book.builder().title("Titulo").author("Autor").isbn("123").build();
	}
	
}

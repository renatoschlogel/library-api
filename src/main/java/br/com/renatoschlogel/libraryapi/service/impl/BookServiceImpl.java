package br.com.renatoschlogel.libraryapi.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.renatoschlogel.libraryapi.exception.BusinessException;
import br.com.renatoschlogel.libraryapi.model.entity.Book;
import br.com.renatoschlogel.libraryapi.model.repository.BookRepository;
import br.com.renatoschlogel.libraryapi.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	BookRepository bookRepository;
	
	public BookServiceImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}
		
	@Override
	public Book incluir(Book book) {
		
		if (bookRepository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já utilizado por outro livro!");
		}
		
		return bookRepository.save(book);
	}

	@Override
	public Optional<Book> getById(Long id) {
		return bookRepository.findById(id);
	}

	@Override
	public void delete(Book book) {
		if (book == null || book.getId() == null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		bookRepository.delete(book);
	}

	@Override
	public Book update(Book book) {
		if (book == null || book.getId() == null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		
		return bookRepository.save(book);
	}

	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		
		Example<Book> example = Example.of(filter, ExampleMatcher.matching()
				                                                 .withIgnoreCase()
				                                                 .withIgnoreNullValues()
				                                                 .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
		return bookRepository.findAll(example, pageRequest)
;	}

	@Override
	public Optional<Book> getBookByIsbn(String isbn) {
		return bookRepository.findByIsbn(isbn);
	}


}

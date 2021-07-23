package br.com.renatoschlogel.libraryapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import br.com.renatoschlogel.libraryapi.model.entity.Book;

public interface BookService {

	Book incluir(Book book);

	Optional<Book> getById(Long id);

	void delete(Book book);

	Book update(Book book);

	Page<Book> find(Book filter, org.springframework.data.domain.Pageable pageRequest);

	Optional<Book> getBookByIsbn(String string);

}

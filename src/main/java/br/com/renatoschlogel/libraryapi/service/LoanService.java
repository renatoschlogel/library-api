package br.com.renatoschlogel.libraryapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.renatoschlogel.libraryapi.api.dto.LoanFilterDTO;
import br.com.renatoschlogel.libraryapi.model.entity.Book;
import br.com.renatoschlogel.libraryapi.model.entity.Loan;

public interface LoanService {

	public Loan save(Loan loan);

	public Loan updateReturnedBook(Long idLoan, Boolean retorned);

	public Optional<Loan> findById(Long id);

	public Page<Loan> find(LoanFilterDTO loan, Pageable pageable);

	public Page<Loan> getloansByBook(Book book, Pageable pageable);
	
	public List<Loan> getAllLateLoans();

}

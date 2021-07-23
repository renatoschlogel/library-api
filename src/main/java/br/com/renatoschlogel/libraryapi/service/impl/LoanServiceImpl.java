package br.com.renatoschlogel.libraryapi.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.renatoschlogel.libraryapi.api.dto.LoanFilterDTO;
import br.com.renatoschlogel.libraryapi.exception.BusinessException;
import br.com.renatoschlogel.libraryapi.model.entity.Book;
import br.com.renatoschlogel.libraryapi.model.entity.Loan;
import br.com.renatoschlogel.libraryapi.model.repository.LoanRepository;
import br.com.renatoschlogel.libraryapi.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService{
	
	private LoanRepository loanRepository;

	public LoanServiceImpl(LoanRepository loanRepository) {
		this.loanRepository = loanRepository;
	}

	@Override
	public Loan save(Loan loan) {
		
		if(loanRepository.existsByBookAndNotReturned(loan.getBook())) {
			throw new BusinessException("Book already loaned.");
		}
		
		return loanRepository.save(loan);
	}

	@Override
	public Loan updateReturnedBook(Long idLoan, Boolean retorned) {
		Loan loan = loanRepository.findById(idLoan).orElseThrow(() -> new BusinessException("Empréstimo não encontrado!") );
		loan.setReturned(retorned);
		return loanRepository.save(loan);
	}

	@Override
	public Optional<Loan> findById(Long id) {
		return loanRepository.findById(id);
	}

	@Override
	public Page<Loan> find(LoanFilterDTO loanFilter, Pageable pageable) {
		return loanRepository.findByBookIsbnOrCustomer(loanFilter.getIsbn(), loanFilter.getCustumer(), pageable);
	}

	@Override
	public Page<Loan> getloansByBook(Book book, Pageable pageable) {
		return loanRepository.findByBook(book, pageable);
	}

	@Override
	public List<Loan> getAllLateLoans() {
		
		final Integer loanDays = 4;
		LocalDate theeDaysAgo = LocalDate.now().minusDays(loanDays);
		
		return loanRepository.findByLoanDateLessThanAndNotReturned(theeDaysAgo);
	}

}

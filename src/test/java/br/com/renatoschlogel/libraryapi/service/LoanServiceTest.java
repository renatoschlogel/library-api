package br.com.renatoschlogel.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.renatoschlogel.libraryapi.api.dto.LoanFilterDTO;
import br.com.renatoschlogel.libraryapi.exception.BusinessException;
import br.com.renatoschlogel.libraryapi.model.entity.Book;
import br.com.renatoschlogel.libraryapi.model.entity.Loan;
import br.com.renatoschlogel.libraryapi.model.entity.Loan.LoanBuilder;
import br.com.renatoschlogel.libraryapi.model.repository.LoanRepository;
import br.com.renatoschlogel.libraryapi.service.impl.LoanServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {
	
	private LoanService loanService;
	
	@MockBean
	private LoanRepository loanRepository;
	
	@BeforeEach
	public void setUp() {
		loanService = new LoanServiceImpl(loanRepository);
	}

	@Test
	@DisplayName("Deve salvar um emprestimo")
	void saveLoan() throws Exception {
		Book book = Book.builder().build();
		String custumer = "Renato";
		Loan loan = Loan.builder().book(book)
								  .customer(custumer)
								  .loanDate(LocalDate.now())
								  .build();
		
		Loan loanExpected = Loan.builder().id(1l)
				  .book(book)
				  .customer(custumer)
				  .loanDate(LocalDate.now())
				  .build();
		
		when(loanRepository.save(loan)).thenReturn(loanExpected);
		
		Loan loanSalved = loanService.save(loan);
		
		assertThat(loan).isNotNull();
		assertThat(loanSalved.getId()).isEqualTo(1l);
		assertThat(loanSalved.getBook()).isEqualTo(book);
		
	}
	
	@Test
	@DisplayName("Deve lançar exceção ao tentar emprestar um livro já emprestado")
	void bookAlreadyBorrowed() throws Exception {
		Book book = Book.builder().build();
		String custumer = "Renato";
		Loan loan = Loan.builder().book(book)
								  .customer(custumer)
								  .loanDate(LocalDate.now())
								  .build();
		when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(true);
		
		Throwable exception = catchThrowable(() -> loanService.save(loan));
		assertThat(exception).isInstanceOf(BusinessException.class)
							 .hasMessage("Book already loaned.");
		
		verify(loanRepository, never()).save(loan);
	}
	
	@Test
	@DisplayName("Deve encontrar o empréstimo pelo id")
	void getLoanById() throws Exception {
		
		Loan loan = loanBuilder().build();
		
		when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
		
		Optional<Loan> optLoan = loanService.findById(loan.getId());

		assertThat(optLoan.isPresent()).isTrue();
		assertThat(optLoan.get()).isEqualTo(loan);
		
		Mockito.verify(loanRepository, times(1)).findById(loan.getId());
		
	}
	
	@Test
	@DisplayName("Deve atualizaro o status do emprestimo")
	void updateReturnedBook() throws Exception {

		Loan loan = loanBuilder().build();
		loan.setReturned(false);
		
		when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
		when(loanRepository.save(Mockito.any(Loan.class))).then(AdditionalAnswers.returnsFirstArg());		
		
		Loan retornedLoan = loanService.updateReturnedBook(loan.getId(), true);
		
		assertThat(retornedLoan).isNotNull();
		assertThat(retornedLoan.getId()).isEqualTo(loan.getId());
		assertThat(retornedLoan.getReturned()).isTrue();
		
		verify(loanRepository, times(1)).save(loan);
	}
	
	@Test
	void updateReturnedBookNotFound() throws Exception {
		Throwable exception = Assertions.catchThrowable(() -> loanService.updateReturnedBook(899l, true));
		assertThat(exception).isInstanceOf(BusinessException.class)
							 .hasMessage("Empréstimo não encontrado!");
		
	}
	
	@Test
	@DisplayName("Deve filtrar empréstimos pelas propriedades")
	void filterLoansByProperties () throws Exception {
		
		Loan loan = loanBuilder().build();
		LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().custumer("Renato").isbn("123").build();
		
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<Loan> list = Arrays.asList(loan);
		
		Page<Loan> page = new PageImpl<Loan>(list, pageRequest , 1);
		
		when(loanRepository.findByBookIsbnOrCustomer(anyString(), anyString(), any(Pageable.class))).thenReturn(page);
		
		Page<Loan> loansPage = loanService.find(loanFilterDTO, pageRequest);
		
		assertThat(loansPage.getTotalElements()).isEqualTo(1);
		assertThat(loansPage.getContent()).isEqualTo(list);
		assertThat(loansPage.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(loansPage.getPageable().getPageSize()).isEqualTo(10);
	}
	
	
	private LoanBuilder loanBuilder() {
		return Loan.builder().id(1l)
								  .book(Book.builder().build())
								  .customer("Renato")
								  .loanDate(LocalDate.now());
	}
	
}

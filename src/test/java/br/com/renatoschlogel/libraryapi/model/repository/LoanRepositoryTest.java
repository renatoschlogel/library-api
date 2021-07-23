package br.com.renatoschlogel.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.renatoschlogel.libraryapi.model.entity.Book;
import br.com.renatoschlogel.libraryapi.model.entity.Book.BookBuilder;
import br.com.renatoschlogel.libraryapi.model.entity.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private LoanRepository loanRepository;
	
	@Test
	@DisplayName("Deve verificar o livro esta emprestado")
	void existsByBookAndNotReturned() throws Exception {
		
		Book availableBook = bookBuilder().build();
		Book borroweBook = bookBuilder().build();
		Loan loan = Loan.builder().book(borroweBook).customer("Renato").loanDate(LocalDate.now()).build();
		
		entityManager.persist(availableBook);
		entityManager.persist(borroweBook);
		entityManager.persist(loan);
		
		assertThat(loanRepository.existsByBookAndNotReturned(borroweBook)).isTrue();
		assertThat(loanRepository.existsByBookAndNotReturned(availableBook)).isFalse();
	}
	
	@Test
	@DisplayName("Deve buscar empréstimo por isbn do livro ou custumer")
	void findByBookIsbnOrCustumer() throws Exception {
		
		Book borroweBook = bookBuilder().isbn("123").build();
		Loan loan = Loan.builder().book(borroweBook).customer("Renato").loanDate(LocalDate.now()).build();
		
		entityManager.persist(borroweBook);
		entityManager.persist(loan);
		
		assertThat(loanRepository.findByBookIsbnOrCustomer("123", "Renato", PageRequest.of(0, 10)).getContent()).hasSize(1);
		assertThat(loanRepository.findByBookIsbnOrCustomer("123", null, PageRequest.of(0, 10)).getContent()).hasSize(1);
		assertThat(loanRepository.findByBookIsbnOrCustomer(null, "Renato", PageRequest.of(0, 10)).getContent()).hasSize(1);
		assertThat(loanRepository.findByBookIsbnOrCustomer(null, null, PageRequest.of(0, 10)).getContent()).hasSize(0);
		assertThat(loanRepository.findByBookIsbnOrCustomer("naodisponivel", "naodisponivel", PageRequest.of(0, 10)).getContent()).hasSize(0);
		
	}
	
	@Test
	@DisplayName("Deve retornar os empréstimos atrasados")
	void findByLoanDateLessThanAndNotReturned() throws Exception {
		
		Book book = bookBuilder().isbn("123").build();
		Loan loan = Loan.builder().book(book).customer("Renato").loanDate(LocalDate.now().minusDays(5)).build();
		
		entityManager.persist(book);
		entityManager.persist(loan);
		
		List<Loan> overdueLoans = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
		
		assertThat(overdueLoans).hasSize(1).contains(loan);
		
	}
	
	@Test
	@DisplayName("Não deve retornar os empréstimos em dia")
	void findByLoanDateLessThanAndNotReturnedEmpty() throws Exception {
		
		Book book = bookBuilder().isbn("123").build();
		Loan loan = Loan.builder().book(book).customer("Renato").loanDate(LocalDate.now()).build();
		
		entityManager.persist(book);
		entityManager.persist(loan);
		
		List<Loan> overdueLoans = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now());
		
		assertThat(overdueLoans).isEmpty();
		
	}
	
	private BookBuilder bookBuilder() {
		return Book.builder().title("Clean Code").author("Uncle Bob");
	}
}

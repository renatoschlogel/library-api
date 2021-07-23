package br.com.renatoschlogel.libraryapi.api.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.renatoschlogel.libraryapi.api.dto.BookDTO;
import br.com.renatoschlogel.libraryapi.api.dto.LoanDTO;
import br.com.renatoschlogel.libraryapi.api.dto.LoanFilterDTO;
import br.com.renatoschlogel.libraryapi.api.dto.ReturnedLoanDTO;
import br.com.renatoschlogel.libraryapi.model.entity.Book;
import br.com.renatoschlogel.libraryapi.model.entity.Loan;
import br.com.renatoschlogel.libraryapi.service.BookService;
import br.com.renatoschlogel.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController{
	
	private final LoanService loanService;
	private final BookService bookService;
	private final ModelMapper modelMapper;
	
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long create(@RequestBody LoanDTO loanDTO) {
		
		Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
							   .orElseThrow(() -> new ResponseStatusException( HttpStatus.BAD_REQUEST, "Book not found fot passad isbn."));
		
		Loan loan = Loan.builder().book(book)
								  .customer(loanDTO.getCustomer())
								  .loanDate(LocalDate.now())
								  .build();
		loan = loanService.save(loan);
		
		return loan.getId();
	}
	
	@PatchMapping("{id}")
	public void returnedBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO returnedLoanDTO) {
		Loan loan = loanService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		loanService.updateReturnedBook(loan.getId(), returnedLoanDTO.getRetorned());
	}
	
	@GetMapping
	public Page<LoanDTO> find(LoanFilterDTO loanFilter, Pageable pageable) {
	
		Page<Loan> result = loanService.find(loanFilter, pageable);
		List<LoanDTO> LoanDtoList = result.getContent().stream()
						   .map( loan -> { 
							   LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
							   loanDTO.setBook(modelMapper.map(loan.getBook(), BookDTO.class));
							return loanDTO;
						   })
						   .collect(Collectors.toList());
		return new PageImpl<LoanDTO>(LoanDtoList, pageable, 0);
	}

	
}

package br.com.renatoschlogel.libraryapi.api.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

	private Long id;
	
	@NotEmpty
	private String isbn;
	
	@NotEmpty
	private String customer;
	
	@NotEmpty
	private String customerEmail;
	
	@NotEmpty
	private BookDTO book;
	
}

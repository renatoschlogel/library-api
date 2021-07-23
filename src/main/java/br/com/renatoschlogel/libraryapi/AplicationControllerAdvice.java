package br.com.renatoschlogel.libraryapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import br.com.renatoschlogel.libraryapi.api.exception.ApiErros;
import br.com.renatoschlogel.libraryapi.exception.BusinessException;

@RestControllerAdvice
public class AplicationControllerAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErros handleValidationException(MethodArgumentNotValidException exception){
		BindingResult bindingResult = exception.getBindingResult();
		return new ApiErros(bindingResult);
	}
	
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErros handleBusinessException(BusinessException exception){
		return new ApiErros(exception);
	}
	
	@ExceptionHandler(ResponseStatusException.class)
	@ResponseStatus
	private ResponseEntity<ApiErros> handleResponseStatusException(ResponseStatusException exception) {
		return new ResponseEntity<ApiErros>(new ApiErros(exception), exception.getStatus());
		
	}
	
}

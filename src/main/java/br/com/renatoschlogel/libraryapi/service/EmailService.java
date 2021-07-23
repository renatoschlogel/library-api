package br.com.renatoschlogel.libraryapi.service;

import java.util.List;

public interface EmailService {

	void sendMails(String messageLateLoans, List<String> mails);
	
}

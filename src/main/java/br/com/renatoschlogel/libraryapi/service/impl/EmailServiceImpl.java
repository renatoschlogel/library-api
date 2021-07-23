package br.com.renatoschlogel.libraryapi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.renatoschlogel.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender javaMailSender;
	
	@Value("${application.mail.default-remetent}")
	private String remetent;
	
	@Override
	public void sendMails(String messageLateLoans, List<String> mails) {
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		
		mailMessage.setFrom(remetent);
		mailMessage.setSubject("Livro em atraso");
		mailMessage.setText(messageLateLoans);
		mailMessage.setTo(mails.toArray(new String[mails.size()]));
		
		
		javaMailSender.send(mailMessage);
	}

}

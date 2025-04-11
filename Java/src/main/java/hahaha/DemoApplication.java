package hahaha;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import hahaha.repository.AccountRepository;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Autowired
	private DataSource dataSource;

	@Bean
	CommandLineRunner test(AccountRepository accountRepository) {

		return args -> {
			System.out.println("Dang test truy van user");

			try (Connection connection = dataSource.getConnection()) {
				System.out.println("Dang ket noi voi schema " + connection.getMetaData().getUserName());
			}

			accountRepository.findByUsername("nguyenvana")
					.ifPresentOrElse(
							acc -> System.out.println("Tim thay user trong DB " + acc.getUsername()),
							() -> System.out.println("Khong tim thay user trong DB"));
		};
	}

}

package hahaha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hahaha.repository.AccountRepository;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private AccountRepository accountRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Bat dau tim user 'nguyenvana'...");
		Thread.sleep(2000); // ✨ CHỜ 2 GIÂY

        accountRepository.findByUsername("nguyenvana").ifPresentOrElse(
            account -> {
                System.out.println("Tim thay user:");
                System.out.println("- Username: " + account.getUsername());
                System.out.println("- Status: " + account.getStatus());
            },
            () -> {
                System.out.println("Khong tim thay user 'nguyenvana' trong database.");
            }
        );

    }
}

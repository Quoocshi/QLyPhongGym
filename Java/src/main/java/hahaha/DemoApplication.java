package hahaha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hahaha.model.Account;
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
		Thread.sleep(2000); 

        Account acc = accountRepository.findAccountByUsername("nguyenvana");
        
            if(acc != null){
                System.out.println("Tim thay user:");
                System.out.println("- Username: " + acc.getUsername());
                System.out.println("- Status: " + acc.getStatus());
            }
            else {
                System.out.println("Khong tim thay user 'nguyenvana' trong database.");
            }

    }
}

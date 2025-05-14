package hahaha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hahaha.model.Account;
import hahaha.repository.AccountRepository;
import hahaha.repository.RoleGroupRepository;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleGroupRepository roleGroupRepository;
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Bat dau tim 'adminuser'...");
		Thread.sleep(2000); 

        Account acc = accountRepository.findAccountByUserName("adminuser");
        long accid = acc.getAccountId();
        Long accRoleGroupID = accountRepository.findRoleGroupIdByAccountId(accid);
        if(acc!=null){
            String accRoleGroupName = roleGroupRepository.findRoleNameByRoleGroupId(accRoleGroupID);
            System.out.println("Tim thay adminuser:");
            System.out.println("- Full name: " + acc.getFullName());
            System.out.println("- Username: " + acc.getUserName());
            System.out.println("- Status: " + acc.getStatus());
            System.out.println("- Role: "+ accRoleGroupName);
        }
        else{
            System.out.println("Khong tim thay adminuser");
        }

    }
}

package hahaha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hahaha.model.Account;
import hahaha.repository.AccountAssignRoleGroupRepository;
import hahaha.repository.AccountRepository;
import hahaha.repository.RoleGroupRepository;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountAssignRoleGroupRepository assignRoleGroupRepository;

    @Autowired
    private RoleGroupRepository roleGroupRepository;
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Bat dau tim 'staff1'...");
		Thread.sleep(2000); 

        Account acc = accountRepository.findAccountByUsername("staff1");
        long accid = acc.getAccountId();
        Long accRoleGroupID = assignRoleGroupRepository.findRoleGroupIdByAccountId(accid);
        String accRoleGroupName = roleGroupRepository.findRoleNameByRoleGroupId(accRoleGroupID);
            if(acc != null){
                System.out.println("Tim thay staff:");
                System.out.println("- Username: " + acc.getUsername());
                System.out.println("- Status: " + acc.getStatus());
                System.out.println("- Role: "+ accRoleGroupName);
            }
            else {
                System.out.println("Khong tim thay 'staff1' trong database.");
            }

    }
}

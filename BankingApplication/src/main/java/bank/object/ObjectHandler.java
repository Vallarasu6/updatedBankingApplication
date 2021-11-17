package bank.object;

import bank.pojo.CustomerInfo;
import pojo_account.AccountInfo;

public class ObjectHandler {

    public static CustomerInfo customerObject(String name, long mobile){

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setName(name);
        customerInfo.setMobileNumber(mobile);
        return customerInfo;
    }
    public static AccountInfo accountObject(String bank, long accountNumber, long balance){

        return accountObject(-1,bank,accountNumber,balance);
    }
    public static AccountInfo accountObject(int id, String bank, long accountNumber, long balance){
        AccountInfo accountInfo = new AccountInfo();
        if(id!=-1){
            accountInfo.setId(id);
        }
        accountInfo.setBankName(bank);
        accountInfo.setAccountNumber(accountNumber);
        accountInfo.setBalance(balance);
        return accountInfo;
    }
}

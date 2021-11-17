package bank.logic;
import bank.database.DbStore;
import bank.exception.HandledException;
import bank.interfaces.InterfaceCommon;
import bank.pojo.CustomerInfo;
import cache.CacheStore;
import hashMap.HashMapHandler;
import historyPojo.History;
import pojo_account.AccountInfo;

import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.plaf.synth.SynthFormattedTextFieldUI;

import com.mysql.cj.x.protobuf.MysqlxExpect.Open.Condition.Key;

import TransactionHistory.TransactionHistoryPojo;
import allHistoryPojo.AllHistory;

public class LogicLayer {
	
	  private InterfaceCommon db;
	    FileReader reader;
	    long depositCharges=1;
	    long withdrawCharges=1;
	    long transactionCharges=2;

	    public LogicLayer() {
	        try {
	            reader = new FileReader("/home/inc15/eclipse-workspace/BankingApplication/src/main/java/file.properties");
	            Properties properties=new Properties();
	            properties.load(reader);
	            String data = properties.getProperty("dbConnection");
	            db = (InterfaceCommon)Class.forName(data).newInstance();

	        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
	            e.printStackTrace();
	        }
	    }
//	
//	DbStore db = null;
//	
//   public LogicLayer() {
//	   
//	  db = new DbStore();
//	// TODO Auto-generated constructor stub
//}
 
	    //change mobile number
	    public void changeMobile(long mobile, int id) {
			db.changeMobile(mobile,id);
		}

	    //change address
	    public void changeAddress(String address, int id) {
			db.changeAddress(address,id);
		}
	    
	    
	    //check balance
	    public long checkBalance(long accountNumber) {
	    	long balance = db.checkBalance(accountNumber);
	    	return balance;
			
		}
	    
	    
//check login
	    public int checkLogin(int id,String password) {
	    	String name = db.checkLogin(id,password);
	    	//System.out.print(name+" name "+userName+" ");
	    		if(name.equals("yes")) {
	    			return 1;
	    		}
	    		else if(name.equals("admin")) {
	    			return 2;
	    		}
	    		else {
	    			return 0;
	    	
	    	}
	    	
			
		}
	//show history
	    
	    
	    public ArrayList<History> showHistory() {
	    	return db.showHistory();
	    }
	    
//	//transaction
//	  public void transaction_history(long sender_accountNumber,long receiver_accountNumber,long balance) throws SQLException {
//	  db.transaction_history(sender_accountNumber, receiver_accountNumber, balance);
//	  }
   
 //check account number exist
   public boolean checkAccountNumber(long acc_num) {
	   return db.checkAccountNumber(acc_num);
   }
 //check customerid exist
   public boolean checkCustomerId(int id) {
	  // System.out.print(id+" ll id");
	   return db.checkCustomerId(id);
   }

 //show customerList - Active
   public ArrayList<CustomerInfo> getCustomerList() {
	   return db.getCustomerList();
   }
   public void customerCountList(int pageCount) {
	db.customerCountList(pageCount);
}

 //show customerList - InActive
   public ArrayList<CustomerInfo> getInActiveCustomerList() {
	   return db.getInActiveCustomerList();
   }
   
 //show accountList - Inactive
   public ArrayList<AccountInfo> getInActiveAccountList() {
	   return db.getInActiveAccountList();
   }
   
 //show Accounts
   public ArrayList<AccountInfo> getAccountList() {
	   return db.getAccountList();
   }
   
 //show Applied loans list
   public ArrayList<AccountInfo> getAppliedLoanList() {
	   return db.getAppliedLoanList();
   }
 //show Approved loans list
   public ArrayList<AccountInfo> getApprovedLoanList() {
	   return db.getApprovedLoanList();
   }
 //show waiting loans list
   public ArrayList<AccountInfo> getWaitingLoanList() {
	   return db.getWaitingLoanList();
   }
   //show block loans list
   public ArrayList<AccountInfo> getBlockLoanList() {
	   return db.getBlockLoanList();
   }
   
 //Delete both tables.

   public void deleteFromAllTables(int id) throws SQLException {
	   db.deleteFromAllTables(id);
   }
   
 //Delete Account

   public void deleteFromAccountTable(long accountNumber) throws SQLException {
	   db.deleteFromAccountTable(accountNumber);
   }
   
 //Update customer status to active
   public void updateCustomerStatusToActive(int id) throws SQLException{
	   db.updateCustomerStatusToActive(id);
   }
   
 //Update account status to active
   public void updateAccountStatusToActive(int id,long accountNumber) throws SQLException{
	   db.updateAccountStatusToActive(id, accountNumber);
   }
 //get balance for W/D
   public long withDraw(long accountNumber) {
	   return db.withDraw(accountNumber);
   }
 //update balance

   public void updateBalance(long balance,long acc_number) throws SQLException {
	   db.updateBalance(balance, acc_number);
	   //history(acc_number, "WithDraw", balance+1);
   }
   
// W/D  History



//public void history(long accountNumber,String process,long balance) throws SQLException {
//	db.history(accountNumber, process, balance);
//}

//public void bankCharges(long accountNumber, String process, long balance) {
//
//	db.bankCharges(accountNumber,process,balance);
//}


//

////show transaction history
//public ArrayList<TransactionHistoryPojo> showTransactionHistory() {
//	return db.showTransactionHistory();
//}
//show single account all history
public ArrayList<History>  allHistory(long accountNumber) {
	ArrayList<History> list = db.allHistory(accountNumber);
//	//ArrayList<AllHistory> list = map.get(accountNumber);
//	for(int i=0;i<list.size();i++) {
//		History allHistory = list.get(i);
//		String dateString = allHistory.getDate();
//		//System.out.println(dateString);
//		String tempString = "";
//		for(int j=0;j<dateString.length();j++) {
//			if(dateString.charAt(j)>='0' && dateString.charAt(j)<='9') {
//				tempString = tempString+dateString.charAt(j);
//			}
//		}
//		allHistory.setDate(tempString);
//				
//	}
//	Collections.sort(list, new Comparator<History>() {
//        @Override
//        public int compare(History object1, History object2) {
//            return object2.getDate().compareTo(object1.getDate());
//        }
//});
//	

	return list;
	
}

  //use  
    public long addNewCustomers(CustomerInfo customerInfo, AccountInfo accountInfo){

        int key=0;
        long accNumber=0;
        try {
        	System.out.println("HI");
            key = db.insertToCustomerTable(customerInfo);
           
            accountInfo.setId(key);
            long bal = accountInfo.getBalance();
            //accountInfo.setBalance(bal);
            System.out.println("HI "+key+" "+bal);
       accNumber = db.insertToAccountTable(accountInfo);
       System.out.println("HI2");
       //long balance = accountInfo.getBalance();
//       List<Long> list = new ArrayList<>();
//       list.add(accNumber);
//       customerInfo.setList(list);
       //CacheStore.INSTANCE.customerMapStore(key,customerInfo);
       //System.out.println(accNumber+" accountNumber"+accountInfo.getBalance()+" balance");
        	history(accNumber,"Deposit",bal,0,bal);
        	
      
        }
            catch (SQLException e) {
            e.printStackTrace();
        }
        return key;
    }
    //history
    public void history(long accountNumber,String process, long balance, long bankCharges, long updatedBalance) {
    	db.history(accountNumber,process,balance,bankCharges,updatedBalance);
		
	}
    //bill
    public void bill(long accountNumber,String process, long mobile, long balance) {
    	db.bill(accountNumber,process,mobile,balance);
		
	}
    //bank account
    public void bankAccount(long charges) {
    	db.bankAccount(charges);
	}
    
    //client cache
    public HashMap<Long, AccountInfo> clientCache(long accountNumber) {
    	
    	HashMap<Long, AccountInfo> map = db.clientCache(accountNumber);
    	return map;
    	
	}
    
    //getAccountNumbersList
    public HashMap<Integer,CustomerInfo> getAccountNumbersList(int id) {
    	HashMap<Integer,CustomerInfo> map = db.getAccountNumbersList(id);
    	//System.out.println(map+" Map2");
//    	CacheStore.INSTANCE.customerMap = map;
//    	HashMap<Integer,CustomerInfo> map2 = CacheStore.INSTANCE.returnCustomerMapStore();
    	//System.out.println(map2+" Map3");
    	return map;
    	
	}
    
    //loan Status Update
    public void loanStatusUpdate(long accountNumber, String loanStatus) {
		db.loanStatusUpdate(accountNumber,loanStatus);
	}
    
    //loan Status Update with amount
    public void loanStatusUpdateWithAmount(long accountNumber, String loanStatus, long amount, int notifyStatus) {
    	
		db.loanStatusUpdateWithAmount(accountNumber,loanStatus,amount,notifyStatus);
	}
    
    // notification
    public int notification() {
	int count = db.notification();
	return count;
	}
    
    //labelData
    
    public int[] labelData() {
    int[] size = 	db.labelData();
    return size;
    }
    
    // notificationLoan
    public int notificationLoan() {
	int count = db.notificationLoan();
	return count;
	}
    
    // notificationUpdate
    public void notificationUpdate() {
//	int count = 
			db.notificationUpdate();
//	return count;
	}
    
    // notificationLoanUpdate
    public void notificationLoanUpdate() {
    	//System.out.print("update notify1");
//	int count = 
			db.notificationLoanUpdate();
//	return count;
	}
    
    //bank amount
    public long bankAmount(String loan) {
	long balanceBank =  db.bankAmount(loan);
	return balanceBank;
	}
    
    //
    public void updateBankAmount(long balanceBank,String one) {
		db.updateBankAmount(balanceBank,one);
	}
    
//    //loanSubmit
//    public void loanSubmit() {
//		
//	}
    
    //insert to account table
    
    
    
	public void accountInsert(AccountInfo accountInfo) {
		// TODO Auto-generated method stub
		long key=0;
		 try {
			 long bal = accountInfo.getBalance();
	            //accountInfo.setBalance(bal);
			 int id = accountInfo.getId();
	           key =  db.insertToAccountTable(accountInfo);
	           System.out.println(key+" auto gen key");
	           history(key, "Deposit", bal,0,bal);
//	           HashMap<Integer, List<Long>> customerMap =  CacheStore.INSTANCE.returnCustomerMapStore();
//	           List<Long> list= customerMap.get(id);
//	           //List<Long> list =   customerInfo.getList();
//	           list.add(key);
	           
	           
	        }
	        catch (SQLException e){
	            e.printStackTrace();
	        }
	}

    public void deleteAccountData(int delId,long accountNumber) throws HandledException {
        int id = delId;
        long account_number = accountNumber;
        try{
            db.deleteFromAccountTable(account_number);
   }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void deleteCustomerData(int delId){
        int id = delId;
        try {
            db.deleteFromCustomerTable(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        HashMapHandler.INSTANCE.dbHashMap.remove(id);
    }


    public void balancDepositeData(int balId,long accNumber,long deposit) throws SQLException {
        HashMap<Long,AccountInfo> map = HashMapHandler.INSTANCE.output(balId);
if(map.containsKey(accNumber)) {
    long balance = map.get(accNumber).getBalance();
    balance = balance + deposit;
    db.updateBalance(balance, accNumber);
    map.get(accNumber).setBalance(balance);
    System.out.println("Your new Balance is " + balance);
}else{
    System.out.println("Please enter the correct id and acc number");
}
        }
        public void balanceWithdrawalData(int balId,long accNumber,long withdraw) throws SQLException {
        HashMap<Long,AccountInfo> map = HashMapHandler.INSTANCE.output(balId);
            if(map.containsKey(accNumber)) {
                long balance = map.get(accNumber).getBalance();
                if (balance >= withdraw) {
                    balance = balance - withdraw;
                    db.updateBalance(balance, accNumber);
                    map.get(accNumber).setBalance(balance);
                    System.out.println("Your remaining Balance is " + balance);

                } else {
                    System.out.println("Balance Insufficient");
                }
            }else{
                System.out.println("Please enter the correct id and acc number");

            }
    }
   public void updateCustomerStatusActive(int id, long accountNumber) throws SQLException {
        db.updateCustomerStatusToActive(id);
   }
   
   public boolean checkExistId(int customerId){
       if(HashMapHandler.INSTANCE.dbHashMapAll.containsKey(customerId)) {
           return true;
       }
       else{
           return false;
       }
    }
    public boolean chechIdActive(int customerId){
        if (HashMapHandler.INSTANCE.dbHashMap.containsKey(customerId)){
            return  true;
        }else{
            return false;
        }
    }
    public boolean checkAccountNumberExist(int customerId,long account_Number){
        HashMap<Long, AccountInfo> accountInfoHashMap = HashMapHandler.INSTANCE.allAcccountData(customerId);
        if(accountInfoHashMap.containsKey(account_Number)) {
            return true;
        }else{
            return false;
        }

        }




}
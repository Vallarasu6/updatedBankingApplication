package bank.database;
import bank.exception.HandledException;
import bank.interfaces.InterfaceCommon;
import bank.logic.LogicLayer;
import bank.pojo.CustomerInfo;
import historyPojo.History;
import pojo_account.AccountInfo;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLEngineResult.Status;
import javax.security.auth.message.callback.PrivateKeyCallback.AliasRequest;
import javax.sound.midi.Soundbank;
import javax.swing.text.html.HTMLDocument.Iterator;

import TransactionHistory.TransactionHistoryPojo;
import allHistoryPojo.AllHistory;

public class DbStore implements InterfaceCommon{
    static Connection con = null;
    public DbStore(){

        try {
            if(con==null) {
            	Class.forName("com.mysql.jdbc.Driver");
            
                con = DriverManager.getConnection("jdbc:mysql://localhost/zoho", "root", "vallaK@6");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //bank account
    
    public void bankAccount(long charges) {
    	PreparedStatement st = null;
    	ResultSet rst = null;
    	long balance=0;
    	try {
	    	String sql1 = "SELECT * from bankAccount where account=?";
	    	st = con.prepareStatement(sql1);
	    	st.setString(1, "loanAccount");
	    	rst = st.executeQuery();
	    	
	 	   while(rst.next()) {
	 		balance = rst.getLong("balance"); 
	 		   }
	 	   
	 	   balance=balance+charges;
	 	  String sql = "UPDATE bankAccount SET balance = ? WHERE account = ?";
          st = con.prepareStatement(sql);
          st.setLong(1, balance);
          st.setString(2,"loanAccount");
          
          st.executeUpdate();
	 	   
	 }
	 catch (Exception e) {
		 System.out.print(e+" Error msg");
	}
		
	}
    
    //update change ADDRESS
    
    public void changeAddress(String address, int id) {
PreparedStatement st = null;
        
        try {

            String sql = "UPDATE customerInfo SET address = ? WHERE customerId = ?";
            st = con.prepareStatement(sql);

            st.setString(1,address);
            st.setInt(2, id);
            st.executeUpdate();
            System.out.println("Status changes in account");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    //update loan status
    
    public void loanStatusUpdate(long accountNumber, String loanStatus) {
PreparedStatement st = null;
        
        try {

            String sql = "UPDATE accountInfo SET loanStatus = ? WHERE customerAccountNumber = ?";
            st = con.prepareStatement(sql);

            st.setString(1,loanStatus);
            st.setLong(2, accountNumber);
            st.executeUpdate();
            
            System.out.println("Status changes in loan");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
  //update loan status with amount
    
    public void loanStatusUpdateWithAmount(long accountNumber, String loanStatus,long loanAmount, int notifyStatus) {
PreparedStatement st = null;
        try {

            String sql = "UPDATE accountInfo SET loanStatus = ?,loanAmount = ?,loanNotify=? WHERE customerAccountNumber = ?";
            st = con.prepareStatement(sql);
            st.setString(1,loanStatus);
            st.setLong(2, loanAmount);
            st.setInt(3, notifyStatus);
            st.setLong(4, accountNumber);
            st.executeUpdate();
            System.out.println("Status changes in loan");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
//update change mobile number
    
    
    public void changeMobile(long mobile, int id) {
    	PreparedStatement st = null;
        
        try {

            String sql = "UPDATE customerInfo SET customerMobile = ? WHERE customerId = ?";
            st = con.prepareStatement(sql);

            st.setLong(1,mobile);
            st.setInt(2, id);
            st.executeUpdate();
            System.out.println("Status changes in account");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
    public HashMap<Integer, CustomerInfo> getAccountNumbersList(int id) {
    	HashMap<Integer, CustomerInfo> map = new HashMap<Integer, CustomerInfo>();
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	List<Long> list = new ArrayList<Long>();
        try {
        	
        	String sql = "select customerInfo.customerId,customerInfo.customerName,customerInfo.customerMobile,customerInfo.address,"
        			+ "accountInfo.customerAccountNumber from customerInfo inner join accountInfo "
        			+ "on customerInfo.customerId = accountInfo.customerId where customerInfo.customerId=? and customerInfo.customerStatus=1"
        			+ " and accountInfo.status=1;";
        	st = con.prepareStatement(sql);
        	st.setLong(1, id);
        	rs = st.executeQuery();
        	while(rs.next()) {
        		CustomerInfo customerInfo = new CustomerInfo();
        		
        		Integer customer_id = rs.getInt("customerId");
        		String customerName = rs.getString("customerName");
        		Long mobile = rs.getLong("customerMobile");
        		String address = rs.getString("address");
        		Long customerAccountNumber = rs.getLong("customerAccountNumber");
        		customerInfo.setId(customer_id);
        		customerInfo.setName(customerName);
        		customerInfo.setMobileNumber(mobile);
        		customerInfo.setAddress(address);
        		list.add(customerAccountNumber);
        		customerInfo.setList(list);
        		map.put(id, customerInfo);
        		//System.out.println(map+" Map");
        	}
        	
        }catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
        return map;
	}
    
    
    //client cache
    public HashMap<Long, AccountInfo> clientCache (long accountNumber) {
    	HashMap<Long, AccountInfo> map = new HashMap<>();
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	
        try {
        	String sql1 = "SELECT * from accountInfo where customerAccountNumber= ?";
        	st = con.prepareStatement(sql1);
        	st.setLong(1, accountNumber);
    	    rs = st.executeQuery();
    	    while(rs.next()) {
    	    	AccountInfo object = new AccountInfo();
    	           Integer customer_id = rs.getInt("customerId");
    	           Long AccountNumber = rs.getLong("customerAccountNumber");
    	           String bankName = rs.getString("customerBankName");
    	           Long balance = rs.getLong("customerBalance");
    	           Integer status = rs.getInt("status");
    	           String loanStatus = rs.getString("loanStatus");
    	           Long loanAmount = rs.getLong("loanAmount");
    	           object.setId(customer_id);
    	           object.setAccountNumber(accountNumber);
    	           object.setBankName(bankName);
    	           object.setBalance(balance);
    	           object.setLoanStatus(loanStatus);
    	           object.setLoanAmount(loanAmount);
    	           map.put(AccountNumber, object);

             }
    	    
        } catch (Exception e) {
    		// TODO: handle exception
    		 e.printStackTrace();
    	}
        return map;
	}
    
    
    
    //check account number exist
    public boolean checkAccountNumber(long acc_num) {
    	PreparedStatement st = null;
    	ResultSet rst = null;
    	int i=0;
        try {
        	String sql1 = "SELECT * from accountInfo where customerAccountNumber= ?";
        	st = con.prepareStatement(sql1);
        	st.setLong(1, acc_num);
    	    rst = st.executeQuery();
    	    if(rst.next()) {
              i=1;
             }
    	    else {
				i=0;
			}
    	    
        } catch (Exception e) {
    		// TODO: handle exception
    		 e.printStackTrace();
    	}
        if(i==1) {
        	return false;
        }else {
			return true;
		}
	}
    
    
    
    
    
    //check customerid exist
    public boolean checkCustomerId(int id) {
    	PreparedStatement st = null;
    	ResultSet rst = null;
    	int i=0;
        try {
        	String sql1 = "SELECT * from customerInfo where customerId= ?";
        	st = con.prepareStatement(sql1);
        	st.setLong(1, id);
    	    rst = st.executeQuery();
    	    if(rst.next()) {
              i=1;
             }
    	    else {
				i=0;
			}
    	    
    	} catch (Exception e) {
    		// TODO: handle exception
    		 e.printStackTrace();
    	}
        if(i==1) {
        	return false;
        }else {
			return true;
		}
	}
    
    //check balance
    public long checkBalance(long accountNumber) {
    	PreparedStatement st = null;
    	ResultSet rst = null;
    	long balance=0;
    	try {
	    	String sql1 = "SELECT * from accountInfo where customerAccountNumber= ? and status=1";
	    	st = con.prepareStatement(sql1);
	    	st.setLong(1, accountNumber);
	    	rst = st.executeQuery();
	 	   while(rst.next()) {
	 		balance = rst.getLong(4); 
	 		   }
	 }
	 catch (Exception e) {
		 System.out.print(e+" Error msg");
	}
    	return balance;
	}
    
    
    
    
    
    
    //check login
    public String checkLogin(int id, String password) {
    	PreparedStatement st = null;
    	ResultSet rst = null;
    	int status=0;
    	//String string="Yes";
    	//String password = null;
    	//ArrayList<Long> accountNumberArrayList = new ArrayList<Long>();
    	 try {
    		 System.out.print("status "+status);
    	    	String sql1 = "SELECT * from customerInfo where customerId= ? and password=MD5(?) and customerStatus=1";
    	    	st = con.prepareStatement(sql1);
    	    	st.setInt(1, id);
    	    	st.setString(2, password);
    	    	rst = st.executeQuery();
    	 	   if(rst.next()) {
    	 		
    	 		return "yes";
    	 		   }
    	 	   
    	 	  String sql2 = "SELECT * from customerInfo where customerId= ? and password=MD5(?) and customerStatus=2";
  	    	st = con.prepareStatement(sql2);
  	    	st.setInt(1, id);
  	    	st.setString(2, password);
  	    	rst = st.executeQuery();
  	 	   if(rst.next()) {
  	 		return "admin";
  	 		   }
  	 	   
    	 }
    	 catch (Exception e) {
    		 System.out.print(e+" Error msg");
		}
    	 
    	 return "no";
	}
    
    //show customerList - Active
public ArrayList<CustomerInfo> getCustomerList() {
	
	ArrayList<CustomerInfo> customerList = new ArrayList<CustomerInfo>();
	Statement st = null;
    ResultSet rs = null;
    try {
        st = con.createStatement();
        rs = st.executeQuery("SELECT * from customerInfo  where customerStatus=1 limit 10");

        while (rs.next()) {
            CustomerInfo object = new CustomerInfo();
            Integer customer_id = rs.getInt("customerId");
            String name = rs.getString("customerName");
            Long mobile = rs.getLong("customerMobile");

          object.setId(customer_id);
          object.setName(name);
          object.setMobileNumber(mobile);
          customerList.add(object);

        }
    }
        catch (Exception e) {
        	System.out.println(e);
        }
					
   // System.out.print(customerList);
	return customerList;
}
//Total history
public ArrayList<History> showHistory() {
	ArrayList<History> list = new ArrayList<History>();
	History history;
	PreparedStatement st = null;
	ResultSet rst = null;
	
    try {
    	String sql1 = "SELECT * FROM history";
    	st = con.prepareStatement(sql1);
    	
    	rst = st.executeQuery();
	   while(rst.next()) {
		   int id = rst.getInt(1);
		   long accountNumber= rst.getLong(2);
		   String process = rst.getString(3);
		   long balance= rst.getLong(4);
		   String date = rst.getString(6);
		  // process = id+" Acc Number "+accountNumber+" "+ process;
		   history = new History();
		   history.setId(id);
		   history.setAccountNumber(accountNumber);
		   history.setProcess(process);
		   history.setAmount(balance);
		   history.setDate(date);
		   list.add(history);
			   }
		   
			   
		   
		   
	   
	   
	   }catch (Exception e) {
		// TODO: handle exception
		   System.out.println(e);
	}
	return list;
    
}



//particular person history
public ArrayList<History> allHistory(long accountNumber) {
	ArrayList<History> list = new ArrayList<History>();
	History history;
	PreparedStatement st = null;
	ResultSet rst = null;
	
    try {
    	String sql1 = "SELECT * FROM history where customerAccountNumber=? order by date DESC";
    	st = con.prepareStatement(sql1);
    	st.setLong(1, accountNumber);
    	rst = st.executeQuery();
	   while(rst.next()) {
		  
		   String process = rst.getString(3);
		   long balance= rst.getLong(4);
		   long bankCharges= rst.getLong(5);
		   String date = rst.getString(6);
		   long remainingBalance = rst.getLong(7);
		   history = new History();
		   history.setProcess(process);
		   history.setAmount(balance);
		   history.setBankCharges(bankCharges);
		   history.setDate(date);
		   history.setUpdatedBalance(remainingBalance);
		   list.add(history);
			   }
		   
			   
		   
		   
	   
	   
	   }catch (Exception e) {
		// TODO: handle exception
		   System.out.println(e);
	}
	return list;
    
}

//show customerList - InActive
public ArrayList<CustomerInfo> getInActiveCustomerList() {
	ArrayList<CustomerInfo> customerList1 = new ArrayList<CustomerInfo>();
	Statement st = null;
    ResultSet rs = null;
    try {
        st = con.createStatement();
        rs = st.executeQuery("SELECT * from customerInfo where customerStatus=0");

        while (rs.next()) {
            CustomerInfo object = new CustomerInfo();
            Integer customer_id = rs.getInt("customerId");
            String name = rs.getString("customerName");
            Long mobile = rs.getLong("customerMobile");

          object.setId(customer_id);
          object.setName(name);
          object.setMobileNumber(mobile);
          customerList1.add(object);

        }
    }
        catch (Exception e) {
        	System.out.println(e);
        }
					
    //System.out.print(customerList1);
	return customerList1;
	
}
//show accountList - Inactive
public ArrayList<AccountInfo> getInActiveAccountList() {
	ArrayList<AccountInfo> accountList = new ArrayList<AccountInfo>();
	Statement st = null;
    ResultSet rs = null;
    try {
        st = con.createStatement();
        rs = st.executeQuery("SELECT * from accountInfo where status=0");

        while (rs.next()) {
           AccountInfo object = new AccountInfo();
           Integer customer_id = rs.getInt("customerId");
           Long accountNumber = rs.getLong("customerAccountNumber");
           String bankName = rs.getString("customerBankName");
           Long balance = rs.getLong("customerBalance");
           object.setId(customer_id);
           object.setAccountNumber(accountNumber);
           object.setBankName(bankName);
           object.setBalance(balance);
           accountList.add(object);

        }
    }
        catch (Exception e) {
        	System.out.println(e);
        }
					

	return accountList;
}

//show Approvedloan list
public ArrayList<AccountInfo> getApprovedLoanList() {
	ArrayList<AccountInfo> accountList = new ArrayList<AccountInfo>();
	
	PreparedStatement st = null;
	ResultSet rs = null;
	
  try {
  	String sql1 = "SELECT * FROM accountInfo where loanStatus=? and status=?";
  	st = con.prepareStatement(sql1);
  	st.setString(1,"Approved");
  	st.setInt(2, 1);
  	rs = st.executeQuery();
    while (rs.next()) {
       AccountInfo object = new AccountInfo();
       Integer customer_id = rs.getInt("customerId");
       Long accountNumber = rs.getLong("customerAccountNumber");
       String bankName = rs.getString("customerBankName");
       Long balance = rs.getLong("customerBalance");
       Long loanAmount = rs.getLong("loanAmount");
       object.setId(customer_id);
       object.setAccountNumber(accountNumber);
       object.setBankName(bankName);
       object.setBalance(balance);
       object.setLoanAmount(loanAmount);
       accountList.add(object);

    }
}
    catch (Exception e) {
    	System.out.println(e);
    }
					

	return accountList;
}


//show Applied loan list
public ArrayList<AccountInfo> getAppliedLoanList() {
	//System.out.print("loan AMount Hi");
	ArrayList<AccountInfo> accountList = new ArrayList<AccountInfo>();
	
	PreparedStatement st = null;
	ResultSet rs = null;
	
    try {
    	String sql1 = "SELECT * FROM accountInfo where loanStatus=? and status=?";
    	st = con.prepareStatement(sql1);
    	st.setString(1,"Processing");
    	//st.setString(2,"Processing");
    	st.setInt(2, 1);
    	rs = st.executeQuery();
      while (rs.next()) {
         AccountInfo object = new AccountInfo();
         Integer customer_id = rs.getInt("customerId");
         Long accountNumber = rs.getLong("customerAccountNumber");
         String bankName = rs.getString("customerBankName");
         Long balance = rs.getLong("customerBalance");
         Long loanAmount = rs.getLong("loanAmount");
         //System.out.print("loan Amount "+loanAmount);
         object.setId(customer_id);
         object.setAccountNumber(accountNumber);
         object.setBankName(bankName);
         object.setBalance(balance);
         object.setLoanAmount(loanAmount);
         accountList.add(object);
         System.out.print(object);
      }
  }
      catch (Exception e) {
      	System.out.println(e);
      }
					

	return accountList;
}



//show waiting loan list
public ArrayList<AccountInfo> getWaitingLoanList() {
	ArrayList<AccountInfo> accountList = new ArrayList<AccountInfo>();
	
	PreparedStatement st = null;
	ResultSet rs = null;
	
  try {
  	String sql1 = "SELECT * FROM accountInfo where loanStatus=? and status=?";
  	st = con.prepareStatement(sql1);
  	st.setString(1,"Waiting List");
  	//st.setString(2,"Processing");
  	st.setInt(2, 1);
  	rs = st.executeQuery();
    while (rs.next()) {
       AccountInfo object = new AccountInfo();
       Integer customer_id = rs.getInt("customerId");
       Long accountNumber = rs.getLong("customerAccountNumber");
       String bankName = rs.getString("customerBankName");
       Long balance = rs.getLong("customerBalance");
       Long loanAmount = rs.getLong("loanAmount");
       object.setId(customer_id);
       object.setAccountNumber(accountNumber);
       object.setBankName(bankName);
       object.setBalance(balance);
       object.setLoanAmount(loanAmount);
       accountList.add(object);

    }
}
    catch (Exception e) {
    	System.out.println(e);
    }
					

	return accountList;
}



//show Block loan list
public ArrayList<AccountInfo> getBlockLoanList() {
	ArrayList<AccountInfo> accountList = new ArrayList<AccountInfo>();
	
	PreparedStatement st = null;
	ResultSet rs = null;
	
try {
	String sql1 = "SELECT * FROM accountInfo where loanStatus=? and status=?";
	st = con.prepareStatement(sql1);
	st.setString(1,"Blocked");
	st.setInt(2, 1);
	rs = st.executeQuery();
  while (rs.next()) {
     AccountInfo object = new AccountInfo();
     Integer customer_id = rs.getInt("customerId");
     Long accountNumber = rs.getLong("customerAccountNumber");
     String bankName = rs.getString("customerBankName");
     Long balance = rs.getLong("customerBalance");
     Long loanAmount = rs.getLong("loanAmount");
     object.setId(customer_id);
     object.setAccountNumber(accountNumber);
     object.setBankName(bankName);
     object.setBalance(balance);
     object.setLoanAmount(loanAmount);
     accountList.add(object);

  }
}
  catch (Exception e) {
  	System.out.println(e);
  }
					

	return accountList;
}




//update bank account amount - loan

public void updateBankAmount(long balanceBank,String one) {
	PreparedStatement st = null;
    try {

        String sql = "UPDATE bankAccount SET balance = ? WHERE account = ?";
        st = con.prepareStatement(sql);
        st.setLong(1,balanceBank);
        st.setString(2, one);
        st.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

//bank amount
public long bankAmount(String loan) {
	PreparedStatement st = null;
	ResultSet rs = null;
	long balance=0;
    try {
    	String sql1 = "SELECT * FROM bankAccount where account=?";
    	st = con.prepareStatement(sql1);
    	st.setString(1,loan);
    	rs = st.executeQuery();
      while (rs.next()) {
         
         balance = rs.getLong("balance");
         
         System.out.println(balance+" balance Bank");
      }
  }
      catch (Exception e) {
      	System.out.println(e);
      }
    return balance;
}


//show Accounts
public ArrayList<AccountInfo> getAccountList() {
	ArrayList<AccountInfo> accountList = new ArrayList<AccountInfo>();
	Statement st = null;
    ResultSet rs = null;
    try {
        st = con.createStatement();
        rs = st.executeQuery("SELECT * from accountInfo where status=1");

        while (rs.next()) {
           AccountInfo object = new AccountInfo();
           Integer customer_id = rs.getInt("customerId");
           Long accountNumber = rs.getLong("customerAccountNumber");
           String bankName = rs.getString("customerBankName");
           Long balance = rs.getLong("customerBalance");
           object.setId(customer_id);
           object.setAccountNumber(accountNumber);
           object.setBankName(bankName);
           object.setBalance(balance);
           accountList.add(object);

        }
    }
        catch (Exception e) {
        	System.out.println(e);
        }
					

	return accountList;
}
//Delete both tables.

public void deleteFromAllTables(int id) throws SQLException {
    PreparedStatement st = null;
    PreparedStatement st1 = null;
        int cust_id = id;
    try {
        String sql1 = "UPDATE customerInfo SET customerStatus = 0 WHERE customerId = ?";
        st1 = con.prepareStatement(sql1);
        st1.setInt(1,cust_id);
        st1.executeUpdate();
        String sql = "UPDATE accountInfo SET status = 0 WHERE customerId = ?";
        st = con.prepareStatement(sql);
        st.setInt(1,cust_id);
        st.executeUpdate();


    } catch (SQLException e) {
        e.printStackTrace();
    }
    st.close();
    st1.close();
}

//Delete Account

public void deleteFromAccountTable(long accountNumber) throws SQLException {
    PreparedStatement st = null;
    long acc_Number = accountNumber;
    try {

        String sql = "UPDATE accountInfo SET status = 0 WHERE customerAccountNumber = ?";
        st = con.prepareStatement(sql);

        st.setLong(1,acc_Number);
        st.executeUpdate();
        System.out.println("Status changes in account");
    } catch (SQLException e) {
        e.printStackTrace();
    }
    st.close();

}

//Update customer status to active
public void updateCustomerStatusToActive(int id) throws SQLException{
   
	PreparedStatement st = null;
    int cus_id = id;
    try {

        String sql = "UPDATE customerInfo SET customerStatus = 1 WHERE customerId = ?";
        st = con.prepareStatement(sql);

        st.setLong(1,cus_id);
        st.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    st.close();
}
//Update account status to active
public void updateAccountStatusToActive(int id,long accountNumber) throws SQLException{
PreparedStatement st = null;
PreparedStatement st1 = null;
int cus_id = id;
    long acc_num = accountNumber;
try {
    String sql1 = "UPDATE customerInfo SET customerStatus = 1 WHERE customerId = ?";
    st1 = con.prepareStatement(sql1);
    st1.setInt(1,cus_id);
    st1.executeUpdate();
    String sql = "UPDATE accountInfo SET status = 1 WHERE customerAccountNumber = ?";
    st = con.prepareStatement(sql);
    st.setLong(1,acc_num);
    st.executeUpdate();


} catch (SQLException e) {
    e.printStackTrace();
}
st.close();
st1.close();
}
//get balance for W/D
public long withDraw(long accountNumber) {
	long acc_num = accountNumber;
	long balance=0;
	PreparedStatement st = null;
	ResultSet rst = null;
    try {
    	String sql1 = "SELECT * from accountInfo where customerAccountNumber= ?";
    	st = con.prepareStatement(sql1);
    	st.setLong(1, acc_num);
	    rst = st.executeQuery();
	    while(rst.next()) {
           balance = rst.getLong(4);
         }
	    
	} catch (Exception e) {
		// TODO: handle exception
		 e.printStackTrace();
	}
    return balance;
}



//update balance

public void updateBalance(long balance,long acc_number) throws SQLException {
    PreparedStatement st = null;
    try {
   long customerBalance = balance;
  
    

        String sql = "UPDATE accountInfo SET customerBalance = ? WHERE  customerAccountNumber = ?";
        st = con.prepareStatement(sql);
        st.setLong(1, customerBalance);
        st.setLong(2,acc_number);
       st.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    st.close();
    //System.out.print("Balance changed");
}




//history

public void history(long accountNumber,String process,long balance, long bankCharge,long updatedBalance) {
PreparedStatement st = null;
    
    try {
        String sql = "INSERT INTO history (customerAccountNumber,process,balance,bankCharge,date,remainingBalance) VALUES (?, ?, ?, ?,now(),?)";
        st = con.prepareStatement(sql);
            st.setLong(1, accountNumber);
            st.setString(2, process); 
            st.setLong(3, balance); 
            st.setLong(4, bankCharge); 
            st.setLong(5, updatedBalance); 
               
            st.executeUpdate();
           }
    catch (Exception e){
        System.out.println("toString(): " + e.toString());
        System.out.println("getMessage(): " + e.getMessage());
   }
   try {
	st.close();
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
}

//bills

public void bill(long accountNumber,String process,long number, long balance) {
PreparedStatement st = null;
    
    try {
        String sql = "INSERT INTO bills (customerAccountNumber,process,number,amount,date) VALUES (?, ?, ?, ?,now())";
        st = con.prepareStatement(sql);
            st.setLong(1, accountNumber);
            st.setString(2, process); 
            st.setLong(3, number); 
            st.setLong(4, balance); 
            
               
            st.executeUpdate();
           }
    catch (Exception e){
        System.out.println("toString(): " + e.toString());
        System.out.println("getMessage(): " + e.getMessage());
   }
   try {
	st.close();
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
}


 
    //use
    
    public int insertToCustomerTable(CustomerInfo customerInfo) throws SQLException {
    	 CustomerInfo passData = customerInfo;
        //con.setAutoCommit(false);
        PreparedStatement st = null;
        int cusIdArray=0;
        int[] batchResults=new int[1];
        try {
            String sql = "INSERT INTO customerInfo (customerName,customerMobile,customerStatus,address,password,notification) VALUES (?, ?, ?, ?, MD5(?),0)";
            st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                String name = passData.getName();
                long mobile = passData.getMobileNumber();
                String address = passData.getAddress();
                String password= passData.getPassword();
                st.setString(1, name);
                st.setLong(2, mobile);
                st.setInt(3,1);
                st.setString(4,address);
                st.setString(5,password);
                st.addBatch();

            batchResults = st.executeBatch();
            //con.commit();
            ResultSet generatedKeysResultSet = st.getGeneratedKeys();

            while (generatedKeysResultSet.next()) {
                cusIdArray = generatedKeysResultSet.getInt(1);
                
            }

        } catch (BatchUpdateException e) {
            System.out.println("Error message: " + e.getMessage());
            batchResults = e.getUpdateCounts();
            System.out.println("Rolling back batch insertion");
           // con.rollback();


        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Return value from inserting batch 1: " + Arrays.toString(batchResults));
        st.close();
        return cusIdArray;
    }
    
    //notification
    
    public int notification() {
		
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	int count=0;
    	//long balance=0;
        try {
        	String sql1 = "SELECT * FROM customerInfo where notification=?";
        	st = con.prepareStatement(sql1);
        	st.setInt(1,0);
        	rs = st.executeQuery();
          while (rs.next()) {
             
             count++;
          }
          
      }
          catch (Exception e) {
          	System.out.println(e);
          }
        return count;
    	
	}
    
    public void customerCountList(int count) {
    	
    	
    	
    }
    
    //label data

    
    public int[] labelData() {
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	int count=0,i=0;
    	int count1=0;
    	int size[]=new int[9];
        try {
        	String sql1 = "SELECT * FROM customerInfo where 1=?";
        	st = con.prepareStatement(sql1);
        	st.setInt(1,1);
        	rs = st.executeQuery();
          while (rs.next()) {
             
             count++;
          }
          size[i]=count;
          i++;
          count=0;
          String sql2 = "SELECT * FROM accountInfo where 1=?";
      	st = con.prepareStatement(sql2);
      	st.setInt(1,1);
      	rs = st.executeQuery();
        while (rs.next()) {
           
           count++;
        }
        size[i]=count;
        i++;
        count=0;
        String sql3 = "SELECT * FROM customerInfo where customerStatus=?";
      	st = con.prepareStatement(sql3);
      	st.setInt(1,0);
      	rs = st.executeQuery();
        while (rs.next()) {
           
           count++;
        }
        size[i]=count;
        i++;
        count=0;
        String sql4 = "SELECT * FROM accountInfo where status=?";
      	st = con.prepareStatement(sql4);
      	st.setInt(1,0);
      	rs = st.executeQuery();
        while (rs.next()) {
           
           count++;
        }
        size[i]=count;
        i++;
        count=0;
        String sql5 = "SELECT * FROM history where 1=?";
      	st = con.prepareStatement(sql5);
      	st.setInt(1,1);
      	rs = st.executeQuery();
        while (rs.next()) {
           
           count++;
        }
        size[i]=count;
        i++;
        count=0;
        String sql6 = "SELECT * FROM accountInfo where loanStatus=?";
      	st = con.prepareStatement(sql6);
      	st.setString(1,"Processing");
      	rs = st.executeQuery();
        while (rs.next()) {
           
           count++;
        }
        size[i]=count;
        i++;
        count=0;
        String sql7 = "SELECT * FROM accountInfo where loanStatus=?";
      	st = con.prepareStatement(sql7);
      	st.setString(1,"Approved");
      	rs = st.executeQuery();
        while (rs.next()) {
           
           count++;
        }
        size[i]=count;
        i++;
        count=0;
        String sql8 = "SELECT * FROM accountInfo where loanStatus=?";
      	st = con.prepareStatement(sql8);
      	st.setString(1,"Waiting List");
      	rs = st.executeQuery();
        while (rs.next()) {
           
           count++;
        }
        size[i]=count;
        i++;
        count=0;
        String sql9 = "SELECT * FROM accountInfo where loanStatus=?";
      	st = con.prepareStatement(sql9);
      	st.setString(1,"Blocked");
      	rs = st.executeQuery();
        while (rs.next()) {
           
           count++;
        }
        size[i]=count;
        
      }
          catch (Exception e) {
          	System.out.println(e);
          }
        System.out.println(size[0]+" "+size[1]+" "+size[8]+" hiiiii ");
        return size;
	}
    
    
  //notificationUpdate
    
    public void notificationUpdate() {
		
    	PreparedStatement st = null;
    	//ResultSet rs = null;
    	int count=0;
    	//long balance=0;
        try {
        	String sql1 = "UPDATE customerInfo set notification=? where notification=?";
        	st = con.prepareStatement(sql1);
        	st.setInt(1,1);
        	st.setInt(2,0);
        	st.executeUpdate();
//          while (rs.next()) {
//             
//             count++;
//          }
        	//System.out.println("Notified");
      }
          catch (Exception e) {
          	System.out.println(e);
          }
//        return count;
    	
	}
    
    //notification loan
   public int notificationLoan() {
		
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	int count=0;
    	//long balance=0;
        try {
        	String sql1 = "SELECT * FROM accountInfo where loanNotify=? AND loanStatus=?";
        	st = con.prepareStatement(sql1);
        	st.setInt(1,0);
        	st.setString(2,"Processing");
        	rs = st.executeQuery();
          while (rs.next()) {
             
             count++;
          }
          
      }
          catch (Exception e) {
          	System.out.println(e);
          }
        return count;
    	
	}
   
   //notificationUpdate
   
   public void notificationLoanUpdate() {
		System.out.print("update notify2");
   	PreparedStatement st = null;
   	//ResultSet rs = null;
   	int count=0;
   	//long balance=0;
       try {
       	String sql1 = "UPDATE accountInfo set loanNotify=? where loanNotify=? AND loanStatus=?";
       	st = con.prepareStatement(sql1);
       	st.setInt(1,1);
    	st.setInt(2,0);
       	st.setString(3,"Processing");
       	st.executeUpdate();
//         while (rs.next()) {
//            
//            count++;
//         }
       	//System.out.println("Notified");
     }
         catch (Exception e) {
         	System.out.println(e);
         }
//       return count;
   	
	}
    
    //use
    
    public int insertToAccountTable(AccountInfo accountInfo) throws SQLException {
    	 AccountInfo passDataAccount = accountInfo;
        //con.setAutoCommit(false);
        PreparedStatement st = null;
        int[] batchResults = new int[1];
        int cusAccNumber=0;
        try {
        	 String sql = "INSERT INTO accountInfo (customerId,customerBankName,customerBalance,status,loanStatus,loanNotify) VALUES ( ?, ?, ?, ?, ?,?)";
             st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//             st = con.prepareStatement(sql);
                
                 int id = passDataAccount.getId();
                 System.out.println("id value "+id);
                 String bankName = passDataAccount.getBankName();
                 long balance = passDataAccount.getBalance();
                 st.setInt(1, id);
                 st.setString(2, bankName);
                 st.setLong(3, balance);
                 st.setInt(4,1);
                 st.setString(5, "NotApplied");
                 st.setInt(6, 0);
                 st.addBatch();
            batchResults = null;
            System.out.println("id value1 "+id);
            batchResults = st.executeBatch();
            System.out.println("id value2 "+id);
            //con.commit();
            ResultSet generatedKeysResultSet = st.getGeneratedKeys();
            System.out.println("id value3 "+id);
            while (generatedKeysResultSet.next()) {
                cusAccNumber = generatedKeysResultSet.getInt(1);
                
            }

            }catch (BatchUpdateException e) {
            batchResults = e.getUpdateCounts();
            System.out.println("Rolling back batch insertion");
            //con.rollback();
        }
        catch (Exception e){
            System.out.println("toString(): " + e.toString());
            System.out.println("getMessage(): " + e.getMessage());
       }
        st.close();
        return cusAccNumber;

    }

    public void deleteFromCustomerTable(int id) throws SQLException {
     con.setAutoCommit(false);
        PreparedStatement st = null;

        int cust_id = id;
        try {
            String sql = "DELETE FROM customerInfo WHERE customerId = ?";
            st = con.prepareStatement(sql);
            st.setInt(1,cust_id);
            st.executeUpdate();
          con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        st.close();

    }
	@Override
	public HashMap<Long, AccountInfo> showFromAccountTable() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void closeConnection() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateCustomerStatus(int id) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public HashMap<Long, AccountInfo> showFromAccountTableAll() throws SQLException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public ArrayList<CustomerInfo> getCustomerListintArrayList(int count) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public void bill(long accountNumber, String process, long mobile, long balance) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public ArrayList<TransactionHistoryPojo> showTransactionHistory() {
//		// TODO Auto-generated method stub
//		return null;
//	}


//  //bank Charges history
//  public void bankCharges(long accountNumber, String process, long balance) {
//  	PreparedStatement st = null;
//      
//      try {
//          String sql = "INSERT INTO charges (customerAccountNumber,process,amount,date) VALUES ( ?, ?, ?,now())";
//          st = con.prepareStatement(sql);
//          	st.setLong(1, accountNumber);
//              st.setString(2, process);
//              st.setLong(3, balance);
//              
//              st.executeUpdate();
//             }
//      catch (Exception e){
//          System.out.println("toString(): " + e.toString());
//          System.out.println("getMessage(): " + e.getMessage());
//     }
//     try {
//		st.close();
//	} catch (SQLException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//     System.out.print("History updated");
//  }
//  
  /////////////////////////////////////////////////////////////////////////////////

////show transaction history
//public ArrayList<TransactionHistoryPojo> showTransactionHistory() {
//	ArrayList<TransactionHistoryPojo> history = new ArrayList<TransactionHistoryPojo>();
//	Statement st = null;
//    ResultSet rs = null;
//    try {
//        st = con.createStatement();
//        rs = st.executeQuery("SELECT * from transactionHistory");
//
//        while (rs.next()) {
//        	TransactionHistoryPojo object = new TransactionHistoryPojo();
//           Integer id = rs.getInt("id");
//           Long senderAccountNumber = rs.getLong("senderAccountNumber");
//           Long receiverAccountNumber = rs.getLong("receiverAccountNumber");
//           Long balance = rs.getLong("amount");
//           String dateString = rs.getString("date");
//           object.setId(id);
//           object.setSenderAccountNumber(senderAccountNumber);
//           object.setReceiverAccountNumber(receiverAccountNumber);
//           object.setAmount(balance);
//           object.setDate(dateString);
//           history.add(object);
//
//        }
//    }
//        catch (Exception e) {
//        	System.out.println(e);
//        }
//					
//
//	return history;
//}
//
//   ////////////////////////////////////////////////////////////////////////////
	/*
	 * // W/D History
	 * 
	 * 
	 * 
	 * public void history(long accountNumber,String process,long balance) throws
	 * SQLException { PreparedStatement st = null;
	 * 
	 * try { String sql =
	 * "INSERT INTO history (customerAccountNumber,process,amount,date) VALUES ( ?, ?, ?,now())"
	 * ; st = con.prepareStatement(sql); st.setLong(1, accountNumber);
	 * st.setString(2, process); st.setLong(3, balance);
	 * 
	 * int i = st.executeUpdate(); System.out.print(i+" value i"); } catch
	 * (Exception e){ e.printStackTrace();
	 * 
	 * } st.close();
	 * 
	 * }
	 *//////////////////////////////////////////////////////////////////
////show history
//
//
//public ArrayList<History> showHistory() {
//	ArrayList<History> history = new ArrayList<History>();
//	Statement st = null;
//    ResultSet rs = null;
//    try {
//        st = con.createStatement();
//        rs = st.executeQuery("SELECT * from history");
//
//        while (rs.next()) {
//           History object = new History();
//           Integer customer_id = rs.getInt("id");
//           Long accountNumber = rs.getLong("customerAccountNumber");
//           String process = rs.getString("process");
//           Long balance = rs.getLong("amount");
//           String dateString = rs.getString("date");
//           object.setId(customer_id);
//           object.setAccountNumber(accountNumber);
//           object.setProcess(process);
//           object.setAmount(balance);
//           object.setDate(dateString);
//          history.add(object);
//          //System.out.print(object+" printining object in show history");
//
//        }
//    }
//        catch (Exception e) {
//        	System.out.println(e);
//        }
//					
//   // System.out.print(history+" printining list in show history");
//	return history;
//}
	
	
	
	
	
////transaction
//public void transaction_history(long sender_accountNumber,long receiver_accountNumber,long balance) throws SQLException {
//	  
//    PreparedStatement st = null;
//    
//    try {
//        String sql = "INSERT INTO transactionHistory (senderAccountNumber,receiverAccountNumber,amount,date) VALUES ( ?, ?, ?,now())";
//        st = con.prepareStatement(sql);
//        	//st.setInt(1, senderId);
//            st.setLong(1, sender_accountNumber);
//            //st.setInt(3, receiverId);
//            st.setLong(2, receiver_accountNumber);          
//            st.setLong(3, balance);    
//            st.executeUpdate();
//           }
//    catch (Exception e){
//        System.out.println("toString(): " + e.toString());
//        System.out.println("getMessage(): " + e.getMessage());
//   }
//   st.close();
//   //System.out.print("Transaction History updated in database");
//
//}
}
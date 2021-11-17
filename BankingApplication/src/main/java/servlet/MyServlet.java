package servlet;
import pojo_account.*;
import bank.logic.LogicLayer;
import java.util.*;
import hashMap.*;
import historyPojo.History;

import java.io.*;

import javax.print.attribute.Size2DSyntax;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.ws.Response;

import org.apache.catalina.valves.rewrite.Substitution.StaticElement;
import org.apache.jasper.tagplugins.jstl.core.If;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.cj.Session;

import TransactionHistory.TransactionHistoryPojo;
import allHistoryPojo.AllHistory;
import bank.database.DbStore;
import bank.exception.HandledException;
import bank.logic.LogicLayer;
import bank.pojo.CustomerInfo;
import cache.CacheStore;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException; 

public class MyServlet extends HttpServlet {
	long depositCharges=1;
    long withdrawCharges=1;
    long transactionCharges=2;
   // String applied="A";
    HashMap<Long, AccountInfo> map;
    ArrayList<Long> list;
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{

    	LogicLayer logicLayer = new LogicLayer();

    	//applied loans list - DONE
  	
      ArrayList<AccountInfo> appliedLoanList=null;
      	
      try {
      		
      		appliedLoanList = logicLayer.getAppliedLoanList() ;
      
      	}
      
      catch (Exception e) {
      	
      		System.out.println(e);		

      }
    	String query = request.getParameter("page");

    	PrintWriter out = response.getWriter();
    	
    	
    	//loan Submit client
    	if(query.equals("loanSubmit")) {
    		
    		long accountNumber = (long) request.getSession().getAttribute("acc");
    		long loanAmount = 	Long.parseLong(request.getParameter("loanAmount"));
    		map = logicLayer.clientCache(accountNumber);
    		AccountInfo accountInfo = 	map.get(accountNumber);
    		if(accountInfo.getLoanStatus().equals("NotApplied") || accountInfo.getLoanStatus().equals("Paid") ||  accountInfo.getLoanStatus().equals("Rejected")) {
    			
    		logicLayer.loanStatusUpdateWithAmount(accountNumber,"Processing",loanAmount,0);
    		
    		map = logicLayer.clientCache(accountNumber);
    		
    			
    		out.print("Rs."+loanAmount+" Loan applied successfully");
    	}
    	else if(accountInfo.getLoanStatus().equals("Approved")) {
    		out.print("You have already applied,You r eligible after you repay the loan");
    	}
    	
    	else {
    		out.print("You have already applied, It is under processing");
    	}
    	}
    	
    	
    	
    	
    	//setSessionAccountNumber
    	else if(query.equals("setSessionAccountNumber")) {
    	long accNumber = 	Long.parseLong(request.getParameter("y"));
    	map = logicLayer.clientCache(accNumber);
    	request.getSession().setAttribute("acc", accNumber);
    	long accountNumber = (long) request.getSession().getAttribute("acc");
    	out.print(accountNumber);
    	}
    	
    	//add new customer  - DONE
    	
    	else if(query.equals("Submit")) {
    		
    		AccountInfo accountInfo = new AccountInfo();
    		int id = (int) request.getSession().getAttribute("message");
    		
    		
    		String bank = request.getParameter("bankname");
    		long balance = Long.parseLong(request.getParameter("balance"));
    		accountInfo.setId(id);
    		accountInfo.setBankName(bank);
    		accountInfo.setBalance(balance);
    		logicLayer.accountInsert(accountInfo);
    		
    		request.getSession().setAttribute("message", id);
			//request.getSession().setAttribute("name", userName);
			HashMap<Integer,CustomerInfo> map1 = logicLayer.getAccountNumbersList(id);
			CustomerInfo customerInfo =  map1.get(id);
			list = (ArrayList<Long>) customerInfo.getList();
			request.getSession().setAttribute("AccountNumberList", list);
			out.print("New Account Created Successfully !!");
    	}
    	
    	//new customer details filling - DONE
    	
    	else if(query.equals("submit")) {
    		
    		CustomerInfo customerInfo = new CustomerInfo();
    		AccountInfo accountInfo = new AccountInfo();
    		
    		String name = request.getParameter("Name");
    		long mobile = Long.parseLong(request.getParameter("mobile"));
    		String bank = request.getParameter("bankname");
    		String address = request.getParameter("Address");
    		String password = request.getParameter("Password");
    		
    				
    		long balance = Long.parseLong(request.getParameter("balance"));
    		
    		customerInfo.setName(name);
    		customerInfo.setMobileNumber(mobile);
    		customerInfo.setAddress(address);
    		customerInfo.setPassword(password);
    		accountInfo.setBankName(bank);
    		accountInfo.setBalance(balance);
    		try {
    			int id = (int) logicLayer.addNewCustomers(customerInfo, accountInfo);
    			out.print("Welcome "+name+" Your id is "+id);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
    		
   		
    	}
    	
    	
    	
    	//transaction page - DONE
    	
    	else if(query.equals("transactionSubmit")) {
    		
    		long accountNumber = (long) request.getSession().getAttribute("acc");
    		long amount = Long.parseLong(request.getParameter("amount"));
    		long receiverAccountNumber = Long.parseLong(request.getParameter("receiverAccountNumber"));
    		if(accountNumber!=receiverAccountNumber) {
    		boolean d = logicLayer.checkAccountNumber(receiverAccountNumber);
    		if(d==false) {
    		long balance = logicLayer.withDraw(accountNumber);
    		long temp =0;
    		
    		if(balance>=amount){
    			
    		balance = balance-amount;
    		System.out.print(balance+" balance");
    		temp=balance-transactionCharges;
    		System.out.print(temp+" tempbalance");
    		try {
    			logicLayer.updateBalance(temp, accountNumber);
    			logicLayer.bankAccount(transactionCharges);
    			logicLayer.history(accountNumber, "Send to "+receiverAccountNumber, amount, transactionCharges,temp);
    			
    		}catch (Exception e) {
				
    			e.printStackTrace();
			}
    		balance = logicLayer.withDraw(receiverAccountNumber);
    		balance = balance+amount;
    		temp=balance;
    		try {
    			logicLayer.updateBalance(temp, receiverAccountNumber);
    			logicLayer.history(receiverAccountNumber, "Received from "+accountNumber, balance, 0,temp);
    			out.print("Fund transferred successfully");
			} catch (SQLException e) {
				
				e.printStackTrace();
			}


    		}else {
    			
              	out.print("Insufficient fund");
			}
    		
    		}else {
    			
              	out.print("No receiver account number found");
			}
    		}else {
    			out.print("Enter the valid account Number");
    		}

    	}
    	
    	//delete from both tables - DONE
    	
    	else if(query.equalsIgnoreCase("DeleteCustomerSubmit")) {
    		int id = (int) request.getSession().getAttribute("message");
    		boolean b = logicLayer.checkCustomerId(id);
    		if(b==false) {
    			
    		
    		try {
    			logicLayer.deleteFromAllTables(id);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
          	out.print("Succesfully!!");
    		}else {
              	out.print("Enter the correct id! Id not exists");
    		}

    		
    	}
    	
    	
//Check balance - Account
    	
    	else if(query.equalsIgnoreCase("accountBalance")) {
        	long accountNumber = 	Long.parseLong(request.getParameter("y"));
    		//long accountNumber = (long) request.getSession().getAttribute("acc");
    		boolean b = logicLayer.checkAccountNumber(accountNumber);
    		if(b==false) {
			long balance = logicLayer.checkBalance(accountNumber);
          	out.print("Balance: "+balance);
    		}
    		else {
              	out.print("Enter the correct Account Number! Account number not exists");
    		}

    	}
    	
    	
    	
    	
    	
    	//delete a account - DONE
    	
    	else if(query.equalsIgnoreCase("deleteAccountSubmit")) {
    		long accountNumber = (long) request.getSession().getAttribute("acc");
    		boolean b = logicLayer.checkAccountNumber(accountNumber);
    		if(b==false) {
    		try {
    			logicLayer.deleteFromAccountTable(accountNumber);
    			out.print("Succesfully!!");
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
          	
//          	RequestDispatcher rd = request.getRequestDispatcher("welcome.jsp");
//            rd.forward(request, response);
    		}else {
              	out.print("Enter the correct Account Number! Account number not exists");
    		}

    	}
    	
    	//renew a customer - DONE
    	
    	else if(query.equalsIgnoreCase("RenewCustomerSubmit")) {
    		int id = Integer.parseInt(request.getParameter("Id"));
    		boolean b = logicLayer.checkCustomerId(id);
    		if(b==false) {
    		try {
    			logicLayer.updateCustomerStatusToActive(id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          	out.print("Succesfully!!");
    		}else {
              	out.print("Enter the correct id! Id not exists");
    		}
    		
			
    	}
    	
    	// activate account - DONE
    	
    	else if(query.equalsIgnoreCase("RenewAccountSubmit")) {
    		int id = Integer.parseInt(request.getParameter("Id"));
    		long acc_Number = Long.parseLong(request.getParameter("accountNumber"));
    		boolean b = logicLayer.checkCustomerId(id);
    		if(b==false) {
    			boolean bb =logicLayer.checkAccountNumber(acc_Number);
    			if(bb==false) {
    		try {
    			logicLayer.updateAccountStatusToActive(id,acc_Number);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          	out.print("Succesfully!!");
    			}
    		else {
                  	out.print("Enter the correct Account Number! Account number not exists");
				}
    		}
    		else {
              	out.print("Enter the correct id! Id not exists");
    		}

    	}
    	
    	//withdraw page - DONE
    	
    	else if(query.equalsIgnoreCase("withDrawSubmit")) {
//    		int id = (int) request.getSession().getAttribute("message");
//    		HashMap<Integer,CustomerInfo> map1 = logicLayer.getAccountNumbersList(id);
//			CustomerInfo customerInfo =  map1.get(id);
//			list = (ArrayList<Long>) customerInfo.getList();
//    		request.getSession().setAttribute("AccountNumberList", list);
    		long acc_Number = (long) request.getSession().getAttribute("acc");
    		boolean a = logicLayer.checkAccountNumber(acc_Number);
    		long updatedBalance=0;
    		if(a==false) {
    		long amountLong = Long.parseLong(request.getParameter("withDrawAmount"));
    		
    		long balance = logicLayer.withDraw(acc_Number);

    		if(balance>=amountLong){
    		balance = balance-amountLong;
    		updatedBalance = balance-withdrawCharges;
    		try {
    			logicLayer.updateBalance(updatedBalance, acc_Number);
    			logicLayer.bankAccount(withdrawCharges);
    			logicLayer.history(acc_Number,"With Draw",amountLong,withdrawCharges,updatedBalance);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}

          	out.print("Successfully!!! and ur remaining balance is "+updatedBalance+" Bank charges: "+withdrawCharges+" Rupees");
    		}else {
    			
              	out.print("Insufficient Fund");
			}
    		}else {
    			
              	out.print("Account Number not found");
			}
    	}
    	
    	//deposit page - DONE
    	
    	else if(query.equalsIgnoreCase("depositSubmit")) {
    		long acc_Number = (long) request.getSession().getAttribute("acc");
    		System.out.print(acc_Number+ " acc nuber");
    		boolean a = logicLayer.checkAccountNumber(acc_Number);
    		long updatedBalance=0;
    		if(a==false) {
    		long amountLong = Long.parseLong(request.getParameter("deposit"));
    		
    		long balance = logicLayer.withDraw(acc_Number);
    		//logicLayer.bankCharges(acc_Number,"Deposit",bankCharge);
    		
    		balance = balance+amountLong;
    		updatedBalance = balance-withdrawCharges;
    		try {
    			logicLayer.updateBalance(updatedBalance, acc_Number);
    			logicLayer.bankAccount(depositCharges);
    			logicLayer.history(acc_Number,"Deposit",amountLong,withdrawCharges,updatedBalance);
			} catch (SQLException e) {
				e.printStackTrace();
			}
          	out.print("Successfully!!! and ur new remaining balance is "+updatedBalance+" Bank charges: "+withdrawCharges+" Rupees");
    		}else {
              	out.print("Account Number not found");
			}
    	}
    	

    	//all history submit
    	else if(query.equalsIgnoreCase("allHistory")) {
    		long acc_Number = (long) request.getSession().getAttribute("acc");
    		boolean a = logicLayer.checkAccountNumber(acc_Number);
    		if(a==false) {
    			ArrayList<History> list = logicLayer.allHistory(acc_Number);
    			
    			JSONArray values= new JSONArray();
    			for(int i=0;i<list.size();i++) {
    				History allHistory = list.get(i);
    				JSONObject data = new JSONObject(allHistory);
    				values.put(data);
    				
    			}
    			
    			out.print(values.toString());
            
    			
    		}else {
    			out.print("Account number not exist!!");
    		}
    	}
    	
 
    	 //Login
    //-----------for cookie---------------------//
    	else if(query.equalsIgnoreCase("loginSubmit")) {
    		//out.print("user id or userName ");
    		int id = Integer.parseInt(request.getParameter("Id"));
    	String password =	request.getParameter("Password");
    	
    		//long accountNumber = Long.parseLong(request.getParameter("AccountNumber"));
    		int n = logicLayer.checkLogin(id,password);
    		if(n==1) {
    			request.getSession().setAttribute("message", id);
    			//request.getSession().setAttribute("name", userName);
    			HashMap<Integer,CustomerInfo> map1 = logicLayer.getAccountNumbersList(id);
    			System.out.print(map1+" maploginsubmit");
    			CustomerInfo customerInfo =  map1.get(id);
    			list = (ArrayList<Long>) customerInfo.getList();
    		long mobile = 	customerInfo.getMobileNumber();
    	String address =	customerInfo.getAddress();
    		request.getSession().setAttribute("mobile", mobile);
    		request.getSession().setAttribute("address", address);
    		
    			//System.out.print(list+" list1");
    			//HashMap<Integer, CustomerInfo>map1 = CacheStore.INSTANCE.returnCustomerMapStore();
    			//request.getSession().setAttribute("acc", accountNumber);
    			//map = logicLayer.clientCache(accountNumber);
    			request.getSession().setAttribute("AccountNumberList", list);
    		//String username = (String)	request.getSession().getAttribute("username");
    			//System.out.print(username+" Username cookie check");
    			//out.print("true");

        		JSONArray values= new JSONArray();
    			for(int i=0;i<list.size();i++) {
    				long acc = list.get(i);
    				
    				values.put(acc);
    				
    			}
    			//System.out.print(values.toString()+" list1");
    			out.print(values.toString());
    			
    			
    			
    			
    			//RequestDispatcher rd = request.getRequestDispatcher("LoginSideNav.jsp");
//    		RequestDispatcher rd = request.getRequestDispatcher("Login.jsp");
//            rd.forward(request, response);
    		}else if(n == 2) {
    			out.print("admin");
    		}
    		else {
    			out.print("false");
//    			request.setAttribute("message", "wrong");
//    			
//    			RequestDispatcher rd = request.getRequestDispatcher("welcome.jsp");
//                rd.forward(request, response);
                
    		}
    	
    		
        }
    	
    	else if(query.equalsIgnoreCase("setAccountNumber")) {
    	List<Long> list = 	(List<Long>) request.getSession().getAttribute("AccountNumberList");
    	JSONArray values= new JSONArray();
		for(int i=0;i<list.size();i++) {
			long acc = list.get(i);
			
			values.put(acc);
			
		}
		//System.out.print(values.toString()+" list1");
		out.print(values.toString());
		
    		
    	}
    	
    	//mobile number update submit
	else if(query.equalsIgnoreCase("mobileUpdateSubmit")) {
        
    		long newMobile = Long.parseLong(request.getParameter("mobileUpdate"));
    		int id = (int) request.getSession().getAttribute("message");
    		request.getSession().setAttribute("AccountNumberList", list);
    			logicLayer.changeMobile(newMobile,id);
    			
    			out.print("changed successfully!!");
    			HashMap<Integer,CustomerInfo> map1 = logicLayer.getAccountNumbersList(id);
    			CustomerInfo customerInfo =  map1.get(id);
    			
    		long mobile = 	customerInfo.getMobileNumber();
    		request.getSession().setAttribute("mobile", mobile);
    		
        }
    	
    	//old mobile
	else if(query.equalsIgnoreCase("oldMobile")) {
        
			long mobileLong = 	(long) request.getSession().getAttribute("mobile");
			
			
			out.print(mobileLong);
			
			}
	
     	//old address
    	else if(query.equalsIgnoreCase("oldAddress")) {
            
    			String address =(String) request.getSession().getAttribute("address");
    			
    			
    			out.print(address);
    			
    			}
    	
    	//address update submit
    	else if(query.equalsIgnoreCase("addressUpdateSubmit")) {
            
    		String newAddress = request.getParameter("addressUpdate");
    		int id = (int) request.getSession().getAttribute("message");
    		request.getSession().setAttribute("AccountNumberList", list);
    			logicLayer.changeAddress(newAddress, id);
    			
    			out.print("changed successfully!!");
    			HashMap<Integer,CustomerInfo> map1 = logicLayer.getAccountNumbersList(id);
    			CustomerInfo customerInfo =  map1.get(id);
    			
    		String address = 	customerInfo.getAddress();
    		request.getSession().setAttribute("address", address);
    			}
    	
    	//load paid
    	else if (query.equalsIgnoreCase("loanPaidSubmit")) {
    		//request.getSession().setAttribute("AccountNumberList", list);
    		long accountNumber = (long) request.getSession().getAttribute("acc");
    		map = logicLayer.clientCache(accountNumber);
    		AccountInfo accountInfo = map.get(accountNumber);
    		System.out.print("object account "+accountInfo);
    		if(accountInfo.getLoanStatus().equals("Approved")) {
    			
    		long loanAmount=accountInfo.getLoanAmount();
    		String one="loanAccount";
    		
    		long balance = logicLayer.withDraw(accountNumber);

    		if(balance>=loanAmount){
    		balance = balance-loanAmount;
    		//updatedBalance = balance-withdrawCharges;
    		try {
    			logicLayer.updateBalance(balance, accountNumber);
    			
    			logicLayer.history(accountNumber,"Loan Paid",loanAmount,0,balance);
    			
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
    		logicLayer.loanStatusUpdate(accountNumber,"Paid");
        	//load arraylist again for new updated list
        	appliedLoanList = logicLayer.getAppliedLoanList() ;
        	long balanceBank = logicLayer.bankAmount(one);
        	balanceBank = balanceBank+loanAmount;
        	logicLayer.updateBankAmount(balanceBank,one);
        	map = logicLayer.clientCache(accountNumber);
          	out.print("Successfully!!!Loan paid");
    		}else {
    			
              	out.print("Insufficient fund");
			}
    			
    		}else {
    			out.print("You can't pay the loan now");
    		}
		}
    	
    	
    	//mobile Recharge Submit
else if(query.equalsIgnoreCase("mobileRechargeSubmit")) {
//	request.setAttribute("AccountNumberList", list);
            long updatedBalance=0;
    		long mobile =Long.parseLong(request.getParameter("Mobile"));
    		long amount =Long.parseLong(request.getParameter("Amount"));
    		long accountNumber = (long) request.getSession().getAttribute("acc");
    		long balance = logicLayer.withDraw(accountNumber);

    		if(balance>=amount){
    		balance = balance-amount;
    		updatedBalance = balance;
    		try {
    			logicLayer.updateBalance(updatedBalance, accountNumber);
    			//logicLayer.bankAccount(withdrawCharges);
    			logicLayer.history(accountNumber,"Mobile Recharge",amount,0,updatedBalance);
    			logicLayer.bill(accountNumber,"Mobile Recharge",mobile,amount);
			} catch (SQLException e) {
				
				e.printStackTrace();
			}

          	out.print("Successfully!!! and ur remaining balance is "+updatedBalance);
    		}else {
    			
              	out.print("Insufficient Fund");
			}
    		
    			
    			}
    	
    	//notification update
else if(query.equalsIgnoreCase("notificationUpdate")) {
	//System.out.print("update notify");
	 logicLayer.notificationUpdate();
	
	}
      	
    	//notification Loan update
else if(query.equalsIgnoreCase("notificationLoanUpdate")) {
	System.out.print("update notify");
	 logicLayer.notificationLoanUpdate();
	
	}



    	
    	
                }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	     
    	String query = request.getParameter("page");
    	PrintWriter out = response.getWriter();
    	LogicLayer logicLayer = new LogicLayer();
    	List<Long> list1 = new ArrayList<Long>();
    	//list1.add((long) 0);
        //customer list active - done
          
        
            //customer list in-active - DONE
          	
            ArrayList<CustomerInfo> inActiveCustomerList=null;
            	try {
            	
            		inActiveCustomerList = logicLayer.getInActiveCustomerList();
            		
            	}
            
            	catch (Exception e) {
            		
            		System.out.println(e);		

            	}
            	
            //account list active - DONE
            
            ArrayList<AccountInfo> accountList=null;
            	try {
            		
            		accountList = logicLayer.getAccountList();
            
            	}
            
            catch (Exception e) {
            	
            		System.out.println(e);		
            
            }
            
            //account list in-active - DONE
            	
            ArrayList<AccountInfo> inActiveAccountList=null;
            	try {
            		
            		inActiveAccountList = logicLayer.getInActiveAccountList() ;
            
            	}
            
            catch (Exception e) {
            	
            		System.out.println(e);		

            }
            
            //history list
            
            ArrayList<History> histories = null;
            	try {
            		
            		histories = logicLayer.showHistory();
      		
            	}
            	catch (Exception e) {

            		System.out.println(e);
      		
            	}
            //transaction list
            
            ArrayList<TransactionHistoryPojo> transactionHistory = null;
            	try {
            		
      			//transactionHistory = logicLayer.showTransactionHistory();
      		
            	} 
            	catch (Exception e) {

            		System.out.println(e);	
      		
            	}
            	
                	//approved loans list - DONE
                	
                    ArrayList<AccountInfo> approvedLoanList=null;
                    	try {
                    		
                    		approvedLoanList = logicLayer.getApprovedLoanList() ;
                    
                    	}
                    
                    catch (Exception e) {
                    	
                    		System.out.println(e);		

                    }
                    	
                    	//appliedloan list
                    	 ArrayList<AccountInfo> appliedLoanList=null;
                       	try {
                       		
                       		appliedLoanList = logicLayer.getAppliedLoanList() ;
                       //System.out.print("list "+appliedLoanList);
                       	}
                       
                       catch (Exception e) {
                       	
                       		System.out.println(e);		

                       }
                       	
                       	//waiting list
                       	
                        ArrayList<AccountInfo> waitingLoanList=null;
                       	try {
                       		
                       		waitingLoanList = logicLayer.getWaitingLoanList() ;
                       
                       	}
                       
                       catch (Exception e) {
                       	
                       		System.out.println(e);		

                       }

                       	//Block list
                       	
                        ArrayList<AccountInfo> blockLoanList=null;
                       	try {
                       		
                       		blockLoanList = logicLayer.getBlockLoanList() ;
                       
                       	}
                       
                       catch (Exception e) {
                       	
                       		System.out.println(e);		

                       }
    	
    	
          //Home - DONE
        	
//        	if(query.equalsIgnoreCase("home")) {
//				/*
//				 * RequestDispatcher rd = request.getRequestDispatcher("welcome.html");
//				 * rd.forward(request, response);
//				 */
//            }
        	//show customer list - active - DONE
        	
        	 if(query.equalsIgnoreCase("customer")) {
//        		System.out.print(customerList+" Hihello");
//        		request.setAttribute("output", customerList);
        		//System.out.print(" Hihello");
        		//out.print(customerList);
         		//int count =Integer.parseInt(request.getParameter("y"));

        		  ArrayList<CustomerInfo> customerList=null;
                	try {
                		
                		customerList = logicLayer.getCustomerList();
                		
                	}
                
                	catch (Exception e) {
                		
                		System.out.println(e);		
                		
                	}
        		
    			JSONArray values= new JSONArray();
    			for(int i=0;i<customerList.size();i++) {
    				CustomerInfo customerInfo = customerList.get(i);
    				JSONObject data = new JSONObject(customerInfo);
    				values.put(data);
    				
    			}
    			
    			out.print(values.toString());

        }
        	
        	 
        	 else if(query.equalsIgnoreCase("customerCountList")) {
         		int pageCount =Integer.parseInt(request.getParameter("y"));

        		 logicLayer.customerCountList(pageCount);
        		 
        	 }
//        	else if(query.equalsIgnoreCase("customers")) {
////        		System.out.print(customerList+" Hihello");
////        		request.setAttribute("output", customerList);
//        		out.print("HI");
//
//        }
//	//mobile number update page
//        	
//        	else if(query.equalsIgnoreCase("MobileUpdate")) {
//        	
//        		request.setAttribute("AccountNumberList", list);
//        RequestDispatcher rd = request.getRequestDispatcher("MobileUpdate.jsp");
//        rd.forward(request, response);
//        }
        
            //show inactive customer - DONE
            
        	else if(query.equalsIgnoreCase("inActiveCustomer")) {
        	
//            request.setAttribute("value", inActiveCustomerList);
//          
//            RequestDispatcher rd = request.getRequestDispatcher("ViewDeleteCustomer.jsp");
//            rd.forward(request, response);
        		JSONArray values= new JSONArray();
    			for(int i=0;i<inActiveCustomerList.size();i++) {
    				CustomerInfo customerInfo = inActiveCustomerList.get(i);
    				JSONObject data = new JSONObject(customerInfo);
    				values.put(data);
    				
    			}
    			
    			out.print(values.toString());
            }
        
      //show inactive Account - DONE
        
        	else if(query.equalsIgnoreCase("inActiveAccount")) {
        	
//            request.setAttribute("output", inActiveAccountList);
//          
//            RequestDispatcher rd = request.getRequestDispatcher("ViewDeleteAccounts.jsp");
//            rd.forward(request, response);
        		
        		JSONArray values= new JSONArray();
    			for(int i=0;i<inActiveAccountList.size();i++) {
    				AccountInfo accountInfo = inActiveAccountList.get(i);
    				JSONObject data = new JSONObject(accountInfo);
    				values.put(data);
    				
    			}
    			
    			out.print(values.toString());

        		
        		
            }
     
     //show withdraw deposit histories - DONE
     
        	else if(query.equalsIgnoreCase("history")) {
     	
//         request.setAttribute("output", histories);
//       
//         RequestDispatcher rd = request.getRequestDispatcher("History.jsp");
//         rd.forward(request, response);
        		JSONArray values= new JSONArray();
    			for(int i=0;i<histories.size();i++) {
    				History history = histories.get(i);
    				JSONObject data = new JSONObject(history);
    				values.put(data);
    				
    			}
    			
    			out.print(values.toString());
         }
     
     //show transaction list - DONE
     
     //no need
//        	else if(query.equalsIgnoreCase("transactionHistory")) {
//        		//request.setAttribute("AccountNumberList", list);
//    	 	
//         request.setAttribute("output", transactionHistory);
//       
//         RequestDispatcher rd = request.getRequestDispatcher("TransactionHistory.jsp");
//         rd.forward(request, response);
//         }
     
     //Show account list - active - DONE
     
        	else if(query.equalsIgnoreCase("account")) {
        		
				/*
				 * request.setAttribute("output", accountList); RequestDispatcher rd =
				 * request.getRequestDispatcher("account.jsp"); rd.forward(request, response);
				 */
        	//System.out.print(" Hihello");
    		//out.print(customerList);
    		

			
			JSONArray values= new JSONArray();
			for(int i=0;i<accountList.size();i++) {
				AccountInfo accountInfo = accountList.get(i);
				JSONObject data = new JSONObject(accountInfo);
				values.put(data);
				
			}
			
			out.print(values.toString());

        	
        	
        }
            //add a new customer page-DONE
            //----------for ajax----------//
//        	else if(query.equalsIgnoreCase("addcustomer")) {
//        	RequestDispatcher rd = request.getRequestDispatcher("AddCustomer.jsp");
//        	rd.forward(request, response);
//        }
//        
        	
        	  //add new account for existing customer - DONE
            //--------------for ajax--------------------//
//        	else if(query.equalsIgnoreCase("addaccount")) {
//        		request.getSession().setAttribute("AccountNumberList", list);
//        	RequestDispatcher rd = request.getRequestDispatcher("AddAccount.jsp");
//        	rd.forward(request, response);
//        }
        	
        	
            //delete page - DONE
            
        	else if(query.equalsIgnoreCase("deleteCustomer")) {
        	RequestDispatcher rd = request.getRequestDispatcher("DeleteCustomer.jsp");
        	rd.forward(request, response);
        }	
        	
        	
            //delete account page - DONE
            
            
        	else if(query.equalsIgnoreCase("deleteAccounts")) {
        		
        	RequestDispatcher rd = request.getRequestDispatcher("DeleteAccount.jsp");
        	rd.forward(request, response);
        }
        
        	 //---for ajax---
//            //activate a customer - DONE
//            
//        	else if(query.equalsIgnoreCase("reNewCustomer")) {
//        	RequestDispatcher rd = request.getRequestDispatcher("RenewOldCustomer.jsp");
//        	rd.forward(request, response);
//        }
//            //Activate an account - DONE
//            
//        	else if(query.equalsIgnoreCase("reNewAccount")) {
//        	RequestDispatcher rd = request.getRequestDispatcher("RenewOldAccount.jsp");
//        	rd.forward(request, response);
//        }	
        	//--------------for ajax------------//
        	  //withdraw page - DONE
//            
//        	else if(query.equalsIgnoreCase("withDraw")) {
////        		int id = (int) request.getSession().getAttribute("message");
////        		HashMap<Integer,CustomerInfo> map1 = logicLayer.getAccountNumbersList(id);
////    			CustomerInfo customerInfo =  map1.get(id);
////    			list = (ArrayList<Long>) customerInfo.getList();
////        		request.getSession().setAttribute("AccountNumberList", list);
////        	RequestDispatcher rd = request.getRequestDispatcher("WithDraw.jsp");
////        	rd.forward(request, response);
//        }
//        
        	 
        	 
        	 //--------for ajax-----------
//        //deposit page - DONE
//        
//        	else if(query.equalsIgnoreCase("deposit")) {
//        		request.getSession().setAttribute("AccountNumberList", list);
//        	RequestDispatcher rd = request.getRequestDispatcher("Deposit.jsp");
//        	rd.forward(request, response);
//        }
    	
        	//--------for ajax-----------
        	 
            //show transaction page-DONE
            
//        	else if(query.equalsIgnoreCase("transaction")) {
//        		request.getSession().setAttribute("AccountNumberList", list);
//        	RequestDispatcher rd = request.getRequestDispatcher("transaction.jsp");
//        	rd.forward(request, response);
//        }
        	
//        	  //transaction history page - DONE
//            // no need
//        	else if(query.equalsIgnoreCase("transactionHistory")) {
//        		request.getSession().setAttribute("AccountNumberList", list);
//        	RequestDispatcher rd = request.getRequestDispatcher("TransactionHistory.jsp");
//        	rd.forward(request, response);
//        }
        	 
        	 
        	 
        	  //all history
        	 //no need
//        	else if(query.equalsIgnoreCase("allHistory")) {
//        		request.getSession().setAttribute("AccountNumberList", list);
//        		RequestDispatcher rd = request.getRequestDispatcher("AllHistory.jsp");
//            	rd.forward(request, response);
//        	}
        	
        	
//  //client Loan - DONE
//            ---for ajax----
//            
//        	else if(query.equalsIgnoreCase("clientLoan")) {
//        		request.getSession().setAttribute("AccountNumberList", list);
//        	RequestDispatcher rd = request.getRequestDispatcher("ClientLoan.jsp");
//        	rd.forward(request, response);
//        }
        	
        	//show approved loans
        	else if(query.equalsIgnoreCase("approvedLoans")) {
        		//request.setAttribute("AccountNumberList", list);
//        		
//        		request.setAttribute("output", approvedLoanList);
//            	RequestDispatcher rd = request.getRequestDispatcher("ShowApprovedLoans.jsp");
//            	
//            	rd.forward(request, response);
        		
        		JSONArray values= new JSONArray();
    			for(int i=0;i<approvedLoanList.size();i++) {
    				AccountInfo accountInfo = approvedLoanList.get(i);
    				JSONObject data = new JSONObject(accountInfo);
    				values.put(data);
    				
    			}
    			
    			out.print(values.toString());
        		
        	}
        	
        	//show waiting loans list
        	else if(query.equalsIgnoreCase("waitingLoansList")) {
        		//request.setAttribute("AccountNumberList", list);
        		
//        		request.setAttribute("output", waitingLoanList);
//            	RequestDispatcher rd = request.getRequestDispatcher("ShowWaitingLoans.jsp");
//            	
//            	rd.forward(request, response);
        		
        		JSONArray values= new JSONArray();
    			for(int i=0;i<waitingLoanList.size();i++) {
    				AccountInfo accountInfo = waitingLoanList.get(i);
    				JSONObject data = new JSONObject(accountInfo);
    				values.put(data);
    				
    			}
    			
    			out.print(values.toString());
        		
        	}
        	
        	//show block loans list
        	else if(query.equalsIgnoreCase("blockLoansList")) {
        		//request.setAttribute("AccountNumberList", list);
        		
//        		request.setAttribute("output", blockLoanList);
//            	RequestDispatcher rd = request.getRequestDispatcher("ShowBlockLoans.jsp");
//            	
//            	rd.forward(request, response);
        		
        		JSONArray values= new JSONArray();
    			for(int i=0;i<blockLoanList.size();i++) {
    				AccountInfo accountInfo = blockLoanList.get(i);
    				JSONObject data = new JSONObject(accountInfo);
    				values.put(data);
    				
    			}
    			
    			out.print(values.toString());
        	}
        	
        	
        	
        	//loan paid page
        	
//        	else if(query.equalsIgnoreCase("loanPaidSubmit")) {
//        		request.getSession().setAttribute("AccountNumberList", list);
//        		RequestDispatcher rd = request.getRequestDispatcher("PayLoan.jsp");
//            	rd.forward(request, response);
//        	}
        	
//show applied loans
        	else if(query.equalsIgnoreCase("appliedLoans")) {
        		
//        		
//        		request.setAttribute("output", appliedLoanList);
//            	RequestDispatcher rd = request.getRequestDispatcher("ShowAppliedLoans.jsp");
//            	
//            	rd.forward(request, response);

    			JSONArray values= new JSONArray();
    			for(int i=0;i<appliedLoanList.size();i++) {
    				AccountInfo accountInfo = appliedLoanList.get(i);
    				JSONObject data = new JSONObject(accountInfo);
    				values.put(data);
    				
    			}
    			
    			out.print(values.toString());
        	}
        	
     //approve loan  
        	
        	else if(query.equals("loanApprove")) {
        		//System.out.print("yes");
        		
        		String one="loanAccount";
        		
        	long accountNumber = Long.parseLong(request.getParameter("accountNumber"));
        	map = logicLayer.clientCache(accountNumber); 
        	AccountInfo accountInfo = map.get(accountNumber);
        	long loanAmount=accountInfo.getLoanAmount();
        	//System.out.print(accountNumber+" accountNumber");
        	long balanceBank = logicLayer.bankAmount(one);
        	
        	logicLayer.loanStatusUpdate(accountNumber,"Approved");
        	
        	appliedLoanList = logicLayer.getAppliedLoanList() ;
        	
        	balanceBank = balanceBank-loanAmount;
        	//System.out.print(balanceBank+" bank balance"+" one "+ one);
        	logicLayer.updateBankAmount(balanceBank,one);
        	long balance = logicLayer.withDraw(accountNumber);
    		balance+=loanAmount;
    			try {
					logicLayer.updateBalance(balance, accountNumber);
				
    			logicLayer.history(accountNumber,"Loan debited",loanAmount,0,balance);
    			out.print("Loan Approved Successfully!! by admin");
    			} catch (SQLException e) {
					
					e.printStackTrace();
				}
				/*
				 * request.setAttribute("output", appliedLoanList); RequestDispatcher rd =
				 * request.getRequestDispatcher("ShowAppliedLoans.jsp");
				 * 
				 * rd.forward(request, response);
				 */
//        	}
//        	else {
//        		
//        		request.setAttribute("output", appliedLoanList);
//            	RequestDispatcher rd = request.getRequestDispatcher("ShowAppliedLoans.jsp");
//            	rd.forward(request, response);
//            	out.print("No sufficient fund in bank account - loan");
//        		
//        	}
        	}
        	
        	//reject loan
        	
        	else if(query.equals("loanReject")) {
        	
        	long accountNumber = Long.parseLong(request.getParameter("accountNumber"));
        	
        	logicLayer.loanStatusUpdate(accountNumber,"Rejected");
        	appliedLoanList = logicLayer.getAppliedLoanList() ;
        	out.print("Loan Rejected Successfully!! by admin");
//        	request.setAttribute("output", appliedLoanList);
//        	RequestDispatcher rd = request.getRequestDispatcher("ShowAppliedLoans.jsp");
//        	
//        	rd.forward(request, response);
        	}
        	
	//waiting list loan
        	
        	else if(query.equals("loanWaitingList")) {
        	
        	long accountNumber = Long.parseLong(request.getParameter("accountNumber"));
        	
        	logicLayer.loanStatusUpdate(accountNumber,"Waiting List");
        	appliedLoanList = logicLayer.getAppliedLoanList() ;
        	out.print("Loan waitinglisted !! by admin");
//        	request.setAttribute("output", appliedLoanList);
//        	RequestDispatcher rd = request.getRequestDispatcher("ShowAppliedLoans.jsp");
//        	
//        	rd.forward(request, response);
        	}
        	
	//block loan
        	
        	else if(query.equals("loanBlock")) {
        	
        	long accountNumber = Long.parseLong(request.getParameter("accountNumber"));
        	
        	logicLayer.loanStatusUpdate(accountNumber,"Blocked");
        	appliedLoanList = logicLayer.getAppliedLoanList() ;
        	out.print("Loan blocked !! by admin");
//        	request.setAttribute("output", appliedLoanList);
//        	RequestDispatcher rd = request.getRequestDispatcher("ShowAppliedLoans.jsp");
//        	
//        	rd.forward(request, response);
        	}
        	
        	
        	
        	//logout
        	
        	else if(query.equalsIgnoreCase("logout")) {
        		request.getSession().invalidate();

//        		request.getSession().setAttribute("message", "");
//        		request.getSession().setAttribute("name", "");
//        		request.getSession().setAttribute("acc", "");
//        		//list.clear();
//        		request.getSession().setAttribute("AccountNumberList", null);
//        		request.getSession().invalidate();
//        	List<Long>list11	= (List<Long>) request.getSession().getAttribute("AccountNumberList");
//        	System.out.print(list11+" list11");
        		//request.getSession().setAttribute("name", userName);
//        		response.setHeader("Pragma","no-cache");
//        		response.setHeader("Cache-Control","no-store");
//        		response.setHeader("Expires","0");
//        		response.setDateHeader("Expires",-1);
//        		request.getSession().invalidate();
        		
        		RequestDispatcher rd = request.getRequestDispatcher("welcome.html");
        		rd.forward(request, response);
        	}
        	 //notification customer
        	 
        	else if(query.equalsIgnoreCase("notification")) {
        	int	count =  logicLayer.notification();
        	out.print(count);
        	}
        	 
        	 //notification loan
        	 else if(query.equalsIgnoreCase("notificationLoan")) {
             	int	count =  logicLayer.notificationLoan();
             	out.print(count);
             	}
        	 
        	 //label data
        	 else if(query.equalsIgnoreCase("labelData")) {
             	//int	count =  
             		int[] sizeOfCount= 	logicLayer.labelData();
             		JSONArray values= new JSONArray();
        			for(int i=0;i<sizeOfCount.length;i++) {
        				//AccountInfo accountInfo = blockLoanList.get(i);
        				//JSONObject data = new JSONObject(accountInfo);
        				values.put(sizeOfCount[i]);
        				
        			}
             		
             	out.print(values);
             	}

    }
}

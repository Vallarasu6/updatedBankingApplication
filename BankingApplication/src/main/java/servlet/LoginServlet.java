package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import bank.logic.LogicLayer;
import bank.pojo.CustomerInfo;

public class LoginServlet extends HttpServlet{
	
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		      throws ServletException, IOException {
		ArrayList<Long> list;
		LogicLayer logicLayer = new LogicLayer();
		String query = request.getParameter("page");

		PrintWriter out = response.getWriter();
		if(query.equals("loginSubmit"))
		{
    		int id = Integer.parseInt(request.getParameter("Id"));
    	String password =	request.getParameter("Password");
    		int n = logicLayer.checkLogin(id,password);
    		if(n==1) {
    			request.getSession().setAttribute("message", id);
    			HashMap<Integer,CustomerInfo> map1 = logicLayer.getAccountNumbersList(id);
    			CustomerInfo customerInfo =  map1.get(id);
    			list = (ArrayList<Long>) customerInfo.getList();
    		long mobile = 	customerInfo.getMobileNumber();
    	String address =	customerInfo.getAddress();
    	request.getSession().setAttribute("password", password);
    		//request.getSession().setAttribute("mobile", mobile);
    		request.getSession().setAttribute("address", address);
    		request.getSession().setMaxInactiveInterval(30*60);
    		//String userId = "user:"+id;
    		Cookie user = new Cookie("userId", id + "");
			user.setMaxAge(30*60);
			response.addCookie(user);
    			request.getSession().setAttribute("AccountNumberList", list);
    	
        		JSONArray values= new JSONArray();
        		
    			for(int i=0;i<list.size();i++) {
    				long acc = list.get(i);
    				
    				values.put(acc);
    				
    			}
    			out.print(values.toString());
   
    		}
    		else {
    			out.print("false");

                }
		}
		 }

}

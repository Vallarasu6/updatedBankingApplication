package bank.pojo;

import java.util.*;

public class CustomerInfo {


    private int id;
    private String name;
    private long mobileNumber;
    private String address;
    private String password;
	private List<Long> list;
	

    public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<Long> getList() {
		return list;
	}
	public void setList(List<Long> list) {
		this.list = list;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setMobileNumber(long mobile){
        this.mobileNumber = mobile;
    }
    public long getMobileNumber(){ return this.mobileNumber; }
	@Override
	public String toString() {
		return "CustomerInfo [id=" + id + ", name=" + name + ", mobileNumber=" + mobileNumber + ", address=" + address
				+ ", password=" + password + ", list=" + list + "]";
	}
	
    

    
}
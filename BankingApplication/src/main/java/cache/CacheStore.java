package cache;

import java.util.*;

import bank.pojo.CustomerInfo;

public enum CacheStore {
		INSTANCE;
	public HashMap<Integer, CustomerInfo> customerMap = new HashMap<>();
//	public void customerMapStore(int id,List<Long> list) {
//		customerMap.put(id, list);
//	}
	public HashMap<Integer, CustomerInfo> returnCustomerMapStore() {
		return customerMap;
	}
	
}

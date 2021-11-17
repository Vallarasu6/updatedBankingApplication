package hashMap;
import pojo_account.AccountInfo;
import java.util.HashMap;
public enum HashMapHandler {
    INSTANCE;
    public HashMap<Integer, HashMap<Long, AccountInfo>> dbHashMap = new HashMap<>();
    public HashMap<Integer, HashMap<Long,AccountInfo>> dbHashMapAll = new HashMap<>();
    public void store(long account_number, AccountInfo accountInfo,int customer_id){
        HashMap<Long, AccountInfo> innerHash = dbHashMap.getOrDefault(customer_id,new HashMap<Long, AccountInfo>());

        innerHash.put(account_number,accountInfo);
        dbHashMap.put(customer_id, innerHash);
    }
    public HashMap<Long, AccountInfo> output(int id) {
        HashMap<Long, AccountInfo> hashMap = dbHashMap.get(id);
        return hashMap;
    }
    public HashMap<Long, AccountInfo> getAccountInfo(int customerId){
        return dbHashMap.get(customerId);
    }


    public void getDbHashMapAll(long account_number,AccountInfo accountInfo,int customer_id){
        HashMap<Long, AccountInfo> innerHashAll = dbHashMapAll.getOrDefault(customer_id,new HashMap<Long, AccountInfo>());

        innerHashAll.put(account_number,accountInfo);
        dbHashMapAll.put(customer_id, innerHashAll);
    }
    public HashMap<Long, AccountInfo> allAcccountData(int id){

        HashMap<Long, AccountInfo> allHashMap = dbHashMapAll.get(id);

        return allHashMap;
    }

}

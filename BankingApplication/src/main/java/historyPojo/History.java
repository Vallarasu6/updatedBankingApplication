package historyPojo;

public class History {
	private int id;
	private long accountNumber;
	private String process;
	private long amount;
	private String date;
	private long bankCharges;
	private long updatedBalance;
	
	
	
	public long getUpdatedBalance() {
		return updatedBalance;
	}
	public void setUpdatedBalance(long updatedBalance) {
		this.updatedBalance = updatedBalance;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public long getBankCharges() {
		return bankCharges;
	}
	public void setBankCharges(long bankCharges) {
		this.bankCharges = bankCharges;
	}
	@Override
	public String toString() {
		return "History [id=" + id + ", accountNumber=" + accountNumber + ", process=" + process + ", amount=" + amount
				+ ", date=" + date + ", bankCharges=" + bankCharges + ", updatedBalance=" + updatedBalance + "]";
	}
	
	
	
	
}

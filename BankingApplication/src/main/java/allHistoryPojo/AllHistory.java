package allHistoryPojo;

public class AllHistory {
	private String process;
	private long amount;
	private long bankCharges;
	private long updatedBalance;
	private String date;
	
	
	public long getUpdatedBalance() {
		return updatedBalance;
	}
	public void setUpdatedBalance(long updatedBalance) {
		this.updatedBalance = updatedBalance;
	}
	public long getBankCharges() {
		return bankCharges;
	}
	public void setBankCharges(long bankCharges) {
		this.bankCharges = bankCharges;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process= process;
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
	@Override
	public String toString() {
		return "AllHistory [process=" + process + ", amount=" + amount + ", bankCharges=" + bankCharges
				+ ", updatedBalance=" + updatedBalance + ", date=" + date + "]";
	}
	
	
	

}

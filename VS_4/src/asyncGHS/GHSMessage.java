package asyncGHS;

enum GHSMessage {
	INIT("000"),
	REPORT("001"),
	TEST("010"),
	ACCEPT("011"),
	REJECT("100"),
	CHANGE_ROOT("101"),
	CONNECT("110");
	
	
	private byte[] message;
	
	GHSMessage(String s){
		this.message=s.getBytes();
	}
	
	byte[] get(){
		return this.message;
	}
}

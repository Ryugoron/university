package asyncGHS;

enum GHSMessage {
	INIT("0000"),
	REPORT("0001"),
	TEST("0010"),
	ACCEPT("0011"),
	REJECT("0100"),
	CHANGE_ROOT("0101"),
	CONNECT("0110"),
	READY("0111"),
	ABSORB("1000"),
	MERGE("1001"),
	NOMWOE("1010"),
	END("1011");
	
	
	private String message;
	
	GHSMessage(String s){
		this.message=s;
	}
	
	String get(){
		return this.message;
	}
}

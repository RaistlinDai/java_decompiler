package test.reader;

public class ftdTest {

	private String selfaaa = new String();

	public ftdTest() {
		this.selfaaa = "No input";
	}

	public ftdTest(String inputaaa) {

		this.selfaaa = inputaaa;
	}

	public String getFtdTest(String input01, Integer input02) {
		System.out.print("The Input params: " + input01 + " and " + input02.toString());
		return this.selfaaa;
	}

	public String getSelfAAA() {
		System.out.print("The selfaaa: " + this.selfaaa);
		return this.selfaaa;
	}
}
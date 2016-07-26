package jp.go.nict.rasc.parser.api;

import jp.go.nict.langrid.commons.rpc.intf.Field;

import org.msgpack.annotation.Message;

@Message
public class Doc {
	@Field(order = 1)	
	private String input;

	public Doc() {
	}

	public Doc(String input) {
		this.input = input;
	}
	
	public final String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
}

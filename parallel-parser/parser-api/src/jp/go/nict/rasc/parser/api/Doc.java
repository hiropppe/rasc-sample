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

	@Override
	public int hashCode() {
		return input.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Doc other = (Doc) obj;
		return other.input.equals(this.input);
	}
}

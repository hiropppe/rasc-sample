package jp.go.nict.rasc.stdio.api;

import org.msgpack.annotation.Message;

import jp.go.nict.langrid.commons.rpc.intf.Field;

@Message
public class StdOut implements Comparable<StdOut> {
	@Field(order = 1)
	private StdIn input;
	@Field(order = 2)
	private String value;

	public StdOut() {
	}

	public StdOut(StdIn input, String value) {
		this.input = input;
		this.value = value;
	}

	public StdIn getInput() {
		return input;
	}
	
	public void setInput(StdIn input) {
		this.input = input;
	}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		if(input == null) {
			return super.hashCode();
		}
		return input.getSeq();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StdOut other = (StdOut) obj;
		if (input == null || other.input == null) {
			return false;
		} else {
			return other.input.getSeq() == this.input.getSeq();
		}
	}

	@Override
	public int compareTo(StdOut o) {
		return this.input.getSeq() - o.input.getSeq();
	}
}

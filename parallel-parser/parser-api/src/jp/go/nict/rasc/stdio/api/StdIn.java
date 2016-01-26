package jp.go.nict.rasc.stdio.api;

import jp.go.nict.langrid.commons.rpc.intf.Field;

import org.msgpack.annotation.Message;

@Message
public class StdIn {
	@Field(order = 1)
	private int seq;
	@Field(order = 2)
	private String value;

	public StdIn() {
	}

	public StdIn(int seq, String value) {
		this.seq = seq;
		this.value = value;
	}
	
	public int getSeq() {
		return seq;
	}
	
	public void setSeq(int seq) {
		this.seq = seq;
	}

	public final String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return seq;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StdIn other = (StdIn) obj;
		return other.seq == this.seq;
	}
}

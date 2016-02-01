package jp.go.nict.rasc.parser.api;

import jp.go.nict.langrid.commons.rpc.intf.Field;

import org.msgpack.annotation.Message;

@Message
public class Doc {
	@Field(order = 1)
	private int seq;
	@Field(order = 2)
	private String sent;

	public Doc() {
	}

	public Doc(int seq, String sent) {
		this.seq = seq;
		this.sent = sent;
	}
	
	public int getSeq() {
		return seq;
	}
	
	public void setSeq(int seq) {
		this.seq = seq;
	}

	public final String getSent() {
		return sent;
	}

	public void setSent(String sent) {
		this.sent = sent;
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
		Doc other = (Doc) obj;
		return other.seq == this.seq;
	}
}

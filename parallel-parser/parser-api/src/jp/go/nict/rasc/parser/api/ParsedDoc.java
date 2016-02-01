package jp.go.nict.rasc.parser.api;

import org.msgpack.annotation.Message;

import jp.go.nict.langrid.commons.rpc.intf.Field;

@Message
public class ParsedDoc implements Comparable<ParsedDoc> {
	@Field(order = 1)
	private Doc input;
	@Field(order = 2)
	private String sent;

	public ParsedDoc() {
	}

	public ParsedDoc(Doc input, String sent) {
		this.input = input;
		this.sent = sent;
	}

	public Doc getInput() {
		return input;
	}
	
	public void setInput(Doc input) {
		this.input = input;
	}

	public String getSent() {
		return sent;
	}
	
	public void setSent(String sent) {
		this.sent = sent;
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
		ParsedDoc other = (ParsedDoc) obj;
		if (input == null || other.input == null) {
			return false;
		} else {
			return other.input.getSeq() == this.input.getSeq();
		}
	}

	@Override
	public int compareTo(ParsedDoc o) {
		return this.input.getSeq() - o.input.getSeq();
	}
}

package jp.go.nict.rasc.parser.api;

import org.msgpack.annotation.Message;

import jp.go.nict.langrid.commons.rpc.intf.Field;

@Message
public class ParsedDoc {
	@Field(order = 1)
	private Doc doc;
	@Field(order = 2)
	private String result;

	public ParsedDoc() {
	}

	public ParsedDoc(Doc doc, String result) {
		this.doc = doc;
		this.result = result;
	}

	public Doc getDoc() {
		return doc;
	}
	
	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public int hashCode() {
		if(doc == null) {
			return super.hashCode();
		} else {
			return doc.hashCode();
		}
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
		if (doc == null || other.doc == null || result == null || other.result == null) {
			return false;
		} else {
			return other.doc.equals(this.doc) && other.result.equals(this.result);
		}
	}
}

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
}

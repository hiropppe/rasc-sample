package jp.go.nict.rasc.parser.worker;

import jp.go.nict.isp.wrapper.wisdom.abstractservice.ResourceApiWrapperBase;
import jp.go.nict.isp.wrapper.wisdom.abstractservice.ResourceApiWrapperReceiver;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.rasc.parser.api.Doc;
import jp.go.nict.rasc.parser.api.ParsedDoc;
import jp.go.nict.wisdom.wrapper.StdIOCommandParallelArrayService;
import jp.go.nict.wisdom.wrapper.StdIOCommandService;

public class ParserResourceApiWrapper extends ResourceApiWrapperBase {

	private static final ParserResourceApiWrapper instance = new ParserResourceApiWrapper();
	
	private StdIOCommandService service;
	
	public static ParserResourceApiWrapper getInstance() {
		return instance;
	}
	
	public ParserResourceApiWrapper() {
		service = new StdIOCommandParallelArrayService();
		service.setCmdLine("/usr/local/bin/tkp");
		service.setDelimiterIn("EOD\\n");
		service.setDelimiterOut("EOD\\n");
		service.setDelLastNewline(true);
		service.setPoolSize(2);
		service.setInitPoolSize(2);
        // service.setTimeOut(180000);
		service.init();
	}

	public void invokeApi(Doc[] in, ResourceApiWrapperReceiver<ParsedDoc> receiver) throws ProcessFailedException {
		String[] input = new String[in.length];
		for(int i=0; i<in.length; i++) {
			input[i] = in[i].getInput();
		}
		
		String[] results;
		try {
			results = service.analyzeArray(input);
		} catch (Exception e) {
			throw new ProcessFailedException(e);
		}
		
		for(int i=0; i<results.length; i++) {
			receiver.receiveNotify(new ParsedDoc(in[i], results[i]));
		}
	}

}

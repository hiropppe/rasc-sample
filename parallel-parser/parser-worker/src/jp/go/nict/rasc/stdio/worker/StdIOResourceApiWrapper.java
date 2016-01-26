package jp.go.nict.rasc.stdio.worker;

import jp.go.nict.isp.wrapper.wisdom.abstractservice.ResourceApiWrapperBase;
import jp.go.nict.isp.wrapper.wisdom.abstractservice.ResourceApiWrapperReceiver;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.rasc.stdio.api.StdIn;
import jp.go.nict.rasc.stdio.api.StdOut;
import jp.go.nict.wisdom.wrapper.StdIOCommandParallelArrayService;
import jp.go.nict.wisdom.wrapper.StdIOCommandService;

public class StdIOResourceApiWrapper extends ResourceApiWrapperBase {

	private static final StdIOResourceApiWrapper instance = new StdIOResourceApiWrapper();
	
	private StdIOCommandService service;
	
	public static StdIOResourceApiWrapper getInstance() {
		return instance;
	}
	
	public StdIOResourceApiWrapper() {
//		service = new StdIOCommandService();
//		service.setCmdLine("/home/hadoop/nict/rasc/run_knp.sh");
//		service.setDelimiterIn("\\n");
//		service.setDelimiterOut("EOS\\n");
//		service.setDelLastNewline(true);
//		service.init();
		
		service = new StdIOCommandParallelArrayService();
		service.setCmdLine("/home/hadoop/nict/rasc/run_knp.sh");
		service.setDelimiterIn("\\n");
		service.setDelimiterOut("EOS\\n");
		service.setDelLastNewline(true);
		service.setPoolSize(4);
		service.setInitPoolSize(4);
		service.init();
	}

	public void invokeApi(StdIn[] in, ResourceApiWrapperReceiver<StdOut> receiver) throws ProcessFailedException {
		String[] text = new String[in.length];
		for(int i=0; i<in.length; i++) {
			text[i] = in[i].getValue();
		}
		
		String[] results;
		try {
			results = service.analyzeArray(text);
		} catch (Exception e) {
			throw new ProcessFailedException(e);
		}
		
		for(int i=0; i<results.length; i++) {
			receiver.receiveNotify(new StdOut(in[i], results[i]));
		}
	}

}

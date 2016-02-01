package jp.go.nict.rasc.parser.worker;

import jp.go.nict.isp.wrapper.wisdom.abstractservice.AbstractWorkerModule;
import jp.go.nict.isp.wrapper.wisdom.abstractservice.AbstractWorkerModuleBase;
import jp.go.nict.isp.wrapper.wisdom.abstractservice.ResourceApiWrapperReceiver;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.rasc.parser.api.ParserService;
import jp.go.nict.rasc.parser.api.Doc;
import jp.go.nict.rasc.parser.api.ParsedDoc;

public class ParserWorker extends AbstractWorkerModuleBase implements ParserService {
		
	@Override
	public ParsedDoc[] process(final Doc[] in) throws ProcessFailedException {
		AbstractWorkerModule<ParsedDoc, ParserResourceApiWrapper> worker =
			new AbstractWorkerModule<ParsedDoc, ParserResourceApiWrapper>(
				ParserResourceApiWrapper.getInstance(), new ParsedDoc[] {}, "ParserService", this
			) {
				@Override
				protected void callResourceApi(ParserResourceApiWrapper wrapper,
						ResourceApiWrapperReceiver<ParsedDoc> receiver) throws ProcessFailedException {
					wrapper.invokeApi(in, receiver);
				}
			};
		return worker.getResult();
	}
	
}

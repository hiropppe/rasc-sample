package jp.go.nict.rasc.stdio.worker;

import jp.go.nict.isp.wrapper.wisdom.abstractservice.AbstractWorkerModule;
import jp.go.nict.isp.wrapper.wisdom.abstractservice.AbstractWorkerModuleBase;
import jp.go.nict.isp.wrapper.wisdom.abstractservice.ResourceApiWrapperReceiver;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.rasc.stdio.api.StdIOService;
import jp.go.nict.rasc.stdio.api.StdIn;
import jp.go.nict.rasc.stdio.api.StdOut;

public class StdIOServiceWorker extends AbstractWorkerModuleBase implements StdIOService {
		
	@Override
	public StdOut[] process(final StdIn[] in) throws ProcessFailedException {
		AbstractWorkerModule<StdOut, StdIOResourceApiWrapper> worker =
			new AbstractWorkerModule<StdOut, StdIOResourceApiWrapper>(
				StdIOResourceApiWrapper.getInstance(), new StdOut[] {}, "StdIOService", this
			) {
				@Override
				protected void callResourceApi(StdIOResourceApiWrapper wrapper,
						ResourceApiWrapperReceiver<StdOut> receiver) throws ProcessFailedException {
					wrapper.invokeApi(in, receiver);
				}
			};
		return worker.getResult();
	}
	
}

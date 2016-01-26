package jp.go.nict.rasc.stdio.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import jp.go.nict.isp.wrapper.wisdom.abstractservice.AbstractServerModule;
import jp.go.nict.isp.wrapper.wisdom.abstractservice.AbstractServerModuleBase;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.rasc.stdio.api.StdIOService;
import jp.go.nict.rasc.stdio.api.StdIn;
import jp.go.nict.rasc.stdio.api.StdOut;

public class StdIOServiceServer extends AbstractServerModuleBase implements StdIOService {

	@Override
	public StdOut[] process(final StdIn[] in) throws ProcessFailedException {
		List<String> endpoints = getEndpointList();
		Map<Integer,List<StdIn>> args = new HashMap<Integer,List<StdIn>>(endpoints.size());
		for(int i=0; i<in.length; i++) {
			int epindex = i%endpoints.size();
			List<StdIn> epdata = args.get(epindex);
			if(epdata == null) {
				epdata = new ArrayList<StdIn>();
				args.put(epindex, epdata);
			}
			epdata.add(in[i]);
		}

		final BlockingQueue<StdIn[]> queue = new ArrayBlockingQueue<>(in.length, false);
		for(List<StdIn> eachArg: args.values()) {
			queue.add(eachArg.toArray(new StdIn[]{}));
		}

		AbstractServerModule<StdOut, StdIOService> server =
			new AbstractServerModule<StdOut, StdIOService>(
				StdIOService.class, "StdIOService", new StdOut[]{}, this
			) {
			@Override
			public StdOut[] sendRequest(StdIOService service) throws ProcessFailedException {
				if(queue.isEmpty()) {
					return new StdOut[]{};
				}
				try {
					return service.process(queue.take());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new StdOut[]{};
				}
			}	
		};
		List<StdOut> ret = Arrays.asList(server.getResult());
		Collections.<StdOut>sort(ret);
		return ret.toArray(new StdOut[]{});
	}

}

package jp.go.nict.rasc.parser.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import jp.go.nict.isp.wrapper.wisdom.abstractservice.AbstractServerModule;
import jp.go.nict.isp.wrapper.wisdom.abstractservice.AbstractServerModuleBase;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.rasc.parser.api.ParserService;
import jp.go.nict.rasc.parser.api.Doc;
import jp.go.nict.rasc.parser.api.ParsedDoc;

public class ParserServer extends AbstractServerModuleBase implements ParserService {

	@Override
	public ParsedDoc[] process(final Doc[] in) throws ProcessFailedException {
		List<String> endpoints = getEndpointList();
		Map<Integer,List<Doc>> args = new HashMap<Integer,List<Doc>>(endpoints.size());
		for(int i=0; i<in.length; i++) {
			int epindex = i%endpoints.size();
			List<Doc> epdata = args.get(epindex);
			if(epdata == null) {
				epdata = new ArrayList<Doc>();
				args.put(epindex, epdata);
			}
			epdata.add(in[i]);
		}

		final BlockingQueue<Doc[]> queue = new ArrayBlockingQueue<>(in.length, false);
		for(List<Doc> eachArg: args.values()) {
			queue.add(eachArg.toArray(new Doc[]{}));
		}

		AbstractServerModule<ParsedDoc, ParserService> server =
			new AbstractServerModule<ParsedDoc, ParserService>(
				ParserService.class, "ParserService", new ParsedDoc[]{}, this
			) {
			@Override
			public ParsedDoc[] sendRequest(ParserService service) throws ProcessFailedException {
				if(queue.isEmpty()) {
					return new ParsedDoc[]{};
				}
				try {
					return service.process(queue.take());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new ParsedDoc[]{};
				}
			}	
		};		
		return server.getResult();
	}

}

package jp.go.nict.rasc.parser.api;

import jp.go.nict.langrid.service_1_2.ProcessFailedException;

public interface ParserService {
	ParsedDoc[] process(Doc[] in) throws ProcessFailedException;
}

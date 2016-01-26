package jp.go.nict.rasc.stdio.api;

import jp.go.nict.langrid.service_1_2.ProcessFailedException;

public interface StdIOService {
	StdOut[] process(StdIn[] in) throws ProcessFailedException;
}

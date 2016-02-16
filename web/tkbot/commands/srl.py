# -*- coding:utf-8 -*-

from __future__ import print_function
from w3lib.url import is_url

from scrapy.commands import ScrapyCommand
from scrapy.http import Request
from scrapy.exceptions import UsageError
from scrapy.utils.spider import spidercls_for_request, DefaultSpider

import re, mojimoji
import extractcontent, nltk
import json, msgpackrpc, requests

class SRL(ScrapyCommand):

  requires_project = False

  def __init__(self):
    self.sent_tokenizer = nltk.RegexpTokenizer(u'[\r\n　！？。]', gaps=True)
    
    self.extractor = extractcontent.ExtractContent()
    self.extractor.set_option({'threshold': 0})
    
    self.re_slash_date = re.compile(ur'(20\d{2})[-/](\d{,2})[-/](\d{,2})')
    self.re_day_of_week = re.compile(ur'（[月火水木金土日祝・]{,3}）')

  def syntax(self):
    return "[options] <url>"

  def short_desc(self):
    return "Extract Semantic Role Label for each sentence in fetched content"

  def add_options(self, parser):
    ScrapyCommand.add_options(self, parser)
    parser.add_option("--spider", dest="spider", help="use this spider")
    parser.add_option("--headers", dest="headers", action="store_true", \
        help="print response HTTP headers instead of body")

  def _print_headers(self, headers, prefix):
    for key, values in headers.items():
      for value in values:
        print('%s %s: %s' % (prefix, key, value))

  def _print_response(self, response, opts):
    encoding = response.encoding
    self.extractor.analyse(response.body.decode(response.encoding))
    
    text, title = self.extractor.as_text()
    title = title.strip()
    print(u'[タイトル]\n%s' % title)
    #self._parse_remote([title])
    
    text = text.strip()
    print(u'[本文]\n%s' % text)
    sents = [sent for sent in self.sent_tokenizer.tokenize(text)]
    
    print(u'[解析結果]')
    if self.parser_server == 'rasc_distrib':
      self._call_distrib(sents)
    else:
      self._call_standalone(sents)
      
  def _call_distrib(self, sents):
    headers = {'content-type': 'application/json-rpc'}
    params = {
      'method': 'process',
      'params': [[{'seq': i, 'sent': self._parse_pre_process(s)} for i, s in enumerate(sents)]]
    }

    response = requests.post(self.parser_jsonrpc_url, data=json.dumps(params), headers=headers).json()
    for i, each_result in enumerate(response['result']):
      for l in each_result['sent'].split('\n'):
        print(l)

  def _call_standalone(self, sents):
    client = msgpackrpc.Client( 
      msgpackrpc.Address(self.parser_msgpack_host, self.parser_msgpack_port), 
      timeout=180 
    )
     
    sents = [self._parse_pre_process(s) for s in sents]
    
    result = client.call('analyzeArray', sents)
    for i, each_result in enumerate(result):
      print(each_result)

  # サーバ側のJUMANの前処理に追加したい
  def _parse_pre_process(self, raw):
    s = self.re_slash_date.sub(ur'\1年\2月\3日', raw)
    s = mojimoji.han_to_zen(s)
    s = self.re_day_of_week.sub('', s)
    s = s.replace(u'\uff5e', u'から').replace(u'\u301c', u'から')
    return s
  
  def run(self, args, opts):
    ## NLP parser settings
    self.parser_server = self.settings['PARSER_RPC_SERVER']
    # For standalone rpc server (msgpack-rpc)
    self.parser_msgpack_host = self.settings['PARSER_MSGPACK_HOST']
    self.parser_msgpack_port = self.settings['PARSER_MSGPACK_PORT']
    # For distributed rpc server (json-rpc)
    self.parser_jsonrpc_url  = self.settings['PARSER_PROXY_URL'] 
    
    if len(args) != 1 or not is_url(args[0]):
      raise UsageError()
    
    cb = lambda x: self._print_response(x, opts)
    request = Request(args[0], callback=cb, dont_filter=True)
    request.meta['handle_httpstatus_all'] = True

    spidercls = DefaultSpider
    spider_loader = self.crawler_process.spider_loader
    if opts.spider:
      spidercls = spider_loader.load(opts.spider)
    else:
      spidercls = spidercls_for_request(spider_loader, request, spidercls)
    self.crawler_process.crawl(spidercls, start_requests=lambda: [request])
    self.crawler_process.start()

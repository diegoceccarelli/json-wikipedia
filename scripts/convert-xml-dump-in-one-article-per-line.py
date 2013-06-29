#!/usr/bin/env python

from optparse import OptionParser 
import codecs

parser = OptionParser()
parser.add_option("-i", "--input", dest="input", help="xml dump", metavar="FILE")
parser.add_option("-o", "--output", dest="output", help="xml dump, one line per article", metavar="FILE")


(options, args) = parser.parse_args()
if options.input == None or options.output == None:
	parser.error("./convert-xml-dump-in-one-article-per-line.py -i input.xml -o output.xml")


i = codecs.open(options.input, 'r', encoding='utf-8')
o = codecs.open(options.output, 'w', encoding='utf-8')

for line in i:
	line = line.strip()
	line = line.replace("<page>","\n<page>");
	o.write(line);

o.close();
	





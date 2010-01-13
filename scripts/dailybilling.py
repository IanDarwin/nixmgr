#!/usr/bin/env python

# Input file: /var/log/cups/page_log

# Input format:
# hpmfc ian 2 [13/Dec/2009:21:40:50 -0500] 1 1 - localhost

import re

from ChargeForPages import ChargeForPages

accountant = ChargeForPages()

PRINTER=0
USER=1
JOBID=2
# split just on words...
PAGES=11
HOST=13

pages={}

for l in open("/var/log/cups/page_log"):
	line = l[:-1] # trim
	tmp = re.split(r'\W+', line)
	username = tmp[USER]
	numpages = int(tmp[PAGES])
	n = int(pages.setdefault(username, 0))
	n += numpages
	pages[username] = n

for (u,qty) in pages.items():
	try:
		accountant.billUserForPages(u, qty)
	except KeyError:
		print "Failed to account for %d pages for %s" % (qty,u)

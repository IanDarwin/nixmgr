#!/usr/bin/env python

''' CUPS filter for accounting, based loosely on accsnmp,
    see http://fritz.potsdam.edu/projects/cupsapps/#accsnmp
'''

import sys
import os
import re

prefix="usermgmt"		# used in device URL

# To find out if your printer supportss  the needed MIB:
# snmpget -v 1 -c 'public' ianprint 1.3.6.1.2.1.43.10.2.1.4.1.1
LIFEPAGECOUNT_OID = "1.3.6.1.2.1.43.10.2.1.4.1.1";	#printer lifetime pagecount
PRINTERSTATUS_OID = "1.3.6.1.2.1.25.3.5.1.1.1";		#printer status

cupsBackendDir = "/usr/lib/cups/backend"

def validateUser(userName):
	print "Validating"

def printerJobPages():
	return 1

def copyFile(f1, f2):
	f2.write(f1.read())

def	billUser(userName, pages):
	print "Billing user %s for %d pages" % (userName, pages)

def	main():
	args = sys.argv[1:] # minus progname
	nargs = len(args)
	if nargs == 0:
		# CUPS calls with no args to discover what we do
		print("network %s \"Unknown\" \"Accounted Printer (SNMP)\"" % prefix)
		sys.exit(0)
	if nargs < 5 or nargs > 6:
		print("Usage: %s jobid username jobtitle copies printopts [file]" % sys.argv[0])
		sys.exit(1)

	if nargs == 5:
		(jobId,userName,jobTitle,copies,printOpts) = args
		file = sys.stdin
	if nargs == 6:
		(jobId,userName,jobTitle,copies,printOpts,fileName) = args
		file = open(fileName, 'r')

	# Grab the device id from the environment, make sure it's for us, strip
	# off our prefix, and return the result to the environment.

	try:
		devURI = os.environ["DEVICE_URI"];
	except KeyError:
		print "No DEVICE_URI in environment!"
		# sys.exit(1)
		devURI = prefix + "://foo/printer1"

	m = re.match("%s://(.*)/(.*)"%prefix, devURI);
	if m == None:
		print "ERROR: No match"
		sys.exit(1)
	newDevUri = "//" + m.group(1) + "//" + m.group(2)
	print newDevUri
	os.environ["DEVICE_URI"] = newDevUri
	
	validateUser(userName)

	n = printerJobPages()

	copyFile(file, sys.stdout)

	n2 = printerJobPages()

	billUser(userName, n2 - n)

	print "All done"

	sys.exit(0)

if __name__ == '__main__':
	main()

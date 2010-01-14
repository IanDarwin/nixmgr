#!/usr/bin/env python

''' CUPS filter for accounting for SNMP-managed networked printers,
	learned how to do some of these things from the excellent (but Perl-based)
	see http://fritz.potsdam.edu/projects/cupsapps/#accsnmp.
	My version uses our account database instead of text files.
'''

# TODO
# retry loop

# later: get SNMP actually working XXX for now daily*.py does the actual billing!

import sys
import os
import re
from subprocess import Popen

from ChargeForPages import ChargeForPages

debuglog=open('/tmp/printlog', 'w')	# XXX TEMPORARY

prefix="usermgmt"		# used in device URL

# To find out if your printer supports the needed MIB:
# snmpget -v 1 -c 'public' ianprint 1.3.6.1.2.1.43.10.2.1.4.1.1
LIFEPAGECOUNT_OID = "1.3.6.1.2.1.43.10.2.1.4.1.1";	# pages since printer manufactured
PRINTERSTATUS_OID = "1.3.6.1.2.1.25.3.5.1.1.1";		# current printer status

cupsBackendDir = "/usr/local/libexec/cups/backend"	# Somewhat OS-specific

accountant = ChargeForPages()

def validateUser(userName):
	try:
		creditBal = accountant.getCurrentPageCredits(userName)
	except KeyError:
		sys.stderr.write("ERROR: could not get balance for %s\n" % userName)
		sys.exit(1)
	print "Current credit for %s is %d" % (userName, creditBal)
	if creditBal < 1:
		sys.stderr.write("ERROR: User %s has page credit of %d\n" % (userName, creditBal))
		sys.exit(1)

def printerJobPages():
	return 1

def copyFile(inFile):
	# build new argv array: drop fileName, and change argv[0] to real back end
	newArgs = sys.argv[:6]
	newArgs[0] = cupsBackendDir + '/' + realBackEnd;
	print >>debuglog, "ARGS:", newArgs
	try:
		proc = Popen(newArgs,
			close_fds=True,	# security, but assume ENV already sanitized
			stdin=inFile)
	except OSError, e:
		sys.stderr.write("ERROR: Could not invoke %s due to error %s\n" % (newArgs[0], e))
		sys.exit(1)
	sys.stderr.write("INFO: Trying to send to printer\n")
	print newArgs
	ret = proc.wait()
	if ret != 0:
		sys.stderr.write("ERROR: Failure in %s backend, ret %d\n" % (realBackEnd,ret))
		sys.exit(1)

def	billUser(userName, pages):
	print "Billing user %s for %d pages" % (userName, pages)
	accountant.billUserForPages(userName, pages)

def	main():
	args = sys.argv[1:] # minus progname
	nargs = len(args)
	if nargs == 0:
		# CUPS calls with no args to discover what we do
		print("network %s \"Unknown\" \"Accounted Printer (SNMP)\"" % prefix)
		sys.exit(0)
	if nargs < 5 or nargs > 6:
		sys.stderr.write("ERROR: Usage: %s jobid username jobtitle copies printopts [file]\n" % sys.argv[0])
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
		sys.stderr.write("ERROR: No DEVICE_URI in environment!\n")
		sys.exit(1)

	# e.g.,  usermgmt://ipp://192.168.100.100
	m = re.match(r'%s://([^/]+)://(.*)'%prefix, devURI);
	if m == None:
		sys.stderr.write("ERROR: Could not parse DEVICE_URI\n")
		sys.exit(1)
	global realBackEnd
	realBackEnd = m.group(1)
	restOfDevice = m.group(2)
	newDevUri = realBackEnd + "://" + restOfDevice
	os.environ["DEVICE_URI"] = newDevUri

	print >>debuglog, 1, realBackEnd	# XXX TEMPORARY
	print >>debuglog, 2, restOfDevice
	print >>debuglog, 3, os.environ["DEVICE_URI"]
	
	validateUser(userName)

	n1 = printerJobPages()

	copyFile(file)

	n2 = printerJobPages()

	billUser(userName, n2 - n1)

	sys.exit(0)

if __name__ == '__main__':
	main()

debuglog.close()	# XXX TEMPORARY

#!/usr/bin/env python

''' CUPS filter for accounting for SNMP-managed networked printers,
	learned how to do some of these things from the excellent (but Perl-based)
	accsnmp, see http://fritz.potsdam.edu/projects/cupsapps/#accsnmp.
	My version uses the usermgmt account database instead of text files.
	MUST INSTALL as 'usermgmt' (minus the .py extension) in $l/libexec/cups/backend/
	Now bills only on the username, which must be in the passwd file.
'''

# TODO
# retry loop?

import os,re,sys,time
from subprocess import Popen,PIPE

from ChargeForPages import ChargeForPages

prefix="usermgmt"		# used in device URL

# To find out if your printer supports the needed MIB:
# snmpget -v 1 -c 'public' ianprint 1.3.6.1.2.1.43.10.2.1.4.1.1
LIFEPAGECOUNT_OID = "1.3.6.1.2.1.43.10.2.1.4.1.1"	# pages since printer manufactured
PRINTERSTATUS_OID = "1.3.6.1.2.1.25.3.5.1.1.1"		# current printer status

cupsBackendDir = "/usr/local/libexec/cups/backend"	# Somewhat OS-specific

accountant = ChargeForPages()

def validateUser(userName):
	try:
		creditBal = accountant.getCurrentPageCredits(userName)
	except KeyError:
		sys.stderr.write("ERROR: could not get balance for %s\n" % userName)
		sys.exit(1)
	# print "Current credit for %s is %d" % (userName, creditBal)
	if creditBal < 1:
		sys.stderr.write("ERROR: User %s has page credit of %d\n" % (userName, creditBal))
		sys.exit(1)

def snmpGet(ip, oid):
	''' send SNMP oid to printer in 'ip', return int result '''
	args = [ 'snmpget', '-O', 'v', '-v', '2c', '-c', 'public', ip, oid ]
	proc = Popen(args, stdout=PIPE)
	printout = proc.stdout.read()
	m=re.search(r'.*\D(\d+)', printout) # get last # on line
	if m == None:
		sys.stderr.write("ERROR: Could not parse SNMP output %s\n" % printout)
		sys.exit(1)
	return int(m.group(1))

def printerJobPages():
	return snmpGet(printerIP, LIFEPAGECOUNT_OID)

def printerStatus():
	return snmpGet(printerIP, PRINTERSTATUS_OID)

def waitForPrinterToFinish():
	time.sleep(1)
        waitCount = 0
        somethingPrinted = False
        while True:
                # 3 is idle, 4 is printing, 5 is warmup, 1 is other
                prStatus = printerStatus()
                if prStatus == 4:
                        sys.stderr.write("INFO: Printer status: printing\n")
                        somethingPrinted = True
                else:
			if prStatus != 4 and somethingPrinted:
				waitCount += 1
				# Wait until status is idle for several counts
				if waitCount == 3:
					sys.stderr.write("INFO: Job printed successfully\n")
					return
			else:
				sys.stderr.write("INFO: Printer status: other\n")
		time.sleep(2)

def copyFile(inFile):
	# build new argv array: drop fileName, and change argv[0] to real back end
	newArgs = sys.argv[:6]
	newArgs[0] = cupsBackendDir + '/' + realBackEnd
	try:
		proc = Popen(newArgs,
			close_fds=True,	# security, but assume ENV already sanitized
			stdin=inFile)
	except OSError, e:
		sys.stderr.write("ERROR: Could not invoke %s due to error %s\n" % (newArgs[0], e))
		sys.exit(1)
	sys.stderr.write("INFO: Trying to send to printer\n")
	# print newArgs
	ret = proc.wait()
	if ret != 0:
		sys.stderr.write("ERROR: Failure in %s backend, ret %d\n" % (realBackEnd,ret))
		sys.exit(1)

def	main():
	args = sys.argv[1:] # minus progname
	nargs = len(args)
	if nargs == 0:
		# CUPS calls with no args to discover what we do
		sys.stderr.write("network %s \"Unknown\" \"Accounted Printer (SNMP)\"" % prefix)
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
		devURI = os.environ["DEVICE_URI"]
	except KeyError:
		sys.stderr.write("ERROR: No DEVICE_URI in environment!\n")
		sys.exit(1)

	# e.g.,  usermgmt://ipp://192.168.100.100
	m = re.match(r'%s://([^/]+)://(.*)'%prefix, devURI)
	if m == None:
		sys.stderr.write("ERROR: Could not parse DEVICE_URI %s\n"%devURI)
		sys.exit(1)
	global realBackEnd
	realBackEnd = m.group(1)
	restOfDevice = m.group(2)
	newDevUri = realBackEnd + "://" + restOfDevice
	os.environ["DEVICE_URI"] = newDevUri
	m=re.match(r'([\d.]+)', restOfDevice)
	if m == None:
		sys.stderr.write("ERROR: Could not parse BACKEND part of %s\n"%devURI)
		sys.exit(1)
	global printerIP
	printerIP = m.group(1)

	validateUser(userName)

	n1 = printerJobPages()

	copyFile(file)

	waitForPrinterToFinish()

	n2 = printerJobPages()

	sys.stderr.write("INFO: Billing %s for %d - %d pages\n"%(userName,n2,n1))
	accountant.billUserForPages(userName, n2 - n1)

	sys.exit(0)

if __name__ == '__main__':
	main()

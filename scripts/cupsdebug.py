#!/usr/bin/env python

import os,re,sys,time
from syslog import openlog,syslog
prefix = "cups-debug"

openlog("cups-debug")
#syslog("Hello from cups-debug backend")
args = sys.argv[1:] # minus progname
nargs = len(args)
if nargs < 5 or nargs > 6:
	sys.stderr.write("ERROR: Usage: %s jobid username jobtitle copies printopts [file]\n" % sys.argv[0])
	sys.exit(1)
if nargs == 5:
	(jobId,userName,jobTitle,copies,printOpts) = args
	fileName = "sys.stdin"
if nargs == 6:
	(jobId,userName,jobTitle,copies,printOpts,fileName) = args

# Grab the device id from the environment, make sure it's for us, strip
# off our prefix, and return the result to the environment.

try:
	devURI = os.environ["DEVICE_URI"]
except KeyError:
	devURI = "(No env(DEVICE_URI))"

debug = \
    "jobId %s, userName %s, jobTitle %s, copies %s, printOpts %s, filename %s, devURI %s" % (jobId, userName, jobTitle, copies, printOpts, fileName, devURI)
sys.stderr.write(debug + "\n")
syslog(debug)

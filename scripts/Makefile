# Makefile for usermgmt/aux/

export:	cupsaccountfilter.pyc

install: cupsaccountfilter.pyc
	rm -f /tmp/printlog
	cp cups* Charge* /usr/local/libexec/cups/backend/
	mv /usr/local/libexec/cups/backend/{cups*.py,usermgmt}

cupsaccountfilter.pyc: cupsaccountfilter.py
	python -c 'import cupsaccountfilter' # make .pyc

test:	try
	./try

clean:
	rm -f *.pyc

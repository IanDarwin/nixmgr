# Makefile for usermgmt/aux/

export:	cupsaccountfilter.pyc

install: cupsaccountfilter.pyc
	cp cups* Charge* /usr/local/libexec/cups/backends/

cupsaccountfilter.pyc: cupsaccountfilter.py
	python -c 'import cupsaccountfilter' # make .pyc

test:	try
	./try

clean:
	rm -f *.pyc

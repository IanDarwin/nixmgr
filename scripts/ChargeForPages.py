#!/usr/bin/env python

import os

""" Bill a user for a given number of pages"""

username='ian'
pages=6

# for PostgreSQL
from psycopg import connect
connString = "dbname=usermgmt user=usermgr password=plugh32 host=localhost"

conn = connect(connString)

cursor = conn.cursor()

cursor.execute("select printcredits,printpagesused from account where username = '%s'" % username);
results = cursor.fetchall()
for row in results:
	print 'User %s has %d page credits, %d printpagesused' % (username, row[0], row[1])

printcredits = int(row[0])
printcredits = printcredits - pages

printpagesused = int(row[1])
printpagesused = printpagesused + pages

cursor.execute(
	"update account set printcredits = %d,printpagesused=%d where username = '%s'" %
	(printcredits, printpagesused, username))

conn.commit()

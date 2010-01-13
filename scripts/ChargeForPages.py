#!/usr/bin/env python

import os

class ChargeForPages:
	""" Bill a user for a given number of pages"""

	def __init__(self):
		# for PostgreSQL
		from psycopg import connect
		self.connString = "dbname=usermgmt user=usermgr password=plugh32 host=localhost"
		self.conn = connect(self.connString)

		self.cursor = self.conn.cursor()

	def getCurrentPageCredits(self, username):
		# returns printcredits
		self.cursor.execute("select printcredits from account where username = '%s'" % username)
		row = self.cursor.fetchone()
		if row is None:
			print "ERROR: cannot find balance for user %s" % username
			continue
		printcredits = int(row[0])
		return printcredits

	def billUserForPages(self, username, pages):
		printcredits = self.getCurrentPageCredits(username)

		printcredits = printcredits - pages

		self.cursor.execute(
			"update account set printcredits = %d where username = '%s'" %
			(printcredits, username))

		self.conn.commit()

		print "User %s successfully charged for %d pages" % (username, pages)

def main():
	accountant = ChargeForPages()
	testuser = 'ian'
	print "Current credit for",testuser,"is", accountant.getCurrentPageCredits(testuser)
	accountant.billUserForPages(testuser, 1)

	print "Current credit for",testuser,"is", accountant.getCurrentPageCredits(testuser)

if __name__ == '__main__':
	main()

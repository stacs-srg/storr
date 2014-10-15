#!/bin/sh

# This script is designed to check that a piece of software is installed.
# Arg1 should be the name of the software, eg "mvn".
# 'type' will check for the software, if not installed it will exit with an error code which is non zero
type $1 >/dev/null 2>&1 || { echo "I require " $1 " but it's not installed.  Aborting."; exit 1; }

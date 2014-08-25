#!/bin/sh

type $1 >/dev/null 2>&1 || { echo "I require " $1 " but it's not installed.  Aborting."; exit 1; }

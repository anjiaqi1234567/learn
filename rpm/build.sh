#!/bin/bash

basedir=$(dirname $(dirname $(readlink -f "${BASH_SOURCE[0]}")))
cd $basedir
git pull
#svn up
cd rpm/
/usr/local/bin/rpm_create blink-learn.spec -p /home/a -v 1.0.0 -r el5 -k
cd noarch
sudo rpm -e blink-learn
sudo rpm -ivh blink-learn*.rpm

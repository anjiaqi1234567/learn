#!/bin/bash

java -cp  /home/a/share/dam/apps/frequency-k2-dsp/conf/:\
/home/a/share/dam/apps/frequency-k2-dsp/lib/frequency-k2-dsp-1.0.0-SNAPSHOT-jar-with-dependencies.jar \
com.alimama.dam.framework.runner.Launcher -engine Blink \
-conf /home/a/share/dam/apps/frequency-k2-dsp/conf/frequency-k2-dsp-offline2.yaml \
-env daily > /home/a/share/dam/apps/frequency-k2-dsp/logs/init.log 2>&1 &

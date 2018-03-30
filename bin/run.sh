#!/bin/bash

export JAVA_HOME=/opt/taobao/java
#export HADOOP_HOME=/home/hadoop/hadoop_hbase/current/hadoop-client
export BLINK_HOME=/home/hadoop/hadoop_hbase/flink-1.3-blink-1.1.2
export HADOOP_CONF_DIR=/etc/hadoop/conf/

BIN=${BLINK_HOME}"/bin/flink"


function helpme()
{
     echo "ERROR Usage $0 [configfile] start"
     exit 1
}

if [ $# -lt 1 ] ; then
    helpme
fi

my_conf_file=$1
MODULE=$2
work_dir=`pwd`"/../"
my_class_dir=$work_dir/lib

for file in $my_class_dir/*.jar; do
	  if [[ $file = *jar-with-dependencies.jar ]]; then
		      runner_jar=$file
			    fi
			done

echo "$runner_jar"

while read LINE
do
    str=`echo $LINE`
    if [[ "$str" == "" ]] ; then
         continue
    fi
    if [[ "${str:0:1}" == "#" ]]  ; then
         continue
    fi

    head=`echo $str |awk -F: '{print $1}'`
    prefix=`echo $head`
    if [[ "${prefix}" == "job.name" ]] ; then
        myjob=`echo $str |awk -F: '{print $2}'`
        break
    fi
done < $my_conf_file

myjob=${myjob//\"/}
myjob=`echo $myjob`

echo   "JOB_NAME:$myjob"

if [[ $myjob == "" ]] ; then
 echo "$my_conf_file not found key:job.name"
 exit 1
fi

case $MODULE in
    "start")
        echo $BIN job run -m yarn -yqu ecpm $runner_jar -c com.andy.blink.KmonitorTest -engine Blink -conf $my_conf_file
        $BIN run -m yarn \
        -yqu ecpm \
        -c com.andy.blink.KmonitorTest $runner_jar -engine Blink -conf $my_conf_file \
    ;;
    *)
        helpme
    ;;
esac
exit 0
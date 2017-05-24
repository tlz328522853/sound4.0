#!/bin/sh
# -----------------------------------------------------------------------------
#

. ./setEnv.sh
export CLASSPATH=$CLASSPATH
$JAVA_HOME/bin/EnvCore -Xms256m -Xmx1024m -Djava.security.policy=java.policy -Djava.awt.headless=true -Dfile.encoding=GB2312 -DProcessPath.home=`pwd` "com.sdcloud.biz.envmapdata.service.main.SetupServiceProvider"
#!/bin/sh
# -----------------------------------------------------------------------------
#

. ./setEnv.sh
export CLASSPATH=$CLASSPATH
$JAVA_HOME/bin/EnvCache -Xms2048m -Xmx2048m -Djava.security.policy=java.policy -Djava.awt.headless=true -Dfile.encoding=GB2312 -DProcessPath.home=`pwd` "com.sdcloud.biz.cache.SetupServiceProvider" false

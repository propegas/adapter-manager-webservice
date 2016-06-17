#!/bin/sh
SERVICE_NAME=${adapterName}
PATH_TO_JAR=${adaptersDirectoryFullPath}/${adapterDirectory}/
JAR=${adapterFileName}
PID_PATH_NAME=/tmp/${adapterName}-pid
JAR_OPTIONS="${activemq_ip} ${activemq_port}"
case $1 in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
	    cd $PATH_TO_JAR
            nohup java -jar $JAR $JAR_OPTIONS 2>> ${adaptersDirectoryFullPath}/${adapterDirectory}/${errorLogFile} >> /dev/null &
                        echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            sleep 3
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            cd $PATH_TO_JAR
            nohup java -jar $JAR $JAR_OPTIONS 2>> ${adaptersDirectoryFullPath}/${adapterDirectory}/${errorLogFile} >> /dev/null &
                        echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    status)
        ps ax | grep -i "$JAR" | grep -v -i -E "metric|grep"
esac 

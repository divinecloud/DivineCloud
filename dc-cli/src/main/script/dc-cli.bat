IF "%DC_CLI_HOME%"=="" (
    ECHO Need to set DC_CLI_HOME
    EXIT /b 1
)

IF NOT EXIST %DC_CLI_HOME%\jre (
    IF "%JAVA_HOME%"=="" (
        ECHO Need to set JAVA_HOME
        EXIT /b 1
    )
    SET CLI_JAVA_HOME=%JAVA_HOME%
) ELSE (
    SET CLI_JAVA_HOME=%DC_CLI_HOME%/jre
)

ECHO %CLI_JAVA_HOME%
%CLI_JAVA_HOME%/bin/java -cp %DC_CLI_HOME%/lib/* com.dc.DivineCloudCli %*

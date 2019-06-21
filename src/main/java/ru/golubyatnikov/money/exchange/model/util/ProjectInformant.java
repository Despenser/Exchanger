package ru.golubyatnikov.money.exchange.model.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ProjectInformant {

    private Logger log;
    private Notification notification;

    public ProjectInformant(Class classForLog){
        log = LogManager.getLogger(classForLog);
        notification = Notification.getInstance();
    }

    public void logError(String message, Throwable throwable){
        log.error(message, throwable);
    }

    public void logInfo(String message){
        log.info(message);
    }

    public void logErrorAndShowNotification(String message, Throwable throwable){
        log.error(message, throwable);
        notification.warning(message);
    }

    public void logInfoAndShowNotificationWarning(String message){
        log.info(message);
        notification.warning(message);
    }

    public void logInfoAndShowNotificationComplete(String message){
        log.info(message);
        notification.complete(message);
    }
}
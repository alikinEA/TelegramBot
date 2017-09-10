package TelegramBotich.service.schedule;

import TelegramBotich.model.TelegramUpdates;
import TelegramBotich.service.ConfigService;
import TelegramBotich.service.Queue.RebootQueueService;
import TelegramBotich.service.healCheck.HealthCheckService;
import TelegramBotich.service.ssh.SSHDev;
import TelegramBotich.service.ssh.SSHService;
import TelegramBotich.service.ssh.SSHServiceDemo3;
import TelegramBotich.service.telegram.TelegramCheckUpdateService;
import TelegramBotich.service.telegram.TelegramSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Alikin E.A. on 23.06.17.
 */
@Service
@EnableScheduling
public class SchedulerBotReboot {

    @Autowired
    HealthCheckService healthCheckService;

    @Autowired
    TelegramCheckUpdateService checkUpdateService;

    @Autowired
    TelegramSenderService senderService;

    @Autowired
    RebootQueueService queueService;


    @Scheduled(fixedDelay = 2000)
    public void checkRebootQueue() {
        try {
            Optional<TelegramUpdates> item = queueService.get();
            if (item.isPresent()) {
                if (checkUpdateService.checkCommad(item.get(), ConfigService.REBOOT_DEMO3_COMMAND)) {
                    rebootDemo3(item.get());
                }
                if (checkUpdateService.checkCommad(item.get(), ConfigService.REBOOT_DEV_COMMAND)) {
                    rebootDev(item.get());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rebootDemo3(TelegramUpdates updates) {
        String user = checkUpdateService.getUserByCommad(updates, ConfigService.REBOOT_DEMO3_COMMAND);
        senderService.sendMessage("start reboot demo3(" + user + ")", ConfigService.REBOOTBOT_BOT_GROUP, ConfigService.REBOOTBOT_BOT_ID);
        SSHService sshService = new SSHServiceDemo3();
        Optional<Integer> javaDID = sshService.getJavaDID();
        try {
            if (javaDID.isPresent()) {
                sshService.getKillProcessByPid(javaDID.get());
                Thread.sleep(1000);
            }
            sshService.startWeblogic();
            senderService.sendMessage("success reboot demo3(" + user + ")", ConfigService.REBOOTBOT_BOT_GROUP, ConfigService.REBOOTBOT_BOT_ID);
        } catch (Exception e) {
            senderService.sendMessage("error reboot demo3(" + user + ") :" + e.getMessage(), ConfigService.REBOOTBOT_BOT_GROUP, ConfigService.REBOOTBOT_BOT_ID);
        }
    }

    private void rebootDev(TelegramUpdates updates) {
        queueService.getIsRun().set(true);
        String user = checkUpdateService.getUserByCommad(updates, ConfigService.REBOOT_DEV_COMMAND);
        senderService.sendMessage("start reboot dev(" + user + ")", ConfigService.REBOOTBOT_BOT_GROUP, ConfigService.REBOOTBOT_BOT_ID);
        SSHService sshService = new SSHDev();
        Optional<Integer> javaDID = sshService.getJavaDID();
        try {
            if (javaDID.isPresent()) {
                sshService.getKillProcessByPid(javaDID.get());
                Thread.sleep(1000);
            }
            sshService.startWeblogic();
            senderService.sendMessage("The application will be up in 5-10 minutes (" + user + ")", ConfigService.REBOOTBOT_BOT_GROUP, ConfigService.REBOOTBOT_BOT_ID);

            int i = 0;
            boolean isSuccess = false;
            while (!isSuccess && i < 60) {
                i++;
                Thread.sleep(10000);
                isSuccess = healthCheckService.checkEAISTHealth();
            }
            if (isSuccess) {
                senderService.sendMessage("Success reboot , The Application is available(" + user + ")", ConfigService.REBOOTBOT_BOT_GROUP, ConfigService.REBOOTBOT_BOT_ID);
            } else {
                senderService.sendMessage("Reboot error , the application is not available(" + user + ")", ConfigService.REBOOTBOT_BOT_GROUP, ConfigService.REBOOTBOT_BOT_ID);
            }
            queueService.getIsRun().set(false);
        } catch (Exception e) {
            queueService.getIsRun().set(false);
            senderService.sendMessage("error reboot dev(" + user + ") :" + e.getMessage(), ConfigService.REBOOTBOT_BOT_GROUP, ConfigService.REBOOTBOT_BOT_ID);
        }
    }

}

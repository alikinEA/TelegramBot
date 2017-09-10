package TelegramBotich.service.schedule;

import TelegramBotich.model.TelegramUpdates;
import TelegramBotich.service.ConfigService;
import TelegramBotich.service.Queue.RebootQueueService;
import TelegramBotich.service.telegram.TelegramCheckUpdateService;
import TelegramBotich.service.telegram.TelegramSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Alikin E.A. on 19.07.17.
 */
@Service
@EnableScheduling
public class SchedulerCheckTelegramUpdates {

    @Autowired
    TelegramCheckUpdateService checkUpdateService;

    @Autowired
    TelegramSenderService senderService;

    @Autowired
    RebootQueueService queueService;

    @Scheduled(fixedDelay = 5000)
    public void rebootScheduler() {
        try {
            Optional<TelegramUpdates> updates = checkUpdateService.getUpdates(ConfigService.REBOOTBOT_BOT_ID);
            if (updates.isPresent()) {
                if ((checkUpdateService.checkCommad(updates.get(), ConfigService.REBOOT_DEMO3_COMMAND)
                        || checkUpdateService.checkCommad(updates.get(), ConfigService.REBOOT_DEV_COMMAND))) {
                    if ((checkUpdateService.checkCommad(updates.get(), ConfigService.REBOOT_DEMO3_COMMAND)
                            || checkUpdateService.checkCommad(updates.get(), ConfigService.REBOOT_DEV_COMMAND)) && !queueService.getIsRun().get()) {
                        queueService.put(updates.get());
                    } else {
                        senderService.sendMessage("Reboot in progress", ConfigService.REBOOTBOT_BOT_GROUP, ConfigService.REBOOTBOT_BOT_ID);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

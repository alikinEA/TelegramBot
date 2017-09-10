package TelegramBotich.service.ssh;

import TelegramBotich.service.ConfigService;

/**
 * Created by Alikin E.A. on 25.06.17.
 */
public class SSHDev extends SSHService {

    @Override
    String getHost() {
        return ConfigService.DEV_HOST;
    }

    @Override
    String getLogin() {
        return ConfigService.DEV_LOGIN;
    }

    @Override
    String getPassword() {
        return ConfigService.DEV_PASS;
    }
}

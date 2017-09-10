package TelegramBotich.service.ssh;

import TelegramBotich.service.ConfigService;

/**
 * Created by Alikin E.A. on 25.06.17.
 */
public class SSHServiceDemo3 extends SSHService {

    @Override
    String getHost() {
        return ConfigService.DEMO3_HOST;
    }

    @Override
    String getLogin() {
        return ConfigService.DEMO3_LOGIN;
    }

    @Override
    String getPassword() {
        return ConfigService.DEMO3_PASS;
    }
}

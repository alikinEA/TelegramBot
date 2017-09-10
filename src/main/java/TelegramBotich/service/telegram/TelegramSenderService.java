package TelegramBotich.service.telegram;

import TelegramBotich.service.ConfigService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

/**
 * Created by Alikin E.A. on 01.05.17.
 */
@Service
public class TelegramSenderService {

    public void sendMessage(String message, Long chatId, String botId) {
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            try(CloseableHttpResponse response = client.execute(new HttpGet(ConfigService.TELEGRAM_URL + botId + "/sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(message,"UTF-8")))) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

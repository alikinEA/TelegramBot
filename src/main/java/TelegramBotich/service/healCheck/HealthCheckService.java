package TelegramBotich.service.healCheck;

import TelegramBotich.service.ConfigService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

/**
 * Created by Alikin E.A. on 14.07.17.
 */
@Service
public class HealthCheckService {

    public boolean checkEAISTHealth() {
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            try(CloseableHttpResponse response = client.execute(new HttpGet(ConfigService.HEATH_CHECK_URL))) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        return true;
                    } else {
                        return false;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

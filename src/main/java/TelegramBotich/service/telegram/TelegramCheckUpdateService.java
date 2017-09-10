package TelegramBotich.service.telegram;

import TelegramBotich.model.From;
import TelegramBotich.model.TelegramUpdates;
import TelegramBotich.service.ConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alikin E.A. on 23.06.17.
 */
@Service
public class TelegramCheckUpdateService {

    private final ObjectMapper mapper = new ObjectMapper();

    private final ConcurrentHashMap<String, Long> offsetMap = new ConcurrentHashMap<>();

    private Long getLastOffsetByBotId(String botId) {
        return offsetMap.get(botId);
    }

    public Optional<TelegramUpdates> getUpdates(String botId) {
        try {
            String resp = getUpdatesAndSaveOffset(botId, getLastOffsetByBotId(botId));
            saveOffset(resp, botId);
            return Optional.of(mapper.readValue(resp, TelegramUpdates.class));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void saveOffset(String resp, String botId) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(resp);
        JSONArray updateArr = (JSONArray) jsonObject.get("result");
        if (updateArr.size() > 0) {
            JSONObject lastUpdate = (JSONObject) updateArr.get(updateArr.size() - 1);
            Long offset = (Long) lastUpdate.get("update_id");
            offsetMap.put(botId, offset + 1);
        }

    }

    private String getUpdatesAndSaveOffset(String botId, Long offset) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = client.execute(new HttpGet(ConfigService.TELEGRAM_URL + botId + "/getUpdates?offset=" + offset))){
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new RuntimeException();
        }
        return "";
    }

    public boolean checkCommad(TelegramUpdates telegramUpdates, String command) {
        return telegramUpdates.getResult().stream().anyMatch(item -> item.getMessage().getText().contains(command) && !item.getMessage().getText().startsWith("/"));
    }

    public String getUserByCommad(TelegramUpdates telegramUpdates, String command) {
        From from = telegramUpdates.getResult().stream()
                .filter(item -> item.getMessage()
                        .getText()
                        .contains(command) && !item.getMessage().getText().startsWith("/"))
                .findFirst().get()
                .getMessage()
                .getFrom();
        if (from.getUsername() != null) {
            return from.getUsername();
        } else if (from.getFirstName() != null) {
            return from.getFirstName();
        } else {
            return "НекийАнонимус";
        }
    }
}

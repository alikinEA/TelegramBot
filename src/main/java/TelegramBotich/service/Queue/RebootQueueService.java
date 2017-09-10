package TelegramBotich.service.Queue;

import TelegramBotich.model.TelegramUpdates;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Alikin E.A. on 17.07.17.
 */
@Service
public class RebootQueueService {

    public AtomicBoolean getIsRun() {
        return isRun;
    }

    private AtomicBoolean isRun = new AtomicBoolean(false);

    private final ConcurrentLinkedQueue<TelegramUpdates> queue = new ConcurrentLinkedQueue();

    public void put(TelegramUpdates telegramUpdates) {
        queue.offer(telegramUpdates);
    }

    public Optional<TelegramUpdates> get() {
        TelegramUpdates updates = this.queue.poll();
        return Optional.ofNullable(updates);
    }


}

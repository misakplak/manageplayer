package cd.misakplak;

import org.bukkit.scheduler.BukkitTask;

public class PendingSession {

    private final String sessionId;
    private final BukkitTask task;

    public PendingSession(String sessionId, BukkitTask task) {
        this.sessionId = sessionId;
        this.task = task;
    }

    public String getSessionId() {
        return sessionId;
    }

    public BukkitTask getTask() {
        return task;
    }
}

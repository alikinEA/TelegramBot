package TelegramBotich.service.ssh;

import com.jcabi.ssh.SSHByPassword;
import com.jcabi.ssh.Shell;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * Created by Alikin E.A. on 25.06.17.
 */
public abstract class SSHService {

    private SSHByPassword sshByPassword;

    public SSHService() {
        try {
            this.sshByPassword = new SSHByPassword(
                    getHost(), SSH_PORT,
                    getLogin(), getPassword()
            );

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    abstract String getHost();

    abstract String getLogin();

    abstract String getPassword();

    private static final int SSH_PORT = 22;

    private static final String GET_PID_JAVA = "ps -ef | grep java | grep -v grep | awk '{print $2}'";
    private static final String KILL_PROCESS_BY_PID = "kill -9 ";
    private static final String START_WEBLOGIC = "service weblogic start";

    private String executeCommand(String command) throws IOException {
        String response = new Shell.Plain(sshByPassword).exec(command);
        return response;
    }

    public Optional<Integer> getJavaDID()  {
        try {
           String result = executeCommand(this.GET_PID_JAVA);
           return Optional.ofNullable(new Integer(result.replace("\n","")));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public String getKillProcessByPid(Integer pid) throws IOException {
        return executeCommand(this.KILL_PROCESS_BY_PID + pid);
    }

    public String startWeblogic() throws IOException {
        return executeCommand(this.START_WEBLOGIC);
    }

}

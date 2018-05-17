package intellif.dto.mqtt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangboz on 10/12/15.
 */
public class MsgBody {
    private List<StatusRpt> statusRpt = new ArrayList<StatusRpt>();

    public List<StatusRpt> getStatusRpt() {
        return statusRpt;
    }

    public void setStatusRpt(List<StatusRpt> statusRpt) {
        this.statusRpt = statusRpt;
    }

    @Override
    public String toString() {
        return "statusRpt:" + statusRpt.toString();
    }
}

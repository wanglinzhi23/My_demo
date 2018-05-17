package intellif.service;

import org.apache.thrift.TException;

/**
 * Created by yangboz on 12/9/15.
 */
public interface IoContrlServiceItf {
    int ioContrlWith(int type, long para0, long para1, long para2, long para3) throws TException;
    int ioContrlWithBatch(int type, long para0, long para1, long para2, long para3) throws TException;
}

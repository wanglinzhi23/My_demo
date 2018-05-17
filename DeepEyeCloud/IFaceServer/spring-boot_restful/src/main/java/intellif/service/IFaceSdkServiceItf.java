package intellif.service;

import java.util.List;

import intellif.enums.IFaceSdkTypes;
import intellif.ifaas.IFaaServiceThriftClient;
import intellif.thrift.IFaceSdkTarget;

import org.apache.thrift.TException;

/**
 * Created by yangboz on 12/1/15.
 */
public interface IFaceSdkServiceItf {
    IFaaServiceThriftClient getTarget(IFaceSdkTypes type) throws TException;

    IFaaServiceThriftClient getTarget(IFaceSdkTypes type, long taskId) throws Exception;

    List<IFaaServiceThriftClient> getAllTarget() throws TException;

    IFaaServiceThriftClient getCenterServer() throws TException;
}
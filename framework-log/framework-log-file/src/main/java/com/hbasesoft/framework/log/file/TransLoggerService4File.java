/**
 * 
 */
package com.hbasesoft.framework.log.file;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.log.core.AbstractTransLoggerService;
import com.hbasesoft.framework.log.core.TransManager;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.hbasesoft.framework.log.file <br>
 */
@Service
public class TransLoggerService4File extends AbstractTransLoggerService {

    /**
     * logger
     */
    private Logger logger = new Logger(TransLoggerService4File.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param parentStackId
     * @param beginTime
     * @param method
     * @param params <br>
     */
    @Override
    public void before(String stackId, String parentStackId, long beginTime, String method, Object[] params) {
        if (alwaysLog) {
            logger.debug("before execute method [{0}], statckId[{1}], parentStackId[{2}], params [{3}] ", method,
                stackId, parentStackId, CommonUtil.isEmpty(params) ? "NULL" : Arrays.toString(params));
        }
        else {
            super.before(stackId, parentStackId, beginTime, method, params);
        }
    }

    @Override
    public void afterReturn(String stackId, long endTime, long consumeTime, String method, Object returnValue) {
        if (alwaysLog) {
            logger.debug("success execute method [{0}], statckId[{1}], consumeTime[{2}], returnValue[{3}]", method,
                stackId, consumeTime, returnValue);
        }
        else {
            super.afterReturn(stackId, endTime, consumeTime, method, returnValue);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param endTime
     * @param consumeTime
     * @param e <br>
     */
    @Override
    public void afterThrow(String stackId, long endTime, long consumeTime, String method, Exception e) {
        if (alwaysLog) {
            logger.error(e, "error execute method[0], statckId[{1}], consumeTime[{2}]", method, stackId, consumeTime);
        }
        else {
            super.afterThrow(stackId, endTime, consumeTime, method, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.log.core.TransLoggerService#end(java.lang.String, long, long, long,
     * java.lang.Object, java.lang.Exception)
     */
    @Override
    public void end(String stackId, long beginTime, long endTime, long consumeTime, String method, Object returnValue,
        Exception e) {
        TransManager manager = TransManager.getInstance();
        if (alwaysLog) {
            logger.debug(
                "execute method [{0}], result [{1}], statckId[{2}], consumeTime [{3}], return [{4}], exception [{5}]",
                method, manager.isError() || manager.isTimeout(), stackId, consumeTime, returnValue,
                getExceptionMsg(e));
        }
        else {
            try {
                for (String key : manager.getIdSet()) {
                    if (manager.isError() || manager.isTimeout()) {
                        logger.warn(getTransBean(key));
                    }

                }
            }
            catch (Exception ex) {
                logger.error(ex);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.log.core.TransLoggerService#sql(java.lang.String, java.lang.String)
     */
    @Override
    public void sql(String stackId, String sql) {
    }

}

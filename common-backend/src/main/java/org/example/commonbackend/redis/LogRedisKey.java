package org.example.commonbackend.redis;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
public class LogRedisKey {

    /*
    * 单个日志详情
    * 格式：log:detail:{logUuid}
    * TTL:3天
    * */
    public static final String LOG_DETAIL = "log:detail:%s";

    /*
    * 业务ID关联的日志列表
    * 格式：log:biz:{businessModule}:{businessId}
    * 存储：最近三天的日志UUID列表
    * TTL:3天
    * */
    public static final String LOG_BIZ_LIST = "log:biz:%s:%s";

    /*
    * 操作者关联的日志列表
    * 格式: log:operator:{operatorType}:{operatorId}
    * TTL:3天
    * */
    public static final String LOG_OPERATOR_LIST = "log:operator:%s:%s";

    /*
     * 按时间索引的日志
     * 格式: log:date:{yyyyMMdd}:{logType}
     * TTL: 3天 (但每天凌晨会生成新的key)
     */
    public static final String LOG_DATE_INDEX = "log:date:%s:%d";

    /*
     * Redis TTL: 3天（秒）
     */
    public static final long TTL_THREE_DAYS = 3 * 24 * 60 * 60;
}

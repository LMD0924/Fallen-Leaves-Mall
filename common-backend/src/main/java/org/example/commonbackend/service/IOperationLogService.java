package org.example.commonbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.commonbackend.entity.MallOperationLog;

import java.util.List;

/*
 * @Author:总会落叶
 * @Date:2026/3/3
 * @Description:
 */
public interface IOperationLogService extends IService<MallOperationLog> {

    /*
    * 异步保存日志
    * */
    void saveAsync(MallOperationLog logEntity);

    /*
    * 同步保存日志
    * */
    boolean save(MallOperationLog logEntity);

}
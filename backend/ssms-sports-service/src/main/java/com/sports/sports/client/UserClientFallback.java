package com.sports.sports.client;

import com.sports.sports.client.vo.UserVO;
import com.sports.sports.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * UserClient 降级逻辑 (Sentinel Fallback)
 * 当 ssms-auth 服务宕机或异常时，Sentinel 会自动触发此类中的方法
 */
@Component
@Slf4j
public class UserClientFallback implements UserClient {

    @Override
    public Result<List<UserVO>> listByIds(List<Long> ids) {
        log.error("Sentinel 触发降级: UserClient.listByIds 调用失败，ids: {}", ids);
        // 返回空列表，确保业务逻辑不因 RPC 失败而彻底崩溃
        return Result.success(Collections.emptyList());
    }
}

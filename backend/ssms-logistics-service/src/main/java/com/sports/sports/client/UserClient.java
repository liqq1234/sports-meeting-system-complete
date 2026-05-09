package com.sports.sports.client;

import com.sports.logistics.common.Result;
import com.sports.sports.client.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ssms-auth", path = "/user")
public interface UserClient {

    @GetMapping("/listByIds")
    Result<List<UserVO>> listByIds(@RequestParam("ids") List<Long> ids);
}

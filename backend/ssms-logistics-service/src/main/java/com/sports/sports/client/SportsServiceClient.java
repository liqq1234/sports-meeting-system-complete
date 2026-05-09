package com.sports.sports.client;

import com.sports.logistics.common.Result;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ssms-sports-service")
public interface SportsServiceClient {

    // 消息相关
    @PostMapping("/message/send")
    Result<?> sendMessage(@RequestBody MessageDTO message);

    // 报名相关
    @GetMapping("/registration/list/approved")
    Result<List<RegistrationDTO>> getApprovedRegistrations(@RequestParam("userId") Long userId, @RequestParam("meetingId") Long meetingId);

    // 项目相关
    @GetMapping("/event/list/{meetingId}")
    Result<List<EventDTO>> getEventsByMeetingId(@PathVariable("meetingId") Long meetingId);

    @Data
    class MessageDTO {
        private Long userId;
        private String title;
        private String content;
        private Integer type; // 1-系统通知
        private Long relatedId;
    }

    @Data
    class RegistrationDTO {
        private Long id;
        private Long eventId;
        private String eventName;
    }

    @Data
    class EventDTO {
        private Long id;
        private String name;
        private String category;
        private String venue;
    }
}

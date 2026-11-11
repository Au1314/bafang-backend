package com.baffang.config;

import com.baffang.user.service.PrivateStationUseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 私桩预约维护调度器（并发控制整改）
 * 每 15 分钟扫描一次：待确认且预约时间已过的预约自动释放为已取消，
 * 防止桩位被"僵尸预约"长期占用（演进项：Redis ZSet 延迟队列实现精确到期释放）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationReleaseScheduler {

    private final PrivateStationUseService privateStationUseService;

    @Scheduled(cron = "0 */15 * * * ?")
    public void releaseExpiredReservations() {
        try {
            privateStationUseService.releaseExpiredReservations();
        } catch (Exception e) {
            log.error("超时预约释放执行异常", e);
        }
    }
}

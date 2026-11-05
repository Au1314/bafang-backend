package com.baffang.admin.service.impl;

import com.baffang.admin.mapper.AuditRecordMapper;
import com.baffang.admin.mapper.StatsMapper;
import com.baffang.admin.mapper.UserManageMapper;
import com.baffang.admin.service.DataExportService;
import com.baffang.admin.vo.UserListVO;
import com.baffang.common.entity.AuditRecord;
import com.baffang.common.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据导出服务实现（真实查询 + CSV 生成，替代此前前端整页的模拟导出）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataExportServiceImpl implements DataExportService {

    private static final String[] AUDIT_TYPE_NAMES = {"私桩认证", "车主认证", "充电桩投诉", "充电桩故障", "评论举报", "交易申诉"};
    private static final String[] AUDIT_STATUS_NAMES = {"待审核", "审核中", "已通过", "已拒绝", "需补充材料", "已撤回"};
    private static final String[] USER_TYPE_NAMES = {"普通用户", "VIP用户"};
    private static final String[] AUTH_STATUS_NAMES = {"未认证", "审核中", "已认证", "认证失败"};
    private static final String[] ORDER_TYPE_NAMES = {"公桩充电", "私桩充电", "V2V充电"};
    private static final String[] ORDER_STATUS_NAMES = {"待支付", "充电中", "充电完成待支付", "已完成", "已取消", "已退款"};

    private final UserManageMapper userManageMapper;
    private final AuditRecordMapper auditRecordMapper;
    private final StatsMapper statsMapper;

    @Override
    public ResponseEntity<byte[]> exportAuditRecords(Map<String, Object> params) {
        Integer auditType = intParam(params, "auditType");
        String[] range = dateRange(params);
        List<AuditRecord> records = auditRecordMapper.selectForExport(auditType, range[0], range[1]);

        List<String> headers = List.of("审核ID", "审核单号", "审核类型", "业务ID", "申请人ID", "标题",
                "当前状态", "优先级", "提交时间", "完成时间", "最终意见", "拒绝原因");
        List<List<Object>> rows = new ArrayList<>();
        for (AuditRecord r : records) {
            rows.add(List.of(
                    r.getId(), r.getAuditNo(), name(AUDIT_TYPE_NAMES, r.getAuditType()),
                    r.getBusinessId(), r.getApplicantId(), r.getTitle(),
                    name(AUDIT_STATUS_NAMES, r.getCurrentStatus()), r.getPriority(),
                    fmt(r.getSubmitTime()), fmt(r.getFinishTime()),
                    r.getFinalOpinion(), r.getRejectReason()));
        }
        return csv("审核记录", headers, rows);
    }

    @Override
    public ResponseEntity<byte[]> exportUsers() {
        // 不启动 PageHelper 即查询全量
        List<UserListVO> users = userManageMapper.selectUserList(new com.baffang.admin.dto.UserQueryDTO());

        List<String> headers = List.of("用户ID", "用户名", "昵称", "手机号", "邮箱", "用户类型",
                "桩认证状态", "车认证状态", "积分", "余额", "状态", "注册时间", "最后登录时间");
        List<List<Object>> rows = new ArrayList<>();
        for (UserListVO u : users) {
            rows.add(List.of(
                    u.getId(), u.getUsername(), u.getName(), u.getPhone(), u.getEmail(),
                    name(USER_TYPE_NAMES, u.getType()),
                    name(AUTH_STATUS_NAMES, u.getAuthChargingStation()),
                    name(AUTH_STATUS_NAMES, u.getAuthVehicle()),
                    u.getPoints(), u.getBalance(),
                    Integer.valueOf(1).equals(u.getIsDeleted()) ? "已删除" : "正常",
                    fmt(u.getCreateTime()), fmt(u.getLastLoginTime())));
        }
        return csv("用户数据", headers, rows);
    }

    @Override
    public ResponseEntity<byte[]> exportOrders(Map<String, Object> params) {
        Integer orderType = intParam(params, "orderType");
        String[] range = dateRange(params);
        List<Map<String, Object>> orders = statsMapper.selectOrdersForExport(orderType, range[0], range[1]);

        List<String> headers = List.of("订单号", "用户ID", "订单类型", "订单状态", "电量(kWh)",
                "单价(元/kWh)", "总金额(元)", "实付金额(元)", "创建时间");
        List<List<Object>> rows = new ArrayList<>();
        for (Map<String, Object> o : orders) {
            rows.add(List.of(
                    o.get("orderNo"), o.get("userId"), name(ORDER_TYPE_NAMES, toInt(o.get("orderType"))),
                    name(ORDER_STATUS_NAMES, toInt(o.get("status"))),
                    o.get("electricity"), o.get("unitPrice"),
                    o.get("totalAmount"), o.get("actualAmount"), o.get("createTime")));
        }
        return csv("订单明细", headers, rows);
    }

    // ==================== 私有工具 ====================

    private ResponseEntity<byte[]> csv(String cnName, List<String> headers, List<List<Object>> rows) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = URLEncoder.encode(cnName + "_" + date + ".csv", StandardCharsets.UTF_8)
                .replace("+", "%20");
        byte[] body = CsvUtil.toCsvBytes(headers, rows);
        log.info("数据导出：{}，共 {} 行", cnName, rows.size());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .contentLength(body.length)
                .body(body);
    }

    private String name(String[] names, Integer code) {
        if (code == null || code < 0 || code >= names.length) {
            return "未知";
        }
        return names[code];
    }

    private Integer toInt(Object v) {
        return v instanceof Number ? ((Number) v).intValue() : null;
    }

    private String fmt(Object time) {
        if (time == null) {
            return "";
        }
        if (time instanceof LocalDateTime) {
            return ((LocalDateTime) time).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return String.valueOf(time);
    }

    @SuppressWarnings("unchecked")
    private String[] dateRange(Map<String, Object> params) {
        Object range = params == null ? null : params.get("dateRange");
        if (range instanceof List<?> list && list.size() == 2 && list.get(0) != null) {
            return new String[]{String.valueOf(list.get(0)), String.valueOf(list.get(1))};
        }
        return new String[]{null, null};
    }

    private Integer intParam(Map<String, Object> params, String key) {
        Object v = params == null ? null : params.get(key);
        return v instanceof Number ? ((Number) v).intValue() : null;
    }
}

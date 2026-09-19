package com.baffang.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 管理端通知分页返回 DTO
 */
@Data
public class AdminNoticePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AdminNoticeDTO> list;

    private Integer total;

    private Integer page;

    private Integer pageSize;

    private Integer totalPages;

    public static AdminNoticePageDTO of(List<AdminNoticeDTO> list, Integer total, Integer page, Integer pageSize) {
        AdminNoticePageDTO dto = new AdminNoticePageDTO();
        dto.setList(list);
        dto.setTotal(total);
        dto.setPage(page);
        dto.setPageSize(pageSize);
        dto.setTotalPages(total == 0 ? 0 : (total + pageSize - 1) / pageSize);
        return dto;
    }
}
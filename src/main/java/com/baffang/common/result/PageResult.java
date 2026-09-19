package com.baffang.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import com.github.pagehelper.Page;

/**
 * 封装分页查询结果
 * 在实际使用时，应使用Result包装PageResult
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {
    // 总记录数
    private int total;
    // 总页数
    private int pages;
    // 当前页数
    private int pageNum;
    // 页面大小
    private int pageSize;
    // 当前页数据集合
    private List<T> list;

    /**
     * 直接传入PageHelper的Page对象快速返回
     * 
     * @param page
     * @return
     */
    public static Result<PageResult> quicklyReturn(Page page) {
        PageResult pageResult = new PageResult(
                (int) page.getTotal(),
                page.getPages(),
                page.getPageNum(),
                page.getPageSize(),
                page.getResult());

        return Result.success(pageResult);
    }

}

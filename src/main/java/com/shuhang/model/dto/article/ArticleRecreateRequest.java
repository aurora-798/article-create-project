package com.shuhang.model.dto.article;

import lombok.Data;

import java.io.Serializable;

/**
 * 重新创建文章请求
 */
@Data
public class ArticleRecreateRequest implements Serializable {

    /**
     * 来源文章任务ID
     */
    private String sourceTaskId;

    private static final long serialVersionUID = 1L;
}

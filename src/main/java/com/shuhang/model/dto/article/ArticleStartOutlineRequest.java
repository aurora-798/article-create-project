package com.shuhang.model.dto.article;

import lombok.Data;

import java.io.Serializable;

/**
 * 启动大纲生成请求
 */
@Data
public class ArticleStartOutlineRequest implements Serializable {

    /**
     * 文章任务ID
     */
    private String taskId;

    private static final long serialVersionUID = 1L;
}

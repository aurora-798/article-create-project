package com.shuhang.service;

import com.mybatisflex.core.service.IService;
import com.shuhang.common.model.Article;
import com.shuhang.common.model.dto.article.ArticleState;

import java.util.function.Consumer;

public interface ArticleAgentService extends IService<Article> {

    /**
     * 执行完整的文章生成流程
     *
     * @param state         文章状态
     * @param streamHandler 流式输出处理器
     */
    public void executeArticleGeneration(ArticleState state, Consumer<String> streamHandler);
}

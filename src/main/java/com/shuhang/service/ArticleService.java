package com.shuhang.service;

import com.mybatisflex.core.paginate.Page;
import com.shuhang.common.model.Article;
import com.shuhang.common.model.User;
import com.shuhang.common.model.dto.article.ArticleQueryRequest;
import com.shuhang.common.model.dto.article.ArticleState;
import com.shuhang.common.model.vo.article.ArticleVO;
import com.shuhang.enums.ArticleStatusEnum;

import java.util.List;

public interface ArticleService {

    public String createArticleTask(String topic, String style, User loginUser);

    public Article getByTaskId(String taskId);

    public void updateArticleStatus(String taskId, ArticleStatusEnum status, String errorMessage);

    public void saveArticleContent(String taskId, ArticleState state);

    public Page<ArticleVO> listArticleByPage(ArticleQueryRequest request, User loginUser);

    public boolean deleteArticle(Long id, User loginUser);

    public ArticleVO getArticleDetail(String taskId, User loginUser);

    /**
     * 创建文章任务（带配额检查）
     * 将配额扣减和任务创建放在同一事务中，确保原子性
     *
     * @param topic     选题
     * @param style     文章风格（可为空）
     * @param loginUser 当前登录用户
     * @return 任务ID
     */
    String createArticleTaskWithQuotaCheck(String topic, String style, User loginUser);

}

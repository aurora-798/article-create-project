package com.shuhang.service;

import com.mybatisflex.core.paginate.Page;
import com.shuhang.common.model.Article;
import com.shuhang.common.model.User;
import com.shuhang.common.model.dto.article.ArticleQueryRequest;
import com.shuhang.common.model.dto.article.ArticleState;
import com.shuhang.common.model.vo.article.ArticleVO;
import com.shuhang.enums.ArticleStatusEnum;

public interface ArticleService {

    public String createArticleTask(String topic, User loginUser);


    public Article getByTaskId(String taskId);

    public void updateArticleStatus(String taskId, ArticleStatusEnum status, String errorMessage);

    public void saveArticleContent(String taskId, ArticleState state);

    public Page<ArticleVO> listArticleByPage(ArticleQueryRequest request, User loginUser);

    public boolean deleteArticle(Long id, User loginUser);

    public ArticleVO getArticleDetail(String taskId, User loginUser);
}

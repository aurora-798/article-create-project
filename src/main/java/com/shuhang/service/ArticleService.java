package com.shuhang.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.shuhang.model.Article;
import com.shuhang.model.User;
import com.shuhang.model.dto.article.ArticleQueryRequest;
import com.shuhang.model.dto.article.ArticleState;
import com.shuhang.model.vo.article.ArticleVO;
import com.shuhang.model.enums.ArticlePhaseEnum;
import com.shuhang.model.enums.ArticleStatusEnum;

import java.util.List;

public interface ArticleService extends IService<Article> {

    public String createArticleTask(String topic, String style, List<String> enabledImageMethods, User loginUser);

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
     * @param enabledImageMethods 允许的配图方式列表（可为空）
     * @param loginUser 当前登录用户
     * @return 任务ID
     */
    String createArticleTaskWithQuotaCheck(String topic, String style, List<String> enabledImageMethods, User loginUser);


    /**
     * 确认标题（用户选择后）
     *
     * @param taskId       任务ID
     * @param mainTitle    选中的主标题
     * @param subTitle     选中的副标题
     * @param userDescription 用户补充描述
     * @param loginUser    当前登录用户
     */
    void confirmTitle(String taskId, String mainTitle, String subTitle, String userDescription, User loginUser);

    /**
     * 确认大纲（用户编辑后）
     *
     * @param taskId    任务ID
     * @param outline   用户编辑后的大纲
     * @param loginUser 当前登录用户
     */
    void confirmOutline(String taskId, List<ArticleState.OutlineSection> outline, User loginUser);

    /**
     * 更新阶段
     *
     * @param taskId 任务ID
     * @param phase  阶段枚举
     */
    void updatePhase(String taskId, ArticlePhaseEnum phase);

    /**
     * 保存标题方案
     *
     * @param taskId       任务ID
     * @param titleOptions 标题方案列表
     */
    void saveTitleOptions(String taskId, List<ArticleState.TitleOption> titleOptions);

    /**
     * AI 修改大纲
     *
     * @param taskId           任务ID
     * @param modifySuggestion 用户修改建议
     * @param loginUser        当前登录用户
     * @return 修改后的大纲
     */
    List<ArticleState.OutlineSection> aiModifyOutline(String taskId, String modifySuggestion, User loginUser);

}

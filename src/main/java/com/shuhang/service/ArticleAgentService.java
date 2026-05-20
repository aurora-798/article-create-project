package com.shuhang.service;

import com.mybatisflex.core.service.IService;
import com.shuhang.model.Article;
import com.shuhang.model.dto.article.ArticleState;

import java.util.List;
import java.util.function.Consumer;

public interface ArticleAgentService extends IService<Article> {


    /**
     * AI 修改大纲
     *
     * @param mainTitle        主标题
     * @param subTitle         副标题
     * @param currentOutline   当前大纲
     * @param modifySuggestion 用户修改建议
     * @return 修改后的大纲
     */
    public List<ArticleState.OutlineSection> aiModifyOutline(String mainTitle, String subTitle,
                                                             List<ArticleState.OutlineSection> currentOutline,
                                                             String modifySuggestion);


    /**
     * 阶段1：生成标题方案（3-5个）
     *
     * @param state         文章状态
     * @param streamHandler 流式输出处理器
     */
    public void executePhase1_GenerateTitles(ArticleState state, Consumer<String> streamHandler);


    /**
     * 阶段2：生成大纲（用户选择标题后）
     *
     * @param state         文章状态
     * @param streamHandler 流式输出处理器
     */
    public void executePhase2_GenerateOutline(ArticleState state, Consumer<String> streamHandler);

    /**
     * 阶段3：生成正文+配图（用户确认大纲后）
     *
     * @param state         文章状态
     * @param streamHandler 流式输出处理器
     */
    public void executePhase3_GenerateContent(ArticleState state, Consumer<String> streamHandler);



    /**
     * 智能体1：生成标题方案（3-5个）
     */
    public void agent1GenerateTitleOptions(ArticleState state);

    /**
     * 智能体2：生成大纲（流式输出）
     */
    public void agent2GenerateOutline(ArticleState state, Consumer<String> streamHandler);

    /**
     * 智能体3：生成正文（流式输出）
     */
    public void agent3GenerateContent(ArticleState state, Consumer<String> streamHandler);

    /**
     * 智能体4：分析配图需求（在正文中插入占位符）
     */
    public void agent4AnalyzeImageRequirements(ArticleState state);

    /**
     * 智能体5：生成配图（串行执行，支持混用多种配图方式，统一上传到 COS）
     */
    public void agent5GenerateImages(ArticleState state, Consumer<String> streamHandler);


    /**
     * 图文合成：根据占位符将配图插入正文
     */
    public void mergeImagesIntoContent(ArticleState state);
}

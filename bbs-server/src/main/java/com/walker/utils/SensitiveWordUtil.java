package com.walker.utils;

import com.walker.pojo.Article;
import com.walker.service.ArticleService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author chengQing
 * @Date 2026/4/9 14:57
 * @PackageName:com.walker.utils
 * @ClassName: SensitiveWordUtil
 * @Description: 敏感词工具类
 */
public class SensitiveWordUtil {
    /**
     * 敏感词列表
     */
    public static List<String> sensitiveWords = new ArrayList<>();

    /**
     * 更新敏感词列表（由 Service 调用）
     */
    public static void updateSensitiveWords(List<String> words) {
        sensitiveWords = new ArrayList<>(words);
        System.out.println("敏感词已更新，共 " + sensitiveWords.size() + " 个");
    }

    /**
     * 获取敏感词列表
     */
    public static List<String> getSensitiveWords() {
        return new ArrayList<>(sensitiveWords);
    }

    /**
     * 脱敏处理
     */
    public static String desensitize(String text) {
        if (text == null || text.isEmpty() || sensitiveWords.isEmpty()) {
            return text;
        }

        String result = text;
        for (String word : sensitiveWords) {
            result = result.replaceAll(word, "***");
        }
        return result;
    }

    /**
     * 方法描述 批量脱敏文章
     * @author chengQing
     * @date 2026/4/9 15:22
     * @param articles 文章列表
     * @return List<Article> 脱敏之后的文章列表
     */
    public static List<Article> desensitizeArticles(List<Article> articles) {
        if (CollectionUtils.isEmpty( articles)) {
            return articles;
        }
        // 脱敏标题、内容、内容HTML、摘要
        for (Article article : articles) {
            article.setArticleTitle(desensitize(article.getArticleTitle()));
            article.setArticleContentHtml(desensitize(article.getArticleContentHtml()));
            article.setArticleContent(desensitize(article.getArticleContent()));
            article.setArticleSummary(desensitize(article.getArticleSummary()));
        }
        return articles;
    }

    /**
     * 方法描述 批量脱敏文章
     * @author chengQing
     * @date 2026/4/9 15:22
     * @param article 文章
     * @return List<Article> 脱敏之后的文章列表
     */
    public static Article desensitizeArticle(Article article) {
        if (!Objects.isNull(article)) {
            // 脱敏标题、内容、内容HTML、摘要
            article.setArticleTitle(desensitize(article.getArticleTitle()));
            article.setArticleContentHtml(desensitize(article.getArticleContentHtml()));
            article.setArticleContent(desensitize(article.getArticleContent()));
            article.setArticleSummary(desensitize(article.getArticleSummary()));
        }
        return article;
    }

}

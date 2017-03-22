package com.haloai.hud.music;

import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangrui on 17/3/2.
 */
public class CategoryInfo {
    private long id;
    private String kind;//默认“category”
    private String  categoryName;//分类名
    private String  categoryUrl;//封面

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public static List<CategoryInfo> getListByXM(CategoryList categoryList){
        List<CategoryInfo> list = new ArrayList<CategoryInfo>();
        for(Category category : categoryList.getCategories()){
            CategoryInfo categoryInfo = new CategoryInfo();
            categoryInfo.setId(category.getId());
            categoryInfo.setKind(category.getKind());
            categoryInfo.setCategoryName(category.getCategoryName());
            categoryInfo.setCategoryUrl(category.getCoverUrlLarge());
            list.add(categoryInfo);
        }
        return list;
    }

}

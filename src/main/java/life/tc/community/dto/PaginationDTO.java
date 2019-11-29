package life.tc.community.dto;


import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

//此对象包括了页面的元素，包括问题，页码
public class PaginationDTO {

    private List<QuestionDTO> questions;
    //是否有向前的按钮
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer totalPage;
    //当前所在页面
    private Integer page;
    //一次所显示的所有page
    private List<Integer> pages = new ArrayList<>();



    //关于页面上的一些逻辑
    public void setPagination(Integer totalCount, Integer page, Integer size) {

        //计算总共需要多少页
        if(totalCount % size == 0) {
            this.totalPage = totalCount / size;
        }
        else{
            this.totalPage = totalCount / size + 1;
        }


        if(page <1){
            page = 1;
        }
        if(page > this.totalPage) {
            page = this.totalPage;
        }

        this.page = page;

        pages.add(page);
        for(int i = 1;i <= 3;i++){
            if(page-i>0){
                pages.add(0,page-i);
            }

            if(page + i <= this.totalPage){
                pages.add(page + i);
            }
        }

        //如果当前为第一页（就不展示上一页）
        if(page == 1){
            showPrevious = false;
        }
        else{
            showPrevious = true;
        }
        //如果当前为最后一页（就不会展示下一页）
        if(page == totalPage){
            showNext = false;
        }
        else{
            showNext = true;
        }

        //判断是否展示前往第一页按钮
        if(pages.contains(1)){
            showFirstPage = false;
        }
        else{
            showFirstPage = true;
        }

        //判断是否展示前往最后一页的按钮
        if(pages.contains(totalPage)){
            showEndPage = false;
        }
        else{
            showEndPage = true;
        }
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }

    public boolean isShowPrevious() {
        return showPrevious;
    }

    public void setShowPrevious(boolean showPrevious) {
        this.showPrevious = showPrevious;
    }

    public boolean isShowFirstPage() {
        return showFirstPage;
    }

    public void setShowFirstPage(boolean showFirstPage) {
        this.showFirstPage = showFirstPage;
    }

    public boolean isShowNext() {
        return showNext;
    }

    public void setShowNext(boolean showNext) {
        this.showNext = showNext;
    }

    public boolean isShowEndPage() {
        return showEndPage;
    }

    public void setShowEndPage(boolean showEndPage) {
        this.showEndPage = showEndPage;
    }


}

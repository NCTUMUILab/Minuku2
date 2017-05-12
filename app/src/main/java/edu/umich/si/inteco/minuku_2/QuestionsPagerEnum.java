package edu.umich.si.inteco.minuku_2;

/**
 * Created by neerajkumar on 11/8/16.
 */
public enum QuestionsPagerEnum {
    FIRST_PAGE(1, R.layout.prompt_questions_first_page),
    SECOND_PAGE(2, R.layout.prompt_questions_second_page),
    THIRD_PAGE(3, R.layout.prompt_questions_third_page),
    FOURTH_PAGE(4, R.layout.prompt_questions_fourth_page);

    private int mTitleResId;
    private int mLayoutResId;

    QuestionsPagerEnum(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}

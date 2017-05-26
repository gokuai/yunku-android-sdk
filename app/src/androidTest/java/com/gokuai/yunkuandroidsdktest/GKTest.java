package com.gokuai.yunkuandroidsdktest;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.gokuai.yunkuandroidsdk.data.FileData;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by qp on 2017/5/24.
 */

@RunWith(AndroidJUnit4.class)
public class GKTest {

    @Rule
    public ActivityTestRule<ConfigActivity> mConfigActivity
            = new ActivityTestRule<>(ConfigActivity.class);

    private String mStringRename;

    @Before
    public void initString(){
        mStringRename = "gokuai";
    }

    @Test
    public void newFolderTest() throws InterruptedException {

        Espresso.onView(withId(R.id.config_start_demo_btn)).perform(scrollTo(), click());

        Espresso.onView(withText("添加")).check(matches(isDisplayed())).perform(click());

        Espresso.onView(withText("文件夹")).check(matches(isDisplayed())).perform(click());

        Espresso.onView(withId(R.id.dialog_edit)).perform(typeText(mStringRename),closeSoftKeyboard());

        Espresso.onView(withText("确定")).check(matches(isDisplayed())).perform(click());

        Espresso.onView(withText(mStringRename)).check(matches(isDisplayed()));
    }

    @Test
    public void newGKBoteTest() throws InterruptedException {

        Espresso.onView(withId(R.id.config_start_demo_btn)).perform(scrollTo(), click());

        Espresso.onView(withText("添加")).check(matches(isDisplayed())).perform(click());

        Espresso.onView(withText("够快笔记")).check(matches(isDisplayed())).perform(click());

        Espresso.onView(withId(R.id.dialog_edit)).check(matches(isDisplayed())).perform(typeText(mStringRename),closeSoftKeyboard());

        Espresso.onView(withText("保存")).check(matches(isDisplayed())).perform(click());

        Espresso.onView(withId(R.id.dialog_edit)).perform(typeText(mStringRename),closeSoftKeyboard());

        Espresso.onView(withText("确定")).check(matches(isDisplayed())).perform(click());

        Espresso.onView(withText(mStringRename)).check(matches(isDisplayed()));
    }

    @Test
    public void deleteFileTest() throws InterruptedException {

        Espresso.onView(withId(R.id.config_start_demo_btn)).perform(scrollTo(), click());

        Thread.sleep(1000);

         onData((WithFilaDataFileName("aa.jpg"))).inAdapterView(withId(R.id.list)).perform(click());

//        onView(withId(R.id.file_item_name)).check(matches(withText("aa.jpg")));


//        Espresso.onView(allOf(withId(R.id.file_item_dropdown_btn), withText("aa.jpg"))).perform(scrollTo(), click());

//        onData(allOf(WithFilaDataFileName(mStringRename), withId(R.id.file_item_dropdown_btn))).perform(click());


//        onData(anything()).inAdapterView(withContentDescription(R.id.file_item_dropdown_btn)).atPosition(0).perform(click());
//
//        Espresso.onView(withText("删除")).check(matches(isDisplayed())).perform(click());
//
//        Espresso.onView(withText("确定")).check(matches(isDisplayed())).perform(click());


//        Espresso.onView(withText("aa.jpg")).check(matches(isDisplayed()));

//        Thread.sleep(1000);

//        Espresso.onData(withText("aa.jpg")).onChildView(withId(R.id.file_item_dropdown_btn)).check(matches(isDisplayed())).perform(click());
//        Espresso.onView(withId(R.id.file_item_dropdown_btn)).check(matches(isDisplayed())).perform(click());
//        Espresso.onView(withChild(withText("aa.jpg"))).check(matches(isDisplayed()));

//        Thread.sleep(1000);

//        Espresso.onView(withText("删除")).check(matches(isDisplayed())).perform(click());

//        Thread.sleep(1000);

//        Espresso.onView(withText("确定")).check(matches(isDisplayed())).perform(click());


//        Espresso.onView(withText("aa.jpg")).check(matches(isDisplayed())).perform(click());

//        Espresso.onData(withText("aa.jpg")).onChildView(withId(R.id.btn_delete)).check(matches(isDisplayed())).perform(click());


//        Espresso.onView(withText("添加")).check(matches(isDisplayed())).perform(click());
//
//        Espresso.onView(withText("文件夹")).check(matches(isDisplayed())).perform(click());
//
//        Espresso.onView(withId(R.id.dialog_edit)).perform(typeText("111"),closeSoftKeyboard());
//
//        Espresso.onView(withText("取消")).check(matches(isDisplayed())).perform(click());
//
//        Espresso.onView(withText("111")).check(matches(isDisplayed()));
}

    private Matcher<Object> WithFilaDataFileName(final String fileName){
        return new BoundedMatcher<Object, FileData>(FileData.class){

            @Override
            protected boolean matchesSafely(FileData item) {
                return item.equals(item.getFilename());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("gokuai" + fileName);

            }

        };
    }
}

package com.gokuai.yunkuandroidsdktest;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by qp on 2017/5/24.
 * <p>
 * demo测试
 */

@RunWith(AndroidJUnit4.class)
public class GKTest {

    @Rule
    public ActivityTestRule<ConfigActivity> mConfigActivity
            = new ActivityTestRule<>(ConfigActivity.class);

    private String mStringRename;

    @Before
    public void initString() {
        mStringRename = "gokuai";
    }

    @Test
    public void startActivityTest() throws InterruptedException {

        Espresso.onView(withId(R.id.config_start_demo_btn)).perform(scrollTo(), click());

        Espresso.onView(withText(mStringRename)).check(matches(isDisplayed()));
    }
}

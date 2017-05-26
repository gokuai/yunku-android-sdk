package com.gokuai.yunkuandroidsdktest;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by qp on 2017/5/25.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ActivityTest {

    @Test
    public void testActivity(){
        ConfigActivity configActivity = Robolectric.setupActivity(ConfigActivity.class);
        Assert.assertNotNull(configActivity);
        Assert.assertEquals(configActivity.getTitle(), "configActivity");
    }

}

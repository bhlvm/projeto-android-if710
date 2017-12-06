package br.ufpe.cin.if710.podcast.ui;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static org.hamcrest.Matchers.not;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

import static org.junit.Assert.*;

/**
 * Created by barre on 06/12/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule(MainActivity.class, true);

    @Test
    public void countInitPodcasts() {
        onView(withId(R.id.items)).check(new AdapterCountAssertion(311));
    }

    @Test
    public void notEmptyTitleEpisodeDetail() {
        onData(anything())
                .inAdapterView(withId(R.id.items))
                .atPosition(0)
                .perform(click());

//        onView(withId(R.id.titleED)).check(matches(withText("Ciência e Pseudociência")));
        onView(withId(R.id.titleED)).check(matches(not(withText(""))));
    }

    @Test
    public void keyEvents() {
        onView(withId(R.id.items)).perform(
                pressKey(KeyEvent.KEYCODE_DPAD_DOWN),
                pressKey(KeyEvent.KEYCODE_DPAD_DOWN)
        ).check(new ListSelectionAssertion(1));
    }

    @Test
    public void playPodcast() {
        clickButton(0);

        onData(anything())
                .inAdapterView(withId(R.id.items))
                .atPosition(0)
                .onChildView(withId(R.id.item_action))
                .check(matches(withText("pause")));
    }

    @Test
    public void pausePodcast() {
        clickButton(0);
        clickButton(0);

        onData(anything())
                .inAdapterView(withId(R.id.items))
                .atPosition(0)
                .onChildView(withId(R.id.item_action))
                .check(matches(withText("unPause")));
    }

    public static void clickButton(int position) {
        onData(anything())
                .inAdapterView(withId(R.id.items))
                .atPosition(position)
                .onChildView(withId(R.id.item_action))
                .perform(click());
    }

    static class AdapterCountAssertion implements ViewAssertion {
        private final int count;

        AdapterCountAssertion(int count) {
            this.count=count;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            Assert.assertTrue(view instanceof AdapterView);
            Assert.assertEquals(count, ((AdapterView)view).getAdapter().getCount());
        }
    }

    static class ListSelectionAssertion implements ViewAssertion {
        private final int position;

        ListSelectionAssertion(int position) {
            this.position=position;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            Assert.assertTrue(view instanceof ListView);
            Assert.assertEquals(position, ((ListView)view).getSelectedItemPosition());
        }
    }
}
package com.example.toko


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Test
    fun registerActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(500)

        val textInputEditText = onView(
            allOf(
                withId(R.id.inputRegisterUsername),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutUsername),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(click())

        val materialButton = onView(
            allOf(
                withId(R.id.btnRegister), withText("REGISTER"),
                childAtPosition(
                    allOf(
                        withId(R.id.LinearLayout),
                        childAtPosition(
                            withId(R.id.mainLayout),
                            3
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.inputRegisterUsername),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutUsername),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText2.perform(replaceText("dito"), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.inputRegisterPassword),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutPassword),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText3.perform(replaceText("dito"), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.inputRegisterEmail),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutEmail),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText4.perform(replaceText("dito"), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(3000))

        val materialButton2 = onView(
            allOf(
                withId(R.id.btnRegister), withText("REGISTER"),
                childAtPosition(
                    allOf(
                        withId(R.id.LinearLayout),
                        childAtPosition(
                            withId(R.id.mainLayout),
                            3
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.inputRegisterEmail), withText("dito"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutEmail),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText5.perform(replaceText("dito@gmail.com"))
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText6 = onView(
            allOf(
                withId(R.id.inputRegisterEmail), withText("dito@gmail.com"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutEmail),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText6.perform(closeSoftKeyboard())

        val textInputEditText7 = onView(
            allOf(
                withId(R.id.inputRegisterTanggalLahir),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutTanggalLahir),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText7.perform(replaceText("7 Agustus 2002"), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText8 = onView(
            allOf(
                withId(R.id.inputRegisterNoTelepon),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.layoutNoTelepon),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText8.perform(replaceText("082322744784"), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(3000))

        val materialButton3 = onView(
            allOf(
                withId(R.id.btnRegister), withText("REGISTER"),
                childAtPosition(
                    allOf(
                        withId(R.id.LinearLayout),
                        childAtPosition(
                            withId(R.id.mainLayout),
                            3
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())
        onView(isRoot()).perform(waitFor(3000))

    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $delay milliseconds."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}

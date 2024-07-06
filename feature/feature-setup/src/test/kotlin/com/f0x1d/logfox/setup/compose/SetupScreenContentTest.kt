package com.f0x1d.logfox.setup.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.f0x1d.logfox.core.tests.ScreenshotTest
import com.f0x1d.logfox.core.tests.compose.clickOn
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.MockSetupScreenListener
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupAdbButtonTestTag
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupAdbDialogTestTag
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupScreenContent
import com.f0x1d.logfox.feature.setup.ui.fragment.setup.compose.SetupScreenState
import com.f0x1d.logfox.ui.compose.theme.LogFoxTheme
import org.junit.Test

class SetupScreenContentTest : ScreenshotTest() {

    @Test
    fun shouldShowSetupScreenContent() = screenshotTestOf {
        LogFoxTheme {
            SetupScreenContent()
        }
    }

    @Test
    fun shouldShowAdbDialogOnSetupScreenContent() = screenshotTestOf(
        whatToCapture = { SetupAdbDialogTestTag.node() },
    ) {
        LogFoxTheme {
            SetupScreenContent(
                state = SetupScreenState(
                    showAdbDialog = true,
                    adbCommand = OG_BUDA_ISKS,
                ),
            )
        }
    }

    @Test
    fun shouldOpenAdbDialogOnSetupScreenContent() = screenshotTestOf(
        actions = { clickOn(SetupAdbButtonTestTag) },
        whatToCapture = { SetupAdbDialogTestTag.node() },
    ) {
        var state by remember {
            mutableStateOf(SetupScreenState())
        }

        LogFoxTheme {
            SetupScreenContent(
                state = state,
                listener = MockSetupScreenListener.copy(
                    onAdbClick = { state = state.copy(showAdbDialog = true) },
                ),
            )
        }
    }

    companion object {
        private val OG_BUDA_ISKS = """
            [Интро]
            revv, what do you mean?
            Кх-кх, оу, я-я
            Оу, я-я, оу, я-я, я-я-я
            Окей (Е, е)
            
            [Припев]
            Вся наша жизнь — это грёбаный квест, quiz (Эй)
            Слава Богу, что я смог поднять грёбаный сыр, cheese (Бабки, бабки)
            Ты же знаешь, на битах я зверь, beast (Р-р)
            У меня то, что не купишь за деньги: свэг, rizz
            Я ща не пизжу, посмотри на моё запястье, потратил пять лямов на wrist (А)
            Я так давно не виделся со своими пацанами, сука (Фью), я карьерист
            Big Boy, салют, мой юрист, если ты обиделся — можешь подать на меня иск (Ха, ха)
            Типов, что тянули меня вниз, я вычеркнул (А, да-да-да), я поставил на них икс (А-а, эй, эй, я-я)
            
            [Постприпев]
            Мне нужен Yeat и BNYX (Я), на битах трюки — BMX (А)
            Я Ruff Ryder, я DMX (У, оу), ты звучишь как на меня ремикс
            I know that you hating on me, I know that you sucking dicks
            
            [Куплет]
            А, большие деньги изменили то, как я думаю (Ха, woah)
            Все эти суки изменили то, как я чувствую, а
            Так много денег, меня на серьёзе спрашивают: «Я чё, иллюминат?» (А)
            Так много денег, на хате теперь мраморный пол, сука, на хуй ламинат (Е-е)
            Е-е, big stepper — big stacks
            А, я живу этим дерьмом, отдаюсь ему весь (Е)
            Я бы раньше простил, но щас жди от меня месть
            Это тебя только что на хуй послала цепь (Е)
            
            [Припев]
            Вся наша жизнь — это грёбаный квест, quiz (Эй)
            Слава Богу, что я смог поднять грёбаный сыр, cheese (Бабки, бабки)
            Ты же знаешь, на битах я зверь, beast (Р-р)
            У меня то, что не купишь за деньги: свэг, rizz
            Я ща не пизжу, посмотри на моё запястье, потратил пять лямов на wrist (А)
            Я так давно не виделся со своими пацанами, сука (Фью), я карьерист
            Big Boy, салют, мой юрист, если ты обиделся — можешь подать на меня иск
            Типов, что тянули меня вниз, я вычеркнул (А), я поставил на них икс (А-а, эй, эй, я-я)
            
            [Постприпев]
            Мне нужен Yeat и BNYX (Я), на битах трюки — BMX (А)
            Я Ruff Ryder, я DMX (У, оу), ты звучишь как на меня ремикс (Да)
            I know that you hating on me, I know that you sucking dicks
            
            [Аутро]
            Да (Я), да (Я)
            Да (Я), да (Я)
            Revv, what do you mean?
        """.trimIndent()
    }
}

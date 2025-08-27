package na.family.prayer.lib

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions

/**
 * Extension NavController
 * 해당 screen으로 이동합니다.
 */
fun <T> NavController.navigate(route: String, pram: T, builder: (NavOptionsBuilder.() -> Unit) = {}) {
    currentBackStackEntry?.savedStateHandle?.set(key = route, value = pram)
    navigate(route, navOptions(builder))
}

/**
 * pram 값을 가져옵니다.
 */
inline fun <reified T> NavController.getPram():T? {
    return try {
        val value = previousBackStackEntry?.savedStateHandle?.get<T>(
            currentBackStackEntry?.destination?.route ?: ""
        )
        if (value is T) value else null
    } catch (e: ClassCastException) {
        return null
    }

}

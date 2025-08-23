package na.family.prayer.lib

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog

val LocalBottomSheetNavigator = compositionLocalOf<BottomSheetNavigator> { error("No BottomSheetNavigator provided") }

enum class BottomSheetValue {
    Hidden,
    Expanded,
}

// BottomSheet 커스텀 NavType
class BottomSheetNavigator(private val navController: NavController) {
    private val _currentState = mutableStateOf(BottomSheetValue.Hidden)
    val currentState: State<BottomSheetValue> = _currentState

    private var currentRoute: String? = null
    private val activeRoutes = HashSet<String>()

    fun navigate(route: String) {
        if (!activeRoutes.add(route)) return

        currentRoute = route
        navController.navigate(route)
        _currentState.value = BottomSheetValue.Expanded
    }

    fun hide() {
        _currentState.value = BottomSheetValue.Hidden
        navController.popBackStack()
        currentRoute?.let { activeRoutes.remove(it) }
        currentRoute = null
    }

    fun isActive(route: String): Boolean = activeRoutes.contains(route)
}

// NavGraphBuilder 확장 함수
fun NavGraphBuilder.bottomSheet(
    route: String,
    dragEnabled: Boolean = false,
    skipPartiallyExpanded: Boolean = true,
    isDismissAction: Boolean = true,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    val properties = DialogProperties(
        dismissOnBackPress = isDismissAction,
        dismissOnClickOutside = isDismissAction
    )
    dialog(
        route = route,
        dialogProperties = properties
    ) { backStackEntry ->
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val bottomSheetState by bottomSheetNavigator.currentState

        if (bottomSheetState != BottomSheetValue.Hidden) {
            BottomSheetContent(
                skipPartiallyExpanded = skipPartiallyExpanded,
                dragEnabled = dragEnabled,
                isDismissAction = isDismissAction,
                onDismiss = { bottomSheetNavigator.hide() }
            ) {
                content(backStackEntry)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetContent(
    skipPartiallyExpanded: Boolean,
    dragEnabled: Boolean,
    isDismissAction: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = { sheetValue ->
            if (dragEnabled) true
            else sheetValue != SheetValue.Hidden
        }
    )
    val bottomSheetProperties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = isDismissAction,
    )

    ModalBottomSheet(
        modifier = Modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        properties = bottomSheetProperties
    ) {
        content()
    }
}
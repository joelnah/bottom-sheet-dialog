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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController

// LocalComposition
val LocalBottomSheetNavigator = compositionLocalOf<BottomSheetNavigator> { error("No BottomSheetNavigator provided") }

// BottomSheet 상태 값
enum class BottomSheetValue {
    Hidden,
    Expanded,
}

@Composable
fun rememberBottomSheetNavigator(): BottomSheetNavigator {
    val navController = rememberNavController()
    return remember(navController) { BottomSheetNavigator(navController) }
}

// BottomSheet 커스텀 NavType
class BottomSheetNavigator(val navController: NavHostController) {
    // 현재 BottomSheet 상태
    private val _currentState = mutableStateOf(BottomSheetValue.Hidden)
    val currentState: State<BottomSheetValue> = _currentState

    // 현재 활성화된 Route 관리
    private var currentRoute: String? = null
    // 중복 방지를 위한 Set
    private val activeRoutes = HashSet<String>()

    // BottomSheet 열기
    fun navigate(route: String) {
        if (!activeRoutes.add(route)) return

        currentRoute = route
        navController.navigate(route)
        _currentState.value = BottomSheetValue.Expanded
    }

    // BottomSheet 열기
    fun <T>navigate(route: String, argument: T) {
        if (!activeRoutes.add(route)) return

        currentRoute = route
        navController.navigate(route, argument)
        _currentState.value = BottomSheetValue.Expanded
    }

    // BottomSheet 닫기
    fun hide() {
        _currentState.value = BottomSheetValue.Hidden
        navController.popBackStack()
        currentRoute?.let { activeRoutes.remove(it) }
        currentRoute = null
    }

    // 특정 Route가 활성화 상태인지 확인
    fun isActive(route: String): Boolean = activeRoutes.contains(route)
}

// NavGraphBuilder 확장 함수
fun NavGraphBuilder.sheetDialog(
    route: String,
    dragEnabled: Boolean = true,
    skipPartiallyExpanded: Boolean = false,
    isDismissAction: Boolean = true,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    // 다이얼로그 속성 설정
    val properties = DialogProperties(
        dismissOnBackPress = isDismissAction,
        dismissOnClickOutside = isDismissAction
    )

    // 다이얼로그 컴포저블 추가
    dialog(
        route = route,
        dialogProperties = properties
    ) { backStackEntry ->
        // LocalComposition에서 BottomSheetNavigator 가져오기
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        // 현재 BottomSheet 상태 관찰
        val bottomSheetState by bottomSheetNavigator.currentState

        // BottomSheet가 열려 있을 때만 내용 표시
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
    // ModalBottomSheet 상태 기억
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = { sheetValue ->
            if (dragEnabled) true
            else sheetValue != SheetValue.Hidden
        }
    )
    // BottomSheet 속성 설정
    val bottomSheetProperties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = isDismissAction,
    )

    // ModalBottomSheet 표시
    ModalBottomSheet(
        modifier = Modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        properties = bottomSheetProperties
    ) {
        content()
    }
}
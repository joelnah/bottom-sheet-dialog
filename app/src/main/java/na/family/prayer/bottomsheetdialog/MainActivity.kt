package na.family.prayer.bottomsheetdialog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import na.family.prayer.bottomsheetdialog.ui.theme.BottomSheetDialogTheme
import na.family.prayer.lib.LocalBottomSheetNavigator
import na.family.prayer.lib.bottomSheetDialog
import na.family.prayer.lib.rememberBottomSheetNavigator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BottomSheetDialogTheme {
                val bottomSheetNavigator = rememberBottomSheetNavigator()

                CompositionLocalProvider(
                    LocalBottomSheetNavigator provides bottomSheetNavigator,
                ) {

                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        startDestination = "home",
                        navController = bottomSheetNavigator.navController
                    ) {
                        composable("home") {
                            HomeScreen()
                        }

                        // Bottom Sheet Dialog
                        bottomSheetDialog(route = "bottomSheet") {
                            BottomSheetDialog()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(){
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    Scaffold(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        topBar = {
            Text(text = "Home Screen")
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        bottomSheetNavigator.navigate("bottomSheet")
                    }
                ) {
                    Text(text = "Open Bottom Sheet")
                }
            }

        }
    )
}


@Composable
fun BottomSheetDialog(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Text(
            text = "I am Bottom Sheet Dialog",
            style = TextStyle(color = Color.Black)
            )
    }
}
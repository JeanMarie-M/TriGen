package com.example.trigen.screens.drsabcd_alternatives

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trigen.screens.drsabcd.SendHelpStep
import com.example.trigen.screens.home.RightTagBadge

@Composable
fun SendHelpAlternative(
    onNavigateToHome: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(scrollState)

    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TriGen",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onBackground
                )
                Text(
                    text = "Emergency First Aid",
                    fontSize = 12.sp,
                    color = colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            RightTagBadge("Call Help!")
        }



        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)

        ) {

            SendHelpStep(onHelpCalled = onNavigateToHome)
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SendHelpAlternativePreview() {
    SendHelpAlternative(onNavigateToHome = {})
}

package com.axioo.feed.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axioo.feed.R
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.ui.components.MonoLabel
import com.axioo.feed.ui.theme.AxiooTokens

@Composable
fun ProfileScreen(
    onOpenFocused: (PitchId) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing

    Box(modifier = Modifier.fillMaxSize().background(palette.cream)) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = spacing.gutter, vertical = spacing.l),
            ) {
                MonoLabel(
                    text = stringResource(R.string.profile_title),
                    color = palette.purple,
                    style = AxiooTokens.type.monoCategory,
                )
                Spacer(Modifier.height(spacing.xs))
                Text(
                    text = "Hello, " + state.userType.name.lowercase() + ".",
                    style = MaterialTheme.typography.displayMedium,
                    color = palette.ink,
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.gutter)
                        .padding(top = spacing.s, bottom = spacing.l),
            ) {
                MonoLabel(
                    text = stringResource(R.string.profile_user_type_section),
                    color = palette.ink.copy(alpha = 0.6f),
                    style = AxiooTokens.type.monoCategory,
                )
                Spacer(Modifier.height(spacing.s))
                UserTypeToggle(
                    selected = state.userType,
                    onSelect = { type -> viewModel.onIntent(ProfileIntent.SetUserType(type)) },
                )
            }

            Spacer(Modifier.height(spacing.l))
            ActivityCarousel(
                title = stringResource(R.string.profile_liked_section),
                items = state.liked,
                onClick = onOpenFocused,
            )
            Spacer(Modifier.height(spacing.xl))
            ActivityCarousel(
                title = stringResource(R.string.profile_saved_section),
                items = state.saved,
                onClick = onOpenFocused,
            )
            Spacer(Modifier.height(spacing.xxxl))
        }
    }
}

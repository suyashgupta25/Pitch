package com.axioo.feed.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.axioo.feed.R
import com.axioo.feed.domain.model.UserType
import com.axioo.feed.ui.components.MonoLabel
import com.axioo.feed.ui.theme.AxiooTokens

@Composable
fun UserTypeToggle(
    selected: UserType,
    onSelect: (UserType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(spacing.pillCorner))
                .background(palette.creamSoft)
                .padding(spacing.xxs),
    ) {
        ToggleSegment(
            text = stringResource(R.string.profile_user_type_investor),
            isSelected = selected == UserType.Investor,
            onClick = { onSelect(UserType.Investor) },
        )
        ToggleSegment(
            text = stringResource(R.string.profile_user_type_founder),
            isSelected = selected == UserType.Founder,
            onClick = { onSelect(UserType.Founder) },
        )
    }
}

@Composable
private fun ToggleSegment(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(spacing.pillCorner))
                .background(if (isSelected) palette.ink else palette.creamSoft)
                .clickable(onClick = onClick)
                .padding(horizontal = spacing.l, vertical = spacing.s),
        contentAlignment = Alignment.Center,
    ) {
        MonoLabel(
            text = text,
            color = if (isSelected) palette.cream else palette.ink.copy(alpha = 0.7f),
            style = AxiooTokens.type.monoLabel,
        )
    }
}

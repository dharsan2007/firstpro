package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ActionColorDark
import com.example.ui.theme.ActionColorLight
import com.example.ui.theme.ActionOnColorDark
import com.example.ui.theme.ActionOnColorLight
import com.example.ui.theme.EqualsColorDark
import com.example.ui.theme.EqualsColorLight
import com.example.ui.theme.EqualsOnColorDark
import com.example.ui.theme.EqualsOnColorLight
import com.example.ui.theme.MemoryColorDark
import com.example.ui.theme.MemoryColorLight
import com.example.ui.theme.NumberBtnDark
import com.example.ui.theme.NumberBtnLight
import com.example.ui.theme.NumberOnBtnDark
import com.example.ui.theme.NumberOnBtnLight
import com.example.ui.theme.OperatorColorDark
import com.example.ui.theme.OperatorColorLight
import com.example.ui.theme.OperatorOnColorDark
import com.example.ui.theme.OperatorOnColorLight

enum class ButtonType {
    NUMBER,
    OPERATOR,
    ACTION,
    EQUALS,
    MEMORY
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.NUMBER,
    icon: ImageVector? = null,
    isWide: Boolean = false,
    testTag: String = "btn_$text"
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 80),
        label = "btnScale"
    )

    val isDark = MaterialTheme.colorScheme.background.red < 0.2f

    val (containerColor, contentColor) = when (buttonType) {
        ButtonType.NUMBER -> if (isDark) NumberBtnDark to NumberOnBtnDark
        else NumberBtnLight to NumberOnBtnLight

        ButtonType.OPERATOR -> if (isDark) OperatorColorDark to OperatorOnColorDark
        else OperatorColorLight to OperatorOnColorLight

        ButtonType.ACTION -> if (isDark) ActionColorDark to ActionOnColorDark
        else ActionColorLight to ActionOnColorLight

        ButtonType.EQUALS -> if (isDark) EqualsColorDark to EqualsOnColorDark
        else EqualsColorLight to EqualsOnColorLight

        ButtonType.MEMORY -> Color.Transparent to (if (isDark) MemoryColorDark else MemoryColorLight)
    }

    val shape = if (isWide) RoundedCornerShape(32.dp) else CircleShape

    Surface(
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .then(if (!isWide && buttonType != ButtonType.MEMORY) Modifier.aspectRatio(1f) else Modifier)
            .padding(2.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onClick()
                    }
                )
            },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = if (buttonType == ButtonType.NUMBER && !isDark) 1.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = contentColor
                )
            } else {
                Text(
                    text = text,
                    fontSize = when (buttonType) {
                        ButtonType.MEMORY -> 13.sp
                        ButtonType.OPERATOR -> 24.sp
                        ButtonType.ACTION, ButtonType.EQUALS -> 20.sp
                        else -> 22.sp
                    },
                    fontWeight = when (buttonType) {
                        ButtonType.MEMORY -> FontWeight.Bold
                        ButtonType.OPERATOR, ButtonType.EQUALS -> FontWeight.Light
                        else -> FontWeight.Normal
                    },
                    color = contentColor
                )
            }
        }
    }
}

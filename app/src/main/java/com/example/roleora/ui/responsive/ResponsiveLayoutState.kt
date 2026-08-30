package com.example.roleora.ui.responsive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Responsive Window Size Classes for ROLEORA across Mobile, Tablet, Laptop, and Desktop.
 */
enum class DeviceScreenClass {
    MOBILE,         // 320dp – 767dp
    TABLET,         // 768dp – 1023dp
    LAPTOP,         // 1024dp – 1439dp
    DESKTOP;        // 1440dp and above

    val isMobile: Boolean get() = this == MOBILE
    val isTablet: Boolean get() = this == TABLET
    val isLaptopOrDesktop: Boolean get() = this == LAPTOP || this == DESKTOP
    val isExpandedScreen: Boolean get() = this != MOBILE
}

/**
 * Resolves the DeviceScreenClass based on available container width in dp.
 */
fun getDeviceScreenClass(maxWidth: Dp): DeviceScreenClass {
    return when {
        maxWidth < 768.dp -> DeviceScreenClass.MOBILE
        maxWidth < 1024.dp -> DeviceScreenClass.TABLET
        maxWidth < 1440.dp -> DeviceScreenClass.LAPTOP
        else -> DeviceScreenClass.DESKTOP
    }
}

/**
 * Grid columns and layout specifications adapted per device form factor.
 */
data class ResponsiveLayoutSpec(
    val screenClass: DeviceScreenClass,
    val dashboardColumns: Int,
    val showLeftSidebar: Boolean,
    val showNavigationRail: Boolean,
    val showBottomNavigation: Boolean,
    val showRightContextPanel: Boolean,
    val contentPadding: Dp,
    val cardSpacing: Dp
)

@Composable
fun rememberResponsiveLayoutSpec(
    maxWidth: Dp,
    isRightPanelUserToggled: Boolean = true,
    forcePcMode: Boolean = false
): ResponsiveLayoutSpec {
    val screenClass = remember(maxWidth, forcePcMode) {
        if (forcePcMode) {
            if (maxWidth >= 1440.dp) DeviceScreenClass.DESKTOP else DeviceScreenClass.LAPTOP
        } else {
            getDeviceScreenClass(maxWidth)
        }
    }

    return remember(screenClass, isRightPanelUserToggled, forcePcMode) {
        if (forcePcMode) {
            ResponsiveLayoutSpec(
                screenClass = if (maxWidth >= 1440.dp) DeviceScreenClass.DESKTOP else DeviceScreenClass.LAPTOP,
                dashboardColumns = if (maxWidth >= 1440.dp) 3 else 2,
                showLeftSidebar = true,
                showNavigationRail = false,
                showBottomNavigation = false,
                showRightContextPanel = isRightPanelUserToggled,
                contentPadding = 20.dp,
                cardSpacing = 16.dp
            )
        } else {
            when (screenClass) {
                DeviceScreenClass.MOBILE -> ResponsiveLayoutSpec(
                    screenClass = screenClass,
                    dashboardColumns = 1,
                    showLeftSidebar = false,
                    showNavigationRail = false,
                    showBottomNavigation = true,
                    showRightContextPanel = false,
                    contentPadding = 14.dp,
                    cardSpacing = 12.dp
                )
                DeviceScreenClass.TABLET -> ResponsiveLayoutSpec(
                    screenClass = screenClass,
                    dashboardColumns = 2,
                    showLeftSidebar = false,
                    showNavigationRail = true,
                    showBottomNavigation = false,
                    showRightContextPanel = false,
                    contentPadding = 20.dp,
                    cardSpacing = 16.dp
                )
                DeviceScreenClass.LAPTOP -> ResponsiveLayoutSpec(
                    screenClass = screenClass,
                    dashboardColumns = 2,
                    showLeftSidebar = true,
                    showNavigationRail = false,
                    showBottomNavigation = false,
                    showRightContextPanel = isRightPanelUserToggled,
                    contentPadding = 24.dp,
                    cardSpacing = 18.dp
                )
                DeviceScreenClass.DESKTOP -> ResponsiveLayoutSpec(
                    screenClass = screenClass,
                    dashboardColumns = 3,
                    showLeftSidebar = true,
                    showNavigationRail = false,
                    showBottomNavigation = false,
                    showRightContextPanel = isRightPanelUserToggled,
                    contentPadding = 32.dp,
                    cardSpacing = 20.dp
                )
            }
        }
    }
}

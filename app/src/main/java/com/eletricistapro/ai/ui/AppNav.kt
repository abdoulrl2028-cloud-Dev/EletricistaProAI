package com.eletricistapro.ai.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eletricistapro.ai.R
import java.util.Locale

@Composable
fun EletricistaApp(onLanguage: (String) -> Unit) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home", modifier = Modifier.fillMaxSize()) {
        composable("home") {
            HomeScreen(
                onLanguage = onLanguage,
                onOpen = { route -> nav.navigate(route) },
            )
        }
        composable("course") { CourseScreen(onBack = { nav.popBackStack() }) }
        composable("calculators") { CalculatorHub(onBack = { nav.popBackStack() }, onOpen = { nav.navigate(it) }) }
        composable("ohm") { OhmScreen(onBack = { nav.popBackStack() }) }
        composable("power") { PowerScreen(onBack = { nav.popBackStack() }) }
        composable("energy") { EnergyScreen(onBack = { nav.popBackStack() }) }
        composable("plans") { PlansScreen(onBack = { nav.popBackStack() }) }
        composable(
            route = "pending/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { entry ->
            PendingScreen(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = { nav.popBackStack() },
            )
        }
    }
}

@Composable
private fun HomeScreen(onLanguage: (String) -> Unit, onOpen: (String) -> Unit) {
    Scaffold { padding ->
        ScrollColumn(padding) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.home_subtitle))
            Text(stringResource(R.string.language), style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LanguageChip("pt-BR", "PT", onLanguage)
                LanguageChip("fr", "FR", onLanguage)
                LanguageChip("en", "EN", onLanguage)
            }
            SectionCard(stringResource(R.string.section_training), stringResource(R.string.section_training_body)) {
                onOpen("course")
            }
            SectionCard(stringResource(R.string.section_calculators), stringResource(R.string.section_calculators_body)) {
                onOpen("calculators")
            }
            SectionCard(stringResource(R.string.section_plans), stringResource(R.string.section_plans_body)) {
                onOpen("plans")
            }
            Text(stringResource(R.string.section_pending), style = MaterialTheme.typography.titleMedium)
            PendingLink(R.string.pending_projects, "projects", onOpen)
            PendingLink(R.string.pending_floor, "floor", onOpen)
            PendingLink(R.string.pending_3d, "viewer", onOpen)
            PendingLink(R.string.pending_sim, "simulator", onOpen)
            PendingLink(R.string.pending_ai, "ai", onOpen)
            PendingLink(R.string.pending_materials, "materials", onOpen)
            PendingLink(R.string.pending_quotes, "quotes", onOpen)
            Text(
                stringResource(R.string.disclaimer_short),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun LanguageChip(tag: String, label: String, onLanguage: (String) -> Unit) {
    val selected = when (tag) {
        "en" -> Locale.getDefault().language == "en"
        "fr" -> Locale.getDefault().language == "fr"
        else -> Locale.getDefault().language == "pt"
    }
    FilterChip(selected = selected, onClick = { onLanguage(tag) }, label = { Text(label) })
}

@Composable
private fun PendingLink(title: Int, id: String, onOpen: (String) -> Unit) {
    SectionCard(stringResource(title), stringResource(R.string.section_pending)) {
        onOpen("pending/$id")
    }
}

@Composable
fun PendingScreen(id: String, onBack: () -> Unit) {
    val title = stringResource(pendingTitle(id))
    val body = stringResource(pendingBody(id))
    ToolScaffold(title, onBack) { padding ->
        ScrollColumn(padding) {
            Text(body, style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.disclaimer_short), style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun pendingTitle(id: String): Int = when (id) {
    "projects" -> R.string.pending_projects
    "floor" -> R.string.pending_floor
    "viewer" -> R.string.pending_3d
    "simulator" -> R.string.pending_sim
    "ai" -> R.string.pending_ai
    "materials" -> R.string.pending_materials
    else -> R.string.pending_quotes
}

private fun pendingBody(id: String): Int = when (id) {
    "projects" -> R.string.pending_body_projects
    "floor" -> R.string.pending_body_floor
    "viewer" -> R.string.pending_body_3d
    "simulator" -> R.string.pending_body_sim
    "ai" -> R.string.pending_body_ai
    "materials" -> R.string.pending_body_materials
    else -> R.string.pending_body_quotes
}

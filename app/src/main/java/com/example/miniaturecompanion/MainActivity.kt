package com.example.miniaturecompanion

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val OK = Color(0xFF1F7A45)
private val WARN = Color(0xFF9A5B00)

fun money(v: Double) = "€" + "%.2f".format(v)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Composable
fun App() {
    val scheme = if (isSystemInDarkTheme())
        darkColorScheme(primary = Color(0xFF86A2F7))
    else
        lightColorScheme(primary = Color(0xFF2B4FB8))
    MaterialTheme(colorScheme = scheme) {
        var screen by remember { mutableStateOf(0) }
        var model by remember { mutableStateOf<Model?>(null) }
        var tab by remember { mutableStateOf(0) }
        var region by remember { mutableStateOf(Region.GR) }
        var overseas by remember { mutableStateOf(false) }
        val fb = remember { mutableStateMapOf<String, String>() }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected = screen == 0, onClick = { screen = 0 }, icon = {}, label = { Text("Models") })
                    NavigationBarItem(selected = screen == 1, onClick = { screen = 1 }, icon = {}, label = { Text("My recommendations") })
                }
            }
        ) { pad ->
            Box(Modifier.padding(pad).fillMaxSize()) {
                if (screen == 0) {
                    ModelsScreen(onPick = { model = it; tab = 0; screen = 1 })
                } else {
                    ProjectScreen(model, tab, { tab = it }, region, { region = it }, overseas, { overseas = it }, fb, { screen = 0 })
                }
            }
        }
    }
}

@Composable
fun ModelsScreen(onPick: (Model) -> Unit) {
    var co by remember { mutableStateOf<Brand?>(null) }
    LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Column {
                Text("Choose a model", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text("Start with the model you plan to paint or build.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(14.dp)) {
                    Text("New here? Three steps.", fontWeight = FontWeight.Bold)
                    Text("Choose a model you want to build. Open its recommendations. Set your country to see paints that are sold near you.")
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = co == null, onClick = { co = null }, label = { Text("All companies") })
                FilterChip(selected = co == Brand.GW, onClick = { co = Brand.GW }, label = { Text("Games Workshop") })
                FilterChip(selected = co == Brand.WL, onClick = { co = Brand.WL }, label = { Text("Warlord Games") })
            }
        }
        items(MODELS.filter { co == null || it.brand == co }, key = { it.id }) { m ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(m.name, fontWeight = FontWeight.Bold)
                    Text("${m.brand.label}, ${m.game}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(m.spec, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = { onPick(m) }) { Text("Get recommendations") }
                }
            }
        }
        item { Text("Mockup with sample data. Names, prices and availability are illustrative.", style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
fun ProjectScreen(
    model: Model?, tab: Int, onTab: (Int) -> Unit,
    region: Region, onRegion: (Region) -> Unit,
    overseas: Boolean, onOverseas: (Boolean) -> Unit,
    fb: SnapshotStateMap<String, String>, onChoose: () -> Unit
) {
    if (model == null) {
        Column(Modifier.padding(14.dp)) {
            Text("No model chosen yet", fontWeight = FontWeight.Bold)
            Text("Pick a model first and the app will suggest parts, paints, tools and tutorials for it.")
            Spacer(Modifier.height(10.dp))
            Button(onClick = onChoose) { Text("Choose a model") }
        }
        return
    }
    val uri = LocalUriHandler.current
    val parts = Recommender.rank(model.parts, region, overseas)
    val paints = Recommender.rank(model.paints, region, overseas)
    val tools = Recommender.rank(Recommender.toolsFor(model), region, overseas)
    val brushes = Recommender.rank(BRUSHES, region, overseas)
    val hidden = when (tab) {
        0 -> model.parts.size - parts.size
        1 -> model.paints.count { p -> p.alt != null && !Recommender.visible(p.alt.brand, region, overseas) }
        2 -> (TOOLS.size - tools.size) + (BRUSHES.size - brushes.size)
        else -> 0
    }

    LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(model.name, fontWeight = FontWeight.Bold)
                            Text("Planned look: ${model.scheme}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedButton(onClick = onChoose) { Text("Change") }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("I live in", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Region.entries.forEach { r ->
                            FilterChip(selected = region == r, onClick = { onRegion(r) }, label = { Text(r.label) })
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = overseas, onCheckedChange = onOverseas)
                        Spacer(Modifier.width(8.dp))
                        Text("Include overseas shops")
                    }
                }
            }
        }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Parts", "Paints", "Tools and brushes", "Tutorials").forEachIndexed { i, label ->
                    FilterChip(selected = tab == i, onClick = { onTab(i) }, label = { Text(label) })
                }
            }
        }
        when (tab) {
            0 -> {
                if (model.parts.isEmpty()) item {
                    InfoCard("No confident matches yet", "The app found no third-party parts that fit this kit well. Tools and paints may still help.")
                }
                items(parts, key = { it.name }) { p -> ProductCard("${model.id}-p-${p.name}", p, region, fb) }
            }
            1 -> items(paints, key = { it.name }) { p ->
                ProductCard("${model.id}-c-${p.name}", p, region, fb) {
                    val a = p.alt
                    if (a != null && Recommender.visible(a.brand, region, overseas)) {
                        val ship = if (Recommender.isLocal(a.brand, region)) "" else " (+${money(SHIPPING)} shipping)"
                        Text(
                            "Value option: ${a.brand.label}, ${a.name} ${money(a.price)}, saves ${money(p.price - a.price)}$ship",
                            Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            2 -> {
                item { Text("Tools", fontWeight = FontWeight.Bold) }
                items(tools, key = { "t" + it.name }) { p -> ProductCard("${model.id}-t-${p.name}", p, region, fb) }
                item { Text("Brushes", fontWeight = FontWeight.Bold) }
                items(brushes, key = { "b" + it.name }) { p -> ProductCard("${model.id}-b-${p.name}", p, region, fb) }
            }
            else -> {
                items(model.tutorials, key = { it.title }) { t ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Text(t.title, fontWeight = FontWeight.Bold)
                            Text("Video, about ${t.length}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = { uri.openUri("https://www.youtube.com/results?search_query=" + Uri.encode(t.query)) }) {
                                Text("Find on YouTube")
                            }
                            FeedbackRow("${model.id}-v-${t.title}", fb)
                        }
                    }
                }
                item { Text("In the full app these would be specific videos chosen for this model. Here each button opens a YouTube search.", style = MaterialTheme.typography.bodySmall) }
            }
        }
        if (hidden > 0) item {
            InfoCard("$hidden item${if (hidden > 1) "s are" else " is"} hidden", "Not sold in ${region.label}. Turn on “Include overseas shops” to see ${if (hidden > 1) "them" else "it"}.")
        }
        item { Text("Mockup with sample data. Match scores are computed from made-up inputs to show how the model would explain itself.", style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
fun InfoCard(title: String, body: String) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(body)
        }
    }
}

@Composable
fun ProductCard(
    key: String, p: Product, region: Region, fb: SnapshotStateMap<String, String>,
    extra: @Composable ColumnScope.() -> Unit = {}
) {
    val score = Recommender.match(p)
    val color = if (score >= 70) OK else WARN
    Card(Modifier.fillMaxWidth().alpha(if (fb[key] == "down") 0.45f else 1f)) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(p.name, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(money(p.price), fontWeight = FontWeight.Bold)
            }
            Text(p.brand.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(Recommender.label(score), color = color, fontWeight = FontWeight.Bold)
                Text("$score% match", color = color)
            }
            Box(
                Modifier.padding(vertical = 6.dp).fillMaxWidth().height(5.dp).clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(Modifier.fillMaxWidth(score / 100f).fillMaxHeight().background(color))
            }
            Text(p.reason)
            extra()
            if (p.warning != null) Text(p.warning, Modifier.padding(top = 8.dp), color = WARN)
            if (!Recommender.isLocal(p.brand, region))
                Text("Ships from overseas, about +${money(SHIPPING)}", Modifier.padding(top = 8.dp), color = WARN, fontWeight = FontWeight.Bold)
            FeedbackRow(key, fb)
        }
    }
}

@Composable
fun FeedbackRow(key: String, fb: SnapshotStateMap<String, String>) {
    Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Useful?")
        FeedbackButton("Yes", fb[key] == "up") { if (fb[key] == "up") fb.remove(key) else fb[key] = "up" }
        FeedbackButton("Not for me", fb[key] == "down") { if (fb[key] == "down") fb.remove(key) else fb[key] = "down" }
    }
}

@Composable
fun FeedbackButton(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) Button(onClick = onClick) { Text(label) }
    else OutlinedButton(onClick = onClick) { Text(label) }
}
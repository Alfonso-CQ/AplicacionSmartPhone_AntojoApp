package com.example.p9_navcontroller

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.p9_navcontroller.antojoapp.AntojoApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navegacion()
        }
    }
}

data class AppItem(val nombre: String, val ruta: String, val icono: Int)
data class BotonGrande(val nombre: String, val ruta: String, val icono: Int)

// ==================== PANTALLA 1: BLOQUEO ====================
@Composable
fun PantallaBloqueo(navController: NavController) {
    val currentDate = remember { Date() }
    val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale.getDefault())

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0B0E14)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = timeFormat.format(currentDate), fontSize = 72.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00FFCC), textAlign = TextAlign.Center)
        Text(text = dateFormat.format(currentDate).replaceFirstChar { it.uppercase() }, fontSize = 24.sp, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp, bottom = 80.dp))
        Button(onClick = { navController.navigate("PantallaInicioCelular") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC))) {
            Text(text = "Desbloquear Celular", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B0E14))
        }
    }
}

// ==================== PANTALLA 2: INICIO CELULAR CON PAGINAS ====================
@Composable
fun PantallaInicioCelular(navController: NavController) {
    var paginaActual by remember { mutableStateOf(0) }

    val pagina1 = listOf(
        BotonGrande("Teléfono", "PantallaTelefono", R.drawable.ic_app_telefono),
        BotonGrande("Calculadora", "PantallaCalculadora", R.drawable.ic_app_calculadora),
        BotonGrande("Cámara", "PantallaCamara", R.drawable.ic_app_camara)
    )

    val pagina2 = listOf(
        BotonGrande("Classroom", "PantallaClassroom", R.drawable.ic_app_classroom),
        BotonGrande("YouTube", "PantallaYoutube", R.drawable.ic_app_youtube),
        BotonGrande("Play Store", "PantallaPlayStore", R.drawable.ic_app_playstore)
    )

    val pagina3 = listOf(
        BotonGrande("AntojoApp", "PantallaAntojoApp", R.drawable.ic_app_antojo)
    )
    val paginas = listOf(pagina1, pagina2, pagina3)
    val dockApps = listOf(
        AppItem("Teléfono", "PantallaTelefono", R.drawable.ic_app_telefono),
        AppItem("Calculadora", "PantallaCalculadora", R.drawable.ic_app_calculadora),
        AppItem("Cámara", "PantallaCamara", R.drawable.ic_app_camara)
    )

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF151A26)).padding(20.dp)) {
        Text(text = "17:00", fontSize = 16.sp, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp))
        Text(text = "Dispositivo Android", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 16.dp))
        Text(text = "Página ${paginaActual + 1} de ${paginas.size}", fontSize = 13.sp, color = Color(0xFF00FFCC))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val paginaActualApps = paginas[paginaActual]
            items(paginaActualApps.chunked(2)) { rowApps ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    rowApps.forEach { boton ->
                        BotonGrandeCard(boton = boton, onClick = { navController.navigate(boton.ruta) }, modifier = Modifier.weight(1f))
                    }
                    if (rowApps.size == 1) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { if (paginaActual > 0) paginaActual-- }, enabled = paginaActual > 0, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))) {
                Text("◀ Ant.", color = Color.White)
            }
            Button(onClick = { if (paginaActual < paginas.size - 1) paginaActual++ }, enabled = paginaActual < paginas.size - 1, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)), modifier = Modifier.padding(start = 24.dp)) {
                Text("Sig. ▶", color = Color.White)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF0B0E14)).padding(vertical = 14.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            dockApps.forEach { app ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { navController.navigate(app.ruta) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = app.icono),
                            contentDescription = app.nombre,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BotonGrandeCard(boton: BotonGrande, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.height(110.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B0E14).copy(alpha = 0.8f)),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = boton.icono),
                    contentDescription = boton.nombre,
                    modifier = Modifier.size(44.dp)
                )
                Text(
                    text = boton.nombre,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

// ==================== HIPERVÍNCULOS QUE SE ABREN SOLOS (WEBVIEW) ====================
// Se mantiene exclusivamente para Classroom, YouTube y Play Store.
@Composable
fun PantallaHipervinculoAutoload(navController: NavController, url: String, tituloApp: String) {
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF151A26))
                .padding(top = 40.dp, bottom = 12.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252D36))) {
                Text("◀ Volver", color = Color(0xFF00FFCC))
            }
            Text(tituloApp, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
        }

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        // ✅ Firma correcta sin WebResourceRequest
                        @Suppress("DEPRECATION")
                        override fun shouldOverrideUrlLoading(view: WebView?, urlString: String?): Boolean {
                            urlString?.let { view?.loadUrl(it) }
                            return true
                        }
                    }
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true          // ✅ Habilita base de datos local
                        cacheMode = WebSettings.LOAD_DEFAULT  // ✅ Usa caché normal
                        allowContentAccess = true
                        allowFileAccess = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/120.0.0.0 Mobile Safari/537.36"
                    }
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable fun PantallaClassroom(navController: NavController) { PantallaHipervinculoAutoload(navController, "https://classroom.google.com", "Google Classroom") }
@Composable fun PantallaYoutube(navController: NavController) { PantallaHipervinculoAutoload(navController, "https://m.youtube.com", "YouTube") }
@Composable fun PantallaPlayStore(navController: NavController) { PantallaHipervinculoAutoload(navController, "https://play.google.com/store", "Google Play Store") }

// ==================== FUNCION: TELÉFONO ====================
@Composable
fun PantallaTelefono(navController: NavController) {
    var numeroMarcado by remember { mutableStateOf("") }
    var estadoLlamada by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF12161A)).padding(top = 40.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252D36))) {
                Text("◀", color = Color.White)
            }
            Text("Llamada Telefónica", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
        }

        if (estadoLlamada.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Llamando...", color = Color(0xFF00FFCC), fontSize = 18.sp)
                Text(estadoLlamada, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 40.dp))
                Button(onClick = { estadoLlamada = "" }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Colgar", color = Color.White)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = numeroMarcado.ifEmpty { "Introduce el número" }, color = Color.White, fontSize = 32.sp, modifier = Modifier.padding(bottom = 20.dp))

                val botonesTeclado = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("*", "0", "#"))
                botonesTeclado.forEach { fila ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        fila.forEach { num ->
                            Button(onClick = { numeroMarcado += num }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252D36))) {
                                Text(num, color = Color.White, fontSize = 20.sp)
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { if (numeroMarcado.isNotEmpty()) estadoLlamada = numeroMarcado }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))) {
                        Text("Llamar", color = Color.White)
                    }
                    Button(onClick = { numeroMarcado = "" }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray), modifier = Modifier.padding(start = 20.dp)) {
                        Text("Borrar", color = Color.White)
                    }
                }
            }
        }
    }
}

// ==================== DISEÑO TRADICIONAL ANTIGUO: CALCULADORA ====================
// ==================== DISEÑO ESTILO XIAOMI (MIUI) OSCURO: CALCULADORA ====================
@Composable
fun PantallaCalculadora(navController: NavController) {
    var numeroActual by remember { mutableStateOf("") }
    var primerValor by remember { mutableStateOf(0.0) }
    var operacionPendiente by remember { mutableStateOf("") }
    var mostrandoResultado by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val colorFondoPantalla = Color(0xFF000000)
    val colorBotonNormal = Color(0xFF282828)
    val colorNaranja = Color(0xFFFF9500)
    val colorCursor = Color(0xFF3E7BFA)
    val colorError = Color(0xFFFF5252)

    // Convierte un Double a texto sin ".0" innecesario, para mostrar el número anterior arriba
    fun formatearNumero(valor: Double): String {
        return if (valor % 1 == 0.0) valor.toInt().toString() else valor.toString()
    }

    fun calcularResultado() {
        val segundoValor = numeroActual.toDoubleOrNull() ?: 0.0

        // ---- EXCEPCIÓN: división entre 0 ----
        if (operacionPendiente == "÷" && segundoValor == 0.0) {
            mensajeError = "No se puede dividir entre 0"
            numeroActual = ""
            operacionPendiente = ""
            mostrandoResultado = false
            return
        }

        val res = when (operacionPendiente) {
            "+" -> primerValor + segundoValor
            "-" -> primerValor - segundoValor
            "×" -> primerValor * segundoValor
            "÷" -> primerValor / segundoValor
            else -> segundoValor
        }

        // ---- EXCEPCIÓN: resultado infinito, NaN o demasiado grande ----
        if (res.isNaN() || res.isInfinite()) {
            mensajeError = "Operación no válida"
            numeroActual = ""
            operacionPendiente = ""
            mostrandoResultado = false
            return
        }
        if (kotlin.math.abs(res) > 1e15) {
            mensajeError = "Número demasiado grande"
            numeroActual = ""
            operacionPendiente = ""
            mostrandoResultado = false
            return
        }

        numeroActual = formatearNumero(res)
        operacionPendiente = ""
        mostrandoResultado = true
    }

    fun cambiarSigno() {
        val valor = numeroActual.toDoubleOrNull() ?: return
        if (valor == 0.0) return
        numeroActual = formatearNumero(-valor)
    }

    Column(modifier = Modifier.fillMaxSize().background(colorFondoPantalla)) {

        // ---------- Barra superior: Calculadora / Convertidor ----------
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                modifier = Modifier.width(36.dp).height(36.dp)
            ) {
                Text("⤡", fontSize = 18.sp, color = Color.White)
            }
            Text(text = "Calculadora", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(start = 12.dp))
            Text(text = "Convertidor", fontSize = 21.sp, fontWeight = FontWeight.Normal, color = Color.White.copy(alpha = 0.45f), modifier = Modifier.padding(start = 18.dp))
            Row(modifier = Modifier.weight(1f)) {}
            Text(text = "⋮", fontSize = 20.sp, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.padding(end = 8.dp))
        }

        // ---------- Pantalla numérica con cursor ----------
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            // Línea pequeña que muestra "1 +" mientras esperas el segundo número
            if (mensajeError == null && operacionPendiente.isNotEmpty()) {
                Text(
                    text = "${formatearNumero(primerValor)} $operacionPendiente",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            if (mensajeError != null) {
                // ---------- Mensaje de error (división entre 0, overflow, etc.) ----------
                Text(
                    text = mensajeError ?: "",
                    color = colorError,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            } else {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = numeroActual.ifEmpty { "0" },
                        color = Color.White,
                        fontSize = 76.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.End
                    )
                    Row(modifier = Modifier.padding(start = 4.dp, bottom = 14.dp).width(2.dp).height(58.dp).background(colorCursor)) {}
                }
                Row(modifier = Modifier.padding(top = 6.dp, bottom = 28.dp).width(64.dp).height(2.dp).background(colorCursor)) {}
            }
        }

        // ---------- Teclado ----------
        val matrizBotones = listOf(
            listOf("AC", "⌫", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("↺", "0", ".", "=")
        )

        Column(
            modifier = Modifier.fillMaxWidth().weight(2.5f).padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            matrizBotones.forEach { fila ->
                Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    fila.forEach { simbolo ->
                        val esIgual = simbolo == "="
                        val esOperadorNaranja = listOf("÷", "×", "-", "+", "%", "AC", "⌫", "↺").contains(simbolo)
                        val colorFondo = if (esIgual) colorNaranja else colorBotonNormal
                        val colorTexto = if (esIgual) Color.White else if (esOperadorNaranja) colorNaranja else Color.White

                        Button(
                            onClick = {
                                // Cualquier tecla (menos AC) limpia un mensaje de error previo
                                if (mensajeError != null && simbolo != "AC") {
                                    mensajeError = null
                                    numeroActual = ""
                                    primerValor = 0.0
                                    operacionPendiente = ""
                                }

                                when (simbolo) {
                                    "AC" -> {
                                        numeroActual = ""; primerValor = 0.0; operacionPendiente = ""
                                        mostrandoResultado = false; mensajeError = null
                                    }
                                    "⌫" -> { if (numeroActual.isNotEmpty()) numeroActual = numeroActual.dropLast(1) }
                                    "%" -> {
                                        if (numeroActual.isNotEmpty()) {
                                            numeroActual = formatearNumero((numeroActual.toDoubleOrNull() ?: 0.0) / 100)
                                        }
                                    }
                                    "↺" -> { cambiarSigno() }
                                    "+", "-", "×", "÷" -> {
                                        // ---- EXCEPCIÓN: encadenar operación sin escribir número ----
                                        if (numeroActual.isEmpty() && operacionPendiente.isNotEmpty()) {
                                            // Solo cambia el operador pendiente, no rompe la cuenta
                                            operacionPendiente = simbolo
                                        } else {
                                            if (operacionPendiente.isNotEmpty() && numeroActual.isNotEmpty()) {
                                                calcularResultado()
                                                primerValor = numeroActual.toDoubleOrNull() ?: primerValor
                                            } else {
                                                primerValor = numeroActual.toDoubleOrNull() ?: primerValor
                                            }
                                            operacionPendiente = simbolo
                                            numeroActual = ""
                                            mostrandoResultado = false
                                        }
                                    }
                                    "=" -> { if (operacionPendiente.isNotEmpty()) calcularResultado() }
                                    "." -> { if (!numeroActual.contains(".")) numeroActual += if (numeroActual.isEmpty()) "0." else "." }
                                    else -> {
                                        if (mostrandoResultado) { numeroActual = ""; mostrandoResultado = false }
                                        // ---- EXCEPCIÓN: limitar longitud del número (evita overflow visual) ----
                                        if (numeroActual.replace("-", "").replace(".", "").length < 15) {
                                            numeroActual += simbolo
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colorFondo),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        ) {
                            Text(simbolo, color = colorTexto, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

// ==================== FUNCION: CÁMARA REAL CAPTURABLE ====================
@Composable
fun PantallaCamara(navController: NavController) {
    val fotosTomadas = remember { mutableStateListOf<Bitmap>() }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotosTomadas.add(0, bitmap)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(top = 40.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252D36))) {
                Text("◀ Atrás", color = Color.White)
            }
            Text("Cámara Nativa", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFF15191E)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Toca el botón de abajo para capturar foto", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
        }

        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            LazyRow(modifier = Modifier.fillMaxWidth().height(70.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(fotosTomadas) { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto",
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Button(onClick = { cameraLauncher.launch() }, colors = ButtonDefaults.buttonColors(containerColor = Color.White), modifier = Modifier.padding(top = 20.dp)) {
                Text("Tomar Foto", color = Color.Black)
            }
        }
    }
}

// ==================== ENRUTADOR PRINCIPAL (NAVHOST) ====================
@Composable
fun Navegacion() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "PantallaBloqueo") {
        composable("PantallaBloqueo") { PantallaBloqueo(navController) }
        composable("PantallaInicioCelular") { PantallaInicioCelular(navController) }
        composable("PantallaTelefono") { PantallaTelefono(navController) }
        composable("PantallaCalculadora") { PantallaCalculadora(navController) }
        composable("PantallaCamara") { PantallaCamara(navController) }
        composable("PantallaClassroom") { PantallaClassroom(navController) }
        composable("PantallaYoutube") { PantallaYoutube(navController) }
        composable("PantallaPlayStore") { PantallaPlayStore(navController) }
        composable("PantallaAntojoApp") { AntojoApp(onSalir = { navController.popBackStack() }) }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() { Navegacion() }

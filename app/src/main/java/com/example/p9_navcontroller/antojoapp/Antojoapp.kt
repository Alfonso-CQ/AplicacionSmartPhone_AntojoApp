package com.example.p9_navcontroller.antojoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ===================== CONFIGURACIÓN DE COLORES =====================
val Naranja = Color(0xFFFF7043)
val NaranjaClaro = Color(0xFFFFAB91)
val Crema = Color(0xFFFFFDD0)
val GrisOscuro = Color(0xFF212121)
val GrisMedio = Color(0xFF757575)
val GrisClaro = Color(0xFFF5F5F5)
val Verde = Color(0xFF4CAF50)

// ===================== MODELOS DE DATOS =====================
data class Platillo(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val emoji: String,
    val calificacion: Double,
    val tiempoEntrega: Int,
    val estadosDeAnimo: List<String>
)

data class ItemCarrito(
    val platillo: Platillo,
    var cantidad: Int
)

data class EstadoAnimo(
    val nombre: String,
    val emoji: String,
    val descripcion: String,
    val estadosClave: List<String>
)

data class Restaurante(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val platillos: List<Platillo>
)

// ===================== COMPONENTES REUTILIZABLES =====================
@Composable
fun InfoChip(texto: String, colorTexto: Color) {
    Text(texto, color = colorTexto, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 10.dp))
}

// ===================== PANTALLA DETALLE PLATILLO =====================
@Composable
fun PantallaDetallePlatillo(
    navController: NavController,
    platillo: Platillo,
    carrito: MutableList<ItemCarrito>
) {
    var cantidad by remember { mutableStateOf(1) }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Crema)) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Button(onClick = { navController.popBackStack() }) {
                        Text("◀ Volver")
                    }
                }
                Text(platillo.emoji, fontSize = 90.sp, modifier = Modifier.padding(top = 12.dp))
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(platillo.nombre, color = GrisOscuro, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(platillo.descripcion, color = GrisMedio, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                    Text("\$${platillo.precio.toInt()}", color = Naranja, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                Row(modifier = Modifier.padding(top = 12.dp)) {
                    InfoChip("⭐ ${platillo.calificacion}", Color(0xFFF57F17))
                    InfoChip("🕐 ${platillo.tiempoEntrega} min", Color(0xFF1565C0))
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text("Cantidad", color = GrisOscuro, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { if (cantidad > 1) cantidad-- }, enabled = cantidad > 1) {
                        Text("－")
                    }
                    Text(
                        "$cantidad",
                        color = GrisOscuro, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )
                    Button(onClick = { cantidad++ }, colors = ButtonDefaults.buttonColors(containerColor = Naranja)) {
                        Text("＋")
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        val existente = carrito.find { it.platillo.id == platillo.id }
                        if (existente != null) existente.cantidad += cantidad
                        else carrito.add(ItemCarrito(platillo, cantidad))
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Naranja),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Agregar al carrito  •  \$${(platillo.precio * cantidad).toInt()}",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ===================== MODO ANTOJO (FEATURE ÚNICO) =====================
@Composable
fun PantallaModoAntojo(
    navController: NavController,
    onPlatilloSeleccionado: (Platillo) -> Unit
) {
    var estadoSeleccionado by remember { mutableStateOf<EstadoAnimo?>(null) }

    val recomendaciones = remember(estadoSeleccionado) {
        estadoSeleccionado?.let { estado ->
            catalogoRestaurantes
                .flatMap { it.platillos }
                .filter { platillo ->
                    platillo.estadosDeAnimo.any { it in estado.estadosClave }
                }
                .shuffled()
                .take(6)
        } ?: emptyList()
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Crema)) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 28.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { navController.popBackStack() }) {
                        Text("◀")
                    }
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text("✨ Modo Antojo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Pedimos según cómo te sientes", color = GrisMedio, fontSize = 13.sp)
                    }
                }

                Text("¿Cómo te sientes ahorita?", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 20.dp, bottom = 14.dp))

                estadosDeAnimo.chunked(3).forEach { fila ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        fila.forEach { estado ->
                            val seleccionado = estadoSeleccionado?.nombre == estado.nombre
                            Button(
                                onClick = { estadoSeleccionado = if (seleccionado) null else estado },
                                colors = ButtonDefaults.buttonColors(containerColor = if (seleccionado) Naranja else GrisClaro),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(estado.emoji, fontSize = 22.sp)
                                    Text(estado.nombre, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                    Row(modifier = Modifier.padding(bottom = 10.dp)) {}
                }
            }
        }

        if (estadoSeleccionado == null) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("☝️", fontSize = 48.sp)
                    Text("Selecciona cómo te sientes y te mostraremos los mejores platillos para ti", color = GrisMedio, fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 20.sp, modifier = Modifier.padding(top = 12.dp))
                }
            }
        } else {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text("Recomendado para ti ${estadoSeleccionado!!.emoji}", color = GrisOscuro, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(estadoSeleccionado!!.descripcion, color = GrisMedio, fontSize = 13.sp)
                }
            }

            if (recomendaciones.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😅", fontSize = 40.sp)
                        Text("Sin recomendaciones por ahora", color = GrisMedio, fontSize = 14.sp)
                    }
                }
            } else {
                items(recomendaciones) { platillo ->
                    val restaurante = catalogoRestaurantes.find { r -> r.platillos.any { it.id == platillo.id } }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { onPlatilloSeleccionado(platillo) }, modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(platillo.emoji, fontSize = 28.sp)
                                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(platillo.nombre, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(restaurante?.nombre ?: "", fontSize = 12.sp)
                                    Row {
                                        Text("⭐ ${platillo.calificacion}", fontSize = 11.sp, modifier = Modifier.padding(end = 6.dp))
                                        Text("🕐 ${platillo.tiempoEntrega} min", fontSize = 11.sp)
                                    }
                                }
                                Text("\$${platillo.precio.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                item { Row(modifier = Modifier.padding(top = 24.dp)) {} }
            }
        }
    }
}

// ===================== PANTALLA CARRITO =====================
@Composable
fun PantallaCarrito(navController: NavController, carrito: MutableList<ItemCarrito>) {
    val subtotal = carrito.sumOf { it.platillo.precio * it.cantidad }
    val envio = if (carrito.isNotEmpty()) 35.0 else 0.0
    val total = subtotal + envio

    LazyColumn(modifier = Modifier.fillMaxSize().background(Crema)) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { navController.popBackStack() }) {
                        Text("◀")
                    }
                    Text("Mi pedido 🛒", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 14.dp))
                }
            }
        }

        if (carrito.isEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 64.sp)
                    Text("Tu carrito está vacío", color = GrisOscuro, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                    Text("Agrega algo rico para empezar", color = GrisMedio, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 20.dp))
                    Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Naranja)) {
                        Text("Explorar restaurantes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(carrito) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.platillo.emoji, fontSize = 36.sp)
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(item.platillo.nombre, color = GrisOscuro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("\$${item.platillo.precio.toInt()} c/u", color = GrisMedio, fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                if (item.cantidad > 1) item.cantidad-- else carrito.remove(item)
                            },
                            contentPadding = ButtonDefaults.TextButtonContentPadding
                        ) {
                            Text("－")
                        }
                        Text("${item.cantidad}", color = GrisOscuro, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                        Button(
                            onClick = { item.cantidad++ },
                            colors = ButtonDefaults.buttonColors(containerColor = Naranja),
                            contentPadding = ButtonDefaults.TextButtonContentPadding
                        ) {
                            Text("＋")
                        }
                    }
                    Text("\$${(item.platillo.precio * item.cantidad).toInt()}", color = Naranja, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp).width(52.dp), textAlign = TextAlign.End)
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text("Resumen", color = GrisOscuro, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    FilaResumen("Subtotal", "\$${subtotal.toInt()}")
                    FilaResumen("Envío", "\$${envio.toInt()}")
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", color = GrisOscuro, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("\$${total.toInt()}", color = Naranja, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Button(
                        onClick = { navController.navigate("confirmacion") },
                        colors = ButtonDefaults.buttonColors(containerColor = Naranja),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ordenar ahora", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(modifier = Modifier.padding(top = 24.dp)) {}
            }
        }
    }
}

@Composable
fun FilaResumen(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = GrisMedio, fontSize = 14.sp)
        Text(valor, color = GrisOscuro, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ===================== PANTALLA CONFIRMACIÓN =====================
@Composable
fun PantallaConfirmacion(navController: NavController, carrito: MutableList<ItemCarrito>) {
    val pasos = listOf(
        "✅" to "Pedido recibido",
        "👨‍🍳" to "Preparando tu comida",
        "🛵" to "En camino a tu casa",
        "🏠" to "¡Entregado!"
    )

    LazyColumn(modifier = Modifier.fillMaxSize().background(Crema), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 80.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎉", fontSize = 72.sp)
                Text("¡Pedido confirmado!", color = Verde, fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                Text("Tiempo estimado: 35-45 min", color = GrisMedio, fontSize = 15.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text("Estado de tu pedido", color = GrisOscuro, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    pasos.forEachIndexed { index, (emoji, texto) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(emoji, fontSize = 22.sp)
                            Column(modifier = Modifier.padding(start = 14.dp)) {
                                Text(texto, color = if (index == 0) Verde else GrisMedio, fontSize = 14.sp, fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal)
                                if (index == 0) Text("Ahora mismo", color = Verde, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
                Button(
                    onClick = {
                        carrito.clear()
                        navController.navigate("inicio") {
                            popUpTo("inicio") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Naranja),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al inicio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Row(modifier = Modifier.padding(top = 32.dp)) {}
        }
    }
}

// ===================== VARIABLES DE CATÁLOGO PROVISIONALES =====================
val estadosDeAnimo = listOf(

    EstadoAnimo(
        "Feliz",
        "😁",
        "Celebra tu buen humor con algo delicioso",
        listOf("feliz")
    ),

    EstadoAnimo(
        "Estresado",
        "🤯",
        "Algo dulce y reconfortante puede ayudarte a relajarte",
        listOf("estresado")
    ),

    EstadoAnimo(
        "Cansado",
        "😴",
        "Necesitas algo que te dé un pequeño impulso de energía",
        listOf("cansado", "energia")
    ),

    EstadoAnimo(
        "Con Antojo",
        "😋",
        "Cuando simplemente quieres darte un gusto",
        listOf("antojo")
    ),

    EstadoAnimo(
        "Triste",
        "🥺",
        "Un postre o una bebida caliente pueden hacer más agradable el momento",
        listOf("triste")
    )
)

val catalogoRestaurantes = listOf(

    Restaurante(
        id = 1,
        nombre = "Antojitos del Callejón",
        direccion = "Callejón de los Sapos 5, Centro Histórico, Puebla",
        platillos = listOf(
            Platillo(
                1,
                "Chalupas Poblanas",
                "Con salsa verde y roja",
                65.0,
                "🌮",
                4.8,
                20,
                listOf("feliz", "antojo")
            ),
            Platillo(
                2,
                "Pelona Poblana",
                "Pan crujiente con carne deshebrada y lechuga",
                70.0,
                "🥙",
                4.7,
                20,
                listOf("feliz", "energia")
            ),
            Platillo(
                3,
                "Tostadas de Tinga",
                "Con pollo deshebrado y crema",
                60.0,
                "🥗",
                4.6,
                18,
                listOf("antojo", "energia")
            )
        )
    ),

    Restaurante(
        id = 2,
        nombre = "Burger Factory",
        direccion = "Av. Juárez 1520, Col. La Paz, Puebla",
        platillos = listOf(
            Platillo(
                4,
                "Hamburguesa Doble",
                "Con extra queso y tocino",
                120.0,
                "🍔",
                4.8,
                30,
                listOf("antojo", "feliz")
            ),
            Platillo(
                5,
                "Papas Supremas",
                "Papas con queso y tocino",
                85.0,
                "🍟",
                4.7,
                20,
                listOf("feliz", "antojo")
            )
        )
    ),

    Restaurante(
        id = 3,
        nombre = "Café Sereno",
        direccion = "2 Norte 604, Centro, Puebla",
        platillos = listOf(
            Platillo(
                6,
                "Café Capuchino",
                "Café caliente con espuma de leche",
                50.0,
                "☕",
                4.9,
                12,
                listOf("cansado", "estresado")
            ),
            Platillo(
                7,
                "Chocolate Caliente",
                "Chocolate cremoso y reconfortante",
                55.0,
                "🍫",
                4.8,
                12,
                listOf("estresado", "triste")
            )
        )
    ),

    Restaurante(
        id = 4,
        nombre = "Dulce Momento",
        direccion = "Blvd. Atlixco 2320, Col. La Paz, Puebla",
        platillos = listOf(
            Platillo(
                8,
                "Cheesecake de Fresa",
                "Pastel de queso con mermelada de fresa",
                65.0,
                "🍰",
                4.8,
                15,
                listOf("estresado", "feliz")
            ),
            Platillo(
                9,
                "Brownie de Chocolate",
                "Brownie tibio con chispas de chocolate",
                45.0,
                "🍫",
                4.9,
                15,
                listOf("estresado", "triste")
            ),
            Platillo(
                10,
                "Malteada de Fresa",
                "Malteada cremosa con crema batida",
                55.0,
                "🥤",
                4.7,
                15,
                listOf("feliz", "antojo")
            )
        )
    )
)

@Composable
fun AntojoApp(onSalir: (() -> Unit)? = null) {

    val navController = rememberNavController()

    val carrito = remember {
        mutableStateListOf<ItemCarrito>()
    }

    val platilloSeleccionado = remember {
        mutableStateOf<Platillo?>(null)
    }

    val restauranteSeleccionado = remember {
        mutableStateOf<Restaurante?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        composable("inicio") {
            PantallaInicio(
                navController = navController,
                onSalir = onSalir,
                carrito = carrito,

                onPlatilloSeleccionado = {
                    platilloSeleccionado.value = it
                    navController.navigate("detalle")
                },

                onRestauranteSeleccionado = {
                    restauranteSeleccionado.value = it
                    navController.navigate("restaurante")
                }
            )
        }

        composable("restaurante") {

            restauranteSeleccionado.value?.let { restaurante ->

                PantallaRestaurante(
                    navController = navController,
                    restaurante = restaurante,

                    onPlatilloSeleccionado = { platillo ->
                        platilloSeleccionado.value = platillo
                        navController.navigate("detalle")
                    }
                )
            }
        }

        composable("detalle") {

            val platillo = platilloSeleccionado.value

            if (platillo != null) {
                PantallaDetallePlatillo(
                    navController = navController,
                    platillo = platillo,
                    carrito = carrito
                )
            }
        }


        composable("carrito") {
            PantallaCarrito(
                navController = navController,
                carrito = carrito
            )
        }

        composable("confirmacion") {
            PantallaConfirmacion(
                navController = navController,
                carrito = carrito
            )
        }

        composable("modoAntojo") {
            PantallaModoAntojo(
                navController = navController,
                onPlatilloSeleccionado = { platillo ->
                    platilloSeleccionado.value = platillo
                    navController.navigate("detalle")
                }
            )
        }
    }
}

@Composable
fun PantallaRestaurante(
    navController: NavController,
    restaurante: Restaurante,
    onPlatilloSeleccionado: (Platillo) -> Unit
) {

    LazyColumn(modifier = Modifier.fillMaxSize().background(Crema)) {

        item {

            Column(modifier = Modifier.fillMaxWidth().background(Naranja).padding(20.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f))) {
                        Text("◀", color = Color.White)
                    }
                    Text(text = "🏪", fontSize = 36.sp, modifier = Modifier.padding(start = 16.dp))
                }

                Text(
                    text = restaurante.nombre,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )

                Text(
                    text = "📍 ${restaurante.direccion}",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Text(
                    text = "${restaurante.platillos.size} platillos disponibles",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        items(restaurante.platillos) { platillo ->

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { onPlatilloSeleccionado(platillo) }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                        Text(text = "🍽️", fontSize = 30.sp)

                        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {

                            Text(
                                text = platillo.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Text(
                                text = platillo.descripcion,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Text(
                                text = "⭐ ${platillo.calificacion} • ${platillo.tiempoEntrega} min",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Text(
                                text = "$${platillo.precio}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Ubicación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GrisOscuro
                )
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "📍", fontSize = 22.sp)
                    Text(
                        text = restaurante.direccion,
                        fontSize = 15.sp,
                        color = GrisOscuro,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
        }

        item {
            Row(modifier = Modifier.padding(top = 24.dp)) {}
        }
    }
}

@Composable
fun PantallaInicio(
    navController: NavController,
    carrito: MutableList<ItemCarrito>,
    onPlatilloSeleccionado: (Platillo) -> Unit,
    onRestauranteSeleccionado: (Restaurante) -> Unit,
    onSalir: (() -> Unit)? = null
) {

    LazyColumn(modifier = Modifier.fillMaxSize().background(Crema)) {

        // Encabezado
        item {
            Column(modifier = Modifier.fillMaxWidth().background(Naranja).padding(20.dp)) {

                if (onSalir != null) {
                    Row(modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)) {
                        Button(onClick = { onSalir() }, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f))) {
                            Text("◀ Volver", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Row(modifier = Modifier.padding(top = 20.dp)) {}
                }

                Text(
                    text = "🍔 AntojoApp",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "¿Qué se te antoja hoy?",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                )
            }
        }

        // Modo Antojo
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Button(onClick = { navController.navigate("modoAntojo") }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 30.sp)
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(text = "Modo Antojo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "Encuentra comida según tu estado de ánimo", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Restaurantes
        item {
            Text(
                text = "Restaurantes cerca",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GrisOscuro
            )
        }

        items(catalogoRestaurantes) { restaurante ->

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                Button(onClick = { onRestauranteSeleccionado(restaurante) }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏪", fontSize = 34.sp)
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(text = restaurante.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "📍 ${restaurante.direccion}", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            Text(text = "${restaurante.platillos.size} platillos disponibles", fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }

        // Platillos
        item {
            Text(
                text = "Platillos destacados",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GrisOscuro
            )
        }

        items(catalogoRestaurantes.flatMap { it.platillos }) { platillo ->

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                Button(onClick = { onPlatilloSeleccionado(platillo) }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                        Text(text = platillo.emoji, fontSize = 26.sp)

                        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {

                            Text(text = platillo.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = platillo.descripcion, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                            Text(text = "⭐ ${platillo.calificacion} • ${platillo.tiempoEntrega} min", fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                            Text(text = "$${platillo.precio}", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(top = 20.dp, bottom = 30.dp)) {
                Button(
                    onClick = { navController.navigate("carrito") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text(text = "🛒 Ver carrito (${carrito.size})")
                }
            }
        }
    }
}

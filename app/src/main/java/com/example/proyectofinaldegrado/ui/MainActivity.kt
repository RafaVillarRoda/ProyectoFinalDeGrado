package com.example.proyectofinaldegrado.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinaldegrado.MyApplication
import com.example.proyectofinaldegrado.R
import com.example.proyectofinaldegrado.ui.theme.ProyectoFinalDeGradoTheme
import com.example.proyectofinaldegrado.SessionManager
import com.example.proyectofinaldegrado.data.local.entity.Book
import com.example.proyectofinaldegrado.data.local.entity.Film
import com.example.proyectofinaldegrado.data.local.entity.MediaItem
import com.example.proyectofinaldegrado.data.local.entity.Serie
import com.example.proyectofinaldegrado.data.local.entity.UserFullLibrary
import com.example.proyectofinaldegrado.data.local.entity.UserLibraryItem
import com.example.proyectofinaldegrado.viewmodels.mainActivity.MainViewModel
import com.example.proyectofinaldegrado.viewmodels.mainActivity.MainViewModelFactory
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlin.time.ExperimentalTime


class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as MyApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalDeGradoTheme {
                val userLibraryState by mainViewModel.userLibrary.collectAsStateWithLifecycle()
                userLibraryState?.let {
                    MainAppScaffold(
                        userLibrary = it,
                        mainViewModel = mainViewModel
                    )
                }
            }
        }
    }


}

// --- Definiciones de la Navegación ---
enum class Destination(
    val route: String, val label: String, @DrawableRes val icon: Int, val contentDescription: String
) {
    HOME("home", "Home", R.drawable.home_icon, "Home"), LIBRARY(
        "biblioteca",
        "Biblioteca",
        R.drawable.library_icon,
        "Biblioteca"
    ),
    PROFILE("perfil", "Perfil", R.drawable.profile_icon, "Perfil"), ADD_ITEM("add_item", "", 0, "")
}

@Composable
fun MainAppScaffold(
    modifier: Modifier = Modifier, userLibrary: UserFullLibrary, mainViewModel: MainViewModel
) {
    val navController = rememberNavController()
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier, bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                Destination.entries.filter { it.icon != 0 }.forEachIndexed { index, destination ->
                    NavigationBarItem(selected = selectedDestination == index, onClick = {
                        if (selectedDestination != index) {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        }
                    }, icon = {
                        Icon(
                            painter = painterResource(id = destination.icon),
                            contentDescription = destination.contentDescription
                        )
                    }, label = { Text(destination.label) })
                }
            }
        }, floatingActionButton = {
            AddButton(navController = navController)
        }, floatingActionButtonPosition = FabPosition.End
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(contentPadding),
            userLibrary = userLibrary,
            mainViewModel = mainViewModel

        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    userLibrary: UserFullLibrary,
    mainViewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,

        ) {
        composable(Destination.HOME.route) { HomeScreen(userLibrary = userLibrary) }
        composable(Destination.LIBRARY.route) {
            libraryScreen(
                userLibrary = userLibrary,
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
        composable(Destination.PROFILE.route) { ProfileScreen(navController = navController) }
        dialog(route = Destination.ADD_ITEM.route) {
            AddDialog(

                onDismiss = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                mainViewModel = mainViewModel
            )
        }
    }
}

@Composable
fun HomeScreen(userLibrary: UserFullLibrary) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {

        if (userLibrary.libraryItems.isNotEmpty()) {
            item {

                LastestCarousel(userLibrary = userLibrary)

            }
        }

        if (userLibrary.libraryItems.isNotEmpty()) {
            val libraryItem = userLibrary.libraryItems
            items(userLibrary.books) { book ->

                val libraryItemInfo = libraryItem.find { libraryItem ->
                    libraryItem.itemId == book.bookTitle && libraryItem.itemType == "book"
                }


                FilledItemCard(item = book, libraryItem = libraryItemInfo)
            }
            items(userLibrary.films) { film ->

                val libraryItemInfo = libraryItem.find { libraryItem ->
                    libraryItem.itemId == film.filmTitle && libraryItem.itemType == "film"
                }


                FilledItemCard(item = film, libraryItem = libraryItemInfo)
            }
            items(userLibrary.series) { serie ->

                val libraryItemInfo = libraryItem.find { libraryItem ->
                    libraryItem.itemId == serie.serieTitle && libraryItem.itemType == "serie"
                }


                FilledItemCard(item = serie, libraryItem = libraryItemInfo)
            }
        }

    }
}

@Composable
fun FilledItemCard(item: MediaItem, libraryItem: UserLibraryItem?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row() {
                Image(
                    painter = painterResource(id = R.drawable.melvin_test_image),
                    contentDescription = "Imagen de Elemento",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RectangleShape) //
                        .border(2.dp, MaterialTheme.colorScheme.primary, RectangleShape)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val dateFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
                        dayOfMonth()
                        char('/')
                        monthNumber()
                        char('/')
                        year()
                    }
                    Text(text = "Titulo: ${item.title}", textAlign = TextAlign.Center)
                    Text(text = "Autor: ${item.author}", textAlign = TextAlign.Center)
                    Text(text = "Genero: ${item.genre}", textAlign = TextAlign.Center)
                    Text(text = "Numero de páginas: ${item.dur}", textAlign = TextAlign.Center)
                    Row() {
                        Text(text = "Calificación: ", textAlign = TextAlign.Center)
                        for (i in 1..5)

                            Icon(
                                imageVector = if (i <= libraryItem!!.rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Estrella $i",
                                tint = if (i <= libraryItem.rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                    }
                    Text(
                        text = "Fecha de adición: ${libraryItem?.additionDate?.format(dateFormat) ?: "Sin fecha de adición"}",
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastestCarousel(userLibrary: UserFullLibrary) {

    val libraryItem = userLibrary.libraryItems


    data class CarouselItem(
        val id: Int, @DrawableRes val imageResId: Int, val contentDescription: String
    )

    val items = remember {


        val lastAdded = libraryItem.sortedBy { it.additionDate }.takeLast(5)


        lastAdded.mapIndexed { index, item ->
            CarouselItem(
                id = index,
                imageResId = R.drawable.melvin_test_image,
                contentDescription = item.itemId
            )
        }
    }
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp
    ) { i ->
        val item = items[i]
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.extraLarge),
            painter = painterResource(id = item.imageResId),
            contentDescription = item.contentDescription,
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun AddButton(navController: NavHostController) {
    FloatingActionButton(
        onClick = { navController.navigate(Destination.ADD_ITEM.route) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add_icon), contentDescription = "Añadir item"
        )
    }
}

@Composable
fun StarRatingBar(
    maxStars: Int = 5, rating: Int, onRatingChanged: (Int) -> Unit, modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        for (i in 1..maxStars) {
            IconButton(onClick = { onRatingChanged(i) }) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Estrella $i",
                    tint = if (i <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AddDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    mainViewModel: MainViewModel,

    ) {
    var rating by rememberSaveable { mutableIntStateOf(0) }
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var selectedItem: MediaItem? by remember { mutableStateOf<MediaItem?>(null) }

    val itemTypes = listOf("Libro", "Película", "Serie")
    var selectedItemType by rememberSaveable { mutableStateOf(itemTypes.first()) }

    val context = LocalContext.current

    Scaffold(
        Modifier.clip(RoundedCornerShape(22.dp)),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nuevo Elemento") }, navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.close_icon
                            ), contentDescription = "Cerrar"
                        )
                    }
                }, actions = {
                    IconButton(onClick = {
                        val itemToSave = selectedItem
                        if (itemToSave == null) {
                            Toast.makeText(
                                context,
                                "No has seleccionado ningún elemento.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (mainViewModel.getItemName(itemToSave.title) != null) {
                            Toast.makeText(
                                context,
                                "El elemento ya existe en tu biblioteca.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            when (itemToSave) {
                                is Book -> {
                                    mainViewModel.addBookToLibrary(itemToSave, rating)
                                    onSave()
                                }

                                is Film -> {
                                    mainViewModel.addFilmToLibrary(itemToSave, rating)
                                    onSave()
                                }

                                is Serie -> {
                                    mainViewModel.addSerieToLibrary(itemToSave, rating)
                                    onSave()
                                }
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.save_icon),
                            contentDescription = "Guardar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }

    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            MultiChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                itemTypes.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = RoundedCornerShape(16.dp),
                        onCheckedChange = {
                            selectedItemType = label
                            text = ""
                            selectedItem = null
                        },
                        checked = selectedItemType == label,
                        icon = {
                            SegmentedButtonDefaults.Icon(active = selectedItemType == label)
                        }

                    ) {
                        Text(text = label)
                    }
                }
            }
            when {
                selectedItemType == "Libro" -> {
                    FilteredSearchBar(
                        onSearch = { active = true },
                        searchResults = mainViewModel.getAllBooks(text),
                        onResultClick = { item ->
                            text = item.title
                            selectedItem = item
                        },
                        query = text,
                        onQueryChange = {
                            text = it
                            if (it.isBlank()) {
                                selectedItem = null
                            }
                        },
                    )
                }

                selectedItemType == "Película" -> {
                    FilteredSearchBar(
                        onSearch = { active = true },
                        searchResults = mainViewModel.getAllFilms(text),
                        onResultClick = { item ->
                            text = item.title
                            selectedItem = item
                        },
                        query = text,
                        onQueryChange = {
                            text = it
                            if (it.isBlank()) {
                                selectedItem = null
                            }
                        },
                    )
                }

                selectedItemType == "Serie" -> {
                    FilteredSearchBar(
                        onSearch = { active = true },
                        searchResults = mainViewModel.getAllSeries(text),
                        onResultClick = { item ->
                            text = item.title
                            selectedItem = item
                        },
                        query = text,
                        onQueryChange = {
                            text = it
                            if (it.isBlank()) {
                                selectedItem = null
                            }
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedItem != null) {
                Text(
                    "Calificación para '${selectedItem?.title}'",
                    style = MaterialTheme.typography.titleMedium
                )
                StarRatingBar(
                    rating = rating,
                    onRatingChanged = { newRating -> rating = newRating }
                )
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    mainViewModel: MainViewModel,

    ) {
    var rating by rememberSaveable { mutableIntStateOf(0) }
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var selectedItem: MediaItem? by remember { mutableStateOf<MediaItem?>(null) }

    val itemTypes = listOf("Libro", "Película", "Serie")
    var selectedItemType by rememberSaveable { mutableStateOf(itemTypes.first()) }

    val context = LocalContext.current

    FilteredSearchBar(
        onSearch = { active = true },
        searchResults = mainViewModel.getAllBooks(text),
        onResultClick = { item ->
            text = item.title
            selectedItem = item
        },
        query = text,
        onQueryChange = {
            text = it
            if (it.isBlank()) {
                selectedItem = null
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun libraryScreen(
    navController: NavHostController,
    userLibrary: UserFullLibrary?,
    mainViewModel: MainViewModel?
) {
    // 1. Estados para controlar la búsqueda
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // 3. Cambia la TopAppBar según si la búsqueda está activa o no
            if (isSearchActive) {
                // --- BARRA DE BÚSQUEDA ACTIVA ---
                TopAppBar(
                    title = {
                        // Campo de texto para la búsqueda
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar en tu biblioteca...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchQuery = ""
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Cerrar búsqueda"
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Biblioteca") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Ir hacia atrás"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar"
                            )
                        }
                    }
                )
            }
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            if (userLibrary != null && userLibrary.libraryItems.isNotEmpty()) {
                val libraryItems = userLibrary.libraryItems

                val filteredBooks = userLibrary.books.filter {
                    it.bookTitle.contains(searchQuery, ignoreCase = true)
                }
                val filteredFilms = userLibrary.films.filter {
                    it.filmTitle.contains(searchQuery, ignoreCase = true)
                }
                val filteredSeries = userLibrary.series.filter {
                    it.serieTitle.contains(searchQuery, ignoreCase = true)
                }

                items(filteredBooks) { book ->
                    val libraryItemInfo =
                        libraryItems.find { it.itemId == book.bookTitle && it.itemType == "book" }
                    FilledItemCard(item = book, libraryItem = libraryItemInfo)
                }
                items(filteredFilms) { film ->
                    val libraryItemInfo =
                        libraryItems.find { it.itemId == film.filmTitle && it.itemType == "film" }
                    FilledItemCard(item = film, libraryItem = libraryItemInfo)
                }
                items(filteredSeries) { serie ->
                    val libraryItemInfo =
                        libraryItems.find { it.itemId == serie.serieTitle && it.itemType == "serie" }
                    FilledItemCard(item = serie, libraryItem = libraryItemInfo)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = { Text("Perfil") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Ir hacia atrás"
                    )
                }
            }, actions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, contentDescription = "Opciones"
                    )
                }
            })
        }) { contentPadding ->

        ProfileBody(modifier = Modifier.padding(contentPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilteredSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<MediaItem>,
    onResultClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = { Text("Search") },
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            Icons.Default.Search, contentDescription = "Search"
        )
    },
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingContent: (@Composable (String) -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
            .heightIn(max = 250.dp)) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        onSearch(query)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            LazyColumn {
                items(count = searchResults.size) { index ->
                    val searchItem = searchResults[index]
                    ListItem(
                        headlineContent = { Text(searchItem.title) },
                        supportingContent = supportingContent?.let { { it(searchItem.title) } },
                        leadingContent = leadingContent,
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                onResultClick(searchItem)
                                expanded = false
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),

                        )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBody(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.melvin_test_image),
                contentDescription = "Imagen de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape) //
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Log.d("SessionDebug", "ProfileBody: Mostrando usuario: ${SessionManager.currentUser}")
            Text(

                text = SessionManager.currentUser?.userName ?: "Invitado",
                style = MaterialTheme.typography.headlineSmall
            )
        }


        Spacer(modifier = Modifier.height(32.dp))
        val dateFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
            dayOfMonth()
            char('/')
            monthNumber()
            char('/')
            year()
        }
        Text(
            text = "Fecha de registro:", style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = SessionManager.currentUser?.startDate?.format(dateFormat)
                ?: "Sin fecha de inicio", style = MaterialTheme.typography.bodyMedium
        )
    }
}





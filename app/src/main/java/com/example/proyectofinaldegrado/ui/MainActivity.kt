package com.example.proyectofinaldegrado.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.proyectofinaldegrado.MyApplication
import com.example.proyectofinaldegrado.R
import com.example.proyectofinaldegrado.ui.theme.ProyectoFinalDeGradoTheme
import com.example.proyectofinaldegrado.SessionManager
import com.example.proyectofinaldegrado.data.local.entity.Book
import com.example.proyectofinaldegrado.data.local.entity.Film
import com.example.proyectofinaldegrado.data.local.entity.Game
import com.example.proyectofinaldegrado.data.local.entity.MediaItem
import com.example.proyectofinaldegrado.data.local.entity.Serie
import com.example.proyectofinaldegrado.data.local.entity.User
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


        composable(Destination.HOME.route) {
            HomeScreen(
                userLibrary = userLibrary,
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
        composable(Destination.LIBRARY.route) {
            val userLibraryState by mainViewModel.userLibrary.collectAsState()
            libraryScreen(
                userLibrary = userLibrary,
                navController = navController,
                userLibraryState = userLibraryState,
                mainViewModel = mainViewModel
            )
        }
        composable(Destination.PROFILE.route) {
            ProfileScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
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
fun HomeScreen(
    userLibrary: UserFullLibrary,
    mainViewModel: MainViewModel,
    navController: NavHostController
) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<MediaItem?>(null) }
    var itemToShowInDialog by remember { mutableStateOf<UserLibraryItem?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        val itemLimit = 3
        if (userLibrary.libraryItems.isNotEmpty()) {
            item {

                LastestCarousel(userLibrary = userLibrary)

            }
        }

        if (userLibrary.libraryItems.isNotEmpty()) {
            val libraryItem = userLibrary.libraryItems
            items(userLibrary.books.take(itemLimit)) { book ->

                val libraryItemInfo = libraryItem.find { libraryItem ->
                    libraryItem.itemId == book.bookTitle && libraryItem.itemType == "book"
                }
                FilledItemCard(
                    item = book,
                    libraryItem = libraryItemInfo,
                    mainViewModel = mainViewModel,
                    navController = navController,
                    onDeleteClick = { mediaItem ->
                        itemToDelete = mediaItem
                        showDeleteDialog = true
                    },
                    onClick = {mediaItem ->  itemToShowInDialog = mediaItem }
                )

            }
            items(userLibrary.films.take(itemLimit)) { film ->

                val libraryItemInfo = libraryItem.find { libraryItem ->
                    libraryItem.itemId == film.filmTitle && libraryItem.itemType == "film"
                }


                FilledItemCard(
                    item = film,
                    libraryItem = libraryItemInfo,
                    mainViewModel = mainViewModel,
                    navController = navController,
                    onDeleteClick = { mediaItem ->
                        itemToDelete = mediaItem
                        showDeleteDialog = true
                    },
                    onClick = {userLibraryItem ->  itemToShowInDialog = userLibraryItem }

                )
            }
            items(userLibrary.series.take(itemLimit)) { serie ->

                val libraryItemInfo = libraryItem.find { libraryItem ->
                    libraryItem.itemId == serie.serieTitle && libraryItem.itemType == "serie"
                }


                FilledItemCard(
                    item = serie,
                    libraryItem = libraryItemInfo,
                    mainViewModel = mainViewModel,
                    navController = navController,
                    onDeleteClick = { mediaItem ->
                        itemToDelete = mediaItem
                        showDeleteDialog = true
                    },
                    onClick = {userLibraryItem ->  itemToShowInDialog = userLibraryItem }

                )
            }
            if (userLibrary.games != null) {
                items(userLibrary.games.take(itemLimit)) { game ->
                    for (i in 0..6) {
                    }
                    val libraryItemInfo = userLibrary.libraryItems.find { li ->
                        li.itemId == game.name && li.itemType == "game"
                    }

                    FilledItemCard(
                        item = game,
                        libraryItem = libraryItemInfo,
                        mainViewModel = mainViewModel,
                        navController = navController,
                        onDeleteClick = { mediaItem ->
                            itemToDelete = mediaItem
                            showDeleteDialog = true
                        },
                        onClick = {userLibraryItem ->  itemToShowInDialog = userLibraryItem }

                    )
                }
            }
        }

    }
    if (showDeleteDialog && itemToDelete != null) {
        DeleteConfirmationDialog(
            onDismiss = {
                showDeleteDialog = false
                itemToDelete = null
            },
            onConfirm = {
                mainViewModel.deleteItem(itemToDelete!!)
                showDeleteDialog = false
                itemToDelete = null
            }
        )
    }
    itemToShowInDialog?.let { libItem ->
        val allMediaItems =
            userLibrary.books + userLibrary.films + userLibrary.series + (userLibrary.games
                ?: emptyList())

        val mediaItemToShow = allMediaItems.find { mediaItem ->
            mediaItem.title == libItem.itemId
        }

        if (mediaItemToShow != null) {
            ItemDetailDialog(
                item = mediaItemToShow,
                libraryItem = libItem,
                onDismiss = { itemToShowInDialog = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailDialog(
    item: MediaItem,
    libraryItem: UserLibraryItem,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                libraryItem.itemId,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }
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
                    val imageModel = if (item is Game) {
                        "https://steamcdn-a.akamaihd.net/steam/apps/${item.appid}/header.jpg"
                    } else {
                        item.poster
                    }

                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Portada de ${libraryItem.itemId}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )

                    val dateFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
                        dayOfMonth()
                        char('/')
                        monthNumber()
                        char('/')
                        year()
                    }

                    when (item) {
                        is Book -> {
                            Text(
                                text = "Autor: ${item.author}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(text = "Género: ${item.genre}")
                            Text(text = "Páginas: ${item.pages}")

                        }

                        is Film -> {
                            Text(text = "Director: ${item.director}")
                            Text(text = "Fecha de estreno: ${item.releaseDate}")
                            Text(text = "Genero: ${item.genre}")
                        }

                        is Serie -> {
                            Text(text = "Director: ${item.director}")
                            Text(text = "Fecha de estreno: ${item.releaseDate}")
                            Text(text = "Capitulos: ${item.chapters}")
                            Text(text = "Genero: ${item.genre}")
                        }

                        is Game -> {
                            Text(text = "Horas jugadas: ${item.playtimeForever}")
                            Text(text = "Genero: ${item.genre}")
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Calificación: ")
                        if(libraryItem.rating == 0){
                            Text(text = "Sin calificar")
                        }else {
                            for (i in 1..5)
                                Icon(
                                    imageVector = if (i <= libraryItem!!.rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Estrella $i",
                                    modifier = Modifier.size(16.dp),
                                    tint = if (i <= libraryItem.rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                    }

                    Text(
                        text = "Añadido: ${libraryItem.additionDate?.format(dateFormat) ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
    @Composable
    fun FilledItemCard(
        item: MediaItem,
        libraryItem: UserLibraryItem?,
        mainViewModel: MainViewModel?,
        navController: NavHostController,
        onDeleteClick: (MediaItem) -> Unit,
        onClick: (UserLibraryItem?) -> Unit
        ) {

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { onClick(libraryItem) }
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {

                val (imageRef, contentRef, deleteIconRef) = createRefs()
                val imageModel = if (item is Game) {
                    "https://steamcdn-a.akamaihd.net/steam/apps/${item.appid}/header.jpg"
                } else {
                    item.poster
                }
                AsyncImage(
                    model = imageModel,
                    contentDescription = "Portada de ${item.title}",
                    placeholder = painterResource(id = R.drawable.melvin_test_image),
                    error = painterResource(id = R.drawable.melvin_test_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(120.dp)
                        .clip(RectangleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, RectangleShape)
                        .constrainAs(imageRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )

                ItemContent(
                    item = item,
                    libraryItem = libraryItem,
                    modifier = Modifier.constrainAs(contentRef) {
                        start.linkTo(imageRef.end, margin = 16.dp)
                        end.linkTo(deleteIconRef.start, margin = 8.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                )

                IconButton(
                    onClick = {
                        onDeleteClick(item)
                    },
                    modifier = Modifier.constrainAs(deleteIconRef) {
                        top.linkTo(parent.top, margin = 4.dp)
                        end.linkTo(parent.end, margin = 4.dp)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar elemento",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }


    @Composable
    fun ItemContent(item: MediaItem, libraryItem: UserLibraryItem?, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .padding(vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp),

            ) {
            val dateFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
                dayOfMonth()
                char('/')
                monthNumber()
                char('/')
                year()
            }

            Text(text = "Título: ${item.title}", maxLines = 1, overflow = TextOverflow.Ellipsis)

            when (item) {
                is Book -> {
                    Text(
                        text = "Autor: ${item.author}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = "Género: ${item.genre}")
                    Text(text = "Páginas: ${item.pages}")
                    Log.d("SessionDebug", "ProfileBody: Mostrando rating: ${item.rating}")

                }

                is Film -> {
                    Text(text = "Director: ${item.director}")
                    Text(text = "Fecha de estreno: ${item.releaseDate}")
                    Text(text = "Genero: ${item.genre}")
                }

                is Serie -> {
                    Text(text = "Director: ${item.director}")
                    Text(text = "Fecha de estreno: ${item.releaseDate}")
                    Text(text = "Capitulos: ${item.chapters}")
                    Text(text = "Genero: ${item.genre}")
                }

                is Game -> {
                    Text(text = "Horas jugadas: ${item.playtimeForever}")
                    Text(text = "Genero: ${item.genre}")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Calificación: ")
                if(libraryItem?.rating == 0){
                    Text(text = "Sin calificar")
                }else {
                    for (i in 1..5)
                        Icon(
                            imageVector = if (i <= libraryItem!!.rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Estrella $i",
                            modifier = Modifier.size(16.dp),
                            tint = if (i <= libraryItem.rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }

            }

            Text(
                text = "Añadido: ${libraryItem?.additionDate?.format(dateFormat) ?: "N/A"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LastestCarousel(userLibrary: UserFullLibrary) {

        val allMediaItems = remember(userLibrary) {
            userLibrary.books + userLibrary.films + userLibrary.series
        }

        val latestItems = remember(userLibrary, allMediaItems) {
            userLibrary.libraryItems
                .sortedByDescending { it.additionDate }
                .take(10)
                .mapNotNull { libraryItem ->
                    allMediaItems.find { mediaItem -> mediaItem.title == libraryItem.itemId }
                }
        }

        if (latestItems.isEmpty()) {

            return
        }
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { latestItems.count() },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            preferredItemWidth = 186.dp,
            itemSpacing = 8.dp
        ) { i ->
            val item = latestItems[i]

            AsyncImage(
                model = item.poster,
                contentDescription = item.title,
                placeholder = painterResource(id = R.drawable.melvin_test_image),
                error = painterResource(id = R.drawable.melvin_test_image),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.extraLarge),
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
                painter = painterResource(id = R.drawable.add_icon),
                contentDescription = "Añadir item"
            )
        }
    }

    @Composable
    fun StarRatingBar(
        maxStars: Int = 5,
        rating: Int,
        onRatingChanged: (Int) -> Unit,
        modifier: Modifier = Modifier
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

                                    else -> {
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
    fun LibrarySearch(
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
        mainViewModel: MainViewModel?,
        userLibraryState: UserFullLibrary?,

        ) {
        var isSearchActive by rememberSaveable { mutableStateOf(false) }
        var searchQuery by rememberSaveable { mutableStateOf("") }

        var showDeleteDialog by remember { mutableStateOf(false) }
        var itemToDelete by remember { mutableStateOf<MediaItem?>(null) }
        var itemToShowInDialog by remember { mutableStateOf<UserLibraryItem?>(null) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (isSearchActive) {
                    TopAppBar(
                        title = {
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
                        it.bookTitle.contains(searchQuery, ignoreCase = true) ||
                                it.genre.contains(searchQuery, ignoreCase = true) ||
                                it.author.contains(searchQuery, ignoreCase = true)
                    }
                    val filteredFilms = userLibrary.films.filter {
                        it.filmTitle.contains(searchQuery, ignoreCase = true) ||
                                it.genre.contains(searchQuery, ignoreCase = true) ||
                                it.author.contains(searchQuery, ignoreCase = true)
                    }
                    val filteredSeries = userLibrary.series.filter {
                        it.serieTitle.contains(searchQuery, ignoreCase = true) ||
                                it.genre.contains(searchQuery, ignoreCase = true) ||
                                it.author.contains(searchQuery, ignoreCase = true)
                    }
                    val filteredGames = userLibrary.games?.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }


                    items(filteredBooks) { book ->
                        val libraryItemInfo =
                            libraryItems.find { it.itemId == book.bookTitle && it.itemType == "book" }
                        FilledItemCard(
                            item = book,
                            libraryItem = libraryItemInfo,
                            mainViewModel = mainViewModel,
                            navController = navController,
                            onDeleteClick = { mediaItem ->
                                itemToDelete = mediaItem
                                showDeleteDialog = true
                            },
                            onClick = {userLibraryItem ->  itemToShowInDialog = userLibraryItem }

                        )
                    }
                    items(filteredFilms) { film ->
                        val libraryItemInfo =
                            libraryItems.find { it.itemId == film.filmTitle && it.itemType == "film" }
                        FilledItemCard(
                            item = film,
                            libraryItem = libraryItemInfo,
                            mainViewModel = mainViewModel,
                            navController = navController,
                            onDeleteClick = { mediaItem ->
                                itemToDelete = mediaItem
                                showDeleteDialog = true
                            },
                            onClick = {userLibraryItem ->  itemToShowInDialog = userLibraryItem }

                        )
                    }
                    items(filteredSeries) { serie ->
                        val libraryItemInfo =
                            libraryItems.find { it.itemId == serie.serieTitle && it.itemType == "serie" }
                        FilledItemCard(
                            item = serie,
                            libraryItem = libraryItemInfo,
                            mainViewModel = mainViewModel,
                            navController = navController,
                            onDeleteClick = { mediaItem ->
                                itemToDelete = mediaItem
                                showDeleteDialog = true
                            },
                            onClick = {userLibraryItem ->  itemToShowInDialog = userLibraryItem }

                        )
                    }
                    if (filteredGames != null) {
                        items(filteredGames) { game ->

                            val libraryItemInfo = libraryItems.find {
                                it.itemId == game.name && it.itemType == "game"
                            }

                            FilledItemCard(
                                item = game,
                                libraryItem = libraryItemInfo,
                                mainViewModel = mainViewModel,
                                navController = navController,
                                onDeleteClick = { mediaItem ->
                                    itemToDelete = mediaItem
                                    showDeleteDialog = true
                                },
                                onClick = {userLibraryItem ->  itemToShowInDialog = userLibraryItem }

                            )
                        }
                    }

                }
            }
        }

        if (showDeleteDialog && itemToDelete != null) {
            DeleteConfirmationDialog(
                onDismiss = {
                    showDeleteDialog = false
                    itemToDelete = null
                },
                onConfirm = {
                    mainViewModel?.deleteItem(itemToDelete!!)
                    showDeleteDialog = false
                    itemToDelete = null
                }
            )
        }
        itemToShowInDialog?.let { libItem ->
            val allMediaItems =
                userLibrary?.books.orEmpty() + userLibrary?.films.orEmpty() + userLibrary?.series.orEmpty() + userLibrary?.games.orEmpty()
            val mediaItemToShow = allMediaItems.find { mediaItem ->
                mediaItem.title == libItem.itemId
            }

            if (mediaItemToShow != null) {
                ItemDetailDialog(
                    item = mediaItemToShow,
                    libraryItem = libItem,
                    onDismiss = { itemToShowInDialog = null }
                )
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfileScreen(
        navController: NavHostController,
        mainViewModel: MainViewModel
    ) {
        val context = LocalContext.current
        var showApiKeyDialog by rememberSaveable { mutableStateOf(false) }

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
                    Button(onClick = {
                        val returnUrl = "https://RafaVillarRoda.github.io/ProyectoFinalDeGrado/auth"
                        val openIdUrl = "https://steamcommunity.com/openid/login?" +
                                "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&" +
                                "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
                                "openid.mode=checkid_setup&" +
                                "openid.ns=http://specs.openid.net/auth/2.0&" +
                                "openid.return_to=$returnUrl&" +
                                "openid.realm=$returnUrl"

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(openIdUrl))
                        context.startActivity(intent)

                        showApiKeyDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Sync, contentDescription = "SteamSync"
                        )
                        Text("SteamSync")
                    }
                })
            }) { contentPadding ->

            ProfileBody(modifier = Modifier.padding(contentPadding), mainViewModel = mainViewModel)
        }

        if (showApiKeyDialog) {
            ApiKeyDialog(
                onDismiss = { showApiKeyDialog = false },
                onConfirm = { apiKey ->
                    val idToUse = SessionManager.currentUser?.steamID
                    Log.d(
                        "MainViewModel",
                        "URL de consulta: https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=$apiKey&steamid=$idToUse&format=json&include_appinfo=1"
                    )
                    mainViewModel.syncSteamLibrary(idToUse, apiKey.trim())
                    showApiKeyDialog = false
                }
            )
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
    fun ProfileBody(modifier: Modifier = Modifier, mainViewModel: MainViewModel) {
        var showEditNicknameDialog by rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current
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
                Log.d(
                    "SessionDebug",
                    "ProfileBody: Mostrando usuario: ${SessionManager.currentUser}"
                )
                Row() {
                    Text(
                        text = SessionManager.currentUser?.userName ?: "Invitado",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(
                        onClick = {
                            showEditNicknameDialog = true

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar perfil",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }


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
        if (showEditNicknameDialog) {
            EditNickname(
                onDismiss = { showEditNicknameDialog = false },
                onConfirm = { newNick ->
                    val userName = SessionManager.currentUser?.userName
                    mainViewModel.editNickname(newNick)
                    SessionManager.currentUser =
                        SessionManager.currentUser?.copy(userName = newNick)
                    showEditNicknameDialog = false

                }
            )
        }
    }

    @Composable
    fun DeleteConfirmationDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Confirmar eliminación")
            },
            text = {
                Text("¿Estás seguro de que quieres eliminar este elemento de tu biblioteca?")
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancelar")
                }
            })
    }

    @Composable
    fun ApiKeyDialog(
        onDismiss: () -> Unit,
        onConfirm: (String) -> Unit
    ) {
        var apiKeyText by rememberSaveable { mutableStateOf("") }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Vincular Steam")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Introduce tu API key de Steam para sincronizar tus juegos.")

                    OutlinedTextField(
                        value = apiKeyText,
                        onValueChange = { apiKeyText = it },
                        label = { Text("Steam API Key") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val description =
                                if (passwordVisible) "Ocultar clave" else "Mostrar clave"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description)
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(apiKeyText) },
                    enabled = apiKeyText.isNotBlank()
                ) {
                    Text("Sincronizar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditNickname(
        onDismiss: () -> Unit,
        onConfirm: (String) -> Unit
    ) {
        var newNick by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Editar Nickname")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Introduce tu nuevo Nickname:")

                    OutlinedTextField(
                        value = newNick,
                        onValueChange = { newNick = it },
                        label = { Text("Nickname") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(newNick) },
                    enabled = newNick.isNotBlank()
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }







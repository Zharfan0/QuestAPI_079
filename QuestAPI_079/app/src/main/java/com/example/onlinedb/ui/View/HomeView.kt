package com.example.onlinedb.ui.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onlinedb.R
import com.example.onlinedb.model.Mahasiswa
import com.example.onlinedb.ui.CustomWidget.CustomTopAppBar
import com.example.onlinedb.ui.Navigation.DestinasiNavigasi
import com.example.onlinedb.ui.ViewModel.HomeUiState
import com.example.onlinedb.ui.ViewModel.HomeViewModel
import com.example.onlinedb.ui.ViewModel.PenyediaViewModel

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Home Mahasiswa"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CustomTopAppBar(
                title = DestinasiHome.titleRes,
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getMhs()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Mahasiswa",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        HomeStatus(
            homeUiState = viewModel.mhsUiState,
            retryAction = { viewModel.getMhs() },
            modifier = Modifier.padding(innerPadding),
            onDetailClick = onDetailClick,
            onDeleteClick = {
                viewModel.deleteMhs(it.nim)
                viewModel.getMhs()
            }
        )
    }
}

@Composable
fun HomeStatus(
    homeUiState: HomeUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Mahasiswa) -> Unit = {},
    onDetailClick: (String) -> Unit = {}
) {
    when (homeUiState) {
        is HomeUiState.Loading -> OnLoading(modifier = Modifier.fillMaxSize())

        is HomeUiState.Success ->
            if (homeUiState.mahasiswa.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Tidak ada data mahasiswa", fontSize = 18.sp, color = Color.Gray)
                }
            } else {
                MhsGridLayout(
                    mahasiswa = homeUiState.mahasiswa,
                    modifier = modifier.fillMaxWidth(),
                    onDeleteClick = {
                        onDeleteClick(it)
                    },
                    onDetailClick = {
                        onDetailClick(it.nim)
                    }
                )
            }

        is HomeUiState.Error -> onErr(retryAction, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading),
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
fun onErr(
    retryAction: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.loading_failed), color = Color.Red, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun MhsGridLayout(
    mahasiswa: List<Mahasiswa>,
    modifier: Modifier = Modifier,
    onDeleteClick: (Mahasiswa) -> Unit = {},
    onDetailClick: (Mahasiswa) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(mahasiswa) { mahasiswa ->
            MhsCard(
                mahasiswa = mahasiswa,
                modifier = Modifier.fillMaxWidth(),
                onDeleteClick = {
                    onDeleteClick(mahasiswa)
                },
                onDetailClick = {
                    onDetailClick(mahasiswa)
                }
            )
        }
    }
}

@Composable
fun MhsCard(
    mahasiswa: Mahasiswa,
    modifier: Modifier = Modifier,
    onDeleteClick: (Mahasiswa) -> Unit = {},
    onDetailClick: (Mahasiswa) -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Mahasiswa Avatar",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = mahasiswa.nama, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${mahasiswa.nim} - ${mahasiswa.kelas}", fontSize = 14.sp, color = Color.Gray)
                Text(text = mahasiswa.alamat, fontSize = 14.sp, color = Color.Gray)
            }
            IconButton(onClick = { onDeleteClick(mahasiswa) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Delete Mahasiswa",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

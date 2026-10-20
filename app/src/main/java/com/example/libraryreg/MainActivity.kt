package com.example.libraryreg

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.libraryreg.ui.theme.LibraryRegTheme

// ---------- Model ----------
data class Member(
    val id: Int,
    val nama: String,
    val nomorInduk: String,
    val prodi: String,
    val alamat: String,
    val telepon: String,
    val email: String,
    val jenis: String
)

// ---------- Navigasi sederhana ----------
sealed class Screen {
    object List : Screen()
    data class Form(val member: Member?) : Screen()   // null = tambah baru
    data class Detail(val member: Member) : Screen()
}

private val jenisOptions = listOf("Mahasiswa", "Dosen", "Tenaga Kependidikan")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { LibraryRegApp() } }
    }
}

@Composable
fun LibraryRegApp() {
    val members = remember { mutableStateListOf<Member>() }
    var nextId by remember { mutableIntStateOf(1) }
    var screen by remember { mutableStateOf<Screen>(Screen.List) }

    when (val s = screen) {
        is Screen.List -> ListScreen(
            members = members,
            onAdd = { screen = Screen.Form(null) },
            onDetail = { screen = Screen.Detail(it) },
            onEdit = { screen = Screen.Form(it) },
            onDelete = { members.remove(it) }
        )
        is Screen.Form -> FormScreen(
            existing = s.member,
            members = members,
            onBack = { screen = Screen.List },
            onSave = { m ->
                if (s.member == null) {
                    members.add(m.copy(id = nextId++))
                } else {
                    val i = members.indexOfFirst { it.id == s.member.id }
                    if (i >= 0) members[i] = m.copy(id = s.member.id)
                }
                screen = Screen.List
            }
        )
        is Screen.Detail -> DetailScreen(
            member = s.member,
            onBack = { screen = Screen.List },
            onEdit = { screen = Screen.Form(s.member) }
        )
    }
}

// ---------- Daftar + pencarian + hapus ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    members: SnapshotStateList<Member>,
    onAdd: () -> Unit,
    onDetail: (Member) -> Unit,
    onEdit: (Member) -> Unit,
    onDelete: (Member) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var toDelete by remember { mutableStateOf<Member?>(null) }
    val filtered = members.filter {
        it.nama.contains(query.trim(), ignoreCase = true) ||
                it.nomorInduk.contains(query.trim())
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("LibraryReg - Daftar Anggota") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAdd) { Text("+ Tambah") }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                label = { Text("Cari nama atau nomor induk") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            if (filtered.isEmpty()) Text("Tidak ada data anggota.", Modifier.padding(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered, key = { it.id }) { m ->
                    Card(Modifier.fillMaxWidth().clickable { onDetail(m) }) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(m.nama, style = MaterialTheme.typography.titleMedium)
                                Text("${m.nomorInduk} • ${m.jenis}", style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { onEdit(m) }) { Text("Edit") }
                            TextButton(onClick = { toDelete = m }) { Text("Hapus") }
                        }
                    }
                }
            }
        }
    }

    toDelete?.let { m ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Hapus anggota?") },
            text = { Text("Data ${m.nama} akan dihapus.") },
            confirmButton = { TextButton(onClick = { onDelete(m); toDelete = null }) { Text("Hapus") } },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Batal") } }
        )
    }
}

// ---------- Formulir (tambah & edit) + validasi ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    existing: Member?,
    members: List<Member>,
    onBack: () -> Unit,
    onSave: (Member) -> Unit
) {
    var nama by remember { mutableStateOf(existing?.nama ?: "") }
    var nomor by remember { mutableStateOf(existing?.nomorInduk ?: "") }
    var prodi by remember { mutableStateOf(existing?.prodi ?: "") }
    var alamat by remember { mutableStateOf(existing?.alamat ?: "") }
    var telepon by remember { mutableStateOf(existing?.telepon ?: "") }
    var email by remember { mutableStateOf(existing?.email ?: "") }
    var jenis by remember { mutableStateOf(existing?.jenis ?: "") }

    var eNama by remember { mutableStateOf<String?>(null) }
    var eNomor by remember { mutableStateOf<String?>(null) }
    var eProdi by remember { mutableStateOf<String?>(null) }
    var eAlamat by remember { mutableStateOf<String?>(null) }
    var eTelepon by remember { mutableStateOf<String?>(null) }
    var eEmail by remember { mutableStateOf<String?>(null) }
    var eJenis by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        val n = nomor.trim()
        eNama = if (nama.isBlank()) "Nama lengkap wajib diisi" else null
        eNomor = when {
            n.isEmpty() -> "NIM/NIP wajib diisi"
            !n.all { it.isDigit() } -> "NIM/NIP hanya boleh angka"
            n.length !in 8..18 -> "NIM/NIP harus 8-18 digit"
            members.any { it.nomorInduk == n && it.id != existing?.id } -> "NIM/NIP sudah terdaftar"
            else -> null
        }
        eProdi = if (prodi.isBlank()) "Program studi wajib diisi" else null
        eAlamat = if (alamat.isBlank()) "Alamat wajib diisi" else null
        val t = telepon.trim()
        eTelepon = when {
            t.isEmpty() -> "Nomor telepon wajib diisi"
            !t.all { it.isDigit() } || t.length !in 9..15 -> "Nomor telepon tidak valid (9-15 digit)"
            else -> null
        }
        eEmail = when {
            email.isBlank() -> "Email wajib diisi"
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Format email tidak valid"
            else -> null
        }
        eJenis = if (jenis.isBlank()) "Pilih jenis keanggotaan" else null
        return listOf(eNama, eNomor, eProdi, eAlamat, eTelepon, eEmail, eJenis).all { it == null }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (existing == null) "Formulir Pendaftaran" else "Edit Anggota") },
            navigationIcon = { TextButton(onClick = onBack) { Text("Kembali") } }
        )
    }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FormField("Nama lengkap", nama, eNama) { nama = it }
            FormField("NIM / NIP", nomor, eNomor, KeyboardType.Number) { nomor = it }
            FormField("Program studi / unit", prodi, eProdi) { prodi = it }
            FormField("Alamat", alamat, eAlamat, singleLine = false) { alamat = it }
            FormField("Nomor telepon", telepon, eTelepon, KeyboardType.Phone) { telepon = it }
            FormField("Email", email, eEmail, KeyboardType.Email) { email = it }

            Text("Jenis keanggotaan", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                jenisOptions.forEach { opt ->
                    FilterChip(selected = jenis == opt, onClick = { jenis = opt }, label = { Text(opt) })
                }
            }
            eJenis?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (validate()) {
                        onSave(
                            Member(
                                id = 0, nama = nama.trim(), nomorInduk = nomor.trim(),
                                prodi = prodi.trim(), alamat = alamat.trim(),
                                telepon = telepon.trim(), email = email.trim(), jenis = jenis
                            )
                        )
                    }
                }
            ) { Text(if (existing == null) "Daftar" else "Simpan perubahan") }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    error: String?,
    keyboard: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label) },
        isError = error != null, supportingText = { error?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        singleLine = singleLine, modifier = Modifier.fillMaxWidth()
    )
}

// ---------- Detail ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(member: Member, onBack: () -> Unit, onEdit: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Detail Anggota") },
            navigationIcon = { TextButton(onClick = onBack) { Text("Kembali") } }
        )
    }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailRow("Nama lengkap", member.nama)
            DetailRow("NIM / NIP", member.nomorInduk)
            DetailRow("Program studi / unit", member.prodi)
            DetailRow("Alamat", member.alamat)
            DetailRow("Nomor telepon", member.telepon)
            DetailRow("Email", member.email)
            DetailRow("Jenis keanggotaan", member.jenis)
            Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) { Text("Edit data") }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun LibraryRegAppPreview() {
    LibraryRegTheme {
        LibraryRegApp()
    }
}
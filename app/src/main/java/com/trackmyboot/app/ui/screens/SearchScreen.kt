package com.trackmyboot.app.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.trackmyboot.app.ui.AppViewModel
import com.trackmyboot.app.ui.OffersViewModel

@Composable
fun SearchScreen(app: AppViewModel, vm: OffersViewModel) {
    var text by rememberSaveable { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current
    val submit = {
        if (text.isNotBlank()) {
            keyboard?.hide()
            vm.search(text)
        }
    }
    OffersContent(
        app, vm, showCategoryChips = true,
        emptyHint = "Busque por produto, marca ou clube. Ex.: \"chuteira society\", \"camisa Grêmio\".",
        header = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Ex.: chuteira Predator, camisa Flamengo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { submit() }),
                trailingIcon = { IconButton(onClick = submit) { Icon(Icons.Filled.Search, contentDescription = "Buscar") } },
            )
        },
    )
}

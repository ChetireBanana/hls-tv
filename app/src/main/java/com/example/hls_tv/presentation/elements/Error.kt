package com.example.hls_tv.presentation.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.hls_tv.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Error(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.error_message),
            style = MaterialTheme.typography.headlineLarge
        )

        GlideImage(
            model = R.raw.error,
            contentDescription = stringResource(R.string.error_picture_description),
            modifier = Modifier.size(300.dp)
        )

        Button(
            onClick = { onRetry() },
        ) {
            Text(text = stringResource(R.string.try_again_button))
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ErrorPreview() {
    Error(
        {}
    )
}

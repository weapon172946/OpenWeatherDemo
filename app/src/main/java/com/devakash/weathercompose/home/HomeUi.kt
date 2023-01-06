package com.devakash.weathercompose.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.devakash.weathercompose.R
import com.devakash.weathercompose.misc.Utils
import com.devakash.weathercompose.ui.theme.WeatherComposeTheme
import kotlin.math.roundToInt

@Composable
fun HomeUi(viewModel: HomeViewModel) {
    WeatherComposeTheme() {
        val sideMargins = 16.dp
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .paint(painterResource(id = R.drawable.image_bg), contentScale = ContentScale.Crop)
                .padding(sideMargins)

        ) {
            val (txtTime, etLocation, tvDegree, tvTrail, pbLoader, tvStatus, ivImage, tvDesc) = createRefs()

            if (viewModel.response.value != null) {
                viewModel.response.value.also {
                    TextField(
                        value = it?.name ?: "Current Location",
                        onValueChange = {},
                        textStyle = TextStyle(
                            color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold
                        ), enabled = false,
                        modifier = Modifier
                            .constrainAs(etLocation) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 20.dp)
                    )

                    Text(text = Utils.getFormatTimeWithTZ(it!!.dt), style = TextStyle(
                        color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Normal
                    ), modifier = Modifier
                        .constrainAs(txtTime) {
                            top.linkTo(etLocation.bottom)
                            start.linkTo(parent.start)
                        }
                        .padding(top = 20.dp, start = 8.dp))

                    Text(text = it.main.temp.roundToInt().toString(), style = TextStyle(
                        color = Color.White,
                        fontSize = 100.sp,
                        fontWeight = FontWeight.Thin,
                        fontFamily = FontFamily.Monospace
                    ), modifier = Modifier
                        .constrainAs(tvDegree) {
                            top.linkTo(txtTime.bottom)
                            start.linkTo(parent.start)
                        }
                        .padding(top = 20.dp))

                    Text(text = "°C", style = TextStyle(
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraLight
                    ), modifier = Modifier
                        .constrainAs(tvTrail) {
                            top.linkTo(txtTime.bottom)
                            start.linkTo(tvDegree.end)
                        }
                        .padding(top = 38.dp))

                    Image(
                        painter = rememberAsyncImagePainter("http://openweathermap.org/img/wn/${it.weather[0].icon}@4x.png"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .constrainAs(ivImage) {
                                top.linkTo(tvDegree.top)
                                bottom.linkTo(tvDegree.bottom)
                                end.linkTo(parent.end)
                            }

                    )


                    Text(text = "Feels like ${it.main.feelsLike.roundToInt()}°C with " + it.weather[0].description,
                        style = TextStyle(
                            color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier
                            .constrainAs(tvDesc) {
                                top.linkTo(tvDegree.bottom)
                                start.linkTo(parent.start)
                            }
                            .padding(top = 10.dp, start = 8.dp)

                    )

                }

            } else {
                CircularProgressIndicator(modifier = Modifier
                    .constrainAs(pbLoader) {
                        linkTo(top = parent.top, bottom = parent.bottom)
                        linkTo(start = parent.start, end = parent.end)
                    }
                    .width(100.dp)
                    .height(100.dp), color = Color.White, strokeWidth = 50.dp)


                Text(text = viewModel.status.value, style = TextStyle(
                    color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Normal
                ), modifier = Modifier
                    .constrainAs(tvStatus) {
                        top.linkTo(pbLoader.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 30.dp))
            }
        }


    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherComposeTheme {
        HomeUi(HomeViewModel())
    }
}
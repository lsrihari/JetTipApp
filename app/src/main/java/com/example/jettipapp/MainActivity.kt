package com.example.jettipapp

import android.accessibilityservice.AccessibilityService
import android.app.assist.AssistContent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MyApp {
//                    TopHeader()
                    MainContent()
                }
            }
        }
}
@Composable
fun MyApp(content: @Composable () -> Unit){
    JetTipAppTheme {
        // A surface container using the 'background' color from the theme
        Surface( color = MaterialTheme.colors.background) {
            content ()
        }
        }
}


@Composable
fun TopHeader(totalPerPerson:Double=134.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
        .height(150.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
        //.clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
    ) {
        Column(modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
            val total="%.2f".format(totalPerPerson)
            Text(text = "Total Per Person",
                style =MaterialTheme.typography.h5)
            Text(text = "$ $total",
                style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent(){
Column(Modifier.padding(all = 4.dp)) {
    BillForm(){billAmt->
        Log.d("Amt", "MainContent: ${billAmt.toInt() *10}")

    }
}

}

@ExperimentalComposeUiApi
@Composable
fun BillForm(modifier: Modifier=Modifier,
             onValChange:(String) -> Unit={}
             )
{
    val totalBillState= remember {
        mutableStateOf("")
    }
    val validState=remember(totalBillState.value){
        totalBillState.value.trim().isNotEmpty()
    }
    val keyBoardController=LocalSoftwareKeyboardController.current


    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage=(sliderPositionState.value*100).toInt()
    val splitByState= remember {
        mutableStateOf(1)
    }
    val range=IntRange(start = 1, endInclusive = 100)

     val tipAmountState = remember {
     mutableStateOf(0.0)
 }
    val totalPerPersonState= remember {
        mutableStateOf(0.0)
    }
    TopHeader(totalPerPerson = totalPerPersonState.value )

    Surface(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {

        Column(modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start)
        {
            InputField(valueState = totalBillState,
                lableId = "Enter Bill",
                enabled = true,
                isSingleLine =true,
                onAction = KeyboardActions{
                    if(!validState)return@KeyboardActions
                    //Todo-OnValueChanhged
                    onValChange(totalBillState.value.trim())
                    keyBoardController?.hide()
                }
            )
//            if(validState){
                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(80.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        RoundIconButton( imageVector = Icons.Default.Remove ,
                            onClick = {
                                if(totalBillState.value==null || totalBillState.value.isEmpty())
                                    return@RoundIconButton
                                Log.d("Icon", "BillForm: Removed")
                                splitByState.value=
                                    if (splitByState.value>1)
                                        splitByState.value-1
                                    else 1
                                totalPerPersonState.value=
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage=tipPercentage)
                            })
                    }
                    Text(text = "${splitByState.value}",
                        modifier = Modifier.align(Alignment.CenterVertically))
                    RoundIconButton( imageVector = Icons.Default.Add ,
                        onClick = {
                            if(totalBillState.value==null || totalBillState.value.isEmpty())
                                return@RoundIconButton
                            Log.d("Icon", "BillForm: Add")
                            if (splitByState.value<range.last){
                                splitByState.value=splitByState.value+1
                            }
                            totalPerPersonState.value=
                                calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage=tipPercentage)
                        })
                }
            //TipRow
            Row(modifier = Modifier
                .padding(horizontal = 3.dp, vertical = 12.dp)) {
                Text(
                    text = "Tip",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(200.dp))
                Text(text = "$ ${tipAmountState.value}")
            }
            Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${tipPercentage}%")
                Spacer(modifier = Modifier.height(14.dp))

                //slider
                Slider(value = sliderPositionState.value, onValueChange ={
                        newVal->

                    sliderPositionState.value=newVal
                    if(totalBillState.value==null || totalBillState.value.isEmpty())
                        return@Slider
                    tipAmountState.value=calculateTotalTip(totalBill = totalBillState.value.toDouble(),tipPercentage=tipPercentage)

                    totalPerPersonState.value=
                        calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                        splitBy = splitByState.value,
                        tipPercentage=tipPercentage)
                    Log.d("TotalPerPerson", "BillForm: ${totalPerPersonState.value}")
//                    Log.d("Slider", "BillForm: $newVal")
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                steps = 5,
                onValueChangeFinished = {

                })
            }
//            }else{
//                Box(){
//                    }
//                }
            }
        }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {
        MyApp {
            Text(text = "Hello Again")
        }
    }
}
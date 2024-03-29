package com.example.notekeeperapp.classes

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.notekeeperapp.R
import kotlinx.android.synthetic.main.color_selector.view.*

class ColorSelector @JvmOverloads //the annotation generates a constructor for each of the 4 parameters declared
constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0, defRes: Int = 0) : LinearLayout(context, attributeSet, defStyle, defRes) {
    private var listOfColors = listOf(Color.BLUE, Color.RED, Color.GREEN)
    private var selectedColorIndex = 0

    /**ColorSelector & ColorSelector_colors can be found in res.values.attrs*/
    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ColorSelector)
        listOfColors = typedArray.getTextArray(R.styleable.ColorSelector_colors).map {
            Color.parseColor(it.toString())
        }
        /**a helper method for LayoutManager who wants to handle its own recycling logic. Has the ability to reuse views*/
        typedArray.recycle()

        orientation = HORIZONTAL
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.color_selector, this)
        selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])

        colorSelectorPreviousArrow.setOnClickListener {
            selectPreviousColor()
        }

        colorSelectorNextArrow.setOnClickListener {
            selectNextColor()
        }

        colorEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
            broadcastColor()
        }
    }

    var selectedColorValue: Int = android.R.color.transparent
        set(value) {
            var index = listOfColors.indexOf(value)
            if (index == -1) {
                colorEnabled.isChecked = false
                index = 0
            }
            else {
                colorEnabled.isChecked = true
            }
            selectedColorIndex = index
            selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])
        }

    private var colorSelectListeners: ArrayList<(Int) -> Unit> = arrayListOf()

    fun addListener (function: (Int) -> Unit) {
        this.colorSelectListeners.add(function)
    }

    private fun selectNextColor() {
        if (selectedColorIndex == listOfColors.lastIndex) {
            selectedColorIndex = 0
        }
        else {
            selectedColorIndex++
        }
        selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])
        broadcastColor()
    }

    private fun selectPreviousColor() {
        if (selectedColorIndex == 0) {
            selectedColorIndex = listOfColors.lastIndex
        }
        else {
            selectedColorIndex--
        }
        selectedColor.setBackgroundColor(listOfColors[selectedColorIndex])
        broadcastColor()
    }

    private fun broadcastColor() {
        val color = if (colorEnabled.isChecked) {
            listOfColors[selectedColorIndex]
        }
        else {
            Color.TRANSPARENT
        }
        this.colorSelectListeners.forEach { function ->
            function(color)
        }
    }
}
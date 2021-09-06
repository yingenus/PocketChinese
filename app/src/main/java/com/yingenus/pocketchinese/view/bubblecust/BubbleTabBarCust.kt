package com.yingenus.pocketchinese.view.bubblecust;

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import com.fxn.OnBubbleClickListener
import com.fxn.bubbletabbar.R

class BubbleTabBarCust : LinearLayout {
    private var onBubbleClickListener: OnBubbleClickListener? = null
    private var disabledIconColorParam: Int = Color.GRAY
    private var disabledBackgroundColorParam: Int = disabledIconColorParam
    private var enabledBackgroundColorParam: Int = Color.RED
    private var horizontalPaddingParam: Float = 0F
    private var iconPaddingParam: Float = 0F
    private var verticalPaddingParam: Float = 0F
    private var iconSizeParam: Float = 0F
    private var titleSizeParam: Float = 0F
    private var customFontParam: Int = 0

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    fun addBubbleListener(onBubbleClickListener: OnBubbleClickListener?) {
        this.onBubbleClickListener = onBubbleClickListener
    }

    fun setSelected(position: Int, callListener: Boolean = true) {
        var it = (this@BubbleTabBarCust.getChildAt(position) as BubbleCust)

        var b = it.id
        if (oldBubble != null && oldBubble!!.id != b) {
            it.isSelected = !it.isSelected
            oldBubble!!.isSelected = false
        }
        if (oldBubble == null){
            it.isSelected = !it.isSelected
        }
        oldBubble = it
        if (onBubbleClickListener != null && callListener) {
            onBubbleClickListener!!.onBubbleClick(it.id)
        }
    }

    fun setSelectedWithId(@IdRes id: Int, callListener: Boolean = true) {
        var it = this@BubbleTabBarCust.findViewById<BubbleCust>(id)
        var b = it.id
        if (oldBubble != null && oldBubble!!.id != b) {
            it.isSelected = !it.isSelected
            oldBubble!!.isSelected = false
        }
        if (oldBubble == null){
            it.isSelected = !it.isSelected
        }
        oldBubble = it
        if (onBubbleClickListener != null && callListener) {
            onBubbleClickListener!!.onBubbleClick(it.id)
        }
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        if (attrs != null) {
            val attributes =
                context.theme.obtainStyledAttributes(attrs, com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust, 0, 0)
            try {
                val menuResource =
                    attributes.getResourceId(com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_menuResource, -1)

                val  disabledIconColorParamList = attributes.getColorStateList(com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_disabled_icon_color)
                disabledIconColorParam = disabledIconColorParamList?.defaultColor
                    ?: Color.RED
                val  disabledBackgroundColorParamList = attributes.getColorStateList( com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_disabled_background_color)
                disabledBackgroundColorParam = disabledBackgroundColorParamList?.defaultColor
                    ?: Color.RED
                val  enabledBackgroundColorParamList = attributes.getColorStateList(com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_enabled_background_color)
                enabledBackgroundColorParam = enabledBackgroundColorParamList?.defaultColor
                    ?: Color.RED

                customFontParam =
                    attributes.getResourceId(com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_custom_font, 0)

                iconPaddingParam = attributes.getDimension(
                        com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_icon_padding,
                    resources.getDimension(R.dimen.bubble_icon_padding)
                )
                horizontalPaddingParam = attributes.getDimension(
                        com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_horizontal_padding,
                    resources.getDimension(R.dimen.bubble_horizontal_padding)
                )
                verticalPaddingParam = attributes.getDimension(
                        com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_vertical_padding,
                    resources.getDimension(R.dimen.bubble_vertical_padding)
                )
                iconSizeParam = attributes.getDimension(
                        com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_icon_size,
                    resources.getDimension(R.dimen.bubble_icon_size)
                )
                titleSizeParam = attributes.getDimension(
                        com.yingenus.pocketchinese.R.styleable.BubbleTabBarCust_bubbletabcust_title_size,
                    resources.getDimension(R.dimen.bubble_icon_size)
                )
                if (menuResource >= 0) {
                    setMenuResource(menuResource)
                }
            } finally {
                attributes.recycle()
            }


        }
    }


    private var oldBubble: BubbleCust? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMenuResource(menuResource: Int) {
        val menu = (MenuParser(context).parse(menuResource))
        removeAllViews()
        Log.e("menu ", "-->" + menu.size)
        menu.forEach { it ->
            if (it.id == 0) {
                throw ExceptionInInitializerError("Id is not added in menu item")
            }
            it.apply {
                it.horizontalPadding = horizontalPaddingParam
                it.verticalPadding = verticalPaddingParam
                it.iconSize = iconSizeParam
                it.iconPadding = iconPaddingParam
                it.customFont = customFontParam
                it.disabledIconColor = disabledIconColorParam
                it.titleSize = titleSizeParam
                it.disabledBackgroundColor= disabledBackgroundColorParam
                it.enabledBackgroundColor= enabledBackgroundColorParam
            }
            addView(BubbleCust(context, it).apply {
                if (it.checked) {
                    this.isSelected = true
                    oldBubble = this
                }
                setOnClickListener {
                    var b = it.id
                    if (oldBubble != null && oldBubble!!.id != b) {
                        (it as BubbleCust).isSelected = !it.isSelected
                        oldBubble!!.isSelected = false
                    }
                    if (oldBubble == null){
                        (it as BubbleCust).isSelected = !it.isSelected
                    }
                    oldBubble = it as BubbleCust
                    if (onBubbleClickListener != null) {
                        onBubbleClickListener!!.onBubbleClick(it.id)
                    }
                }
            })

        }
        invalidate()
    }
}

package android

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import com.modernbanc.android.R
import java.util.*

class ModernbancInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    val client: ModernbancApiClient,
    val elementId: String = UUID.randomUUID().toString(),
) : FrameLayout(context, attrs, defStyleAttr) {

    // Define default values for custom attributes
    private var textColor = Color.BLACK
    private var hint: CharSequence? = null
    private var hintTextColor = Color.GRAY
    private var maxLines = Int.MAX_VALUE
    private var singleLine = false
    private var inputType = InputType.TYPE_CLASS_TEXT

    private val editText: AppCompatEditText

    var isValid = false
    var validationFn: ((String) -> Boolean)? = null

    init {
        // Retrieve the attribute values using the TypedArray class
        val typedArray = context.obtainStyledAttributes(attrs,
            R.styleable.ModernbancInput, defStyleAttr, 0)

        textColor = typedArray.getColor(R.styleable.ModernbancInput_mbi_textColor, Color.BLACK)
        hint = typedArray.getText(R.styleable.ModernbancInput_mbi_hint)
        hintTextColor = typedArray.getColor(R.styleable.ModernbancInput_mbi_hintTextColor, Color.GRAY)
        maxLines = typedArray.getInt(R.styleable.ModernbancInput_mbi_maxLines, Int.MAX_VALUE)
        singleLine = typedArray.getBoolean(R.styleable.ModernbancInput_mbi_singleLine, false)
        inputType = typedArray.getInt(R.styleable.ModernbancInput_mbi_inputType, InputType.TYPE_CLASS_TEXT)

        // Recycle the TypedArray object
        typedArray.recycle()

        // Create and configure the EditText component
        editText = AppCompatEditText(context)
        editText.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        editText.setTextColor(textColor)
        editText.hint = hint
        editText.setHintTextColor(hintTextColor)
        editText.maxLines = maxLines
        editText.isSingleLine = singleLine
        editText.inputType = inputType

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val text = s.toString()
                isValid = validationFn?.invoke(text) ?: false
            }
        })

        addView(editText)
    }

    fun createSecret(onResponse: (CreateSecretResponse?) -> Unit, onFailure: (MdbApiError?) -> Unit) {
        val textValue = editText.text?.toString()
        if (textValue.isNullOrEmpty()) {
            return
        }

        val body = listOf(mapOf("name" to elementId, "value" to textValue))

        client.apiCall(
            method = "POST",
            endpoint = "/secrets",
            requestBody = body,
            responseClass = CreateSecretResponse::class.java,
            onResponse = onResponse,
            onFailure = onFailure
        )
    }

    fun setText(text: CharSequence?) {
        editText.setText(text)
    }

    /* Functions below are just to give as much control as you'd expect if
    *  you were to access EditText component directly. */
    fun setTextColor(color: Int) {
        textColor = color
        editText.setTextColor(textColor)
    }

    fun setHint(hintText: CharSequence?) {
        hint = hintText
        editText.hint = hint
    }

    fun setHintTextColor(color: Int) {
        hintTextColor = color
        editText.setHintTextColor(hintTextColor)
    }

    fun setMaxLines(lines: Int) {
        maxLines = lines
        editText.maxLines = maxLines
    }


    fun setSingleLine(single: Boolean) {
        singleLine = single
        editText.isSingleLine = singleLine
    }

    fun setInputType(type: Int) {
        inputType = type
        editText.inputType = inputType
    }
}

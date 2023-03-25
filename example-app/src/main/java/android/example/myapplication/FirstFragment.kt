package android.example.myapplication
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.CreateTokenResponse
import android.MdbApiError
import android.ModernbancApiClient
import android.ModernbancInput
import com.example.myapplication.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var parentLayout: LinearLayout
    private lateinit var tokenLabel: TextView
    private val apiKey = "API key that you can find in your workspace"
    private val apiClient = ModernbancApiClient(apiKey)
    private lateinit var modernbancInput: ModernbancInput

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        setupLayout()
        setupModernbancInput()
        setupButton()
        setupTokenLabel()
        return binding.root

    }

    private fun setupLayout() {
        // Init parent layout
        parentLayout = LinearLayout(context)
        parentLayout?.orientation = LinearLayout.VERTICAL
        binding.root.addView(parentLayout)
        // Set layout params for the LinearLayout container
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        parentLayout?.layoutParams = layoutParams
    }

    private fun setupModernbancInput() {
        // Create an instance of MyEditText
        modernbancInput = ModernbancInput(context = requireContext(), client = apiClient)

        modernbancInput.setInputType(InputType.TYPE_CLASS_TEXT)
        modernbancInput.setHint("Enter your data to encrypt")
        modernbancInput.setHintTextColor(Color.RED)

        modernbancInput.setMaxLines(3)
        modernbancInput.setText("Initial text")
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        modernbancInput.layoutParams = layoutParams
        parentLayout.addView(modernbancInput)
    }


    private fun setupButton() {
        // Create a Button instance
        val button = Button(context)

        // Set the button's properties like text, layout parameters, etc.
        button.text = "Create Token"
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        button.layoutParams = layoutParams

        // Set an OnClickListener for the button
        button.setOnClickListener {
            // Call createToken method of your SecureEditText instance
            modernbancInput?.createToken(
                onResponse = { tokenResponse: CreateTokenResponse? ->
                    // Handle the token response here
                    val token = tokenResponse?.result?.firstOrNull()
                    Log.d("CreateToken", "Token created: ${token?.id}")
                    activity?.runOnUiThread {
                        tokenLabel.text = "Created token with id ${tokenResponse?.result?.first()?.id}"
                    }
                },
                onFailure = { error: MdbApiError? ->
                    // Handle the error here
                    Log.e("CreateToken", "Error: ${error?.code} - ${error?.message}")
                    activity?.runOnUiThread {
                        tokenLabel.text = "Oops there was an error ${error.toString()}"
                    }
                }
            )
        }

        // Add the button to the container
        parentLayout?.addView(button)
    }

    private fun setupTokenLabel() {
        tokenLabel = TextView(requireContext())
        tokenLabel.text = "Newly created token will be shown here"

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 16 // Set top margin to 16 pixels
        layoutParams.gravity = LinearLayout.HORIZONTAL
        tokenLabel.layoutParams = layoutParams

        parentLayout.addView(tokenLabel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
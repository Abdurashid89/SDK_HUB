package uz.ssd.koinot_library.ui.base

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import uz.ssd.koinot_library.R
import uz.ssd.koinot_library.utils.TextWatcherWrapper

private const val PROGRESS_TAG = "bf_progress"

abstract class BaseFragment : Fragment() {
    abstract val layoutRes: Int

    private var instanceStateSaved: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(layoutRes, container, false)

    override fun onResume() {
        super.onResume()
        instanceStateSaved = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        instanceStateSaved = true
    }

    protected fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun isNotEmpty(
        et: EditText, v: TextInputLayout? = null,
    ): Boolean {
        return if (et.text.toString().isEmpty()) {
            if (v == null) et.setError(et.context.getString(R.string.is_not_empty), null)
            else {
//                (requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(
//                    100
//                )
                et.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
                v.error = et.context.getString(R.string.is_not_empty)
                v.errorIconDrawable = null
            }
            et.requestFocus()
            false
        } else {
            et.error = null
            true
        }
    }

    protected fun addTextWatcher(et: EditText, tvLabel: TextInputLayout) {
        et.addTextChangedListener(object : TextWatcherWrapper() {
            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true) {
                    tvLabel.error = null
                    s.toString()
                } else {
                    tvLabel.error = getString(R.string.is_not_empty)
                    tvLabel.errorIconDrawable = null
                }
            }
        })
    }

    protected fun showProgressDialog(progress: Boolean) {
        if (!isAdded || instanceStateSaved) return

        val fragment = childFragmentManager.findFragmentByTag(PROGRESS_TAG)
        if (fragment != null && !progress) {
            (fragment as ProgressDialog).dismissAllowingStateLoss()
            childFragmentManager.executePendingTransactions()
        } else if (fragment == null && progress) {
            ProgressDialog().show(
                childFragmentManager,
                PROGRESS_TAG
            )
            childFragmentManager.executePendingTransactions()
        }
    }
}
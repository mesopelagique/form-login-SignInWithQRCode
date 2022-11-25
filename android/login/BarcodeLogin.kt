package ___PACKAGE___

import android.Manifest
import android.view.View
import androidx.camera.core.ExperimentalGetImage
import androidx.databinding.DataBindingUtil
import com.qmobile.qmobiledatasync.app.BaseApp
import com.qmobile.qmobiledatasync.toast.ToastMessage
import com.qmobile.qmobiledatasync.utils.LoginForm
import com.qmobile.qmobiledatasync.utils.LoginHandler
import com.qmobile.qmobileui.activity.loginactivity.LoginActivity
import com.qmobile.qmobileui.barcode.Scanner
import com.qmobile.qmobileui.binding.bindImageFromDrawable
import com.qmobile.qmobileui.ui.SnackbarHelper
import com.qmobile.qmobileui.ui.setOnSingleClickListener
import com.qmobile.qmobileui.ui.setOnVeryLongClickListener
import com.qmobile.qmobileui.utils.PermissionChecker
import ___APP_PACKAGE___.R
import ___APP_PACKAGE___.databinding.BarcodeLoginBinding

@LoginForm
@ExperimentalGetImage
class BarcodeLogin(private val activity: LoginActivity) : LoginHandler {

    private var _binding: BarcodeLoginBinding? = null
    private val binding get() = _binding!!

    override val ensureValidMail = true
    private val scanner = Scanner()

    init {
        _binding = DataBindingUtil.setContentView<BarcodeLoginBinding?>(activity, R.layout.barcode_login).apply {
            lifecycleOwner = activity
        }
    }

    override fun initLayout() {
        bindImageFromDrawable(binding.loginLogo, BaseApp.loginLogoDrawable)

        binding.loginLogo.setOnVeryLongClickListener {
            activity.showRemoteUrlDialog()
        }

        binding.retryButton.setOnSingleClickListener {
            binding.retryButton.visibility = View.GONE
            scannerVisibility(true)
            scanner.reset()
        }

        (activity as? PermissionChecker)?.askPermission(
            context = activity,
            permission = Manifest.permission.CAMERA,
            rationale = activity.resources.getString(R.string.permission_rationale_barcode)
        ) { isGranted ->
            if (isGranted) {
                scannerVisibility(true)
                startScanning()
            }
        }
    }
     
    override fun validate(input: String): Boolean {
        return true
    }

    override fun onInputInvalid() {
        SnackbarHelper.show(
            activity,
            activity.resources.getString(R.string.login_invalid_email),
            ToastMessage.Type.WARNING
        )
        binding.retryButton.visibility = View.VISIBLE
        scannerVisibility(false)
    }

    override fun onLoginInProgress(inProgress: Boolean) {
        if (inProgress) {
            scannerVisibility(false)
            binding.loginProgressbar.visibility = View.VISIBLE
        } else {
            binding.loginProgressbar.visibility = View.GONE
            scannerVisibility(true)
        }
    }

    override fun onLoginSuccessful() {
        scanner.shutdown()
    }

    override fun onLoginUnsuccessful() {
        scannerVisibility(false)
        binding.retryButton.visibility = View.VISIBLE
    }

    override fun onLogout() {
        // Nothing to do
    }

    private fun startScanning() {
        scanner.start(
            context = activity,
            lifecycleOwner = activity,
            previewView = binding.cameraView,
            barcodeOverlay = binding.barcodeOverlay,
            progressIndicator = binding.progress,
            onScanned = { value -> onScanned(value) }
        )
    }

    private fun scannerVisibility(visible: Boolean) {
        binding.barcodeOverlay.visibility = if (visible) View.VISIBLE else View.GONE
        binding.cameraView.visibility = if (visible) View.VISIBLE else View.GONE
        binding.caption.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun onScanned(value: String) {
        activity.login(value)
    }
}

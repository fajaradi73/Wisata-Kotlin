package com.fajarproject.travels.util

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Resources
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.Paint.Align
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Process
import android.telephony.TelephonyManager
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager.LayoutParams
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.akexorcist.googledirection.model.Route
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.fajarproject.travels.BuildConfig
import com.fajarproject.travels.FlavorConfig
import com.fajarproject.travels.R
import com.fajarproject.travels.base.view.DialogNoListener
import com.fajarproject.travels.base.view.DialogYesListener
import com.fajarproject.travels.base.widget.ImageLoader
import com.fajarproject.travels.base.widget.Spannable
import com.fajarproject.travels.config.apiUrl
import com.fajarproject.travels.config.apiUrl.Companion.My_Permissions_Request_Location
import com.fajarproject.travels.models.UserModel
import com.fajarproject.travels.preference.AppPreference
import com.fajarproject.travels.ui.opsiLogin.OpsiLoginActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import kotlin.math.*


/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


@SuppressLint("StaticFieldLeak","VisibleForTests")

object Util {

    fun getRetrofitRxJava2(context: Context) : Retrofit?{
        return Builder()
            .baseUrl(FlavorConfig.baseUrl())
            .client(getOkHttp(context))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
    }

    private fun getOkHttp(context: Context): OkHttpClient {
        val interceptor =
            HttpLoggingInterceptor()
        interceptor.level = Level.BODY
        val chucker = ChuckerInterceptor(context)
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor).addInterceptor(chucker).build()
    }

    fun okHttpDownload(): OkHttpClient {
        val interceptor =
            HttpLoggingInterceptor()
        interceptor.level = Level.BODY
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(interceptor).build()
    }
    private var dialog : Dialog? = null

    private fun progressBar(context : Context){
        dialog = Dialog(context)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(false)
    }

    fun showLoading(context: Context){
        progressBar(context)
        if (dialog != null && !dialog!!.isShowing){
            dialog!!.show()
            dialog!!.setContentView(R.layout.progress)
        }
    }

    fun hideLoading(){
        if(dialog != null && dialog!!.isShowing){
            dialog!!.dismiss()
            dialog!!.setContentView(R.layout.progress)
        }
    }

    // Thread Factory to set Thread priority to Background
    internal class ImageThreadFactory : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable).apply {
                name = "ImageLoader Thread"
                priority = Process.THREAD_PRIORITY_BACKGROUND
            }
        }
    }

    fun downloadBitmapFromURL(imageUrl: String): Bitmap? {
        val url = URL(imageUrl)
        val inputStream = BufferedInputStream(url.openConnection().getInputStream())

        // Scale Bitmap to Screen Size to store in Cache
        return scaleBitmap(inputStream, ImageLoader.screenWidth, ImageLoader.screenHeight)
    }

    fun scaleBitmapForLoad(bitmap: Bitmap, width: Int, height: Int): Bitmap? {

        if(width == 0 || height == 0) return bitmap

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val inputStream = BufferedInputStream(ByteArrayInputStream(stream.toByteArray()))

        // Scale Bitmap to required ImageView Size
        return scaleBitmap(inputStream,  width, height)
    }

    private fun scaleBitmap(inputStream: BufferedInputStream, width: Int, height: Int) : Bitmap? {
        return BitmapFactory.Options().run {
            inputStream.mark(inputStream.available())

            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)

            inSampleSize = calculateInSampleSize(this, width, height)

            inJustDecodeBounds = false
            inputStream.reset()
            BitmapFactory.decodeStream(inputStream, null,  this)
        }
    }

    // From Developer Site
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) inSampleSize *= 2
        }

        return inSampleSize
    }

    private fun getDeepChildOffset(mainParent : ViewGroup, parent: ViewParent, child : View, accumulatedOffset : Point){
        val parentGroup = parent as ViewGroup
        accumulatedOffset.x += child.left
        accumulatedOffset.y += child.top
        if (parentGroup == mainParent){
            return
        }
        getDeepChildOffset(mainParent,parentGroup.parent,parentGroup,accumulatedOffset)
    }
    fun animateHeart(view: CardView) {
        val scaleAnimation =
            ScaleAnimation(
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
        prepareAnimation(scaleAnimation)
        val alphaAnimation =
            AlphaAnimation(0.0f, 1.0f)
        prepareAnimation(alphaAnimation)
        val animation =
            AnimationSet(true)
        animation.addAnimation(alphaAnimation)
        animation.addAnimation(scaleAnimation)
        animation.duration = 700
        animation.fillAfter = true
        view.startAnimation(animation)
    }

    private fun prepareAnimation(animation: Animation): Animation {
        animation.repeatCount = 1
        animation.repeatMode = Animation.REVERSE
        return animation
    }
    fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): BitmapDescriptor? {
        val background: Drawable? = ContextCompat.getDrawable(context!!, vectorResId)
        background?.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val bitmap: Bitmap? = Bitmap.createBitmap(
            background?.intrinsicWidth!!,
            background.intrinsicHeight,
            Config.ARGB_8888
        )
        val canvas = Canvas(bitmap!!)
        background.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun setStyleMaps(googleMap : GoogleMap?,context: Context?){
        try {
            // Customise map styling via JSON fil
            val success = googleMap!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.json_style
                )
            )
            if (!success) {
                Log.e("Wisata", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("Wisata", "Can't find style. Error: ", e)
        }
    }

    fun checkGooglePlayServicesAvailable(activity: Activity?): Boolean {
        if(activity != null){
            val googleAPI: GoogleApiAvailability = GoogleApiAvailability.getInstance()
            val result = googleAPI.isGooglePlayServicesAvailable(activity)
            if (result != ConnectionResult.SUCCESS) {
                if (googleAPI.isUserResolvableError(result)) {
                    googleAPI.getErrorDialog(
                        activity, result,
                        0
                    ).show()
                }
                return false
            }
            return true
        }
        return false
    }

    fun setCameraWithCoordinationBounds(route: Route,googleMap: GoogleMap?) {
        val southwest: LatLng? =
            route.bound.southwestCoordination.coordination
        val northeast: LatLng? =
            route.bound.northeastCoordination.coordination
        val bounds =
            LatLngBounds(southwest, northeast)
        googleMap!!.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                100
            )
        )
    }
    fun setAds(adView: AdView){
        val adRequest: AdRequest? =
            AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    fun setAds(interstitialAd: InterstitialAd){
        val adRequest: AdRequest? =
            AdRequest.Builder().build()
        interstitialAd.loadAd(adRequest)
    }
    private const val MEGABYTE = 1024 * 1024

    fun downloadFile(fileUrl: String?, directory: File?) {
        try {
            val url = URL(fileUrl)
            val urlConnection =
                url.openConnection() as HttpURLConnection
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect()
            val inputStream: InputStream = urlConnection.inputStream
            val fileOutputStream = FileOutputStream(directory!!)
            val buffer = ByteArray(MEGABYTE)
            var bufferLength: Int
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fileOutputStream.write(buffer, 0, bufferLength)
            }
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun decoded(JWTEncoded: String): String {
        try {
            val split = JWTEncoded.split("\\.").toTypedArray()
            return getJson(split[1])
        } catch (e: UnsupportedEncodingException) {
            //Error
            e.printStackTrace()
        }
        return ""
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes: ByteArray? =
            Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes!!, StandardCharsets.UTF_8)
    }
    fun changeFocusable(views: Array<View>, state: Boolean) {
        for (view in views) {
            view.isFocusable = state
        }
    }

    fun changeVisibility(views: Array<View>, state: Int) {
        for (view in views) {
            view.visibility = state
        }
    }

    fun changeEnabledStatus(views: Array<View>, state: Boolean) {
        for (view in views) {
            view.isEnabled = state
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun getKeyHash(context: Context?){
        try {
            val info: PackageInfo = context!!.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d(
                    "KeyHash:",
                    Base64.encodeToString(md.digest(), Base64.DEFAULT)
                )
            }
        } catch (e: NameNotFoundException) {
            Log.d("nama not found : ", "" + e.fillInStackTrace())
        } catch (e: NoSuchAlgorithmException) {
            Log.d("gala not found : ", "" + e.fillInStackTrace())
        }
    }

    @SuppressLint("HardwareIds")
    fun getDeviceID(context: Context) : String? {

        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(context, permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null
        }
        return tm.deviceId
    }

    fun isValidEmail(email: String?): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(
            email.toString()
        ).matches()
    }
    fun isValidPassword(passwordhere: String, confirmed: String, errorList: MutableList<String?>, context: Context): Boolean {
        val specailCharPatten: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val upperCasePatten: Pattern = Pattern.compile("[A-Z ]")
        val lowerCasePatten: Pattern = Pattern.compile("[a-z ]")
        val digitCasePatten: Pattern = Pattern.compile("[0-9 ]")
        errorList.clear()
        var flag = true
        if (passwordhere != confirmed) {
            errorList.add(context.getString(R.string.password_confirm))
            flag = false
        }
        if (passwordhere.length < 8) {
            errorList.add(context.getString(R.string.password_8_karakter))
            flag = false
        }
        if (!specailCharPatten.matcher(passwordhere).find()) {
            errorList.add(context.getString(R.string.password_special_karakter))
            flag = false
        }
        if (!upperCasePatten.matcher(passwordhere).find()) {
            errorList.add(context.getString(R.string.password_uppercase))
            flag = false
        }
        if (!lowerCasePatten.matcher(passwordhere).find()) {
            errorList.add(context.getString(R.string.password_lowercase))
            flag = false
        }
        if (!digitCasePatten.matcher(passwordhere).find()) {
            errorList.add(context.getString(R.string.passwrod_number))
            flag = false
        }
        return flag
    }
    fun isValidPassword(password: String, errorList: MutableList<String?>, context: Context): Boolean {
        val specialCharPatten: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val upperCasePatten: Pattern = Pattern.compile("[A-Z ]")
        val lowerCasePatten: Pattern = Pattern.compile("[a-z ]")
        val digitCasePatten: Pattern = Pattern.compile("[0-9 ]")
        errorList.clear()
        var flag = true
        if (password.length < 8) {
            errorList.add(context.getString(R.string.password_8_karakter))
            flag = false
        }
        if (!specialCharPatten.matcher(password).find()) {
            errorList.add(context.getString(R.string.password_special_karakter))
            flag = false
        }
        if (!upperCasePatten.matcher(password).find()) {
            errorList.add(context.getString(R.string.password_uppercase))
            flag = false
        }
        if (!lowerCasePatten.matcher(password).find()) {
            errorList.add(context.getString(R.string.password_lowercase))
            flag = false
        }
        if (!digitCasePatten.matcher(password).find()) {
            errorList.add(context.getString(R.string.passwrod_number))
            flag = false
        }
        return flag
    }
    fun requestFocus(view: View,activity: Activity?) {
        if (view.requestFocus()) {
            activity?.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

    fun setColorFilter(@NonNull drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(
                color,
                BlendMode.SRC_ATOP
            )
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
    fun mediaType(): MediaType? {
        return "application/json; charset=utf-8".toMediaTypeOrNull()
    }

    fun justify(textView: TextView) {
        val isJustify =
            AtomicBoolean(false)
        val textString = textView.text.toString()
        val textPaint: TextPaint = textView.paint
        val builder = SpannableStringBuilder()
        textView.post {
            if (!isJustify.get()) {
                val lineCount = textView.lineCount
                val textViewWidth = textView.width
                for (i in 0 until lineCount) {
                    val lineStart = textView.layout.getLineStart(i)
                    val lineEnd = textView.layout.getLineEnd(i)
                    val lineString = textString.substring(lineStart, lineEnd)
                    if (i == lineCount - 1) {
                        builder.append(SpannableString(lineString))
                        break
                    }
                    val trimSpaceText = lineString.trim { it <= ' ' }
                    val removeSpaceText = lineString.replace(" ".toRegex(), "")
                    val removeSpaceWidth = textPaint.measureText(removeSpaceText)
                    val spaceCount =
                        trimSpaceText.length - removeSpaceText.length.toFloat()
                    val eachSpaceWidth =
                        (textViewWidth - removeSpaceWidth) / spaceCount
                    val spannableString =
                        SpannableString(lineString)
                    for (j in trimSpaceText.indices) {
                        val c = trimSpaceText[j]
                        if (c == ' ') {
                            val drawable: Drawable =
                                ColorDrawable(0x00ffffff)
                            drawable.setBounds(0, 0, eachSpaceWidth.toInt(), 0)
                            val span =
                                ImageSpan(drawable)
                            spannableString.setSpan(
                                span,
                                j,
                                j + 1,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                    builder.append(spannableString)
                }
                textView.text = builder
                isJustify.set(true)
            }
        }
    }
    fun checkFileByName(context: Context, vararg strings: String): Boolean {
        var result = false
        if (!File(context.filesDir.toString() + File.separator + apiUrl.BASE_FILE).exists()) {
            File(context.filesDir.toString() + File.separator + apiUrl.BASE_FILE)
                .mkdirs()
        }
        val string = strings[strings.size - 1]
        val directory = context.filesDir.toString() + File.separator + apiUrl.BASE_FILE + File.separator
        val listFiles: Array<File> = File(directory).listFiles()!!
        for (file in listFiles) {
            if (file.absolutePath.contains(string)) {
                result = true
                break
            }
        }
        return result
    }
    fun getDirectory(context: Context, of: String): String {
        return context.filesDir.toString() + File.separator + of
    }

    fun writeResponseBodyToDisk(context: Context, body: ResponseBody, filename: String, ext: String?): Boolean {
        return try {
            if (!File(context.filesDir.toString() + File.separator + apiUrl.BASE_FILE).exists()) {
                File(context.filesDir.toString() + File.separator + apiUrl.BASE_FILE)
                    .mkdirs()
            }
            val directory =
                context.filesDir.toString() + File.separator + apiUrl.BASE_FILE + File.separator
            val path: String
            path = if (ext == null) {
                directory + filename
            } else {
                "$directory$filename.$ext"
            }
            val fileToBeSaved = File(path)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(fileToBeSaved)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }
    fun getCurrentDate(): String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd MMMM yyyy", Locale("id","ID"))
        return df.format(c)
    }

    fun getCurrentDateTime(): String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss",Locale("id","ID"))
        return df.format(c)
    }

    fun getCurrentDateFull(): String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("EEEEEE, dd-MMMM-yyyy, HH:mm:ss",Locale("id","ID"))
        return df.format(c)
    }

    //method to convert your text to image
    fun textAsBitmap(text: String?, textSize: Float, textColor: Int): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Align.LEFT
        val baseline = -paint.ascent() // ascent() is negative

        val width = (paint.measureText(text) + 0.0f).toInt() // round

        val height = (baseline + paint.descent() + 0.0f).toInt()
        val image: Bitmap = Bitmap.createBitmap(
            width,
            height,
            Config.ARGB_8888
        )
        val canvas = Canvas(image)
        canvas.drawText(text!!, 0f, baseline, paint)
        return image
    }

    private var firebase_token : String? = null
    fun getFirebaseToken(context: Context) : String?{
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task: Task<InstanceIdResult> ->
                if (!task.isSuccessful) {
                    Log.w("coba", "getInstanceId failed", task.exception)
                    return@addOnCompleteListener
                }

                firebase_token = task.result!!.token
                // Log and toast
                val msg: String = context.getString(R.string.msg_token_fmt, firebase_token)
                Log.d("Token", msg)
            }
        return firebase_token
    }

    fun getDeviceName(): String {
        val manufacturer: String? = Build.MANUFACTURER
        val model: String = Build.MODEL
        return if (model.startsWith(manufacturer!!)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    @Suppress("DEPRECATION")
    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }
    fun getUserToken(context: Context): UserModel {
        val type: Type? =
            object : TypeToken<UserModel?>() {}.type
        return Gson()
            .fromJson(AppPreference.getStringPreferenceByName(context, "user"), type)
    }

    fun getAppVersion() : String? {
        val tmpVersionName: String = BuildConfig.VERSION_NAME
        val versionCode: Int = BuildConfig.VERSION_CODE
        val versionName = tmpVersionName.substring(0, 3)
        return "$versionName ($versionCode)"
    }

    private var alertDialog : AlertDialog? = null
    private var btnYes : CardView? = null
    private var btnNo : CardView? = null
    private var textYes : TextView? = null
    private fun initShowDialog(activity: Activity,title : String, message : String, isTwoButton : Boolean){
        if (alertDialog != null && alertDialog!!.isShowing  || activity.isFinishing){
            // A dialog is already open, wait for it to be dismissed, do nothing
            println("alert dialog is not null or alert dialog is showing or context (activity) is finishing")
        }else{
            alertDialog         = AlertDialog.Builder(activity).create()
            val inflater        = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val viewDialog      = inflater.inflate(R.layout.show_dialog,null)
            val titleDialog     = viewDialog.findViewById<TextView>(R.id.title_dialog)
            val messageDialog   = viewDialog.findViewById<TextView>(R.id.message_dialog)
            btnYes              = viewDialog.findViewById(R.id.btn_yes)
            btnNo               = viewDialog.findViewById(R.id.btn_no)
            textYes             = viewDialog.findViewById(R.id.tvYes)
            alertDialog?.setView(viewDialog)
            alertDialog?.setCancelable(false)
            titleDialog.text    = title
            messageDialog.text  = message
            if (message.isNotEmpty()){
                messageDialog.visibility = View.VISIBLE
            }
            if (title.isEmpty()){
                titleDialog.visibility = View.GONE
            }
            if (isTwoButton){
                btnNo?.visibility       = View.VISIBLE
                textYes?.text = activity.resources.getString(R.string.yes)
            }
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog?.show()
        }
    }

    fun showRoundedDialog(activity: Activity,title : String,message: String,isTwoButton: Boolean,dialogYesListener: DialogYesListener,dialogNoListener: DialogNoListener){
        initShowDialog(activity,title,message,isTwoButton)
        btnYes?.setOnClickListener {
            alertDialog?.dismiss()
            dialogYesListener.onYes()
        }
        btnNo?.setOnClickListener {
            alertDialog?.dismiss()
            dialogNoListener.onNo()
        }
    }
    fun showRoundedDialog(activity: Activity,title : String,message: String,isTwoButton: Boolean){
        initShowDialog(activity,title,message,isTwoButton)
        btnYes?.setOnClickListener {
            alertDialog?.dismiss()
        }
        btnNo?.setOnClickListener {
            alertDialog?.dismiss()
        }
    }

    fun checkLocationPermission(context: Activity) {
        val permission = ContextCompat.checkSelfPermission(context,
            permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf<String?>(Manifest.permission.ACCESS_FINE_LOCATION),My_Permissions_Request_Location
                )
            } else {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf<String?>(Manifest.permission.ACCESS_FINE_LOCATION),My_Permissions_Request_Location
                )
            }
        }
    }
    @SuppressLint("DefaultLocale")
    fun milisecondTotimes(millis: Long): String? {
        return String.format(
            "%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millis)
            ),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millis)
            )
        )
    }
    var days =
        arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    var months = arrayOf(
        "Januari",
        "Februari",
        "Maret",
        "April",
        "Mei",
        "Juni",
        "Juli",
        "Agustus",
        "September",
        "Oktober",
        "November",
        "Desember"
    )
    var hourFormat =
        SimpleDateFormat("HH:mm", Locale.ENGLISH)

    fun showDateFormat(start: Date, end: Date): String? {
        var builder =
            days[start.day] + ", " + start.date + " " + months[start.month] + " " + (1900 + start.year) + " " + hourFormat.format(
                start
            )
        builder =
            builder + " - " + days[end.day] + ", " + end.date + " " + months[end.month] + " " + (1900 + end.year) + " " + hourFormat.format(
                end
            )
        return builder
    }

    fun showDate(date: Date): String? {
        return days[date.day] + ", " + date.date + " " + months[date.month] + " " + (1900 + date.year)
    }

    fun showDateWithHour(date: Date): String? {
        return days[date.day] + ", " + date.date + " " + months[date.month] + " " + (1900 + date.year) + " " + hourFormat.format(
            date
        ) + " wib"
    }

    fun getOneDayMillis(): Long {
        return 24 * 60 * 60 * 1000
    }
    fun epochToTime(epoch: Long): String? {
        val date = Date(epoch)
        val df =
            SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.getDefault())
        return df.format(date)
    }

    fun epochToTimeFormat(epoch: Long?): String? {
        val date = Date(epoch!!)
        val df =
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        return df.format(date)
    }

    fun epochToTimeTwentyFourHours(epoch: String): String? {
        val date = Date(epoch.toLong())
        val df =
            SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault())
        return df.format(date)
    }

    fun epochToDate(epoch: String): String? {
        val date = Date(epoch.toLong())
        val df =
            SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return df.format(date)
    }

    fun epochToDateFromDate(
        epoch: String?,
        oldFormatDate: String?,
        newFormatDate: String?
    ): String? {
        var dateString: String? = null
        try {
            val dateFormatOld: DateFormat =
                SimpleDateFormat(oldFormatDate!!, Locale.getDefault())
            val dateFormatNew: DateFormat =
                SimpleDateFormat(newFormatDate!!, Locale.getDefault())
            val date = dateFormatOld.parse(epoch!!)
            dateString = dateFormatNew.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateString
    }

    fun dateTimetoMilis(dateString: String?): Long {
        var sec: Long = 0
        val df = SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss", Locale.getDefault())
        try {
            val time = df.parse(dateString!!)
            val millis = time!!.time
            sec = TimeUnit.MILLISECONDS.toSeconds(millis)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return sec
    }

    fun getDateTimeDeltatoSecond(dateString: String?): Long {
        val oldTime = dateTimetoMilis(dateString)
        val currentTime: String = getCurrentDateTime()
        val newTime = dateTimetoMilis(currentTime)
        return newTime - oldTime
    }
    fun sessionExpired(context: Activity) {
        AppPreference.writePreference(context, "user", "")
        Toast.makeText(
            context,
            "Session expired, Please re-login",
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(context, OpsiLoginActivity::class.java)
        context.startActivity(intent)
        context.finish()
    }

    fun circleLoading(context: Context) : CircularProgressDrawable{
        val circularProgressDrawable            = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth    = 5f
        circularProgressDrawable.centerRadius   = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }

    fun configureGoogleSignIn(context: Context): GoogleSignInClient {
        val mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, mGoogleSignInOptions)
    }

    fun saveUser(user: UserModel, context: Context){
        val type: Type? = object : TypeToken<UserModel?>() {}.type
        val json: String? = Gson().toJson(user, type)
        AppPreference.writePreference(context,"user",json!!)
    }

    fun convertLongToDateWithTime(timestamp: Long) :String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = timestamp
        return android.text.format.DateFormat.format("dd MMMM yyyy HH:mm:dd", calendar).toString()
    }

    fun convertLongToDate(timestamp: Long) :String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = timestamp
        return android.text.format.DateFormat.format("dd MMMM yyyy", calendar).toString()
    }

    fun prettyCount(number: Int): String? {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val numValue: Long = number.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.0").format(
                numValue / 10.0.pow(base * 3.toDouble())
            ) + suffix[base]
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }
    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, tv: TextView,
        maxLine: Int, spannableText: String, viewMore: Boolean
    ): SpannableStringBuilder? {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spannableText)) {
            ssb.setSpan(object : Spannable(false) {
                override fun onClick(widget: View) {
                    tv.layoutParams = tv.layoutParams
                    tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                    tv.invalidate()
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "", !viewMore)
                    } else {
                        makeTextViewResizable(tv, maxLine, "...$spannableText", viewMore)
                    }
                }
            }, str.indexOf(spannableText), str.indexOf(spannableText) + spannableText.length, 0)
        }
        return ssb
    }

    fun makeTextViewResizable(
        tv: TextView,
        maxLine: Int,
        expandText: String,
        viewMore: Boolean
    ) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto: ViewTreeObserver = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val text: String
                val lineEndIndex: Int
                val obs: ViewTreeObserver = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                if (maxLine == 0) {
                    lineEndIndex = tv.layout.getLineEnd(0)
                    text = tv.text.subSequence(
                        0,
                        lineEndIndex - expandText.length + 1
                    ).toString() + "..." + expandText
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    text = tv.text.subSequence(
                        0,
                        lineEndIndex - expandText.length + 1
                    ).toString() + "..." + expandText
                } else {
                    lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    text = tv.text.subSequence(0, lineEndIndex).toString()
                }
                tv.text = text
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(
                    addClickablePartTextViewResizable(
                        Html.fromHtml(tv.text.toString()), tv, lineEndIndex, expandText,
                        viewMore
                    ), TextView.BufferType.SPANNABLE
                )
            }
        })
    }

    fun longToDate(timestamp: Long): Date{
        return Date(timestamp * 1000L)
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getCurrentMilliSecond() : Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        return TimeUnit.HOURS.toMillis(calendar.get(Calendar.HOUR_OF_DAY).toLong()) +
                TimeUnit.MINUTES.toMillis(calendar.get(Calendar.MINUTE).toLong()) +
                TimeUnit.SECONDS.toMillis(calendar.get(Calendar.SECOND).toLong()) +
                calendar.get(Calendar.MILLISECOND)
    }

    fun convertDate(year: Int, month: Int, day: Int): String? {
        val temp = year.toString() + "-" + (month + 1) + "-" + day
        val calendarDateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val newFormatDate =
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return try {
            newFormatDate.format(calendarDateFormat.parse(temp)!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun checkDataNull(data: String?): String? {
        return if (data == null || data.isEmpty()) {
            ""
        } else {
            data
        }
    }
    fun getYear(timestamp: Long) :String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = timestamp * 1000L
        return android.text.format.DateFormat.format("yyyy", calendar).toString()
    }
    fun getMonth(timestamp: Long) :String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = timestamp * 1000L
        return android.text.format.DateFormat.format("M", calendar).toString()
    }
    fun getDate(timestamp: Long) :String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = timestamp * 1000L
        return android.text.format.DateFormat.format("dd", calendar).toString()
    }

    fun convertTanggal(
        tanggal: String,
        oldFormat: String,
        newFormat: String
    ): String {
        val newDateFormat =
            SimpleDateFormat(newFormat, Locale.getDefault())
        val oldDateFormat =
            SimpleDateFormat(oldFormat, Locale.getDefault())
        return try {
            newDateFormat.format(oldDateFormat.parse(tanggal)!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }
    fun convertDpToPixel(dp: Float): Int {
        val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
        val px: Float = dp * (metrics.densityDpi / 160f)
        return px.roundToInt()
    }

    fun sign(i: Int): Int {
        if (i == 0) return 0
        return if (i shr 31 != 0) -1 else +1
    }

    fun sign(i: Long): Int {
        if (i == 0L) return 0
        return if (i shr 63 != 0L) -1 else +1
    }

    fun sign(double: Double): Int {
        var f = double
        require(f == f) { "NaN" }
        if (f == 0.0) return 0
        f *= Double.POSITIVE_INFINITY
        if (f == Double.POSITIVE_INFINITY) return +1
        if (f == Double.NEGATIVE_INFINITY) return -1
        throw IllegalArgumentException("Unfathomed double")
    }

    fun calculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val radius = 6371 // radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (sin(dLat / 2) * sin(dLat / 2)
                + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2)))
        val c = 2 * asin(sqrt(a))
        val valueResult = radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        return radius * c
    }
    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}
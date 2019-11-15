package com.fajarproject.wisata.util

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
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
import android.text.style.ImageSpan
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager.LayoutParams
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.model.Route
import com.bumptech.glide.Glide
import com.fajarproject.wisata.App
import com.fajarproject.wisata.App.Companion.My_Permissions_Request_Location
import com.fajarproject.wisata.BuildConfig
import com.fajarproject.wisata.R
import com.fajarproject.wisata.login.model.User
import com.fajarproject.wisata.preference.AppPreference
import com.fajarproject.wisata.view.DialogNoListener
import com.fajarproject.wisata.view.DialogYesListener
import com.fajarproject.wisata.widget.ImageLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
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
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.*
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern

/**
 * Created by Fajar Adi Prasetyo on 09/10/19.
 */


@SuppressLint("StaticFieldLeak")
object Util {
    fun getDefaultRetrofit(): Retrofit? {
        return Builder()
            .baseUrl(App.API)
            .client(getOkHttp())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofitRxJava2() : Retrofit?{
        return Builder()
            .baseUrl(App.API)
            .client(getOkHttp())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun getStringResponseRetrofit(): Retrofit? {
        return Builder()
            .baseUrl(App.API)
            .client(getOkHttp())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    fun getEmptyResponseRetrofit(): Retrofit? {
        return Builder()
            .baseUrl(App.API)
            .client(getOkHttp())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getOkHttp(): OkHttpClient {
        val interceptor =
            HttpLoggingInterceptor()
        interceptor.level = Level.BODY
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()
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
            AdRequest.Builder().addTestDevice("828CDDB6B368965E6B6BB3B6C5321FD6").build()
        adView.loadAd(adRequest)
    }

    fun setAds(interstitialAd: InterstitialAd){
        val adRequest: AdRequest? =
            AdRequest.Builder().addTestDevice("828CDDB6B368965E6B6BB3B6C5321FD6").build()
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
            val totalSize = urlConnection.contentLength
            val buffer = ByteArray(MEGABYTE)
            var bufferLength = 0
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
    fun isValidPassword(passwordhere: String, confirmhere: String, errorList: MutableList<String?>,context: Context): Boolean {
        val specailCharPatten: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val UpperCasePatten: Pattern = Pattern.compile("[A-Z ]")
        val lowerCasePatten: Pattern = Pattern.compile("[a-z ]")
        val digitCasePatten: Pattern = Pattern.compile("[0-9 ]")
        errorList.clear()
        var flag = true
        if (passwordhere != confirmhere) {
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
        if (!UpperCasePatten.matcher(passwordhere).find()) {
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
    fun isValidPassword(passwordhere: String, errorList: MutableList<String?>,context: Context): Boolean {
        val specailCharPatten: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val UpperCasePatten: Pattern = Pattern.compile("[A-Z ]")
        val lowerCasePatten: Pattern = Pattern.compile("[a-z ]")
        val digitCasePatten: Pattern = Pattern.compile("[0-9 ]")
        errorList.clear()
        var flag = true
        if (passwordhere.length < 8) {
            errorList.add(context.getString(R.string.password_8_karakter))
            flag = false
        }
        if (!specailCharPatten.matcher(passwordhere).find()) {
            errorList.add(context.getString(R.string.password_special_karakter))
            flag = false
        }
        if (!UpperCasePatten.matcher(passwordhere).find()) {
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
    private fun requestFocus(view: View,activity: Activity?) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }
    private fun capitalize(s: String?): String {
        if (s == null || s.length == 0) {
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
        return MediaType.parse("application/json; charset=utf-8")
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
        if (!File(context.filesDir.toString() + File.separator + App.BASE_FILE).exists()) {
            File(context.filesDir.toString() + File.separator + App.BASE_FILE)
                .mkdirs()
        }
        val string = strings[strings.size - 1]
        val directory = context.filesDir.toString() + File.separator + App.BASE_FILE + File.separator
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
            if (!File(context.filesDir.toString() + File.separator + App.BASE_FILE).exists()) {
                File(context.filesDir.toString() + File.separator + App.BASE_FILE)
                    .mkdirs()
            }
            val directory =
                context.filesDir.toString() + File.separator + App.BASE_FILE + File.separator
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
    fun getUserToken(context: Context): User {
        val type: Type? =
            object : TypeToken<User?>() {}.type
        return Gson()
            .fromJson(AppPreference.getStringPreferenceByName(context, "user"), type)
    }

    fun getAppVersion() : String? {
        val tmpVersionName: String = BuildConfig.VERSION_NAME
        val versionCode: Int = BuildConfig.VERSION_CODE
        val versionName = tmpVersionName.substring(0, 3)
        return "$versionName ($versionCode)"
    }
    private var loadingdialog : Dialog? = null

    fun showLoadingGIF(activity: Activity){
        if (loadingdialog != null && loadingdialog!!.isShowing){
            println("loading is show")
        }else {
            loadingdialog = Dialog(activity)
            loadingdialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingdialog!!.setCancelable(false)
            loadingdialog!!.setContentView(R.layout.custom_loading)

            val gifImageView: ImageView =
                loadingdialog!!.findViewById(R.id.custom_loading_imageView)
            Glide.with(activity)
                .load(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(gifImageView)
            loadingdialog!!.show()
        }
    }
    fun hideLoadingGIF(){
        loadingdialog!!.dismiss()
    }

    private var alertDialog : AlertDialog? = null
    private var btnYes : Button? = null
    private var btnNo : Button? = null
    private fun initShowDialog(activity: Activity,title : String, message : String, isTwoButton : Boolean){
        if (alertDialog != null && alertDialog!!.isShowing  || activity.isFinishing){
            // A dialog is already open, wait for it to be dismissed, do nothing
            println("alert dialog is not null or alert dialog is showing or context (activity) is finishing")
        }else{
            alertDialog         = AlertDialog.Builder(activity).create()
            val inflater        = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val viewDialog      = inflater.inflate(R.layout.alert_dialog,null)
            val titleDialog     = viewDialog.findViewById<TextView>(R.id.title_dialog)
            val messageDialog   = viewDialog.findViewById<TextView>(R.id.message_dialog)
            btnYes              = viewDialog.findViewById(R.id.btn_yes)
            btnNo               = viewDialog.findViewById(R.id.btn_no)
            val viewLine        = viewDialog.findViewById<View>(R.id.view_line)
            alertDialog!!.setView(viewDialog)
            alertDialog!!.setCancelable(false)
            titleDialog.text    = title
            messageDialog.text  = message
            if (message.isNotEmpty()){
                messageDialog.visibility = View.VISIBLE
            }
            if (title.isEmpty()){
                titleDialog.visibility = View.GONE
            }
            if (isTwoButton){
                btnNo!!.visibility       = View.VISIBLE
                viewLine.visibility    = View.VISIBLE
                btnYes!!.text = activity.resources.getString(R.string.yes)
            }
            alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog!!.show()
        }
    }

    fun showRoundedDialog(activity: Activity,title : String,message: String,isTwoButton: Boolean,dialogYesListener: DialogYesListener,dialogNoListener: DialogNoListener){
        initShowDialog(activity,title,message,isTwoButton)
        btnYes!!.setOnClickListener {
            alertDialog!!.dismiss()
            dialogYesListener.onYes()
        }
        btnNo!!.setOnClickListener {
            alertDialog!!.dismiss()
            dialogNoListener.onNo()
        }
    }
    fun showRoundedDialog(activity: Activity,title : String,message: String,isTwoButton: Boolean){
        initShowDialog(activity,title,message,isTwoButton)
        btnYes!!.setOnClickListener {
            alertDialog!!.dismiss()
        }
        btnNo!!.setOnClickListener {
            alertDialog!!.dismiss()
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
}
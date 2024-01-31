package com.fajarproject.travels.ui.wisataTerdekat


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.Shimmer
import com.fajarproject.travels.R
import com.fajarproject.travels.adapter.WisataTerdekatAdapter
import com.fajarproject.travels.api.WisataApi
import com.fajarproject.travels.base.mvp.MvpFragment
import com.fajarproject.travels.base.view.OnItemClickListener
import com.fajarproject.travels.base.widget.PaginationScrollListener
import com.fajarproject.travels.databinding.FragmentWisataTerdekatBinding
import com.fajarproject.travels.models.NearbyModel
import com.fajarproject.travels.preference.AppPreference
import com.fajarproject.travels.ui.main.MainActivity
import com.fajarproject.travels.util.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Fajar Adi Prasetyo on 12/01/20.
 */

class WisataTerdekatFragment : MvpFragment<WisataTerdekatPresenter>(), WisataTerdekatView {

	private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
	private var currentLatitude: Double = 0.0
	private var currentLongitude: Double = 0.0
	private var mLocationPermissionGranted = false
	private var requestCodeLocations = 123
	private var mLastKnownLocation: Location? = null

	private lateinit var linearLayoutManager: LinearLayoutManager
	private var limit: Int? = 10
	private var currentPage = 0
	private var adapter: WisataTerdekatAdapter? = null
	private var countData = 0

	private var isLoading = false
	private var isLastPage = false
	private lateinit var binding: FragmentWisataTerdekatBinding

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		binding = FragmentWisataTerdekatBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setRecycleView()
		limit = AppPreference.getIntPreferenceByName(requireActivity(), "sizePerPage")
		updateLocationUI()
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
			isLoading = false
			isLastPage = false
			currentPage = 0
			presenter?.getNearbyWisata(currentLatitude, currentLongitude, limit!!, currentPage)
		}
	}

	override fun createPresenter(): WisataTerdekatPresenter {
		val wisataApi: WisataApi = Util.getRetrofitRxJava2(requireContext())!!.create(WisataApi::class.java)
		activity = context as MainActivity
		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
		return WisataTerdekatPresenter(this, requireActivity(), mFusedLocationProviderClient!!, wisataApi)
	}

	override fun setRecycleView() {
		linearLayoutManager = LinearLayoutManager(context)
		linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.listNearby.layoutManager = linearLayoutManager
        binding.listNearby.setHasFixedSize(true)
	}

	private fun setScrollRecycleView() {
        binding.listNearby.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
			override fun loadMoreItems() {
				isLoading = true
				currentPage += 1
				presenter?.getNearbyWisata(
					currentLatitude,
					currentLongitude,
					limit!!,
					currentPage
				)
			}

			override fun getTotalPageCount(): Int {
				return limit!!
			}

			override fun isLastPage(): Boolean {
				return isLastPage
			}

			override fun isLoading(): Boolean {
				return isLoading
			}

		})
	}


	override fun showLoading() {
        binding.shimmerView.visibility = View.VISIBLE
        binding.shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        binding.shimmerView.startShimmer()
        binding.swipeRefresh.visibility = View.GONE
	}

	override fun hideLoading() {
        binding.shimmerView.stopShimmer()
        binding.shimmerView.visibility = View.GONE
        binding.swipeRefresh.visibility = View.VISIBLE
	}

	override fun getLocationPermission() {
		val permissionAccessCoarseLocationApproved = ActivityCompat
			.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
				PackageManager.PERMISSION_GRANTED

		if (permissionAccessCoarseLocationApproved) {
			mLocationPermissionGranted = true
			presenter!!.setDeviceLocation(mLocationPermissionGranted)
		} else {
			// App doesn't have access to the device's location at all. Make full request
			// for permission.
			requestPermissions(
				arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
				requestCodeLocations
			)
		}
	}

	override fun updateLocationUI() {
		try {
			if (mLocationPermissionGranted) {
				if (isConnection) {
					presenter!!.setDeviceLocation(mLocationPermissionGranted)
				}
			} else {
				mLastKnownLocation = null
				getLocationPermission()
			}
		} catch (e: SecurityException) {
			Log.e("Exception: %s", e.message!!)
		}
	}

	override fun showDeviceLocation(location: Location) {
		mLastKnownLocation = location
		currentLatitude = mLastKnownLocation!!.latitude
		currentLongitude = mLastKnownLocation!!.longitude
		currentPage = 0
		presenter?.getNearbyWisata(currentLatitude, currentLongitude, limit!!, currentPage)
	}

	override fun getDataSuccess(list: MutableList<NearbyModel>) {
		countData = list.size
		if (currentPage == 0) {
			adapter = WisataTerdekatAdapter(
				list,
				requireActivity(),
				presenter!!
			)
            binding.listNearby.adapter = adapter
			setScrollRecycleView()
		} else {
			adapter?.removeLoadingFooter()
			isLoading = false
			adapter?.addData(list)
			adapter?.notifyDataSetChanged()
		}

		adapter?.setOnItemClickListener(object : OnItemClickListener {
			override fun onItemClick(view: View?, position: Int) {
				val model = adapter?.getDataList()
				presenter?.getItem(model!![position])
			}
		})
		checkLastData()
		checkData()
	}

	private fun checkLastData() {
		if (countData == limit) {
			adapter?.addLoadingFooter()
		} else {
			isLastPage = true
		}
	}

	private fun checkData() {
		if (adapter?.itemCount == 0) {
			showData(false)
		} else {
			showData(true)
		}
	}

	override fun getDataFail(message: String) {
		Log.d("ErrorFavorite", message)
		showData(false)
	}

	override fun showData(isShow: Boolean) {
		if (isShow) {
            binding.listNearby.visibility = View.VISIBLE
            binding.llNodata.visibility = View.GONE
		} else {
            binding.listNearby.visibility = View.GONE
            binding.llNodata.visibility = View.VISIBLE
		}
	}

	override fun moveToDetail(intent: Intent) {
		startActivity(intent)
	}

	override fun getDataFailLike(message: String?) {
		Log.d("ErrorWisataLike", message!!)
	}

	override fun showMessageFavorite(message: String?, view: View) {
		Snackbar.make(requireActivity().findViewById(R.id.clContainer), message!!, Snackbar.LENGTH_LONG)
			.show()
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		mLocationPermissionGranted = false
		when (requestCode) {
			requestCode -> {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.isNotEmpty()
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED
				) {
					mLocationPermissionGranted = true
				}
			}
		}
		updateLocationUI()
	}
}
